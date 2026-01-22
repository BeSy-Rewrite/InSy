# InSy Backend

Inventarisierungsmodul (Inventory Management Module) backend - a Spring Boot REST API for managing inventory, articles, and related data with OAuth2 security and PostgreSQL database.

## Overview

The InSy backend is a Spring Boot 3.4.6 application built with:
- **Java 17**
- **Spring Boot** for REST API and web services
- **Spring Data JPA** for database access
- **Spring Security** with OAuth2 resource server (Keycloak integration)
- **PostgreSQL** for data persistence
- **Maven** for build management
- **Docker** for containerization

## Prerequisites

### For Local Development
- Java 17 JDK
- Maven 3.9.x
- PostgreSQL 12+
- Docker and Docker Compose (optional, for database container)

### Environment Variables
Create a `.env` file in the backend directory with the following variables:

```env
# Database Configuration
POSTGRES_USER=insy_user
POSTGRES_PASSWORD=your_secure_password
POSTGRES_DB=insy
POSTGRES_HOST=localhost
POSTGRES_PORT=5432

# Server Configuration
PORT=8080

# Keycloak Configuration
ISSUER_URI=https://auth.insy.hs-esslingen.com/realms/insy
INSY_REQUIRED_ROLE=inventory-manager

# CORS Configuration
ALLOWED_ORIGIN=https://insy.hs-esslingen.com

# PgAdmin Configuration (for development)
PGA_EMAIL=admin@example.com
PGA_PASSWORD=admin

# JPA Configuration
JPA_HIBERNATE_DDL_AUTO=update
```

A `.env.example` file is provided as a template.

## Development Setup

### 1. Clone and Navigate
```bash
cd backend
```

### 2. Database Setup

#### Option A: Using Docker Compose (Recommended)
```bash
docker-compose -f dev-db.docker-compose.yml up -d
```

This starts:
- PostgreSQL database on port 5432
- PgAdmin4 on port 5050 (for database management)

#### Option B: Manual PostgreSQL Setup
Ensure PostgreSQL is running and create the database:
```sql
CREATE USER insy_user WITH PASSWORD 'your_password';
CREATE DATABASE insy OWNER insy_user;
```

### 3. Configure Environment
Copy and configure the `.env` file:
```bash
cp .env.example .env
```

Edit `.env` with your database credentials and Keycloak settings.

### 4. Build the Project
```bash
mvn clean install
```

### 5. Run in Development Mode
```bash
mvn spring-boot:run -Dspring-boot.run.arguments="--spring.profiles.active=dev"
```

Or run directly from your IDE:
- Open the project in your IDE (IntelliJ IDEA, VS Code, etc.)
- Run `InsyApplication.java` with the `dev` profile

The backend will start on `http://localhost:8080`

### 6. Access PgAdmin (if using Docker)
- Navigate to `http://localhost:5050`
- Login with credentials from `.env`
- Connect to the PostgreSQL server

## Building and Running

### Maven Commands

```bash
# Clean build
mvn clean install

# Run tests
mvn test

# Skip tests during build
mvn clean install -DskipTests

# Run application
mvn spring-boot:run

# Package as JAR
mvn clean package
```

### IDE Configuration
- **IntelliJ IDEA**: File → Project Structure → Project SDK → Java 17
- **VS Code**: Install "Extension Pack for Java" and ensure Java 17 is selected

## Docker Deployment

### Production Docker Compose

The `docker-compose.yml` file is configured for production deployment with:
- PostgreSQL database
- Backend service
- Traefik reverse proxy integration
- External network connectivity

### Building Docker Image
```bash
docker build -t insy-backend:latest .
```

The Dockerfile uses a multi-stage build:
1. **Build stage**: Compiles the Maven project
2. **Runtime stage**: Runs the JAR on Eclipse Temurin Java 17

### Running with Docker Compose (Production)

```bash
# Start all services
docker compose up -d

# View logs
docker compose logs -f backend

# Stop services
docker compose down
```

### Configuration for Docker
Ensure your `.env` file contains:
- `POSTGRES_HOST` pointing to the database service name
- `PORT` set to the exposed port (default: 8080)
- All required OAuth2 and CORS settings

### Push to Container Registry
```bash
# Tag image
docker tag insy-backend:latest ghcr.io/kr1pt0n05/insy-backend:latest

# Push to GitHub Container Registry
docker push ghcr.io/kr1pt0n05/insy-backend:latest
```

## API Documentation

The API documentation is available in the `../Documentation/` folder:
- `API-Documentation.yaml` - OpenAPI/Swagger specification
- `swagger.json` - Alternative JSON format

Access Swagger UI at: `http://localhost:8080/swagger-ui.html` (if enabled)

## Security

### Keycloak Integration
The application uses Keycloak for OAuth2 authentication:
- JWT tokens are validated against the configured issuer
- Users must have the `inventory-manager` role (configurable)
- CORS is restricted to allowed origins

### Database
- Uses prepared statements to prevent SQL injection
- Hikari connection pooling with secure configuration
- Environment-based password management

### File Upload
- Maximum file size: 10MB
- Maximum request size: 10MB

## Database Migrations

Hibernate DDL auto-update is configured via the `JPA_HIBERNATE_DDL_AUTO` environment variable:
- `update` (default): Automatically creates/updates schema
- `create`: Drops and recreates schema on startup (development only)
- `validate`: Validates schema without changes
- `none`: No automatic schema management

For production, consider using Flyway or Liquibase for schema versioning.

## Testing

Run the test suite:
```bash
mvn test
```

Tests include:
- Service layer tests
- Repository tests
- Controller integration tests
- Spring Security tests

Test files are located in `src/test/java/`

## Project Structure

```
backend/
├── src/
│   ├── main/
│   │   ├── java/com/hs_esslingen/insy/
│   │   │   ├── configuration/       # Spring beans and configuration
│   │   │   ├── controller/          # REST endpoints
│   │   │   ├── dto/                 # Data Transfer Objects
│   │   │   ├── exception/           # Custom exceptions
│   │   │   ├── mapper/              # Entity-DTO mappers (MapStruct)
│   │   │   ├── model/               # JPA entities
│   │   │   ├── repository/          # Data access layer
│   │   │   ├── security/            # Security configuration
│   │   │   ├── service/             # Business logic
│   │   │   └── utils/               # Utility classes
│   │   └── resources/
│   │       ├── application.properties
│   │       ├── application-dev.properties
│   │       └── application-test.properties
│   └── test/
│       └── java/com/hs_esslingen/insy/
├── Dockerfile                       # Multi-stage Docker build
├── docker-compose.yml               # Production deployment
├── dev-db.docker-compose.yml        # Development database setup
├── pom.xml                          # Maven dependencies
└── README.md                        # This file
```

## Troubleshooting

### Connection Refused
- Ensure PostgreSQL is running and accessible
- Check `POSTGRES_HOST` and `POSTGRES_PORT` in `.env`
- Verify credentials are correct

### Maven Build Failures
```bash
mvn clean install -U  # Force update of dependencies
```

### Docker Container Exits
```bash
docker-compose logs backend  # Check logs for errors
```

### Keycloak Token Issues
- Verify `ISSUER_URI` is correct and accessible
- Check token expiration time
- Ensure client configuration in Keycloak is correct

## Contributing

1. Create a feature branch
2. Make your changes
3. Run tests: `mvn test`
4. Commit and push
5. Create a pull request

## License

See the [LICENSE](../LICENSE) file in the project root.

## Additional Resources

- [Spring Boot Documentation](https://spring.io/projects/spring-boot)
- [Spring Security Documentation](https://spring.io/projects/spring-security)
- [PostgreSQL Documentation](https://www.postgresql.org/docs/)
- [Keycloak Documentation](https://www.keycloak.org/documentation)
- [Docker Documentation](https://docs.docker.com/)
