# Customer Order Services - WebSphere to OpenLiberty Migration Plan

## Executive Summary

This document outlines the comprehensive migration strategy for modernizing the Customer Order Services application from WebSphere Application Server 8.5.5 to OpenLiberty running on Kubernetes. The migration will transform a traditional JavaEE monolith into a cloud-native application while preserving core business functionality.

## Claude Code Usage Guide

**For Claude Code Users**: This migration plan is optimized for execution by Claude Code. Each task includes:
- ✅ **Prerequisites**: What must be completed first
- 🔧 **Tools**: Which Claude tools to use (Read, Edit, MultiEdit, Bash, etc.)
- 📂 **File Paths**: Specific files to modify
- ⚡ **Commands**: Exact bash commands to execute
- 🧪 **Validation**: How to verify task completion
- 📝 **Todo Integration**: Use `TodoWrite` tool to track progress

**Important**: Always read relevant files first using the `Read` tool before making modifications. Use `TodoWrite` to break down phases into actionable tasks.

## Quick Start Guide for Claude Code

### Initial Setup Steps
1. **Read project structure**: `Read CLAUDE.md` and `LS /mnt/d/monolith-websphere-855`
2. **Create migration todos**: Use `TodoWrite` to create phase-specific task lists
3. **Validate prerequisites**: Run `mvn --version` and `java --version`
4. **Baseline measurements**: Execute current build to establish baseline

### Recommended Task Workflow
1. **Plan**: Use `TodoWrite` to break each phase into actionable tasks
2. **Analyze**: Use `Grep` and `Read` to understand current state
3. **Implement**: Use `Edit` or `MultiEdit` for code changes
4. **Validate**: Use `Bash` to test changes immediately
5. **Verify**: Mark todos complete only after validation passes

### Common Tool Usage Patterns
- **File Discovery**: `Glob "**/*.java"` → `Grep "import javax"` → `Read` specific files
- **Bulk Changes**: `Grep` to find → `MultiEdit` to change → `Bash` to validate
- **Configuration**: `Read` existing → `Edit` or `Write` new → `Bash` to test

### Error Recovery Strategies
If any step fails:
1. **Check prerequisites**: Ensure previous phase is complete
2. **Validate syntax**: Use appropriate validators (xmllint, javac, etc.)
3. **Rollback if needed**: Use git to revert changes
4. **Break down task**: Split complex tasks into smaller pieces
5. **Update todos**: Mark failed tasks as blocked and note the issue

## Current State Analysis

### Technology Stack
- **Runtime**: WebSphere Application Server 8.5.5
- **JavaEE Version**: Mixed (JavaEE 5 in web module, JavaEE 7 in EJB module)
- **Java Version**: Inconsistent (Java 1.6 in web, Java 1.8 in EJB)
- **Specifications**: EJB 3.0, JPA 2.0, JAX-RS 1.1, Servlet 3.0/2.5
- **Database**: DB2 (ORDERDB, INDB)
- **Frontend**: Dojo Toolkit-based JavaScript
- **Security**: WebSphere security realm with BASIC authentication

### Architecture Components
1. **CustomerOrderServices** (EJB Module): Business logic and JPA entities
2. **CustomerOrderServicesWeb** (Web Module): JAX-RS endpoints and Dojo frontend
3. **CustomerOrderServicesTest** (Test Module): Integration tests
4. **CustomerOrderServicesApp** (EAR Module): Enterprise application package

### Migration Challenges Identified

#### Critical Issues
1. **WebSphere-specific Dependencies**:
   - `com.ibm.websphere.jaxrs.server.IBMRestServlet`
   - IBM JSON4J providers
   - WebSphere API dependencies

2. **Legacy Specifications**:
   - JAX-RS 1.1 (JSR-311) instead of modern JAX-RS
   - Mixed JavaEE versions across modules
   - Old Java compiler targets (1.6/1.8)

3. **Configuration Dependencies**:
   - IBM-specific binding files (`ibm-web-bnd.xml`, `ibm-application-bnd.xml`)
   - WebSphere JNDI datasource lookups
   - WebSphere security realm integration

4. **Database Integration**:
   - Hardcoded DB2 connection parameters
   - WebSphere-managed datasources and transactions

## Target State Architecture

### Cloud-Native Design Principles
- **12-Factor App Compliance**: Externalized configuration, stateless design
- **Microservices Ready**: Modular deployment capability
- **Container-First**: Docker containerization with OpenLiberty
- **Kubernetes Native**: Health checks, config maps, secrets management

### Technology Stack Upgrade
- **Runtime**: OpenLiberty 23.x.x with Jakarta EE 10
- **Java Version**: Java 17 LTS
- **Specifications**: Jakarta EE 10 (JAX-RS 3.1, JPA 3.1, CDI 4.0)
- **Database**: PostgreSQL or containerized DB2
- **Security**: JWT/OAuth2 with Kubernetes RBAC integration
- **Observability**: MicroProfile Metrics, Health, OpenTracing

## Migration Strategy

### Phase 1: Foundation Modernization (2-3 weeks)

#### 1.1 Java and Build System Upgrade
```bash
# Update all modules to Java 17
# Standardize Maven configuration
# Update dependency versions
```

**Tasks** (Use TodoWrite to track these):

**1.1.1 Update Parent POM Java Version**
- ✅ **Prerequisites**: None
- 🔧 **Tools**: `Read`, `Edit`
- 📂 **File**: `CustomerOrderServicesProject/pom.xml`
- ⚡ **Commands**: None (file edit only)
- 🧪 **Validation**: `cd CustomerOrderServicesProject && mvn validate`

**1.1.2 Update Module POMs**
- ✅ **Prerequisites**: Parent POM updated
- 🔧 **Tools**: `Glob`, `Read`, `MultiEdit`
- 📂 **Files**: All `**/pom.xml` files
- ⚡ **Commands**: `find . -name "pom.xml" -exec grep -l "<maven.compiler.source>" {} \;`
- 🧪 **Validation**: `mvn clean compile -f CustomerOrderServicesProject/pom.xml`

**1.1.3 Verify Java 17 Compilation**
- ✅ **Prerequisites**: All POMs updated
- 🔧 **Tools**: `Bash`
- 📂 **Files**: None
- ⚡ **Commands**: `cd CustomerOrderServicesProject && mvn clean compile`
- 🧪 **Validation**: Check for compilation errors, verify bytecode version

#### 1.2 Dependency Migration

**1.2.1 Analyze Current Dependencies**
- ✅ **Prerequisites**: Java version updated
- 🔧 **Tools**: `Bash`, `Grep`
- 📂 **Files**: All `pom.xml` files
- ⚡ **Commands**: 
  ```bash
  # Find all WebSphere dependencies
  grep -r "com.ibm.websphere" --include="*.xml" .
  grep -r "javax." --include="*.xml" .
  ```
- 🧪 **Validation**: Document all findings for replacement

**1.2.2 Replace JavaEE with Jakarta EE Dependencies**
- ✅ **Prerequisites**: Dependency analysis complete
- 🔧 **Tools**: `Read`, `Edit`
- 📂 **Files**: 
  - `CustomerOrderServicesProject/pom.xml`
  - `CustomerOrderServices/pom.xml`
  - `CustomerOrderServicesWeb/pom.xml`
- ⚡ **Commands**: None (file edits only)
- 🧪 **Validation**: `mvn dependency:tree | grep jakarta`

**Example Jakarta EE Dependencies**:
```xml
<!-- Replace in parent POM -->
<dependency>
    <groupId>jakarta.platform</groupId>
    <artifactId>jakarta.jakartaee-api</artifactId>
    <version>10.0.0</version>
    <scope>provided</scope>
</dependency>
```

**1.2.3 Remove WebSphere Dependencies**
- ✅ **Prerequisites**: Jakarta EE dependencies added
- 🔧 **Tools**: `MultiEdit`
- 📂 **Files**: All `pom.xml` files with WebSphere dependencies
- ⚡ **Commands**: `mvn dependency:analyze`
- 🧪 **Validation**: No `com.ibm.websphere` in dependency tree

#### 1.3 Package Namespace Migration

**1.3.1 Find All javax Imports**
- ✅ **Prerequisites**: Dependencies updated
- 🔧 **Tools**: `Bash`, `Grep`
- 📂 **Files**: All `.java` files
- ⚡ **Commands**: 
  ```bash
  # Find all javax imports
  find . -name "*.java" -exec grep -l "import javax\." {} \;
  
  # Count occurrences by type
  grep -r "import javax\." --include="*.java" . | cut -d: -f2 | sort | uniq -c
  ```
- 🧪 **Validation**: Document all javax usage patterns

**1.3.2 Replace javax with jakarta Imports**
- ✅ **Prerequisites**: javax imports identified
- 🔧 **Tools**: `Task` (for bulk operations), `MultiEdit` (for specific files)
- 📂 **Files**: All `.java` files with javax imports
- ⚡ **Commands**: 
  ```bash
  # Backup before mass replace
  git checkout -b javax-to-jakarta-migration
  
  # Replace common patterns (run one at a time and validate)
  find . -name "*.java" -exec sed -i 's/import javax\.ejb\./import jakarta.ejb./g' {} \;
  find . -name "*.java" -exec sed -i 's/import javax\.persistence\./import jakarta.persistence./g' {} \;
  find . -name "*.java" -exec sed -i 's/import javax\.ws\.rs\./import jakarta.ws.rs./g' {} \;
  find . -name "*.java" -exec sed -i 's/import javax\.servlet\./import jakarta.servlet./g' {} \;
  ```
- 🧪 **Validation**: 
  ```bash
  # Verify no javax imports remain
  grep -r "import javax\." --include="*.java" .
  
  # Compile to check for issues
  mvn clean compile -f CustomerOrderServicesProject/pom.xml
  ```

**Package Transformation Reference**:
```java
// javax → jakarta transformations
javax.ejb.* → jakarta.ejb.*
javax.persistence.* → jakarta.persistence.*
javax.ws.rs.* → jakarta.ws.rs.*
javax.servlet.* → jakarta.servlet.*
javax.annotation.* → jakarta.annotation.*
javax.enterprise.* → jakarta.enterprise.*
javax.inject.* → jakarta.inject.*
javax.json.* → jakarta.json.*
javax.validation.* → jakarta.validation.*
```

### Phase 2: Application Server Migration (3-4 weeks)

#### 2.1 OpenLiberty Configuration

**2.1.1 Create OpenLiberty server.xml**
- ✅ **Prerequisites**: Phase 1 complete, dependencies migrated
- 🔧 **Tools**: `Write`
- 📂 **File**: `CustomerOrderServicesProject/src/main/liberty/config/server.xml`
- ⚡ **Commands**: `mkdir -p CustomerOrderServicesProject/src/main/liberty/config`
- 🧪 **Validation**: Validate XML syntax with `xmllint --noout server.xml`

**server.xml Content**:
```xml
<server description="Customer Order Services">
    <featureManager>
        <feature>jakartaee-10.0</feature>
        <feature>microProfile-6.0</feature>
        <feature>restConnector-2.0</feature>
    </featureManager>
    
    <httpEndpoint id="defaultHttpEndpoint" 
                  httpPort="9080" 
                  httpsPort="9443"/>
    
    <application location="CustomerOrderServicesApp.ear"/>
    
    <dataSource id="orderDS" jndiName="jdbc/orderds">
        <jdbcDriver>
            <library>
                <fileset dir="/opt/liberty/usr/shared/resources" includes="*.jar"/>
            </library>
        </jdbcDriver>
        <properties.postgresql serverName="${DB_HOST}" 
                              portNumber="${DB_PORT}"
                              databaseName="${DB_NAME}"
                              user="${DB_USER}"
                              password="${DB_PASSWORD}"/>
    </dataSource>
</server>
```

#### 2.2 Application Code Updates

**2.2.1 Create JAX-RS Application Class**
- ✅ **Prerequisites**: OpenLiberty server.xml created
- 🔧 **Tools**: `Write`
- 📂 **File**: `CustomerOrderServicesWeb/src/main/java/org/pwte/example/resources/CustomerServicesApplication.java`
- ⚡ **Commands**: None (file creation only)
- 🧪 **Validation**: Check compilation with `mvn compile`

**JAX-RS Application Class**:
```java
package org.pwte.example.resources;

import jakarta.ws.rs.ApplicationPath;
import jakarta.ws.rs.core.Application;
import java.util.Set;
import java.util.HashSet;

@ApplicationPath("/jaxrs")
public class CustomerServicesApplication extends Application {
    @Override
    public Set<Class<?>> getClasses() {
        Set<Class<?>> classes = new HashSet<>();
        classes.add(CategoryResource.class);
        classes.add(CustomerOrderResource.class);
        classes.add(ProductResource.class);
        return classes;
    }
}
```

**2.2.2 Remove WebSphere-specific Code**
- ✅ **Prerequisites**: JAX-RS application class created
- 🔧 **Tools**: `Grep`, `Read`, `Edit`
- 📂 **Files**: Find with `grep -r "com.ibm" --include="*.java" .`
- ⚡ **Commands**: 
  ```bash
  # Find WebSphere-specific imports
  grep -r "import com.ibm" --include="*.java" .
  
  # Find IBM JSON usage
  grep -r "JSON4J\|JSONObject" --include="*.java" .
  ```
- 🧪 **Validation**: No IBM-specific imports in code

**2.2.3 Update web.xml for Jakarta EE**
- ✅ **Prerequisites**: WebSphere code removed
- 🔧 **Tools**: `Read`, `Edit`
- 📂 **File**: `CustomerOrderServicesWeb/WebContent/WEB-INF/web.xml`
- ⚡ **Commands**: None (file edit only)
- 🧪 **Validation**: XML validation and deployment test

#### 2.3 Security Migration

**2.3.1 Update web.xml Security Configuration**
- ✅ **Prerequisites**: web.xml updated for Jakarta EE
- 🔧 **Tools**: `Read`, `Edit`
- 📂 **File**: `CustomerOrderServicesWeb/WebContent/WEB-INF/web.xml`
- ⚡ **Commands**: None (file edit only)
- 🧪 **Validation**: Test authentication with sample users

**Updated web.xml Security Section**:
```xml
<security-constraint>
    <web-resource-collection>
        <web-resource-name>Customer</web-resource-name>
        <url-pattern>/jaxrs/Customer</url-pattern>
        <url-pattern>/jaxrs/Customer/*</url-pattern>
    </web-resource-collection>
    <auth-constraint>
        <role-name>SecureShopper</role-name>
    </auth-constraint>
</security-constraint>

<login-config>
    <auth-method>BASIC</auth-method>
</login-config>
```

### Phase 3: Database Migration (2-3 weeks)

**Claude Usage Notes**: Use `TodoWrite` to break this phase into specific database tasks. Each database operation should be validated before proceeding to the next step.

#### 3.1 Database Technology Decision

**3.1.1 Analyze Current Database Usage**
- ✅ **Prerequisites**: Application migration complete
- 🔧 **Tools**: `Read`, `Grep`
- 📂 **Files**: 
  - `Common/createOrderDB.sql`
  - `Common/InventoryDdl.sql`
  - `Common/InventoryData.sql`
- ⚡ **Commands**: 
  ```bash
  # Analyze DB2-specific syntax
  grep -i "db2\|varchar.*for bit data\|generated always" Common/*.sql
  
  # Find data types that need migration
  grep -E "CLOB\|BLOB\|BIGINT\|DECIMAL" Common/*.sql
  ```
- 🧪 **Validation**: Document all DB2-specific features in use

**Decision Matrix**:
- **Option A: PostgreSQL Migration** - Better for cloud-native, requires schema migration
- **Option B: Containerized DB2** - Faster migration, licensing costs

#### 3.2 Database Configuration Updates
**persistence.xml for PostgreSQL**:
```xml
<persistence-unit name="CustomerOrderServices">
    <jta-data-source>jdbc/orderds</jta-data-source>
    <properties>
        <property name="eclipselink.target-database" value="PostgreSQL"/>
        <property name="eclipselink.ddl-generation" value="create-or-extend-tables"/>
    </properties>
</persistence-unit>
```

#### 3.3 Connection Management
- Replace WebSphere datasource with OpenLiberty datasource
- Externalize database configuration using environment variables
- Implement connection pooling with HikariCP

### Phase 4: Containerization (2 weeks)

**Claude Usage Notes**: This phase involves creating container configurations and testing. Use `Write` tool for Dockerfile creation and `Bash` for Docker commands.

#### 4.1 Docker Configuration

**4.1.1 Create Application Dockerfile**
- ✅ **Prerequisites**: Database migration complete, application working
- 🔧 **Tools**: `Write`
- 📂 **File**: `CustomerOrderServicesProject/Dockerfile`
- ⚡ **Commands**: 
  ```bash
  # Test Docker build
  docker build -t customer-order-services:latest .
  
  # Test local run
  docker run -p 9080:9080 customer-order-services:latest
  ```
- 🧪 **Validation**: Application starts successfully in container

**Dockerfile Content**:
```dockerfile
FROM icr.io/appcafe/open-liberty:kernel-slim-java17-openj9-ubi

COPY --chown=1001:0 server.xml /config/
COPY --chown=1001:0 CustomerOrderServicesApp.ear /config/apps/
COPY --chown=1001:0 postgres-driver.jar /opt/liberty/usr/shared/resources/

RUN configure.sh
```

#### 4.2 Configuration Externalization
**bootstrap.properties**:
```properties
# Database configuration
DB_HOST=${DB_HOST}
DB_PORT=${DB_PORT}
DB_NAME=${DB_NAME}
DB_USER=${DB_USER}
DB_PASSWORD=${DB_PASSWORD}

# Application configuration
APP_CONTEXT_ROOT=${APP_CONTEXT_ROOT}
```

### Phase 5: Kubernetes Deployment (2-3 weeks)

**Claude Usage Notes**: Use `Write` tool for creating Kubernetes manifests. Test each manifest individually before combining into full deployment.

#### 5.1 Kubernetes Manifests

**5.1.1 Create Application Deployment**
- ✅ **Prerequisites**: Docker image built and tested
- 🔧 **Tools**: `Write`
- 📂 **File**: `CustomerOrderServicesProject/k8s/deployment.yaml`
- ⚡ **Commands**: 
  ```bash
  # Create k8s directory
  mkdir -p CustomerOrderServicesProject/k8s
  
  # Validate manifest
  kubectl apply --dry-run=client -f k8s/deployment.yaml
  
  # Deploy to test namespace
  kubectl apply -f k8s/deployment.yaml -n test
  ```
- 🧪 **Validation**: Pods start successfully and pass health checks

**Deployment Manifest**:
```yaml
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
      - name: customer-order-services
        image: customer-order-services:latest
        ports:
        - containerPort: 9080
        - containerPort: 9443
        env:
        - name: DB_HOST
          valueFrom:
            configMapKeyRef:
              name: db-config
              key: host
        - name: DB_USER
          valueFrom:
            secretKeyRef:
              name: db-credentials
              key: username
        livenessProbe:
          httpGet:
            path: /health/live
            port: 9080
        readinessProbe:
          httpGet:
            path: /health/ready
            port: 9080
```

**Service**:
```yaml
apiVersion: v1
kind: Service
metadata:
  name: customer-order-services-service
spec:
  selector:
    app: customer-order-services
  ports:
  - name: http
    port: 80
    targetPort: 9080
  - name: https
    port: 443
    targetPort: 9443
```

**ConfigMap and Secrets**:
```yaml
apiVersion: v1
kind: ConfigMap
metadata:
  name: db-config
data:
  host: "postgresql-service"
  port: "5432"
  database: "orderdb"

---
apiVersion: v1
kind: Secret
metadata:
  name: db-credentials
type: Opaque
data:
  username: <base64-encoded-username>
  password: <base64-encoded-password>
```

#### 5.2 Database Deployment
**PostgreSQL StatefulSet**:
```yaml
apiVersion: apps/v1
kind: StatefulSet
metadata:
  name: postgresql
spec:
  serviceName: postgresql-service
  replicas: 1
  selector:
    matchLabels:
      app: postgresql
  template:
    metadata:
      labels:
        app: postgresql
    spec:
      containers:
      - name: postgresql
        image: postgres:15
        ports:
        - containerPort: 5432
        env:
        - name: POSTGRES_DB
          value: "orderdb"
        - name: POSTGRES_USER
          valueFrom:
            secretKeyRef:
              name: db-credentials
              key: username
        - name: POSTGRES_PASSWORD
          valueFrom:
            secretKeyRef:
              name: db-credentials
              key: password
        volumeMounts:
        - name: postgresql-storage
          mountPath: /var/lib/postgresql/data
  volumeClaimTemplates:
  - metadata:
      name: postgresql-storage
    spec:
      accessModes: ["ReadWriteOnce"]
      resources:
        requests:
          storage: 10Gi
```

### Phase 6: Observability and Monitoring (1-2 weeks)

**Claude Usage Notes**: Implement monitoring components incrementally. Test each health check and metric before proceeding.

#### 6.1 Health Checks Implementation

**6.1.1 Create Database Health Check**
- ✅ **Prerequisites**: Application deployed on Kubernetes
- 🔧 **Tools**: `Write`
- 📂 **File**: `CustomerOrderServices/ejbModule/org/pwte/example/health/DatabaseHealthCheck.java`
- ⚡ **Commands**: 
  ```bash
  # Test health endpoint
  curl http://localhost:9080/health
  
  # Test in Kubernetes
  kubectl port-forward pod/<pod-name> 9080:9080
  curl http://localhost:9080/health
  ```
- 🧪 **Validation**: Health check returns UP status when database is available

**Health Check Implementation**:
```java
@ApplicationScoped
@Health
public class DatabaseHealthCheck implements HealthCheck {
    
    @Inject
    private DataSource dataSource;
    
    @Override
    public HealthCheckResponse call() {
        try (Connection connection = dataSource.getConnection()) {
            return HealthCheckResponse.up("Database connection successful");
        } catch (SQLException e) {
            return HealthCheckResponse.down("Database connection failed: " + e.getMessage());
        }
    }
}
```

#### 6.2 Metrics Configuration
```java
@ApplicationScoped
public class BusinessMetrics {
    
    @Inject
    @Metric(name = "orders_processed_total")
    private Counter ordersProcessedCounter;
    
    @Inject
    @Metric(name = "order_processing_duration_seconds")
    private Timer orderProcessingTimer;
}
```

## Testing Strategy

### Unit Testing Updates

**Testing Phase 1: Update Test Dependencies**
- ✅ **Prerequisites**: Application code migrated
- 🔧 **Tools**: `Read`, `Edit`
- 📂 **Files**: Test module `pom.xml` files
- ⚡ **Commands**: 
  ```bash
  # Run existing tests to establish baseline
  mvn test -f CustomerOrderServicesProject/pom.xml
  
  # Update to Jakarta EE test dependencies
  mvn dependency:tree | grep test
  ```
- 🧪 **Validation**: All tests compile and run

### Integration Testing

**Testing Phase 2: Local Kubernetes Testing**
- ✅ **Prerequisites**: Kubernetes manifests created
- 🔧 **Tools**: `Bash`
- 📂 **Files**: None (commands only)
- ⚡ **Commands**: 
  ```bash
  # Start local cluster
  minikube start
  
  # Deploy application
  kubectl apply -f CustomerOrderServicesProject/k8s/
  
  # Test endpoints
  kubectl port-forward service/customer-order-services-service 8080:80
  curl http://localhost:8080/CustomerOrderServicesWeb/jaxrs/Customer
  ```
- 🧪 **Validation**: All REST endpoints respond correctly

### Migration Validation

**Testing Phase 3: End-to-End Validation**
- ✅ **Prerequisites**: Integration tests passing
- 🔧 **Tools**: `Bash`, `Task`
- 📂 **Files**: Test scripts directory
- ⚡ **Commands**: 
  ```bash
  # Performance baseline
  curl -w "@curl-format.txt" -o /dev/null -s http://localhost:8080/api/health
  
  # Load testing
  ab -n 1000 -c 10 http://localhost:8080/CustomerOrderServicesWeb/jaxrs/Customer
  ```
- 🧪 **Validation**: Performance within 10% of WebSphere baseline

## Risk Mitigation

### High-Risk Areas
1. **Database Migration**: Potential data loss during schema migration
2. **Security Model Changes**: Authentication/authorization differences
3. **Transaction Management**: EJB transaction behavior changes
4. **Performance**: Potential performance degradation

### Mitigation Strategies
1. **Database Migration**:
   - Full database backup before migration
   - Parallel deployment for validation
   - Rollback procedures documented

2. **Security**:
   - Gradual security model migration
   - Side-by-side security testing
   - User acceptance testing

3. **Performance**:
   - Baseline performance measurements
   - Load testing in staging environment
   - Resource allocation optimization

## Rollback Strategy

### Application Rollback
- Keep WebSphere deployment available during migration
- Blue-green deployment approach
- Database synchronization mechanisms

### Emergency Procedures
- Documented rollback procedures for each phase
- Monitoring and alerting for early issue detection
- Automated rollback triggers based on SLA breaches

## Timeline and Milestones

**Claude Usage Integration**: Use `TodoWrite` to track progress through each milestone. Each week should have specific, measurable deliverables that Claude can validate.

### Phase 1: Foundation (Weeks 1-3)
- **Week 1**: Java 17 upgrade and dependency analysis
- **Week 2**: Jakarta EE namespace migration
- **Week 3**: Build system standardization and testing

### Phase 2: Application Server (Weeks 4-7)
- **Week 4**: OpenLiberty setup and basic configuration
- **Week 5**: Application code migration and testing
- **Week 6**: Security migration and testing
- **Week 7**: Integration testing and validation

### Phase 3: Database (Weeks 8-10)
- **Week 8**: Database technology decision and setup
- **Week 9**: Schema migration and data transfer
- **Week 10**: Application database integration testing

### Phase 4: Containerization (Weeks 11-12)
- **Week 11**: Docker image creation and optimization
- **Week 12**: Container testing and registry setup

### Phase 5: Kubernetes (Weeks 13-15)
- **Week 13**: Kubernetes manifest creation and testing
- **Week 14**: Deployment pipeline setup
- **Week 15**: Production-like environment testing

### Phase 6: Observability (Weeks 16-17)
- **Week 16**: Monitoring and health check implementation
- **Week 17**: Performance testing and optimization

## Success Criteria

**Claude Validation Commands**: Each criterion includes specific commands that Claude can execute to verify completion.

### Functional Requirements

**Functional Validation Tasks** (Use TodoWrite to track):

**FR-1: REST Endpoints Validation**
- 🔧 **Tools**: `Bash`
- ⚡ **Commands**: 
  ```bash
  # Test all endpoints
  curl -X GET http://localhost:9080/CustomerOrderServicesWeb/jaxrs/Category
  curl -X GET http://localhost:9080/CustomerOrderServicesWeb/jaxrs/Product
  curl -X GET http://localhost:9080/CustomerOrderServicesWeb/jaxrs/Customer
  ```
- 🧪 **Success**: All return HTTP 200 with valid JSON

**FR-2: Database Operations Validation**
- 🔧 **Tools**: `Bash`
- ⚡ **Commands**: 
  ```bash
  # Test database connectivity
  kubectl exec -it <pod-name> -- /opt/liberty/wlp/bin/server dump defaultServer
  # Check logs for database connection errors
  kubectl logs <pod-name> | grep -i "database\|connection"
  ```
- 🧪 **Success**: No database connection errors in logs

**FR-3: Authentication Validation**
- 🔧 **Tools**: `Bash`
- ⚡ **Commands**: 
  ```bash
  # Test with valid credentials
  curl -u rbarcia:bl0wfish http://localhost:9080/CustomerOrderServicesWeb/jaxrs/Customer
  # Test with invalid credentials
  curl -u invalid:invalid http://localhost:9080/CustomerOrderServicesWeb/jaxrs/Customer
  ```
- 🧪 **Success**: Valid credentials return 200, invalid return 401

**FR-4: Frontend Application Validation**
- 🔧 **Tools**: `Bash`
- ⚡ **Commands**: 
  ```bash
  # Test frontend access
  curl -I http://localhost:9080/CustomerOrderServicesWeb/
  curl -I http://localhost:9080/CustomerOrderServicesWeb/index.html
  ```
- 🧪 **Success**: Frontend pages load without errors

### Non-Functional Requirements

**Performance Validation Tasks** (Use TodoWrite to track):

**NFR-1: Startup Time Validation**
- 🔧 **Tools**: `Bash`
- ⚡ **Commands**: 
  ```bash
  # Measure startup time
  kubectl delete pod -l app=customer-order-services
  start_time=$(date +%s)
  kubectl wait --for=condition=ready pod -l app=customer-order-services --timeout=60s
  end_time=$(date +%s)
  echo "Startup time: $((end_time - start_time)) seconds"
  ```
- 🧪 **Success**: Startup time < 30 seconds

**NFR-2: Response Time Validation**
- 🔧 **Tools**: `Bash`
- ⚡ **Commands**: 
  ```bash
  # Measure API response times
  curl -w "Response time: %{time_total}s\n" -o /dev/null -s http://localhost:9080/CustomerOrderServicesWeb/jaxrs/Category
  # Run load test
  ab -n 100 -c 5 http://localhost:9080/CustomerOrderServicesWeb/jaxrs/Category
  ```
- 🧪 **Success**: Average response time within 10% of baseline

**NFR-3: Availability Validation**
- 🔧 **Tools**: `Bash`
- ⚡ **Commands**: 
  ```bash
  # Test rolling updates
  kubectl rollout restart deployment/customer-order-services
  kubectl rollout status deployment/customer-order-services
  # Continuous availability test during update
  while true; do curl -f http://localhost:9080/health || echo "FAILED $(date)"; sleep 1; done
  ```
- 🧪 **Success**: No more than 0.1% failed requests during rolling update

**NFR-4: Horizontal Scaling Validation**
- 🔧 **Tools**: `Bash`
- ⚡ **Commands**: 
  ```bash
  # Scale up
  kubectl scale deployment customer-order-services --replicas=3
  kubectl get pods -l app=customer-order-services
  # Test load distribution
  for i in {1..20}; do curl -s http://localhost:9080/health | grep hostname; done
  ```
- 🧪 **Success**: All replicas healthy and serving traffic

### Operational Requirements

**Operational Validation Tasks** (Use TodoWrite to track):

**OR-1: Deployment Pipeline Validation**
- 🔧 **Tools**: `Bash`
- ⚡ **Commands**: 
  ```bash
  # Test CI/CD pipeline
  git commit -m "test deployment" --allow-empty
  git push origin main
  # Monitor deployment
  kubectl get events --sort-by=.metadata.creationTimestamp
  ```
- 🧪 **Success**: Automated deployment completes without manual intervention

**OR-2: Monitoring Validation**
- 🔧 **Tools**: `Bash`
- ⚡ **Commands**: 
  ```bash
  # Test metrics endpoint
  curl http://localhost:9080/metrics
  # Test health endpoints
  curl http://localhost:9080/health/live
  curl http://localhost:9080/health/ready
  ```
- 🧪 **Success**: All monitoring endpoints return valid data

**OR-3: Log Aggregation Validation**
- 🔧 **Tools**: `Bash`
- ⚡ **Commands**: 
  ```bash
  # Test log output
  kubectl logs -l app=customer-order-services --tail=100
  # Test structured logging
  kubectl logs -l app=customer-order-services | grep -E '"level":|"timestamp":|"message":'
  ```
- 🧪 **Success**: Logs are structured and searchable

**OR-4: Backup and Recovery Validation**
- 🔧 **Tools**: `Bash`
- ⚡ **Commands**: 
  ```bash
  # Test database backup
  kubectl exec -it postgresql-0 -- pg_dump orderdb > backup.sql
  # Test application state backup
  kubectl create backup app-backup --include-namespaces=customer-order-services
  ```
- 🧪 **Success**: Backup and restore procedures documented and tested

## Post-Migration Optimization

### Performance Tuning
- JVM heap optimization for container environment
- Connection pool tuning for cloud databases
- OpenLiberty feature optimization

### Security Hardening
- Implementation of OAuth2/JWT authentication
- HTTPS/TLS configuration optimization
- Kubernetes security policies

### Cloud-Native Enhancements
- Implementation of circuit breaker patterns
- Distributed tracing with OpenTelemetry
- Auto-scaling based on metrics

## Conclusion

This migration plan provides a structured approach to modernizing the Customer Order Services application from WebSphere 8.5.5 to OpenLiberty on Kubernetes. The phased approach minimizes risk while ensuring business continuity. Success depends on thorough testing at each phase and maintaining the ability to rollback when necessary.

The resulting cloud-native application will provide improved scalability, maintainability, and operational efficiency while preserving all existing business functionality.

## Claude Code Best Practices for This Migration

### Tool Selection Guidelines
- **Use `Task` for**: Complex searches across many files, open-ended exploration
- **Use `Grep` for**: Finding specific patterns in known file types
- **Use `Read` for**: Understanding individual files before modification
- **Use `MultiEdit` for**: Multiple changes to the same file
- **Use `Edit` for**: Single, targeted changes
- **Use `Bash` for**: Testing, validation, and running build commands

### Migration Phase Checkpoints
Each phase should include these validation checkpoints:

**Phase 1 Checkpoint**:
```bash
# Verify Java 17 compilation
mvn clean compile -f CustomerOrderServicesProject/pom.xml
# Check for jakarta namespace usage
grep -r "import jakarta" --include="*.java" . | wc -l
```

**Phase 2 Checkpoint**:
```bash
# Verify OpenLiberty can parse configuration
/opt/liberty/wlp/bin/server validate defaultServer
# Test basic application startup
mvn liberty:run -f CustomerOrderServicesProject/pom.xml
```

**Phase 3 Checkpoint**:
```bash
# Test database connectivity
mvn test -Dtest=*Database* -f CustomerOrderServicesProject/pom.xml
```

**Phase 4 Checkpoint**:
```bash
# Verify container build and run
docker build -t cos-test . && docker run --rm -p 9080:9080 cos-test
```

**Phase 5 Checkpoint**:
```bash
# Verify Kubernetes deployment
kubectl apply --dry-run=client -f k8s/
```

### Common Migration Issues and Solutions

**Issue: javax → jakarta imports failing**
- **Detection**: `grep -r "import javax" --include="*.java" .`
- **Solution**: Use `find` with `sed` for bulk replacement, then compile to find edge cases

**Issue: WebSphere dependencies not resolved**
- **Detection**: `mvn dependency:analyze | grep "Unused declared dependencies"`
- **Solution**: Remove from POM, use `MultiEdit` for multiple files

**Issue: OpenLiberty features not loading**
- **Detection**: Check `messages.log` for feature resolution errors
- **Solution**: Verify `server.xml` features match application requirements

**Issue: Database connection failures**
- **Detection**: `kubectl logs` showing connection errors
- **Solution**: Verify datasource JNDI names match between `server.xml` and application

### Rollback Procedures
If migration fails at any phase:

1. **Immediate rollback**:
   ```bash
   git checkout main
   git clean -fd
   ```

2. **Partial rollback** (keep some changes):
   ```bash
   git stash
   git checkout main -- <specific-files>
   git stash pop
   ```

3. **Database rollback**:
   ```bash
   # Restore from backup
   pg_restore -d orderdb backup.sql
   ```

### Performance Optimization Notes
- **Monitor startup time**: Each phase should not increase startup time by more than 10%
- **Track memory usage**: Container memory should not exceed WebSphere baseline + 20%
- **API response times**: Should remain within 10% of original performance

### Final Validation Checklist
Before marking migration complete, verify:
- [ ] All original REST endpoints respond correctly
- [ ] Database operations complete successfully  
- [ ] Authentication/authorization works as expected
- [ ] Frontend application loads and functions
- [ ] Health checks return valid status
- [ ] Metrics are being collected
- [ ] Logs are structured and searchable
- [ ] Container starts within time requirements
- [ ] Kubernetes deployment is stable