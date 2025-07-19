# Customer Order Services - Jakarta EE 10 Enterprise Application

[![Java](https://img.shields.io/badge/Java-21-orange.svg)](https://adoptium.net/)
[![Jakarta EE](https://img.shields.io/badge/Jakarta%20EE-10-blue.svg)](https://jakarta.ee/)
[![OpenLiberty](https://img.shields.io/badge/OpenLiberty-24.0.0.1-green.svg)](https://openliberty.io/)
[![PostgreSQL](https://img.shields.io/badge/PostgreSQL-15-blue.svg)](https://www.postgresql.org/)
[![Docker](https://img.shields.io/badge/Docker-Enabled-blue.svg)](https://www.docker.com/)

## 📋 Table of Contents

- [Overview](#overview)
- [Architecture](#architecture)
- [Prerequisites](#prerequisites)
- [Quick Start](#quick-start)
- [Local Development](#local-development)
- [Container Development](#container-development)
- [Database Setup](#database-setup)
- [Testing](#testing)
- [Deployment](#deployment)
- [Migration History](#migration-history)
- [Development Workflow](#development-workflow)
- [API Documentation](#api-documentation)
- [Troubleshooting](#troubleshooting)
- [Contributing](#contributing)

## 📖 Overview

The Customer Order Services application is a modernized enterprise e-commerce platform built with Jakarta EE 10 and OpenLiberty. Originally designed as a Web 2.0 store-front application, it has been completely migrated from legacy WebSphere 8.5.5 to a cloud-native architecture.

This application demonstrates a traditional 3-tier enterprise architecture with modern cloud-native deployment capabilities, featuring a browser-based shopping interface where users can manage shopping carts and submit orders.

### 🎯 Key Features

- **Modern Technology Stack**: Jakarta EE 10, Java 21, OpenLiberty
- **Cloud-Native Ready**: Docker containerization, Kubernetes deployment
- **Database Flexibility**: PostgreSQL (migrated from DB2)
- **RESTful APIs**: Jakarta JAX-RS services
- **Security**: Role-based authentication and authorization
- **Comprehensive Testing**: JPA and REST integration tests
- **Development Tools**: Hot-reload development server, Docker Compose

## 🏗️ Architecture

### Application Architecture

```
┌─────────────────┐    ┌──────────────────┐    ┌─────────────────┐
│   Web Browser   │────│   OpenLiberty    │────│   PostgreSQL    │
│  (Dojo + HTML)  │    │  Jakarta EE 10   │    │    Database     │
└─────────────────┘    └──────────────────┘    └─────────────────┘
```

### 3-Tier Enterprise Architecture

- **📊 Database Layer**: PostgreSQL database for orders and inventory
- **🔧 Business Logic Layer**: Jakarta EE 10 with JPA 3.1 persistence
- **🌐 Presentation Layer**: Jakarta JAX-RS REST services with Dojo-based frontend

### 📦 Module Structure

```
CustomerOrderServicesProject/
├── CustomerOrderServices/          # EJB Module (Business Logic)
│   ├── ejbModule/org/pwte/example/
│   │   ├── domain/                 # JPA Entities
│   │   ├── service/                # Business Services
│   │   └── exception/              # Custom Exceptions
│   └── META-INF/
│       └── persistence.xml         # JPA Configuration
├── CustomerOrderServicesWeb/       # Web Module (REST + UI)
│   ├── src/org/pwte/example/
│   │   ├── resources/              # JAX-RS Resources
│   │   └── app/                    # Application Configuration
│   └── WebContent/                 # Static Web Content
├── CustomerOrderServicesTest/      # Test Module
│   └── src/org/pwte/example/       # Integration Tests
├── CustomerOrderServicesApp/       # EAR Module (Packaging)
└── src/main/liberty/config/
    └── server.xml                  # OpenLiberty Configuration
```

## 🔧 Prerequisites

### Required Software

- **Java 21** or higher ([Eclipse Temurin](https://adoptium.net/) recommended)
- **Maven 3.8+** for building
- **Docker & Docker Compose** for containerized development
- **Git** for version control

### Optional Tools

- **IDE**: IntelliJ IDEA, Eclipse, or VS Code with Java extensions
- **PostgreSQL Client**: pgAdmin, DBeaver, or psql CLI
- **API Testing**: Postman, curl, or REST client

### System Requirements

- **Memory**: 4GB RAM minimum (8GB recommended)
- **Storage**: 2GB free space
- **Network**: Internet access for downloading dependencies

## 🚀 Quick Start

### 1. Clone and Build

```bash
# Clone the repository
git clone <repository-url>
cd monolith-websphere-855

# Build the application
cd CustomerOrderServicesProject
mvn clean package
```

### 2. Start with Docker Compose (Recommended)

```bash
# Start the full application stack
docker-compose up --build

# Access the application
open http://localhost:9080/CustomerOrderServicesWeb
open http://localhost:9080/CustomerOrderServicesTest
```

### 3. Verify Installation

1. **Web Application**: http://localhost:9080/CustomerOrderServicesWeb
2. **Test Suite**: http://localhost:9080/CustomerOrderServicesTest
3. **Health Check**: http://localhost:9080/health
4. **Metrics**: http://localhost:9080/metrics

### 🔐 Default Test Users

| Username | Password   | Role          |
|----------|------------|---------------|
| rbarcia  | bl0wfish   | SecureShopper |
| kbrown   | bl0wfish   | SecureShopper |

## 💻 Local Development

### OpenLiberty Development Server

```bash
# Start development server with hot-reload
cd CustomerOrderServicesProject
mvn liberty:dev

# The server will start on http://localhost:9080
# Code changes trigger automatic redeployment
```

### Development Features

- **🔄 Hot Reload**: Automatic redeployment on code changes
- **🐛 Debug Support**: Remote debugging on port 7777
- **📊 Metrics**: Built-in application metrics
- **🔍 Health Checks**: Liveness and readiness probes
- **📝 Logging**: Structured JSON logging

### IDE Configuration

#### IntelliJ IDEA

1. Import as Maven project
2. Set Project SDK to Java 21
3. Configure remote debugger: `localhost:7777`

#### Eclipse

1. Import existing Maven projects
2. Right-click → Properties → Java Build Path → Libraries → Modulepath → Add External JARs
3. Add OpenLiberty runtime

#### VS Code

1. Install Java Extension Pack
2. Open folder in VS Code
3. Configure `launch.json` for debugging

## 🐳 Container Development

### Docker Compose Stack

```bash
# Full stack with PostgreSQL
docker-compose up --build

# Scale application instances
docker-compose up --scale app=3

# View logs
docker-compose logs -f app

# Clean up
docker-compose down -v
```

### Container Features

- **🔧 Multi-stage builds**: Optimized production images
- **🔒 Security hardening**: Non-root user, minimal attack surface
- **📈 Performance tuning**: JVM optimization for containers
- **🌍 Environment configuration**: External configuration support

### Custom Docker Build

```bash
# Build application image
docker build -t customer-order-services .

# Run with external database
docker run -p 9080:9080 \
  -e DB_HOST=your-postgres-host \
  -e DB_USER=orderuser \
  -e DB_PASSWORD=orderpass \
  customer-order-services
```

## 🗄️ Database Setup

### PostgreSQL with Docker

```bash
# Start PostgreSQL container
docker run --name postgres-dev \
  -e POSTGRES_DB=orderdb \
  -e POSTGRES_USER=orderuser \
  -e POSTGRES_PASSWORD=orderpass \
  -p 5432:5432 \
  -d postgres:15

# Connect to database
docker exec -it postgres-dev psql -U orderuser -d orderdb
```

### Database Configuration

The application uses JPA auto-DDL generation for development. Tables are automatically created based on entity definitions.

#### Manual Schema Creation

```sql
-- Connect to PostgreSQL
\c orderdb

-- Tables are auto-generated by JPA
-- See CustomerOrderServices/ejbModule/org/pwte/example/domain/ for entity definitions
```

### Environment Variables

| Variable | Description | Default |
|----------|-------------|---------|
| `DB_HOST` | Database hostname | `localhost` |
| `DB_PORT` | Database port | `5432` |
| `DB_NAME` | Database name | `orderdb` |
| `DB_USER` | Database username | `orderuser` |
| `DB_PASSWORD` | Database password | `orderpass` |

## 🧪 Testing

### Integration Tests

```bash
# Run all tests
mvn test

# Run specific test module
cd CustomerOrderServicesTest
mvn test
```

### Web-based Testing

1. **Access Test Suite**: http://localhost:9080/CustomerOrderServicesTest
2. **Login**: Use `rbarcia` or `kbrown` credentials
3. **Run JPA Tests**: Populate database with test data
4. **Run JAX-RS Tests**: Validate REST endpoints

### Test Categories

- **🔗 JPA Tests**: Database persistence and transactions
- **🌐 REST Tests**: API endpoint validation
- **🔒 Security Tests**: Authentication and authorization
- **📊 Performance Tests**: Load and stress testing

### Sample Test Data

The test suite includes:
- Sample customers (business and residential)
- Product catalog with categories
- Order and line item examples
- Inventory data

## 🚀 Deployment

### Local Deployment

```bash
# Build EAR file
mvn clean package

# Deploy to OpenLiberty
cp CustomerOrderServicesApp/target/CustomerOrderServicesApp-0.1.0-SNAPSHOT.ear \
  /path/to/liberty/usr/servers/defaultServer/dropins/
```

### Container Deployment

```bash
# Build and push image
docker build -t your-registry/customer-order-services:latest .
docker push your-registry/customer-order-services:latest

# Deploy with Kubernetes
kubectl apply -f k8s/
```

### Kubernetes Deployment

```yaml
# Example deployment
apiVersion: apps/v1
kind: Deployment
metadata:
  name: customer-order-services
spec:
  replicas: 3
  selector:
    matchLabels:
      app: customer-order-services
  template:
    metadata:
      labels:
        app: customer-order-services
    spec:
      containers:
      - name: app
        image: customer-order-services:latest
        ports:
        - containerPort: 9080
        env:
        - name: DB_HOST
          value: "postgres-service"
```

### Health Checks

- **Liveness Probe**: `/health/live`
- **Readiness Probe**: `/health/ready`
- **Startup Probe**: `/health/started`

## 🔄 Migration History

### Migration Summary

This application has been completely modernized from legacy WebSphere to cloud-native architecture:

| Component | Before | After |
|-----------|---------|--------|
| **Java Version** | Java 8 | Java 21 |
| **Application Server** | WebSphere 8.5.5 | OpenLiberty 24.0.0.1 |
| **Enterprise Platform** | JavaEE 7 | Jakarta EE 10 |
| **Database** | IBM DB2 (dual databases) | PostgreSQL (unified) |
| **Persistence** | JPA 2.0 | JPA 3.1 |
| **Web Services** | JAX-RS 1.1 | Jakarta JAX-RS |
| **Build System** | Ant/Manual | Maven |
| **Deployment** | Manual EAR deployment | Container/K8s ready |

### Key Migration Changes

- ✅ **Namespace Migration**: All `javax.*` → `jakarta.*`
- ✅ **Database Consolidation**: Two DB2 databases → Single PostgreSQL
- ✅ **Container Ready**: Docker and Kubernetes deployment
- ✅ **Cloud Native**: Health checks, metrics, external configuration
- ✅ **Developer Experience**: Hot-reload, modern tooling

## ⚙️ Development Workflow

### Feature Development

```bash
# 1. Create feature branch
git checkout -b feature/new-feature

# 2. Start development server
mvn liberty:dev

# 3. Make changes and test
# Hot-reload automatically deploys changes

# 4. Run tests
mvn test

# 5. Build and verify
mvn clean package

# 6. Commit and push
git add .
git commit -m "feat: add new feature"
git push origin feature/new-feature
```

### Code Quality

```bash
# Format code (if configured)
mvn fmt:format

# Static analysis
mvn spotbugs:check

# Security scan
mvn org.owasp:dependency-check-maven:check

# Generate reports
mvn site
```

### Performance Monitoring

```bash
# Application metrics
curl http://localhost:9080/metrics

# Health status
curl http://localhost:9080/health

# JVM metrics
curl http://localhost:9080/metrics/vendor
```

## 📚 API Documentation

### REST Endpoints

| Endpoint | Method | Description |
|----------|--------|-------------|
| `/Customer/customer` | GET | List all customers |
| `/Customer/customer/{id}` | GET | Get customer by ID |
| `/Customer/customer` | POST | Create new customer |
| `/Customer/customer/{id}` | PUT | Update customer |
| `/Customer/order` | GET | List orders |
| `/Customer/order/{id}` | GET | Get order by ID |
| `/Customer/order` | POST | Create new order |
| `/Product/product` | GET | List products |
| `/Product/product/{id}` | GET | Get product by ID |

### Request Examples

#### Create Customer

```bash
curl -X POST http://localhost:9080/CustomerOrderServicesWeb/Customer/customer \
  -H "Content-Type: application/json" \
  -d '{
    "firstName": "John",
    "lastName": "Doe",
    "email": "john.doe@example.com"
  }'
```

#### Create Order

```bash
curl -X POST http://localhost:9080/CustomerOrderServicesWeb/Customer/order \
  -H "Content-Type: application/json" \
  -d '{
    "customerId": 1,
    "items": [
      {"productId": 1, "quantity": 2}
    ]
  }'
```

## 🔧 Troubleshooting

### Common Issues

#### Build Failures

```bash
# Clear Maven cache
rm -rf ~/.m2/repository

# Clean and rebuild
mvn clean compile

# Check Java version
java -version
mvn -version
```

#### Database Connection Issues

```bash
# Check PostgreSQL status
docker ps | grep postgres

# Test database connection
docker exec -it postgres-dev psql -U orderuser -d orderdb -c "SELECT 1;"

# Check application logs
docker-compose logs app
```

#### Port Conflicts

```bash
# Check what's using port 9080
lsof -i :9080

# Kill process if needed
kill -9 $(lsof -t -i:9080)

# Use different ports
mvn liberty:dev -Dliberty.var.default.http.port=9081
```

### Performance Issues

```bash
# Increase JVM memory
export MAVEN_OPTS="-Xmx2g -XX:MaxMetaspaceSize=512m"

# Monitor JVM metrics
curl http://localhost:9080/metrics/vendor | grep jvm

# Check container resources
docker stats
```

### Debug Mode

```bash
# Start with debugging enabled
mvn liberty:dev -DdebugEnabled=true

# Connect debugger to port 7777
# IntelliJ: Run → Edit Configurations → Remote JVM Debug
```

### Log Analysis

```bash
# View OpenLiberty logs
tail -f target/liberty/wlp/usr/servers/defaultServer/logs/messages.log

# View container logs
docker-compose logs -f app

# Increase log level
# Edit server.xml: <logging traceSpecification="*=info:org.pwte.example.*=fine"/>
```

## 🤝 Contributing

### Development Setup

1. **Fork the repository**
2. **Clone your fork**: `git clone <your-fork-url>`
3. **Create feature branch**: `git checkout -b feature/amazing-feature`
4. **Install dependencies**: `mvn clean compile`
5. **Start development server**: `mvn liberty:dev`

### Code Standards

- **Java Code Style**: Follow Google Java Style Guide
- **Commit Messages**: Use conventional commits format
- **Testing**: Write tests for new features
- **Documentation**: Update README and JavaDoc

### Pull Request Process

1. **Update tests** for new functionality
2. **Update documentation** if needed
3. **Ensure CI passes** all checks
4. **Request review** from maintainers
5. **Address feedback** promptly

### Release Process

```bash
# 1. Update version
mvn versions:set -DnewVersion=1.1.0

# 2. Build and test
mvn clean package
mvn test

# 3. Create release tag
git tag -a v1.1.0 -m "Release version 1.1.0"
git push origin v1.1.0

# 4. Deploy to registry
docker build -t customer-order-services:1.1.0 .
docker push your-registry/customer-order-services:1.1.0
```

---

## 📄 License

This project is licensed under the Apache License 2.0 - see the [LICENSE](LICENSE) file for details.

## 🆘 Support

- **Documentation**: Check this README and inline code comments
- **Issues**: [Create an issue](../../issues) for bugs or feature requests
- **Discussions**: [Join discussions](../../discussions) for questions
- **Enterprise Support**: Contact your organization's development team

---

**Happy Coding! 🚀**