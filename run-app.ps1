param(
    [ValidateSet("start", "stop")]
    [string]$Action = "start"
)

Set-StrictMode -Version Latest
$ErrorActionPreference = "Stop"

$workspaceRoot = Split-Path -Parent $PSCommandPath
$runtimeDir = Join-Path $workspaceRoot ".runtime"
$logsDir = Join-Path $runtimeDir "logs"
$statePath = Join-Path $runtimeDir "run-app.state.json"

$frontendDir = Join-Path $workspaceRoot "frontend"
$backendDir = Join-Path $workspaceRoot "task-mgmt"
$keycloakDir = Join-Path $workspaceRoot "keycloak-26.2.5"
$backendJar = Join-Path $backendDir "target\task-mgmt-0.0.1-SNAPSHOT.jar"

$frontendPort = 3000
$backendPort = 8003
$postgresPort = 5435
$keycloakPort = 8081

$keycloakBaseUrl = "http://localhost:$keycloakPort"
$backendBaseUrl = "http://localhost:$backendPort"
$frontendUrl = "http://localhost:$frontendPort"

$managerUsername = "tsk_001"
$managerPassword = "Task@1234"
$realmName = "task-mgmt"
$clientId = "task-mgmt-client"
$clientSecret = "task-mgmt-secret-key"

function Write-Step {
    param([string]$Message)
    Write-Host ""
    Write-Host "==> $Message"
}

function Ensure-Directory {
    param([string]$Path)

    if (-not (Test-Path -LiteralPath $Path)) {
        New-Item -ItemType Directory -Force -Path $Path | Out-Null
    }
}

function Get-ListeningProcessId {
    param([int]$Port)

    $connection = Get-NetTCPConnection -State Listen -LocalPort $Port -ErrorAction SilentlyContinue |
        Select-Object -First 1

    if ($null -ne $connection) {
        return [int]$connection.OwningProcess
    }

    return $null
}

function Get-LogTail {
    param(
        [string]$Path,
        [int]$Lines = 80
    )

    if (Test-Path -LiteralPath $Path) {
        return (Get-Content -LiteralPath $Path -Tail $Lines | Out-String).Trim()
    }

    return "Log file not found: $Path"
}

function Wait-Until {
    param(
        [scriptblock]$Condition,
        [string]$Description,
        [int]$TimeoutSeconds = 180,
        [int]$PollSeconds = 3,
        [Nullable[int]]$LauncherProcessId = $null,
        [string]$LogPath = ""
    )

    $deadline = (Get-Date).AddSeconds($TimeoutSeconds)

    while ((Get-Date) -lt $deadline) {
        try {
            if (& $Condition) {
                return
            }
        } catch {
        }

        if ($null -ne $LauncherProcessId) {
            $launcher = Get-Process -Id $LauncherProcessId -ErrorAction SilentlyContinue
            if ($null -eq $launcher) {
                break
            }
        }

        Start-Sleep -Seconds $PollSeconds
    }

    $message = "$Description did not become ready within $TimeoutSeconds seconds."
    if ($LogPath) {
        $message += "`n`n" + (Get-LogTail -Path $LogPath)
    }

    throw $message
}

function Resolve-CommandPath {
    param(
        [string[]]$Candidates,
        [string]$FriendlyName
    )

    foreach ($candidate in $Candidates) {
        if (-not [string]::IsNullOrWhiteSpace($candidate) -and (Test-Path -LiteralPath $candidate)) {
            return $candidate
        }

        $resolved = Get-Command $candidate -ErrorAction SilentlyContinue
        if ($null -ne $resolved) {
            return $resolved.Source
        }
    }

    throw "$FriendlyName could not be found."
}

function Resolve-JavaPath {
    $javaHomeCandidate = if ($env:JAVA_HOME) { Join-Path $env:JAVA_HOME "bin\java.exe" } else { $null }
    return Resolve-CommandPath -Candidates @(
        $javaHomeCandidate,
        "java.exe",
        "C:\Program Files\Microsoft\jdk-17.0.17.10-hotspot\bin\java.exe"
    ) -FriendlyName "Java"
}

function Resolve-NpmPath {
    return Resolve-CommandPath -Candidates @(
        "npm.cmd",
        "C:\Program Files\nodejs\npm.cmd"
    ) -FriendlyName "npm"
}

function Resolve-MavenPath {
    $direct = Get-Command "mvn.cmd" -ErrorAction SilentlyContinue
    if ($null -ne $direct) {
        return $direct.Source
    }

    $wrapperRoot = Join-Path $HOME ".m2\wrapper\dists"
    if (Test-Path -LiteralPath $wrapperRoot) {
        $cached = Get-ChildItem -Path $wrapperRoot -Filter "mvn.cmd" -Recurse -ErrorAction SilentlyContinue |
            Sort-Object FullName -Descending |
            Select-Object -First 1

        if ($null -ne $cached) {
            return $cached.FullName
        }
    }

    throw "Maven could not be found. Install Maven or keep the cached wrapper distribution under $wrapperRoot."
}

function Test-KeycloakReady {
    try {
        $response = Invoke-WebRequest -Uri "$keycloakBaseUrl/realms/$realmName/.well-known/openid-configuration" `
            -UseBasicParsing `
            -TimeoutSec 10
        return $response.StatusCode -eq 200
    } catch {
        return $false
    }
}

function Test-FrontendReady {
    try {
        $response = Invoke-WebRequest -Uri $frontendUrl -UseBasicParsing -TimeoutSec 10
        return $response.StatusCode -eq 200
    } catch {
        return $false
    }
}

function Get-ManagerToken {
    return Invoke-RestMethod -Uri "$keycloakBaseUrl/realms/$realmName/protocol/openid-connect/token" `
        -Method Post `
        -ContentType "application/x-www-form-urlencoded" `
        -Body @{
            client_id     = $clientId
            client_secret = $clientSecret
            username      = $managerUsername
            password      = $managerPassword
            grant_type    = "password"
        } `
        -TimeoutSec 20
}

function Test-ManagerLogin {
    try {
        $tokenResponse = Get-ManagerToken
        return -not [string]::IsNullOrWhiteSpace($tokenResponse.access_token)
    } catch {
        return $false
    }
}

function Test-BackendReady {
    try {
        $loginResponse = Invoke-RestMethod -Uri "$backendBaseUrl/api/v1/auth/login" `
            -Method Post `
            -ContentType "application/json" `
            -Body (@{
                userName = $managerUsername
                password = $managerPassword
            } | ConvertTo-Json) `
            -TimeoutSec 20

        return -not [string]::IsNullOrWhiteSpace($loginResponse.token)
    } catch {
        return $false
    }
}

function Assert-ReadyOrFreePort {
    param(
        [int]$Port,
        [scriptblock]$ReadyCheck,
        [string]$ServiceName
    )

    if (& $ReadyCheck) {
        return "ready"
    }

    $owner = Get-ListeningProcessId -Port $Port
    if ($null -ne $owner) {
        throw "$ServiceName is not responding correctly, and port $Port is already occupied by PID $owner."
    }

    return "start"
}

function Start-LoggedPowerShellProcess {
    param(
        [string]$Name,
        [string]$WorkingDirectory,
        [string]$Command,
        [string]$LogPath
    )

    $process = Start-Process powershell.exe `
        -ArgumentList @("-NoProfile", "-ExecutionPolicy", "Bypass", "-Command", $Command) `
        -WorkingDirectory $WorkingDirectory `
        -PassThru

    return @{
        name = $Name
        pid  = $process.Id
        log  = $LogPath
    }
}

function Get-ChildProcessIds {
    param(
        [int]$ParentId,
        [object[]]$AllProcesses
    )

    $children = @($AllProcesses | Where-Object { $_.ParentProcessId -eq $ParentId })
    $ids = New-Object System.Collections.Generic.List[int]

    foreach ($child in $children) {
        $ids.Add([int]$child.ProcessId)
        foreach ($nestedId in Get-ChildProcessIds -ParentId $child.ProcessId -AllProcesses $AllProcesses) {
            $ids.Add([int]$nestedId)
        }
    }

    return $ids
}

function Save-State {
    param([object[]]$Processes)

    $state = @{
        generatedAt = (Get-Date).ToString("o")
        processes   = @($Processes)
    }

    $state | ConvertTo-Json -Depth 5 | Set-Content -LiteralPath $statePath -Encoding ASCII
}

function Stop-RecordedProcesses {
    if (-not (Test-Path -LiteralPath $statePath)) {
        Write-Step "No previous run state found."
        return
    }

    $state = Get-Content -LiteralPath $statePath -Raw | ConvertFrom-Json
    $records = @($state.processes)
    $allProcesses = Get-CimInstance Win32_Process
    $idsToStop = New-Object System.Collections.Generic.List[int]

    foreach ($record in $records) {
        $rootId = [int]$record.pid
        $rootProcess = Get-Process -Id $rootId -ErrorAction SilentlyContinue
        if ($null -eq $rootProcess) {
            continue
        }

        if (-not $idsToStop.Contains($rootId)) {
            $idsToStop.Add($rootId)
        }

        foreach ($childId in Get-ChildProcessIds -ParentId $rootId -AllProcesses $allProcesses) {
            if (-not $idsToStop.Contains([int]$childId)) {
                $idsToStop.Add([int]$childId)
            }
        }
    }

    foreach ($stopId in ($idsToStop | Sort-Object -Descending)) {
        Get-Process -Id $stopId -ErrorAction SilentlyContinue | Stop-Process -Force -ErrorAction SilentlyContinue
    }

    Remove-Item -LiteralPath $statePath -Force -ErrorAction SilentlyContinue
    Write-Step "Stopped previously launched frontend, backend, and Keycloak processes."
}

function Ensure-KeycloakManagerUser {
    if (Test-ManagerLogin) {
        return
    }

    Write-Step "Seeding the manager user in Keycloak."

    $adminToken = Invoke-RestMethod -Uri "$keycloakBaseUrl/realms/master/protocol/openid-connect/token" `
        -Method Post `
        -ContentType "application/x-www-form-urlencoded" `
        -Body @{
            client_id  = "admin-cli"
            username   = "admin"
            password   = "admin"
            grant_type = "password"
        } `
        -TimeoutSec 20

    $authHeaders = @{
        Authorization = "Bearer $($adminToken.access_token)"
    }
    $jsonHeaders = @{
        Authorization = "Bearer $($adminToken.access_token)"
        "Content-Type" = "application/json"
    }

    $lookupUri = "$keycloakBaseUrl/admin/realms/$realmName/users?username=$managerUsername"
    $users = @(
        Invoke-RestMethod -Uri $lookupUri -Headers $authHeaders -Method Get -TimeoutSec 20
    )

    if ($users.Count -eq 0) {
        $createBody = @{
            username      = $managerUsername
            enabled       = $true
            firstName     = "John"
            lastName      = "Doe"
            email         = "john.doe@example.com"
            emailVerified = $true
        } | ConvertTo-Json -Compress

        Invoke-RestMethod -Uri "$keycloakBaseUrl/admin/realms/$realmName/users" `
            -Headers $jsonHeaders `
            -Method Post `
            -Body $createBody `
            -TimeoutSec 20 | Out-Null

        $users = @(
            Invoke-RestMethod -Uri $lookupUri -Headers $authHeaders -Method Get -TimeoutSec 20
        )
    }

    $userId = $users[0].id
    $resetPasswordBody = @{
        type      = "password"
        value     = $managerPassword
        temporary = $false
    } | ConvertTo-Json -Compress

    Invoke-RestMethod -Uri "$keycloakBaseUrl/admin/realms/$realmName/users/$userId/reset-password" `
        -Headers $jsonHeaders `
        -Method Put `
        -Body $resetPasswordBody `
        -TimeoutSec 20 | Out-Null

    $managerRole = Invoke-RestMethod -Uri "$keycloakBaseUrl/admin/realms/$realmName/roles/MANAGER" `
        -Headers $authHeaders `
        -Method Get `
        -TimeoutSec 20

    $roleMappingBody = @(
        @{
            id          = $managerRole.id
            name        = $managerRole.name
            description = $managerRole.description
        }
    ) | ConvertTo-Json -Compress

    Invoke-RestMethod -Uri "$keycloakBaseUrl/admin/realms/$realmName/users/$userId/role-mappings/realm" `
        -Headers $jsonHeaders `
        -Method Post `
        -Body $roleMappingBody `
        -TimeoutSec 20 | Out-Null

    if (-not (Test-ManagerLogin)) {
        throw "The manager user could not be created in Keycloak."
    }
}

function Ensure-FrontendDependencies {
    if (Test-Path -LiteralPath (Join-Path $frontendDir "node_modules")) {
        return
    }

    Write-Step "Installing frontend dependencies."
    $npmPath = Resolve-NpmPath
    Push-Location $frontendDir
    try {
        & $npmPath "install"
    } finally {
        Pop-Location
    }
}

function Build-BackendJar {
    Write-Step "Building the backend jar."
    $mavenPath = Resolve-MavenPath

    Push-Location $backendDir
    try {
        & $mavenPath "-DskipTests" "package"
    } finally {
        Pop-Location
    }

    if (-not (Test-Path -LiteralPath $backendJar)) {
        throw "Backend jar was not produced at $backendJar."
    }
}

Ensure-Directory -Path $runtimeDir
Ensure-Directory -Path $logsDir

if ($Action -eq "stop") {
    Stop-RecordedProcesses
    exit 0
}

if (-not (Test-Path -LiteralPath $frontendDir)) {
    throw "Frontend directory not found at $frontendDir."
}

if (-not (Test-Path -LiteralPath $backendDir)) {
    throw "Backend directory not found at $backendDir."
}

if (-not (Test-Path -LiteralPath $keycloakDir)) {
    throw "Keycloak directory not found at $keycloakDir."
}

Stop-RecordedProcesses

if ($null -eq (Get-ListeningProcessId -Port $postgresPort)) {
    throw "PostgreSQL is not listening on port $postgresPort. Start the database first."
}

$startedProcesses = New-Object System.Collections.Generic.List[object]

$keycloakStatus = Assert-ReadyOrFreePort -Port $keycloakPort -ReadyCheck ${function:Test-KeycloakReady} -ServiceName "Keycloak"
if ($keycloakStatus -eq "start") {
    Write-Step "Starting Keycloak on port $keycloakPort."
    $keycloakLog = Join-Path $logsDir "keycloak.log"
    $keycloakCommand = "`$env:KEYCLOAK_ADMIN='admin'; `$env:KEYCLOAK_ADMIN_PASSWORD='admin'; & '$keycloakDir\bin\kc.bat' start-dev --http-port=$keycloakPort --import-realm *> '$keycloakLog'"
    $keycloakProcess = Start-LoggedPowerShellProcess -Name "keycloak" -WorkingDirectory $keycloakDir -Command $keycloakCommand -LogPath $keycloakLog
    $startedProcesses.Add($keycloakProcess)

    Wait-Until -Condition ${function:Test-KeycloakReady} `
        -Description "Keycloak" `
        -TimeoutSeconds 300 `
        -PollSeconds 5 `
        -LauncherProcessId $keycloakProcess.pid `
        -LogPath $keycloakLog
} else {
    Write-Step "Reusing Keycloak already running on port $keycloakPort."
}

Ensure-KeycloakManagerUser
Ensure-FrontendDependencies
Build-BackendJar

$backendStatus = Assert-ReadyOrFreePort -Port $backendPort -ReadyCheck ${function:Test-BackendReady} -ServiceName "Backend"
if ($backendStatus -eq "start") {
    Write-Step "Starting the Spring backend on port $backendPort."
    $backendLog = Join-Path $logsDir "backend.log"
    $javaPath = Resolve-JavaPath
    $backendCommand = "& '$javaPath' '-Dkeycloak.server-url=$keycloakBaseUrl' '-Dkeycloak.token-uri=$keycloakBaseUrl/realms/$realmName/protocol/openid-connect/token' '-Dkeycloak.userinfo-uri=$keycloakBaseUrl/realms/$realmName/protocol/openid-connect/token/introspect' -jar '$backendJar' *> '$backendLog'"
    $backendProcess = Start-LoggedPowerShellProcess -Name "backend" -WorkingDirectory $backendDir -Command $backendCommand -LogPath $backendLog
    $startedProcesses.Add($backendProcess)

    Wait-Until -Condition ${function:Test-BackendReady} `
        -Description "Backend login" `
        -TimeoutSeconds 240 `
        -PollSeconds 5 `
        -LauncherProcessId $backendProcess.pid `
        -LogPath $backendLog
} else {
    Write-Step "Reusing backend already running on port $backendPort."
}

$frontendStatus = Assert-ReadyOrFreePort -Port $frontendPort -ReadyCheck ${function:Test-FrontendReady} -ServiceName "Frontend"
if ($frontendStatus -eq "start") {
    Write-Step "Starting the React frontend on port $frontendPort."
    $frontendLog = Join-Path $logsDir "frontend.log"
    $npmPath = Resolve-NpmPath
    $frontendCommand = "`$env:BROWSER='none'; & '$npmPath' start *> '$frontendLog'"
    $frontendProcess = Start-LoggedPowerShellProcess -Name "frontend" -WorkingDirectory $frontendDir -Command $frontendCommand -LogPath $frontendLog
    $startedProcesses.Add($frontendProcess)

    Wait-Until -Condition ${function:Test-FrontendReady} `
        -Description "Frontend" `
        -TimeoutSeconds 240 `
        -PollSeconds 5 `
        -LauncherProcessId $frontendProcess.pid `
        -LogPath $frontendLog
} else {
    Write-Step "Reusing frontend already running on port $frontendPort."
}

Save-State -Processes $startedProcesses

Write-Step "Application stack is ready."
Write-Host "Frontend : $frontendUrl"
Write-Host "Backend  : $backendBaseUrl"
Write-Host "Keycloak : $keycloakBaseUrl"
Write-Host "Username : $managerUsername"
Write-Host "Password : $managerPassword"
Write-Host "Logs     : $logsDir"
Write-Host ""
Write-Host "To stop the processes started by this script, run:"
Write-Host "powershell -ExecutionPolicy Bypass -File `"$PSCommandPath`" -Action stop"
