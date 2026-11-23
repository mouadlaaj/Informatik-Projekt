# Informatik Projekt

Task management project with:

- React frontend on `http://localhost:3000`
- Spring Boot backend on `http://localhost:8003`
- Keycloak on `http://localhost:8081`
- PostgreSQL on `localhost:5435`
- Playwright end-to-end tests

## Documentation Video


## Local Config

The real backend config is intentionally not committed.

Create it locally:

```powershell
Copy-Item .\task-mgmt\src\main\resources\application.example.properties .\task-mgmt\src\main\resources\application.properties
```

Provide secrets through the environment of the process that uses them. Use separate values for the master-realm administrator, imported realm administrator and client secret. Store them in your local password manager or IDE settings.

| Variable | Used by |
| --- | --- |
| `DB_PASSWORD` | Spring Boot, for the PostgreSQL user |
| `KEYCLOAK_CLIENT_SECRET` | Keycloak realm import and Spring Boot, with the same value |
| `KEYCLOAK_SEED_ADMIN_PASSWORD` | First import of the task realm administrator |
| `KEYCLOAK_ADMIN_PASSWORD` | Spring Boot, for the configured Keycloak admin account in the master realm |
| `DEFAULT_MEMBER_PASSWORD` | Backend member provisioning and Playwright's newly created test accounts |
| `MANAGER_PASSWORD` | Playwright, for the existing test manager |

Use a dedicated test realm for Playwright. Supply the credentials to each process before starting it. `MANAGER_USERNAME` can select a different test manager.

## Run

Download Keycloak `26.2.5` and extract it as `keycloak-26.2.5`. Its realm import is under `keycloak-26.2.5/data/import/`. From PowerShell, with the values above already set in the environment, run:

```powershell
foreach ($name in 'KEYCLOAK_CLIENT_SECRET', 'KEYCLOAK_SEED_ADMIN_PASSWORD', 'KEYCLOAK_ADMIN_PASSWORD') {
    if (-not [Environment]::GetEnvironmentVariable($name)) { throw "Set $name before starting Keycloak." }
}
$env:KC_BOOTSTRAP_ADMIN_USERNAME = 'admin'
$env:KC_BOOTSTRAP_ADMIN_PASSWORD = $env:KEYCLOAK_ADMIN_PASSWORD
.\keycloak-26.2.5\bin\kc.bat start-dev --http-port 8081 --import-realm
```

Keycloak skips an existing realm at startup. For an existing installation, rotate the client secret in the Keycloak Admin Console and update the backend environment. Reset the seeded realm administrator's password and rotate any reused member or master-realm credentials in their respective accounts. Restart the backend with the updated settings.

Start backend:

```powershell
cd task-mgmt; .\mvnw.cmd -DskipTests package; java -jar .\target\task-mgmt-0.0.1-SNAPSHOT.jar
```

Start frontend:

```powershell
cd frontend; npm install; npm start
```

Run Playwright:

```powershell
cd Playwright; npm install; npx playwright test --headed
```
