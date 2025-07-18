# Customer Order Services - WebSphere to OpenLiberty Migration Plan

## Executive Summary

This document outlines the comprehensive migration strategy for modernizing the Customer Order Services application from WebSphere Application Server 8.5.5 to OpenLiberty running on Kubernetes. The migration will transform a traditional JavaEE monolith into a cloud-native application while preserving core business functionality.

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

**Tasks**:
- [ ] Upgrade Java version to 17 across all modules
- [ ] Update Maven compiler plugin to use Java 17
- [ ] Standardize Maven configuration in parent POM
- [ ] Update Maven Surefire/Failsafe plugins for testing

#### 1.2 Dependency Migration
**JavaEE to Jakarta EE**:
```xml
<!-- Replace JavaEE dependencies -->
<dependency>
    <groupId>jakarta.platform</groupId>
    <artifactId>jakarta.jakartaee-api</artifactId>
    <version>10.0.0</version>
    <scope>provided</scope>
</dependency>
```

**WebSphere Dependencies Removal**:
- Remove `com.ibm.websphere.appserver.api.*` dependencies
- Replace IBM JSON providers with standard Jackson
- Remove WebSphere-specific JAX-RS servlet configuration

#### 1.3 Package Namespace Migration
```java
// Update all Java imports
javax.* → jakarta.*

// Example transformations:
javax.ejb.* → jakarta.ejb.*
javax.persistence.* → jakarta.persistence.*
javax.ws.rs.* → jakarta.ws.rs.*
javax.servlet.* → jakarta.servlet.*
```

### Phase 2: Application Server Migration (3-4 weeks)

#### 2.1 OpenLiberty Configuration
**Create `server.xml`**:
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

**JAX-RS Application Class**:
```java
@ApplicationPath("/jaxrs")
public class CustomerServicesApp extends Application {
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

**Remove WebSphere-specific Code**:
- Replace IBM JSON providers with Jackson
- Update JNDI lookups for CDI injection
- Remove WebSphere-specific servlet configuration

#### 2.3 Security Migration
**Update web.xml**:
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

#### 3.1 Database Technology Decision
**Option A: PostgreSQL Migration**
- Migrate schemas and data from DB2 to PostgreSQL
- Update JPA dialect configuration
- Cloud-native database solution

**Option B: Containerized DB2**
- Keep DB2 but containerize it
- Minimal application changes required
- Licensing considerations

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

#### 4.1 Docker Configuration
**Dockerfile**:
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

#### 5.1 Kubernetes Manifests

**Deployment**:
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

#### 6.1 Health Checks Implementation
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
- Update test dependencies to Jakarta EE equivalents
- Migrate MockEJB tests to CDI-based testing
- Implement TestContainers for database integration tests

### Integration Testing
- Deploy to local Kubernetes cluster (minikube/kind)
- Automated testing with Helm charts
- Load testing with Apache JMeter

### Migration Validation
- Functional testing of all REST endpoints
- Database migration validation
- Performance comparison with baseline

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

### Functional Requirements
- [ ] All REST endpoints functioning correctly
- [ ] Database operations working as expected
- [ ] User authentication and authorization working
- [ ] Frontend application fully functional

### Non-Functional Requirements
- [ ] Application startup time < 30 seconds
- [ ] API response time within 10% of baseline
- [ ] 99.9% availability during business hours
- [ ] Horizontal scaling capability demonstrated

### Operational Requirements
- [ ] Automated deployment pipeline working
- [ ] Monitoring and alerting configured
- [ ] Log aggregation and analysis available
- [ ] Backup and disaster recovery procedures tested

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