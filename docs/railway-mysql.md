# Railway MySQL Deployment and Initialization

This document explains how to attach a Railway MySQL service to the Spring Boot backend and run the project SQL files during the first deployment.

## 1. Create a MySQL service

1. Open the Railway project.
2. Click **New**.
3. Choose **Database** -> **Add MySQL**.
4. Wait until the MySQL service is running.

Railway provides database variables on the MySQL service, including:

- `MYSQLHOST`
- `MYSQLPORT`
- `MYSQLUSER`
- `MYSQLPASSWORD`
- `MYSQLDATABASE`

## 2. Configure backend variables

Open the Spring Boot backend service in Railway, then open **Variables**.

If the database service is not named `MySQL`, replace `MySQL` in `${{MySQL.xxx}}` with the actual service name.

```env
SPRING_DATASOURCE_URL=jdbc:mysql://${{MySQL.MYSQLHOST}}:${{MySQL.MYSQLPORT}}/${{MySQL.MYSQLDATABASE}}?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=Asia/Shanghai&characterEncoding=UTF-8
SPRING_DATASOURCE_USERNAME=${{MySQL.MYSQLUSER}}
SPRING_DATASOURCE_PASSWORD=${{MySQL.MYSQLPASSWORD}}
SPRING_SQL_INIT_MODE=always
JWT_SECRET=please-change-to-a-long-random-secret-at-least-32-chars
```

`SPRING_SQL_INIT_MODE=always` is only for the first database initialization. It makes Spring Boot run:

- `backend/src/main/resources/sql/schema.sql`
- `backend/src/main/resources/sql/seed.sql`

## 3. First initialization

1. Save the backend service variables.
2. Click **Redeploy** on the backend service.
3. Check the logs and confirm that the app starts successfully.

Expected log lines:

```text
HikariPool-1 - Start completed.
Started PetServicePlatformApplication
```

Then verify:

```text
https://your-backend.railway.app/health
https://your-backend.railway.app/swagger-ui.html
```

## 4. Disable automatic import after success

After the first initialization succeeds, change the backend variable to:

```env
SPRING_SQL_INIT_MODE=never
```

Then redeploy the backend again.

Reason: `seed.sql` is demo-data initialization. It clears related tables before inserting demo records. Keeping `always` enabled can reset business data whenever the backend restarts or redeploys.

## 5. Troubleshooting

### Logs stop at `HikariPool-1 - Starting...`

Check:

- The MySQL service is running.
- `SPRING_DATASOURCE_URL` starts with `jdbc:mysql://`.
- The Railway service name in `${{MySQL.xxx}}` is correct.
- Username, password, host, port, and database variables reference the MySQL service.

### `Unknown database pet_service_platform`

The connection or SQL is still using the local database name. The current SQL files use the database selected by `SPRING_DATASOURCE_URL` and no longer execute `USE pet_service_platform`.

### `Access denied` or `CREATE DATABASE` errors

The Railway managed database is created by Railway. The application should only create tables. The current `schema.sql` no longer runs `CREATE DATABASE` or `DROP DATABASE`.

### Future schema changes

For this course project, incremental SQL can be executed manually. For production, use Flyway or Liquibase to manage database migrations.
