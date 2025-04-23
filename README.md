# License Service API

## Overview
This project implements a simple license management API consisting of two microservices:

- **auth-service** – user registration and authentication using JWT
- **license-service** – license entitlement verification and license creation

The services communicate via JWT and are containerized using Docker.



## Technologies Used

- **Language**: Kotlin
- **Framework**: Spring Boot
- **Authentication**: JWT
- **Persistence**: PostgreSQL (via Spring Data JPA)
- **Build Tool**: Maven
- **Testing**: JUnit, MockK, SpringMockK, @DataJpaTest
- **Containerization**: Docker, Docker Compose



## Services

### auth-service
Responsible for user management and JWT issuance.

#### Features:
- `POST /auth/register` – register new user
- `POST /auth/login` – authenticate user and receive JWT

#### Technologies:
- Spring Boot Web
- Spring Security (JWT-based)
- PostgreSQL (Flyway migrations)
- MockK + WebMvcTest + @DataJpaTest for testing

#### Test Coverage:
- Unit: `AuthService`, `JwtTokenProvider`
- Controller: `AuthController`



### license-service
Handles license validation and creation.

#### Features:
- `GET /license?contentId=...` – check if current user has a license
- `POST /license` – create a new license (requires system token)

#### Authorization:
- Access to `/license` is protected via JWT
- `System <token>` authorization required for license creation

#### Technologies:
- Spring Boot Web
- Custom JWT Filter for user/system authorization
- PostgreSQL (Flyway migrations)
- MockK + WebMvcTest + @DataJpaTest for testing

#### Test Coverage:
- Unit: `LicenseService`, `JwtTokenProvider`, `JwtAuthenticationFilter`
- Controller: `LicenseController`



## Database

Both services use separate PostgreSQL databases with Flyway for schema migrations. Databases are defined in the `docker-compose.yml` file.



## How to Run the Project

### Prerequisites:
- Docker & Docker Compose
- Java 17+

### Run:

```bash
docker-compose up --build
```

This will start:
- PostgreSQL databases
- auth-service
- license-service

Services are accessible via:
- `http://localhost:8080` – auth-service
- `http://localhost:8081` – license-service



## Running Tests

You can run each service’s tests independently.

### auth-service

From the project root or inside the `auth-service` folder:

```bash
cd auth-service
./mvnw test
```

### license-service

From the project root or inside the `license-service` folder:

```bash
cd license-service
./mvnw test
```



### Environment Variables (`docker-compose.yml`)

- **auth-service**: `SPRING_DATASOURCE_URL`, `SPRING_DATASOURCE_USERNAME`, `SPRING_DATASOURCE_PASSWORD`, `security.jwt.secret`, `security.jwt.expiration`
- **license-service**: `SPRING_DATASOURCE_*`, `security.jwt.secret`, `SYSTEM_TOKEN`



## Example Requests

### Register User
```http
POST http://localhost:8080/auth/register
Content-Type: application/json

{
  "email": "test@example.com",
  "password": "password123"
}
```

### Login and Get JWT
```http
POST http://localhost:8080/auth/login
Content-Type: application/json

{
  "email": "test@example.com",
  "password": "password123"
}
```
_Response:_
```json
{
  "token": "<JWT>"
}
```

### Check License
```http
GET http://localhost:8081/license?contentId=avatar
Authorization: Bearer <JWT>
```

### Add License (System Token)
```http
POST http://localhost:8081/license
Authorization: System <SYSTEM_TOKEN>
Content-Type: application/json

{
  "userId": "<UUID>",
  "contentId": "avatar"
}
```
