# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Common Development Commands

### Building the Application
```bash
# Build the entire multi-module project from CustomerOrderServicesProject
cd CustomerOrderServicesProject
mvn clean package

# The EAR file will be generated at:
# CustomerOrderServicesApp/target/CustomerOrderServicesApp-0.1.0-SNAPSHOT.ear
```

### Local Development with OpenLiberty
```bash
# Start OpenLiberty development server with hot-reload
cd CustomerOrderServicesProject
mvn liberty:dev

# Access application at:
# http://localhost:9080/CustomerOrderServicesWeb
# http://localhost:9080/CustomerOrderServicesTest
```

### Database Setup (PostgreSQL)
```bash
# Start PostgreSQL via Docker (recommended for development)
docker run --name postgres-dev \
  -e POSTGRES_DB=orderdb \
  -e POSTGRES_USER=orderuser \
  -e POSTGRES_PASSWORD=orderpass \
  -p 5432:5432 \
  -d postgres:15

# Setup database tables (migrate from Common/ scripts)
# Tables will be auto-created via JPA DDL generation in development
```

### Container Development
```bash
# Build and run with Docker Compose
docker-compose up --build

# Access containerized application at:
# http://localhost:9080/CustomerOrderServicesWeb
# http://localhost:9080/CustomerOrderServicesTest
```

### Running Tests
Access the test application at `http://localhost:9080/CustomerOrderServicesTest` and run JPA tests to populate database. Must login with `rbarcia` or `kbrown` users.

## Application Architecture

### 3-Tier Enterprise Architecture
- **Database Layer**: PostgreSQL database (migrated from DB2)
- **Business Logic Layer**: Jakarta EE 10 with JPA 3.1 persistence
- **Presentation Layer**: Jakarta JAX-RS REST services with Dojo-based JavaScript frontend

### Core Modules
1. **CustomerOrderServices** (EJB Module)
   - Contains JPA entities and business service interfaces
   - Uses Jakarta EE 10 and JPA 3.1
   - Source in `ejbModule/org/pwte/example/`

2. **CustomerOrderServicesWeb** (Web Module)
   - Jakarta JAX-RS REST endpoints at `/Customer` path
   - Dojo-based frontend in `WebContent/`
   - Context root: `CustomerOrderServicesWeb`

3. **CustomerOrderServicesTest** (Test Module)
   - Integration tests for JPA and JAX-RS
   - Context root: `CustomerOrderServicesTest`
   - Sample JSON data in `WebContent/sampleJSON/`

4. **CustomerOrderServicesApp** (EAR Module)
   - Enterprise application packaging all modules
   - Security role: `SecureShopper`

### Key Components
- **Domain Model**: `org.pwte.example.domain.*` - JPA entities (Customer, Order, Product, etc.)
- **Services**: `org.pwte.example.service.*` - Business logic interfaces and implementations  
- **REST Resources**: `org.pwte.example.resources.*` - JAX-RS endpoints
- **Exception Handling**: `org.pwte.example.exception.*` - Custom business exceptions

### Security Configuration
- Role-based security with `SecureShopper` role
- Default test users: `rbarcia/bl0wfish`, `kbrown/bl0wfish`
- OpenLiberty basic registry for development
- JNDI data source: `jdbc/orderds` (PostgreSQL)

### Database Configuration  
- **PostgreSQL Database**: Combined customer orders and product inventory
- Connection pooling via OpenLiberty data sources
- Jakarta Persistence (JPA 3.1) with PostgreSQL dialect
- Auto DDL generation for development environment

## File Structure Notes
- Maven multi-module project with parent POM in `CustomerOrderServicesProject/`
- Each module has its own `pom.xml` and follows Maven standard directory layout
- OpenLiberty server configuration in `CustomerOrderServicesProject/src/main/liberty/config/server.xml`
- Deployment configuration in `Deployment/` including Dockerfile for containerization
- Jakarta EE descriptors in `META-INF/` and `WEB-INF/` directories
- Legacy WebSphere configuration scripts in `Common/WAS_Config/` (archived)

## Migration Status
- ✅ **Java Version**: Upgraded to Java 21
- ✅ **Application Server**: Migrated from WebSphere 8.5.5 to OpenLiberty
- ✅ **Enterprise Framework**: Migrated from JavaEE to Jakarta EE 10
- ✅ **Database**: Migrated from DB2 to PostgreSQL
- ✅ **Build System**: Updated Maven configuration for Jakarta EE dependencies
- ✅ **Namespace Migration**: All `javax.*` imports converted to `jakarta.*`