# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Common Development Commands

### Building the Application
```bash
# Build the entire multi-module project from CustomerOrderServicesProject
cd CustomerOrderServicesProject
mvn clean package

# The EAR file will be generated at:
# CustomerOrderServicesApp/target/CustomerOrderServicesApp-X.Y.Z-SNAPSHOT.ear
```

### Database Setup
```bash
# Start DB2 instance
su {database_instance_name}
db2start

# Create databases
db2 create database ORDERDB
db2 create database INDB

# Setup ORDERDB tables
db2 connect to ORDERDB
db2 -tf Common/createOrderDB.sql

# Setup INDB tables
db2 connect to INDB
db2 -tf Common/InventoryDdl.sql
db2 -tf Common/InventoryData.sql
```

### WebSphere Configuration
```bash
# Automated configuration using Jython script
# Edit Common/WAS_Config/was.properties first with correct paths
cd Common/WAS_Config
sh was_config_jython.sh -f was.properties

# Run generated configuration
<Profile Home>/bin/wsadmin.(bat/sh) –lang jython –f WAS_config.py
```

### Running Tests
Access the test application at `http://localhost:9080/CustomerOrderServicesTest` and run JPA tests to populate database. Must login with `rbarcia` or `kbrown` users.

## Application Architecture

### 3-Tier Enterprise Architecture
- **Database Layer**: Two DB2 databases (ORDERDB for orders, INDB for inventory)
- **Business Logic Layer**: EJB 3.0 with JPA 2.0 persistence
- **Presentation Layer**: JAX-RS 1.1 REST services with Dojo-based JavaScript frontend

### Core Modules
1. **CustomerOrderServices** (EJB Module)
   - Contains JPA entities and business service interfaces
   - Uses EJB 3.0 and JPA 2.0
   - Source in `ejbModule/org/pwte/example/`

2. **CustomerOrderServicesWeb** (Web Module)
   - JAX-RS REST endpoints at `/Customer` path
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
- J2C authentication data: `DBUser` for database connections
- JNDI data sources: `jdbc/orderds`, `jdbc/inds`

### Database Configuration  
- **ORDERDB**: Customer orders and related data
- **INDB**: Product inventory and catalog
- Connection pooling via WebSphere XA data sources
- OpenJPA as JPA provider with DB2 dialect

## File Structure Notes
- Maven multi-module project with parent POM in `CustomerOrderServicesProject/`
- Each module has its own `pom.xml` and follows Maven standard directory layout
- Configuration automation scripts in `Common/WAS_Config/`
- Deployment configuration in `Deployment/` including Dockerfile for containerization
- WebSphere-specific descriptors in `META-INF/` and `WEB-INF/` directories