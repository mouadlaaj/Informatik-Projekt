# Informatik Projekt

Task management project with:

- React frontend on `http://localhost:3000`
- Spring Boot backend on `http://localhost:8003`
- Keycloak on `http://localhost:8081`
- PostgreSQL on `localhost:5435`
- Playwright end-to-end tests

## Local Config

The real backend config is intentionally not committed.

Create it locally:

```powershell
Copy-Item .\task-mgmt\src\main\resources\application.example.properties .\task-mgmt\src\main\resources\application.properties
```

Then edit `application.properties` if your local passwords or ports differ.

## Run

Download Keycloak `26.2.5` and extract it as `keycloak-26.2.5`. The realm import file is included under `keycloak-26.2.5/data/import/`.

Start Keycloak:

```powershell
cd keycloak-26.2.5; $env:KEYCLOAK_ADMIN="admin"; $env:KEYCLOAK_ADMIN_PASSWORD="admin"; .\bin\kc.bat start-dev --http-port=8081 --import-realm
```

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
