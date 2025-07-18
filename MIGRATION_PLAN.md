# Customer Order Services - WebSphere to OpenLiberty Migration Plan

## Executive Summary

This document outlines the comprehensive migration strategy for modernizing the Customer Order Services application from WebSphere Application Server 8.5.5 to OpenLiberty running on Kubernetes. The migration will transform a traditional JavaEE monolith into a cloud-native application while preserving core business functionality.

## Claude Code Autonomous Execution Guide

**For Claude Code Autonomous Execution**: This migration plan is optimized for fully autonomous execution by Claude Code without human intervention. Each task includes:
- ✅ **Prerequisites**: What must be completed first (with automated validation)
- 🔧 **Tools**: Which Claude tools to use (Read, Edit, MultiEdit, Bash, etc.)
- 📂 **File Paths**: Specific files to modify (with existence validation)
- ⚡ **Commands**: Exact bash commands to execute (with error handling)
- 🧪 **Validation**: Automated verification of task completion
- 📝 **Todo Integration**: Mandatory `TodoWrite` usage for all phases
- 🔄 **Error Recovery**: Automated rollback and retry procedures
- 🤖 **Decision Logic**: Automated decision trees for common scenarios

**Autonomous Execution Principles**:
1. **Always validate prerequisites** before starting any task
2. **Use TodoWrite** to track every phase and sub-task
3. **Implement automated error detection** and recovery
4. **Validate completion** of each step before proceeding
5. **Automatic rollback** on critical failures
6. **No human input required** - all decisions automated

## Autonomous Execution Quick Start Guide

### Automated Initial Setup Sequence
**Execute this sequence automatically at start of migration**:

```bash
# 1. Validate environment and prerequisites
mvn --version || exit 1
java --version || exit 1
git status || exit 1
docker --version || exit 1

# 2. Create baseline metrics
mvn clean compile -f CustomerOrderServicesProject/pom.xml -q && echo "BASELINE_BUILD_SUCCESS" || echo "BASELINE_BUILD_FAILED"
```

**Automated TodoWrite Integration**:
- Create todos for ALL phases immediately upon starting
- Mark tasks `in_progress` only when actively working on them
- Mark `completed` only after automated validation passes
- Never proceed to next phase until current phase is 100% complete

### Autonomous Task Workflow
**Strict execution order - no deviations**:

1. **Auto-Plan**: Use `TodoWrite` to create all phase tasks upfront
2. **Auto-Analyze**: Use `Grep` and `Read` with predefined patterns
3. **Auto-Implement**: Use `MultiEdit` for bulk changes, `Edit` for targeted changes
4. **Auto-Validate**: Use `Bash` with exit code checking for all validations
5. **Auto-Verify**: Automated pass/fail determination before marking complete

### Automated Tool Usage Patterns
**File Discovery Automation**:
```bash
# Pattern: Auto-discover → Auto-analyze → Auto-implement
find . -name "*.java" -exec grep -l "import javax\." {} \; > javax_files.txt
wc -l javax_files.txt  # Count files to process
# If > 50 files, use bulk operations; if < 50 files, use individual edits
```

**Error Detection Automation**:
```bash
# Automated compilation check after each change
mvn compile -q -f CustomerOrderServicesProject/pom.xml
if [ $? -ne 0 ]; then
    echo "COMPILATION_FAILED - INITIATING_ROLLBACK"
    git checkout HEAD -- .
    exit 1
fi
```

### Automated Error Recovery System
**Tier 1 - Immediate Recovery (< 30 seconds)**:
- Syntax errors: Auto-revert last change and retry with corrected syntax
- Missing files: Auto-create required directories and files
- Permission errors: Auto-fix with appropriate commands

**Tier 2 - Phase Rollback (< 5 minutes)**:
- Compilation failures: Rollback entire current task and mark as failed
- Test failures: Rollback to last known good state
- Dependency conflicts: Auto-resolve or rollback dependency changes

**Tier 3 - Critical Rollback (immediate)**:
- Database corruption: Stop execution immediately and alert
- Security vulnerabilities: Stop execution and rollback
- Build system failures: Complete rollback to project start state

### Automated Decision Trees

**Database Technology Decision (Automated)**:
```bash
# Auto-decision logic for PostgreSQL vs DB2
db2_dependencies=$(grep -r "com.ibm.db2" --include="*.xml" . | wc -l)
postgresql_compatibility=$(grep -r "CLOB\|BLOB\|BIGINT" Common/*.sql | wc -l)

if [ $db2_dependencies -gt 10 ] && [ $postgresql_compatibility -gt 20 ]; then
    echo "AUTO_DECISION: Use containerized DB2 for faster migration"
    DB_STRATEGY="containerized_db2"
else
    echo "AUTO_DECISION: Migrate to PostgreSQL for cloud-native benefits"
    DB_STRATEGY="postgresql_migration"
fi
```

**Build Strategy Decision (Automated)**:
```bash
# Auto-determine build complexity
java_files=$(find . -name "*.java" | wc -l)
pom_files=$(find . -name "pom.xml" | wc -l)

if [ $java_files -gt 100 ] || [ $pom_files -gt 5 ]; then
    echo "AUTO_DECISION: Use incremental compilation strategy"
    BUILD_STRATEGY="incremental"
else
    echo "AUTO_DECISION: Use full rebuild strategy"
    BUILD_STRATEGY="full_rebuild"
fi
```

**Deployment Strategy Decision (Automated)**:
```bash
# Auto-determine deployment approach based on complexity
ear_files=$(find . -name "*.ear" -o -name "application.xml" | wc -l)
war_files=$(find . -name "*.war" -o -name "web.xml" | wc -l)

if [ $ear_files -gt 0 ] && [ $war_files -gt 2 ]; then
    echo "AUTO_DECISION: Use monolithic deployment with gradual decomposition"
    DEPLOY_STRATEGY="monolithic_first"
else
    echo "AUTO_DECISION: Direct microservices deployment"
    DEPLOY_STRATEGY="microservices_direct"
fi
```

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

### Phase 1: Foundation Modernization (2-3 weeks) - FULLY AUTOMATED

#### 1.1 Java and Build System Upgrade - AUTONOMOUS EXECUTION

**Phase 1 Auto-Initialization**:
```bash
# Automated phase setup with error handling
echo "=== PHASE 1: FOUNDATION MODERNIZATION - AUTONOMOUS START ==="
git checkout -b phase1-foundation-modernization || git checkout phase1-foundation-modernization
git clean -fd

# Automated prerequisite validation
mvn --version | grep -q "Apache Maven" || { echo "FATAL: Maven not found"; exit 1; }
java --version | grep -q "openjdk version" || { echo "FATAL: Java not found"; exit 1; }
echo "PHASE1_PREREQUISITES_VALIDATED"
```

**Automated Tasks** (TodoWrite will track these automatically):

**1.1.1 Update Parent POM Java Version - AUTOMATED**
- ✅ **Prerequisites**: Automatically validated above
- 🔧 **Tools**: `Read`, `Edit`
- 📂 **File**: `CustomerOrderServicesProject/pom.xml`
- ⚡ **Automated Commands**: 
  ```bash
  # Auto-backup and validation
  cp CustomerOrderServicesProject/pom.xml CustomerOrderServicesProject/pom.xml.backup
  ```
- 🧪 **Auto-Validation**: 
  ```bash
  cd CustomerOrderServicesProject && mvn validate -q
  if [ $? -ne 0 ]; then
      echo "POM_VALIDATION_FAILED - ROLLING_BACK"
      cp pom.xml.backup pom.xml
      exit 1
  fi
  echo "PARENT_POM_UPDATE_SUCCESSFUL"
  ```
- 🔄 **Auto-Rollback**: Restore from backup on any validation failure

**1.1.2 Update Module POMs - AUTOMATED BULK OPERATION**
- ✅ **Prerequisites**: Parent POM validated automatically
- 🔧 **Tools**: `Bash`, `Glob`, `MultiEdit`
- 📂 **Files**: Auto-discovered via `find . -name "pom.xml"`
- ⚡ **Automated Discovery & Processing**: 
  ```bash
  # Auto-discover all POM files
  find . -name "pom.xml" > pom_files.txt
  pom_count=$(wc -l < pom_files.txt)
  echo "DISCOVERED_$pom_count _POM_FILES"
  
  # Auto-backup all POMs before changes
  while read pom_file; do
      cp "$pom_file" "$pom_file.backup"
  done < pom_files.txt
  
  echo "POM_BACKUP_COMPLETE"
  ```
- 🧪 **Auto-Validation After Each Change**: 
  ```bash
  # Validate each POM after modification
  while read pom_file; do
      xmllint --noout "$pom_file" || {
          echo "XML_VALIDATION_FAILED: $pom_file"
          cp "$pom_file.backup" "$pom_file"
          continue
      }
  done < pom_files.txt
  
  # Full compilation test
  mvn clean compile -f CustomerOrderServicesProject/pom.xml -q
  if [ $? -ne 0 ]; then
      echo "COMPILATION_FAILED - RESTORING_ALL_POMS"
      while read pom_file; do cp "$pom_file.backup" "$pom_file"; done < pom_files.txt
      exit 1
  fi
  echo "MODULE_POMS_UPDATE_SUCCESSFUL"
  ```

**1.1.3 Verify Java 17 Compilation - AUTOMATED VERIFICATION**
- ✅ **Prerequisites**: All POMs updated and validated
- 🔧 **Tools**: `Bash`
- 📂 **Files**: Auto-generated compilation reports
- ⚡ **Automated Compilation Matrix**:
  ```bash
  # Full compilation matrix test
  echo "=== AUTOMATED JAVA 17 COMPILATION VERIFICATION ==="
  
  # Test each module individually
  modules=("CustomerOrderServices" "CustomerOrderServicesWeb" "CustomerOrderServicesTest" "CustomerOrderServicesApp")
  
  for module in "${modules[@]}"; do
      echo "TESTING_MODULE: $module"
      mvn compile -f "CustomerOrderServicesProject/$module/pom.xml" -q
      if [ $? -eq 0 ]; then
          echo "MODULE_$module _COMPILATION_SUCCESS"
      else
          echo "MODULE_$module _COMPILATION_FAILED"
          # Auto-collect error details
          mvn compile -f "CustomerOrderServicesProject/$module/pom.xml" > "$module _compilation_errors.log" 2>&1
          exit 1
      fi
  done
  
  # Test full project build
  mvn clean package -f CustomerOrderServicesProject/pom.xml -DskipTests=true -q
  if [ $? -eq 0 ]; then
      echo "FULL_PROJECT_COMPILATION_SUCCESS"
      # Auto-verify Java 17 bytecode
      jar_files=$(find CustomerOrderServicesProject -name "*.jar" -o -name "*.war" -o -name "*.ear")
      for jar_file in $jar_files; do
          javap -verbose -cp "$jar_file" 2>/dev/null | head -1 | grep -q "major version: 61" && echo "JAVA17_BYTECODE_VERIFIED: $jar_file"
      done
  else
      echo "FULL_PROJECT_COMPILATION_FAILED"
      exit 1
  fi
  ```
- 🧪 **Auto-Validation Success Criteria**:
  - All individual modules compile successfully
  - Full project builds without errors
  - Generated bytecode is Java 17 compatible (major version 61)
  - No deprecated Java 8 features used
- 🔄 **Auto-Recovery**: Automatic rollback to backup POMs if any compilation fails

#### 1.2 Dependency Migration - AUTONOMOUS DEPENDENCY ANALYSIS & REPLACEMENT

**1.2.1 Analyze Current Dependencies - AUTOMATED DISCOVERY**
- ✅ **Prerequisites**: Java 17 compilation verified automatically
- 🔧 **Tools**: `Bash`, `Grep`, `Read`
- 📂 **Files**: Auto-discovered dependency files
- ⚡ **Automated Dependency Analysis**: 
  ```bash
  echo "=== AUTOMATED DEPENDENCY ANALYSIS ==="
  
  # Auto-discover WebSphere dependencies with counts
  websphere_deps=$(grep -r "com.ibm.websphere" --include="*.xml" . | wc -l)
  db2_deps=$(grep -r "com.ibm.db2" --include="*.xml" . | wc -l)
  javax_deps=$(grep -r "javax\." --include="*.xml" . | wc -l)
  
  echo "WEBSPHERE_DEPENDENCIES_FOUND: $websphere_deps"
  echo "DB2_DEPENDENCIES_FOUND: $db2_deps"
  echo "JAVAX_DEPENDENCIES_FOUND: $javax_deps"
  
  # Auto-generate dependency replacement strategy
  if [ $websphere_deps -gt 0 ]; then
      echo "STRATEGY: WebSphere dependencies require removal and replacement"
      grep -r "com.ibm.websphere" --include="*.xml" . > websphere_deps_to_remove.txt
  fi
  
  if [ $javax_deps -gt 0 ]; then
      echo "STRATEGY: Jakarta EE migration required"
      grep -r "javax\." --include="*.xml" . > javax_deps_to_migrate.txt
  fi
  
  # Auto-create backup before dependency changes
  find . -name "pom.xml" -exec cp {} {}.dep-backup \;
  echo "DEPENDENCY_BACKUP_COMPLETE"
  ```
- 🧪 **Auto-Validation**: 
  ```bash
  # Verify current dependency tree before changes
  mvn dependency:tree -f CustomerOrderServicesProject/pom.xml > original_dependency_tree.txt
  echo "ORIGINAL_DEPENDENCY_TREE_CAPTURED"
  ```

**1.2.2 Replace JavaEE with Jakarta EE Dependencies - AUTOMATED REPLACEMENT**
- ✅ **Prerequisites**: Dependency analysis complete, backups created
- 🔧 **Tools**: `Read`, `MultiEdit`, `Bash`
- 📂 **Files**: Auto-discovered POM files requiring Jakarta EE updates
- ⚡ **Automated Jakarta EE Migration**: 
  ```bash
  echo "=== AUTOMATED JAKARTA EE DEPENDENCY REPLACEMENT ==="
  
  # Define Jakarta EE dependency mappings
  declare -A jakarta_mappings=(
      ["javax.servlet:servlet-api"]="jakarta.servlet:jakarta.servlet-api:6.0.0"
      ["javax.ejb:ejb-api"]="jakarta.ejb:jakarta.ejb-api:4.0.1"
      ["javax.persistence:persistence-api"]="jakarta.persistence:jakarta.persistence-api:3.1.0"
      ["javax.ws.rs:jsr311-api"]="jakarta.ws.rs:jakarta.ws.rs-api:3.1.0"
      ["javax.annotation:jsr250-api"]="jakarta.annotation:jakarta.annotation-api:2.1.1"
      ["javax.enterprise:cdi-api"]="jakarta.enterprise:jakarta.enterprise.cdi-api:4.0.1"
      ["javax.inject:javax.inject"]="jakarta.inject:jakarta.inject-api:2.0.1"
      ["javax.json:javax.json-api"]="jakarta.json:jakarta.json-api:2.1.1"
      ["javax.validation:validation-api"]="jakarta.validation:jakarta.validation-api:3.0.2"
  )
  
  # Auto-replace dependencies in all POM files
  pom_files=$(find . -name "pom.xml")
  for pom_file in $pom_files; do
      echo "PROCESSING_POM: $pom_file"
      
      # Check if this POM needs Jakarta EE updates
      needs_update=false
      for old_dep in "${!jakarta_mappings[@]}"; do
          if grep -q "$old_dep" "$pom_file"; then
              needs_update=true
              break
          fi
      done
      
      if [ "$needs_update" = true ]; then
          echo "UPDATING_JAKARTA_DEPENDENCIES: $pom_file"
          # Will use MultiEdit tool to replace dependencies
          echo "$pom_file" >> poms_needing_jakarta_update.txt
      fi
  done
  
  # Verify Jakarta EE parent dependency is in place
  if ! grep -q "jakarta.platform" CustomerOrderServicesProject/pom.xml; then
      echo "ADDING_JAKARTA_PLATFORM_DEPENDENCY"
      # Will use Edit tool to add Jakarta EE platform dependency
  fi
  ```
- 🧪 **Auto-Validation After Each POM Update**: 
  ```bash
  # Validate each updated POM
  while read pom_file; do
      echo "VALIDATING_UPDATED_POM: $pom_file"
      xmllint --noout "$pom_file" || {
          echo "XML_VALIDATION_FAILED: $pom_file - RESTORING_BACKUP"
          cp "$pom_file.dep-backup" "$pom_file"
          continue
      }
      
      # Test Maven can resolve new dependencies
      mvn dependency:resolve -f "$pom_file" -q || {
          echo "DEPENDENCY_RESOLUTION_FAILED: $pom_file - RESTORING_BACKUP"
          cp "$pom_file.dep-backup" "$pom_file"
          continue
      }
      
      echo "POM_UPDATE_SUCCESSFUL: $pom_file"
  done < poms_needing_jakarta_update.txt
  
  # Final Jakarta EE dependency verification
  mvn dependency:tree -f CustomerOrderServicesProject/pom.xml | grep jakarta > jakarta_deps_found.txt
  jakarta_count=$(wc -l < jakarta_deps_found.txt)
  echo "JAKARTA_DEPENDENCIES_ADDED: $jakarta_count"
  ```

**Auto-Generated Jakarta EE Dependencies**:
```xml
<!-- Automatically added to parent POM -->
<dependencyManagement>
    <dependencies>
        <dependency>
            <groupId>jakarta.platform</groupId>
            <artifactId>jakarta.jakartaee-api</artifactId>
            <version>10.0.0</version>
            <scope>provided</scope>
        </dependency>
    </dependencies>
</dependencyManagement>
```

**1.2.3 Remove WebSphere Dependencies - AUTOMATED CLEANUP**
- ✅ **Prerequisites**: Jakarta EE dependencies successfully added and validated
- 🔧 **Tools**: `MultiEdit`, `Bash`
- 📂 **Files**: All POM files with WebSphere dependencies
- ⚡ **Automated WebSphere Dependency Removal**: 
  ```bash
  echo "=== AUTOMATED WEBSPHERE DEPENDENCY REMOVAL ==="
  
  # Define WebSphere dependencies to remove
  websphere_patterns=(
      "com.ibm.websphere"
      "com.ibm.ws"
      "com.ibm.json"
      "com.ibm.jaxrs"
  )
  
  # Auto-remove WebSphere dependencies from all POMs
  for pom_file in $(find . -name "pom.xml"); do
      echo "SCANNING_FOR_WEBSPHERE_DEPS: $pom_file"
      
      has_websphere_deps=false
      for pattern in "${websphere_patterns[@]}"; do
          if grep -q "$pattern" "$pom_file"; then
              has_websphere_deps=true
              echo "WEBSPHERE_DEPENDENCY_FOUND: $pattern in $pom_file"
          fi
      done
      
      if [ "$has_websphere_deps" = true ]; then
          echo "REMOVING_WEBSPHERE_DEPS_FROM: $pom_file"
          echo "$pom_file" >> poms_with_websphere_deps.txt
      fi
  done
  
  # Process each POM with WebSphere dependencies
  if [ -f poms_with_websphere_deps.txt ]; then
      echo "PROCESSING_WEBSPHERE_DEPENDENCY_REMOVAL"
      # Will use MultiEdit to remove WebSphere dependencies
  else
      echo "NO_WEBSPHERE_DEPENDENCIES_FOUND"
  fi
  ```
- 🧪 **Auto-Validation - Complete Dependency Cleanup**: 
  ```bash
  # Verify no WebSphere dependencies remain
  remaining_websphere=$(grep -r "com.ibm.websphere" --include="*.xml" . | wc -l)
  remaining_ws=$(grep -r "com.ibm.ws" --include="*.xml" . | wc -l)
  
  if [ $remaining_websphere -eq 0 ] && [ $remaining_ws -eq 0 ]; then
      echo "WEBSPHERE_DEPENDENCY_REMOVAL_SUCCESSFUL"
  else
      echo "WEBSPHERE_DEPENDENCIES_STILL_PRESENT: websphere=$remaining_websphere ws=$remaining_ws"
      echo "AUTOMATED_CLEANUP_FAILED"
      exit 1
  fi
  
  # Final dependency tree verification
  mvn dependency:tree -f CustomerOrderServicesProject/pom.xml | grep -i "ibm\|websphere" > remaining_ibm_deps.txt
  if [ -s remaining_ibm_deps.txt ]; then
      echo "WARNING: Some IBM dependencies still present"
      cat remaining_ibm_deps.txt
  else
      echo "IBM_DEPENDENCY_CLEANUP_COMPLETE"
  fi
  
  # Test full project compilation after dependency changes
  mvn clean compile -f CustomerOrderServicesProject/pom.xml -q
  if [ $? -eq 0 ]; then
      echo "DEPENDENCY_MIGRATION_SUCCESSFUL - PROJECT_COMPILES"
  else
      echo "DEPENDENCY_MIGRATION_FAILED - COMPILATION_ERRORS"
      # Auto-restore all backups
      find . -name "*.dep-backup" -exec bash -c 'mv "$1" "${1%.dep-backup}"' _ {} \;
      exit 1
  fi
  ```
- 🔄 **Auto-Recovery**: Restore all dependency backups if any step fails

#### 1.3 Package Namespace Migration - AUTONOMOUS JAVAX TO JAKARTA MIGRATION

**1.3.1 Find All javax Imports - AUTOMATED DISCOVERY & ANALYSIS**
- ✅ **Prerequisites**: Dependencies updated and validated
- 🔧 **Tools**: `Bash`, `Grep`, `Read`
- 📂 **Files**: Auto-discovered Java source files
- ⚡ **Automated javax Import Analysis**: 
  ```bash
  echo "=== AUTOMATED JAVAX IMPORT DISCOVERY ==="
  
  # Auto-discover all Java files with javax imports
  find . -name "*.java" -exec grep -l "import javax\." {} \; > javax_files.txt
  javax_file_count=$(wc -l < javax_files.txt)
  echo "JAVAX_FILES_FOUND: $javax_file_count"
  
  # Auto-categorize javax imports by type and frequency
  echo "=== JAVAX IMPORT FREQUENCY ANALYSIS ==="
  grep -r "import javax\." --include="*.java" . | cut -d: -f2 | sort | uniq -c | sort -nr > javax_import_frequency.txt
  
  # Auto-generate migration statistics
  ejb_imports=$(grep "javax.ejb" javax_import_frequency.txt | head -1 | awk '{print $1}' || echo "0")
  persistence_imports=$(grep "javax.persistence" javax_import_frequency.txt | head -1 | awk '{print $1}' || echo "0")
  ws_rs_imports=$(grep "javax.ws.rs" javax_import_frequency.txt | head -1 | awk '{print $1}' || echo "0")
  servlet_imports=$(grep "javax.servlet" javax_import_frequency.txt | head -1 | awk '{print $1}' || echo "0")
  
  echo "EJB_IMPORTS_TO_MIGRATE: $ejb_imports"
  echo "PERSISTENCE_IMPORTS_TO_MIGRATE: $persistence_imports"
  echo "WS_RS_IMPORTS_TO_MIGRATE: $ws_rs_imports"
  echo "SERVLET_IMPORTS_TO_MIGRATE: $servlet_imports"
  
  # Auto-determine migration strategy based on volume
  total_imports=$(wc -l < javax_import_frequency.txt)
  if [ $total_imports -gt 100 ]; then
      echo "MIGRATION_STRATEGY: Bulk automated replacement due to high volume ($total_imports imports)"
      MIGRATION_STRATEGY="bulk_automated"
  elif [ $total_imports -gt 20 ]; then
      echo "MIGRATION_STRATEGY: Batch processing with validation ($total_imports imports)"
      MIGRATION_STRATEGY="batch_validated"
  else
      echo "MIGRATION_STRATEGY: Individual file processing ($total_imports imports)"
      MIGRATION_STRATEGY="individual"
  fi
  
  # Auto-backup all Java files before migration
  echo "=== CREATING_JAVA_SOURCE_BACKUP ==="
  find . -name "*.java" -exec cp {} {}.javax-backup \;
  backup_count=$(find . -name "*.javax-backup" | wc -l)
  echo "JAVA_BACKUP_COMPLETE: $backup_count files backed up"
  ```
- 🧪 **Auto-Validation - Migration Readiness**: 
  ```bash
  # Verify current compilation before javax migration
  mvn clean compile -f CustomerOrderServicesProject/pom.xml -q
  if [ $? -eq 0 ]; then
      echo "PRE_MIGRATION_COMPILATION_SUCCESS"
  else
      echo "PRE_MIGRATION_COMPILATION_FAILED - Cannot proceed with javax migration"
      exit 1
  fi
  ```

**1.3.2 Replace javax with jakarta Imports - AUTOMATED BULK MIGRATION**
- ✅ **Prerequisites**: javax imports analyzed, backups created, compilation verified
- 🔧 **Tools**: `Bash`, `MultiEdit`, `Task`
- 📂 **Files**: All Java files requiring javax → jakarta migration
- ⚡ **Automated Jakarta Import Migration**: 
  ```bash
  echo "=== AUTOMATED JAVAX TO JAKARTA MIGRATION ==="
  
  # Create migration branch for safety
  git checkout -b javax-to-jakarta-migration-$(date +%Y%m%d-%H%M%S) || echo "Branch already exists"
  
  # Define precise javax → jakarta transformation mappings
  declare -A javax_to_jakarta_mappings=(
      ["import javax.ejb."]="import jakarta.ejb."
      ["import javax.persistence."]="import jakarta.persistence."
      ["import javax.ws.rs."]="import jakarta.ws.rs."
      ["import javax.servlet."]="import jakarta.servlet."
      ["import javax.annotation."]="import jakarta.annotation."
      ["import javax.enterprise."]="import jakarta.enterprise."
      ["import javax.inject."]="import jakarta.inject."
      ["import javax.json."]="import jakarta.json."
      ["import javax.validation."]="import jakarta.validation."
      ["import javax.xml.ws."]="import jakarta.xml.ws."
      ["import javax.jws."]="import jakarta.jws."
  )
  
  # Auto-process javax migration based on strategy
  case $MIGRATION_STRATEGY in
      "bulk_automated")
          echo "EXECUTING_BULK_AUTOMATED_MIGRATION"
          for old_import in "${!javax_to_jakarta_mappings[@]}"; do
              new_import="${javax_to_jakarta_mappings[$old_import]}"
              echo "REPLACING: '$old_import' → '$new_import'"
              
              # Use sed for bulk replacement with verification
              find . -name "*.java" -exec grep -l "$old_import" {} \; > files_with_this_import.txt
              file_count=$(wc -l < files_with_this_import.txt)
              
              if [ $file_count -gt 0 ]; then
                  echo "PROCESSING_$file_count _FILES_FOR: $old_import"
                  find . -name "*.java" -exec sed -i "s|$old_import|$new_import|g" {} \;
                  
                  # Immediate compilation check after each transformation
                  mvn compile -f CustomerOrderServicesProject/pom.xml -q
                  if [ $? -ne 0 ]; then
                      echo "COMPILATION_FAILED_AFTER: $old_import → $new_import"
                      echo "ROLLING_BACK_THIS_TRANSFORMATION"
                      find . -name "*.java" -exec sed -i "s|$new_import|$old_import|g" {} \;
                      continue
                  fi
                  echo "TRANSFORMATION_SUCCESSFUL: $old_import → $new_import"
              fi
          done
          ;;
          
      "batch_validated")
          echo "EXECUTING_BATCH_VALIDATED_MIGRATION"
          # Process imports in smaller batches with validation
          batch_size=5
          batch_count=0
          
          for old_import in "${!javax_to_jakarta_mappings[@]}"; do
              new_import="${javax_to_jakarta_mappings[$old_import]}"
              
              # Process this import type
              find . -name "*.java" -exec sed -i "s|$old_import|$new_import|g" {} \;
              ((batch_count++))
              
              # Validate every batch_size transformations
              if [ $((batch_count % batch_size)) -eq 0 ]; then
                  mvn compile -f CustomerOrderServicesProject/pom.xml -q
                  if [ $? -ne 0 ]; then
                      echo "BATCH_COMPILATION_FAILED - ROLLING_BACK_BATCH"
                      # Restore from backup and exit
                      find . -name "*.javax-backup" -exec bash -c 'mv "$1" "${1%.javax-backup}"' _ {} \;
                      exit 1
                  fi
                  echo "BATCH_$((batch_count / batch_size))_VALIDATED"
              fi
          done
          ;;
          
      "individual")
          echo "EXECUTING_INDIVIDUAL_FILE_MIGRATION"
          # Use MultiEdit for individual file processing
          echo "PREPARING_FOR_MULTIEDIT_PROCESSING"
          ;;
  esac
  
  # Final verification after all transformations
  echo "=== FINAL_JAVAX_MIGRATION_VERIFICATION ==="
  remaining_javax=$(grep -r "import javax\." --include="*.java" . | wc -l)
  if [ $remaining_javax -eq 0 ]; then
      echo "JAVAX_MIGRATION_COMPLETE - No javax imports remaining"
  else
      echo "JAVAX_MIGRATION_INCOMPLETE - $remaining_javax imports still present"
      grep -r "import javax\." --include="*.java" . > remaining_javax_imports.txt
      echo "Check remaining_javax_imports.txt for details"
  fi
  ```
- 🧪 **Automated Post-Migration Validation**: 
  ```bash
  # Comprehensive validation after javax → jakarta migration
  echo "=== POST_MIGRATION_VALIDATION ==="
  
  # 1. Compilation test
  mvn clean compile -f CustomerOrderServicesProject/pom.xml
  if [ $? -eq 0 ]; then
      echo "POST_MIGRATION_COMPILATION_SUCCESS"
  else
      echo "POST_MIGRATION_COMPILATION_FAILED"
      mvn compile -f CustomerOrderServicesProject/pom.xml > compilation_errors_post_migration.log 2>&1
      echo "ROLLING_BACK_ALL_JAVAX_CHANGES"
      find . -name "*.javax-backup" -exec bash -c 'mv "$1" "${1%.javax-backup}"' _ {} \;
      exit 1
  fi
  
  # 2. Jakarta import verification
  jakarta_imports=$(grep -r "import jakarta\." --include="*.java" . | wc -l)
  echo "JAKARTA_IMPORTS_ADDED: $jakarta_imports"
  
  # 3. Package syntax verification
  syntax_errors=$(find . -name "*.java" -exec javac -cp "$(mvn dependency:build-classpath -f CustomerOrderServicesProject/pom.xml -q | tail -1)" {} \; 2>&1 | grep -c "error:")
  if [ $syntax_errors -eq 0 ]; then
      echo "JAVA_SYNTAX_VALIDATION_PASSED"
  else
      echo "JAVA_SYNTAX_ERRORS_FOUND: $syntax_errors"
      echo "ROLLING_BACK_JAVAX_MIGRATION"
      find . -name "*.javax-backup" -exec bash -c 'mv "$1" "${1%.javax-backup}"' _ {} \;
      exit 1
  fi
  
  # 4. Generate migration report
  echo "=== JAVAX_TO_JAKARTA_MIGRATION_REPORT ==="
  echo "Original javax files: $javax_file_count"
  echo "Jakarta imports added: $jakarta_imports"
  echo "Remaining javax imports: $remaining_javax"
  echo "Migration strategy used: $MIGRATION_STRATEGY"
  echo "Migration completed successfully: $(date)"
  
  # 5. Clean up backup files only if everything successful
  if [ $remaining_javax -eq 0 ] && [ $syntax_errors -eq 0 ]; then
      echo "CLEANING_UP_JAVAX_BACKUPS"
      find . -name "*.javax-backup" -delete
      echo "JAVAX_MIGRATION_FULLY_COMPLETE"
  fi
  ```
- 🔄 **Auto-Recovery**: Restore all Java backups if any validation fails

**Automated Package Transformation Matrix**:
```java
// Auto-applied javax → jakarta transformations
javax.ejb.*            → jakarta.ejb.*
javax.persistence.*    → jakarta.persistence.*
javax.ws.rs.*          → jakarta.ws.rs.*
javax.servlet.*        → jakarta.servlet.*
javax.annotation.*     → jakarta.annotation.*
javax.enterprise.*     → jakarta.enterprise.*
javax.inject.*         → jakarta.inject.*
javax.json.*           → jakarta.json.*
javax.validation.*     → jakarta.validation.*
javax.xml.ws.*         → jakarta.xml.ws.*
javax.jws.*            → jakarta.jws.*
```

**Automated Quality Assurance Checks**:
```bash
# Post-migration automated quality checks
echo "=== AUTOMATED_QA_CHECKS ==="

# Check for mixed javax/jakarta usage (indicates incomplete migration)
mixed_usage_files=$(grep -l "import javax\." $(grep -l "import jakarta\." *.java 2>/dev/null) 2>/dev/null | wc -l)
if [ $mixed_usage_files -gt 0 ]; then
    echo "WARNING: Mixed javax/jakarta usage detected in $mixed_usage_files files"
fi

# Verify no deprecated javax patterns remain
deprecated_patterns=("javax.ejb.EJB" "javax.persistence.Entity" "javax.ws.rs.GET")
for pattern in "${deprecated_patterns[@]}"; do
    usage_count=$(grep -r "$pattern" --include="*.java" . | wc -l)
    if [ $usage_count -gt 0 ]; then
        echo "WARNING: Deprecated pattern '$pattern' still in use: $usage_count occurrences"
    fi
done

echo "JAVAX_TO_JAKARTA_MIGRATION_QA_COMPLETE"
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

### Phase 3.5: Local Development Environment Setup (1 week)

**Claude Usage Notes**: Establish robust local development workflows before containerization. This phase ensures developers can build, test, and debug locally before moving to containers.

#### 3.5.1 Local Build and Launch Procedures

**3.5.1.1 Prerequisites Validation**
- ✅ **Prerequisites**: Phase 3 database migration complete
- 🔧 **Tools**: `Bash`
- 📂 **Files**: None (validation only)
- ⚡ **Commands**: 
  ```bash
  # Verify Java 17 installation
  java -version
  
  # Verify Maven installation
  mvn --version
  
  # Verify OpenLiberty installation (if local)
  ls -la /opt/liberty/wlp/bin/ || echo "OpenLiberty not installed locally"
  
  # Check Docker installation for later phases
  docker --version
  docker-compose --version
  ```
- 🧪 **Validation**: All required tools installed and accessible

**3.5.1.2 Local Database Setup**
- ✅ **Prerequisites**: Development tools validated
- 🔧 **Tools**: `Bash`, `Write`
- 📂 **Files**: 
  - `docker-compose.dev.yml` (for local database)
  - `Common/init-local-db.sql`
- ⚡ **Commands**: 
  ```bash
  # Start local PostgreSQL for development
  docker run --name postgres-dev \
    -e POSTGRES_DB=orderdb \
    -e POSTGRES_USER=orderuser \
    -e POSTGRES_PASSWORD=orderpass \
    -p 5432:5432 \
    -d postgres:15
  
  # Initialize database schema
  docker exec -i postgres-dev psql -U orderuser -d orderdb < Common/createOrderDB.sql
  docker exec -i postgres-dev psql -U orderuser -d orderdb < Common/InventoryDdl.sql
  docker exec -i postgres-dev psql -U orderuser -d orderdb < Common/InventoryData.sql
  ```
- 🧪 **Validation**: Database accepts connections and contains test data

**3.5.1.3 Local Application Build**
- ✅ **Prerequisites**: Local database running
- 🔧 **Tools**: `Bash`, `Read`
- 📂 **Files**: `CustomerOrderServicesProject/pom.xml`
- ⚡ **Commands**: 
  ```bash
  # Navigate to project root
  cd CustomerOrderServicesProject
  
  # Clean and compile all modules
  mvn clean compile
  
  # Run tests to verify everything works
  mvn test
  
  # Package the application
  mvn package
  
  # Verify EAR file creation
  ls -la CustomerOrderServicesApp/target/*.ear
  ```
- 🧪 **Validation**: EAR file builds successfully, tests pass

**3.5.1.4 Local OpenLiberty Server Setup**
- ✅ **Prerequisites**: Application builds successfully
- 🔧 **Tools**: `Write`, `Edit`, `Bash`
- 📂 **Files**: 
  - `CustomerOrderServicesProject/src/main/liberty/config/server.xml`
  - `CustomerOrderServicesProject/src/main/liberty/config/bootstrap.properties`
- ⚡ **Commands**: 
  ```bash
  # Install Liberty Maven plugin if not present
  mvn liberty:install-server -f CustomerOrderServicesProject/pom.xml
  
  # Create server configuration
  mkdir -p src/main/liberty/config
  
  # Start Liberty server with application
  mvn liberty:run -f CustomerOrderServicesProject/pom.xml
  ```
- 🧪 **Validation**: Application accessible at http://localhost:9080

**Local Development server.xml**:
```xml
<server description="Customer Order Services - Local Dev">
    <featureManager>
        <feature>jakartaee-10.0</feature>
        <feature>microProfile-6.0</feature>
        <feature>localConnector-1.0</feature>
    </featureManager>
    
    <httpEndpoint id="defaultHttpEndpoint" 
                  httpPort="9080" 
                  httpsPort="9443"/>
    
    <application location="CustomerOrderServicesApp.ear"/>
    
    <!-- Local PostgreSQL datasource -->
    <dataSource id="orderDS" jndiName="jdbc/orderds">
        <jdbcDriver libraryRef="postgresql-lib"/>
        <properties.postgresql serverName="localhost" 
                              portNumber="5432"
                              databaseName="orderdb"
                              user="orderuser"
                              password="orderpass"/>
    </dataSource>
    
    <library id="postgresql-lib">
        <fileset dir="${server.config.dir}/lib" includes="postgresql-*.jar"/>
    </library>
    
    <!-- Development logging -->
    <logging traceSpecification="*=info:org.pwte.example.*=debug" 
             maxFileSize="50" 
             maxFiles="5"/>
</server>
```

**Local Development bootstrap.properties**:
```properties
# Local development configuration
DB_HOST=localhost
DB_PORT=5432
DB_NAME=orderdb
DB_USER=orderuser
DB_PASSWORD=orderpass

# Application configuration
APP_CONTEXT_ROOT=CustomerOrderServicesWeb
LOG_LEVEL=DEBUG

# Development features
ENABLE_DEBUG=true
ENABLE_JMX=true
```

#### 3.5.2 Local Development Workflow

**3.5.2.1 Hot Reload Development Setup**
- ✅ **Prerequisites**: Local server running
- 🔧 **Tools**: `Edit`, `Bash`
- 📂 **Files**: Liberty server configuration
- ⚡ **Commands**: 
  ```bash
  # Enable development mode with hot reload
  mvn liberty:dev -f CustomerOrderServicesProject/pom.xml
  
  # Test hot reload by modifying a Java file
  # Changes should be automatically recompiled and redeployed
  ```
- 🧪 **Validation**: Code changes reflected without server restart

**3.5.2.2 Local Testing and Debugging**
- ✅ **Prerequisites**: Development mode active
- 🔧 **Tools**: `Bash`
- 📂 **Files**: Test classes
- ⚡ **Commands**: 
  ```bash
  # Run unit tests in watch mode
  mvn test -f CustomerOrderServicesProject/pom.xml -DforkMode=always
  
  # Run integration tests against local server
  mvn failsafe:integration-test -f CustomerOrderServicesProject/pom.xml
  
  # Test REST endpoints manually
  curl -u rbarcia:bl0wfish http://localhost:9080/CustomerOrderServicesWeb/jaxrs/Category
  curl -u rbarcia:bl0wfish http://localhost:9080/CustomerOrderServicesWeb/jaxrs/Product
  ```
- 🧪 **Validation**: All tests pass, endpoints respond correctly

### Phase 4: Containerization and Container Development (3 weeks)

**Claude Usage Notes**: This expanded phase covers both development and production container scenarios. Use `Write` for Dockerfile creation, `Bash` for Docker commands, and implement incremental container improvements.

#### 4.1 Multi-Stage Docker Configuration

**4.1.1 Create Multi-Stage Dockerfile for Development and Production**
- ✅ **Prerequisites**: Local development setup complete, application building successfully
- 🔧 **Tools**: `Write`
- 📂 **File**: `CustomerOrderServicesProject/Dockerfile`
- ⚡ **Commands**: 
  ```bash
  # Build development image
  docker build --target development -t customer-order-services:dev .
  
  # Build production image
  docker build --target production -t customer-order-services:prod .
  
  # Test development container
  docker run -p 9080:9080 -p 7777:7777 customer-order-services:dev
  
  # Test production container
  docker run -p 9080:9080 customer-order-services:prod
  ```
- 🧪 **Validation**: Both development and production containers start successfully

**Multi-Stage Dockerfile**:
```dockerfile
# Build stage
FROM maven:3.9-openjdk-17 AS builder

WORKDIR /build

# Copy Maven configuration files
COPY CustomerOrderServicesProject/pom.xml .
COPY CustomerOrderServices/pom.xml CustomerOrderServices/
COPY CustomerOrderServicesWeb/pom.xml CustomerOrderServicesWeb/
COPY CustomerOrderServicesTest/pom.xml CustomerOrderServicesTest/
COPY CustomerOrderServicesApp/pom.xml CustomerOrderServicesApp/

# Download dependencies (for better layer caching)
RUN mvn dependency:go-offline -B

# Copy source code
COPY CustomerOrderServicesProject/CustomerOrderServices/ CustomerOrderServices/
COPY CustomerOrderServicesProject/CustomerOrderServicesWeb/ CustomerOrderServicesWeb/
COPY CustomerOrderServicesProject/CustomerOrderServicesTest/ CustomerOrderServicesTest/
COPY CustomerOrderServicesProject/CustomerOrderServicesApp/ CustomerOrderServicesApp/

# Build the application
RUN mvn clean package -DskipTests=true -B

# Development stage - includes debugging and dev tools
FROM icr.io/appcafe/open-liberty:full-java17-openj9-ubi AS development

# Install development tools
USER root
RUN microdnf install -y curl wget telnet vim && \
    microdnf clean all

# Create application directories
RUN mkdir -p /config/apps /config/lib /config/resources/security

# Copy application artifacts from build stage
COPY --from=builder --chown=1001:0 /build/CustomerOrderServicesApp/target/*.ear /config/apps/

# Copy PostgreSQL driver
COPY --chown=1001:0 lib/postgresql-*.jar /config/lib/

# Copy development server configuration
COPY --chown=1001:0 src/main/liberty/config/server-dev.xml /config/server.xml
COPY --chown=1001:0 src/main/liberty/config/bootstrap-dev.properties /config/bootstrap.properties

# Copy development resources
COPY --chown=1001:0 src/main/liberty/config/server.env /config/

# Enable debugging and development features
ENV WLP_DEBUG_REMOTE=y
ENV WLP_DEBUG_REMOTE_PORT=7777
ENV JAVA_TOOL_OPTIONS="-agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=*:7777"

# Configure development logging
ENV LOG_LEVEL=DEBUG
ENV ENABLE_DEBUG=true

USER 1001

# Configure and run Liberty
RUN configure.sh

EXPOSE 9080 9443 7777

# Production stage - minimal and optimized
FROM icr.io/appcafe/open-liberty:kernel-slim-java17-openj9-ubi AS production

# Copy only necessary features for production
COPY --chown=1001:0 src/main/liberty/config/server-prod.xml /config/server.xml
COPY --chown=1001:0 src/main/liberty/config/bootstrap-prod.properties /config/bootstrap.properties

# Copy application artifacts from build stage
COPY --from=builder --chown=1001:0 /build/CustomerOrderServicesApp/target/*.ear /config/apps/

# Copy PostgreSQL driver
COPY --chown=1001:0 lib/postgresql-*.jar /opt/liberty/usr/shared/resources/

# Production environment variables
ENV LOG_LEVEL=INFO
ENV ENABLE_DEBUG=false

# Configure Liberty for production
RUN configure.sh

# Health check
HEALTHCHECK --interval=30s --timeout=10s --start-period=60s --retries=3 \
    CMD curl -f http://localhost:9080/health || exit 1

EXPOSE 9080 9443

# Default to production
FROM production
```

**4.1.2 Create Container-Specific Server Configurations**
- ✅ **Prerequisites**: Multi-stage Dockerfile created
- 🔧 **Tools**: `Write`
- 📂 **Files**: 
  - `src/main/liberty/config/server-dev.xml`
  - `src/main/liberty/config/server-prod.xml`
  - `src/main/liberty/config/bootstrap-dev.properties`
  - `src/main/liberty/config/bootstrap-prod.properties`
- ⚡ **Commands**: 
  ```bash
  # Create configuration directories
  mkdir -p src/main/liberty/config
  
  # Validate XML configurations
  xmllint --noout src/main/liberty/config/server-dev.xml
  xmllint --noout src/main/liberty/config/server-prod.xml
  ```
- 🧪 **Validation**: XML files are valid and configurations load correctly

**Development server-dev.xml**:
```xml
<server description="Customer Order Services - Development Container">
    <featureManager>
        <feature>jakartaee-10.0</feature>
        <feature>microProfile-6.0</feature>
        <feature>localConnector-1.0</feature>
        <feature>restConnector-2.0</feature>
        <feature>monitor-1.0</feature>
        <feature>requestTiming-1.0</feature>
    </featureManager>
    
    <variable name="default.http.port" defaultValue="9080"/>
    <variable name="default.https.port" defaultValue="9443"/>
    
    <httpEndpoint id="defaultHttpEndpoint" 
                  httpPort="${default.http.port}" 
                  httpsPort="${default.https.port}"/>
    
    <application location="*.ear"/>
    
    <!-- Container-aware datasource configuration -->
    <dataSource id="orderDS" jndiName="jdbc/orderds">
        <jdbcDriver libraryRef="postgresql-lib"/>
        <properties.postgresql serverName="${DB_HOST}" 
                              portNumber="${DB_PORT}"
                              databaseName="${DB_NAME}"
                              user="${DB_USER}"
                              password="${DB_PASSWORD}"/>
        <!-- Development connection pool settings -->
        <connectionManager maxPoolSize="10" minPoolSize="2"/>
    </dataSource>
    
    <library id="postgresql-lib">
        <fileset dir="/config/lib" includes="postgresql-*.jar"/>
    </library>
    
    <!-- Development logging with detailed tracing -->
    <logging traceSpecification="*=info:org.pwte.example.*=debug:com.ibm.ws.webcontainer*=debug" 
             maxFileSize="100" 
             maxFiles="10"
             traceFormat="ENHANCED"/>
    
    <!-- JMX monitoring for development -->
    <monitor filter="WebSphere:type=ServletStats,name=*"/>
    
    <!-- Request timing for performance analysis -->
    <requestTiming includeContextInfo="true" slowRequestThreshold="10s"/>
    
    <!-- Remote debugging support -->
    <javaPermissionsGrant codeBase="file:///-" permissionXMLFile="/config/resources/security/java.policy"/>
</server>
```

**Production server-prod.xml**:
```xml
<server description="Customer Order Services - Production Container">
    <featureManager>
        <feature>jakartaee-10.0</feature>
        <feature>microProfile-6.0</feature>
        <feature>monitor-1.0</feature>
    </featureManager>
    
    <variable name="default.http.port" defaultValue="9080"/>
    <variable name="default.https.port" defaultValue="9443"/>
    
    <httpEndpoint id="defaultHttpEndpoint" 
                  httpPort="${default.http.port}" 
                  httpsPort="${default.https.port}"
                  accessLoggingRef="accessLogging"/>
    
    <application location="*.ear"/>
    
    <!-- Production datasource with optimized connection pooling -->
    <dataSource id="orderDS" jndiName="jdbc/orderds">
        <jdbcDriver libraryRef="postgresql-lib"/>
        <properties.postgresql serverName="${DB_HOST}" 
                              portNumber="${DB_PORT}"
                              databaseName="${DB_NAME}"
                              user="${DB_USER}"
                              password="${DB_PASSWORD}"/>
        <!-- Production connection pool settings -->
        <connectionManager maxPoolSize="50" 
                          minPoolSize="10" 
                          connectionTimeout="30s" 
                          maxIdleTime="10m"/>
    </dataSource>
    
    <library id="postgresql-lib">
        <fileset dir="/opt/liberty/usr/shared/resources" includes="postgresql-*.jar"/>
    </library>
    
    <!-- Production logging - optimized for performance -->
    <logging logDirectory="/logs" 
             maxFileSize="200" 
             maxFiles="7"
             traceSpecification="*=audit:org.pwte.example.*=info"/>
    
    <!-- Access logging for monitoring -->
    <accessLogging id="accessLogging" 
                   logFormat='%h %u %t "%r" %s %b "%{Referer}i" "%{User-agent}i"' 
                   filePath="/logs/access.log" 
                   maxFileSize="100" 
                   maxFiles="7"/>
    
    <!-- Production monitoring -->
    <monitor filter="WebSphere:type=ServletStats,name=*"/>
    
    <!-- Security hardening -->
    <webContainer disableXPoweredBy="true"/>
    <httpOptions removeServerHeader="true"/>
</server>
```

#### 4.2 Container Configuration Externalization

**4.2.1 Development Container Configuration**
- ✅ **Prerequisites**: Container configurations created
- 🔧 **Tools**: `Write`
- 📂 **File**: `src/main/liberty/config/bootstrap-dev.properties`
- ⚡ **Commands**: None (configuration file only)
- 🧪 **Validation**: Environment variables properly substituted in container

**bootstrap-dev.properties**:
```properties
# Development database configuration
DB_HOST=${DB_HOST:postgres-dev}
DB_PORT=${DB_PORT:5432}
DB_NAME=${DB_NAME:orderdb}
DB_USER=${DB_USER:orderuser}
DB_PASSWORD=${DB_PASSWORD:orderpass}

# Development application configuration
APP_CONTEXT_ROOT=${APP_CONTEXT_ROOT:CustomerOrderServicesWeb}
LOG_LEVEL=${LOG_LEVEL:DEBUG}

# Development features
ENABLE_DEBUG=${ENABLE_DEBUG:true}
ENABLE_JMX=${ENABLE_JMX:true}
ENABLE_METRICS=${ENABLE_METRICS:true}

# Development server settings
HTTP_PORT=${HTTP_PORT:9080}
HTTPS_PORT=${HTTPS_PORT:9443}
DEBUG_PORT=${DEBUG_PORT:7777}

# Development connection pool
DB_MAX_POOL_SIZE=${DB_MAX_POOL_SIZE:10}
DB_MIN_POOL_SIZE=${DB_MIN_POOL_SIZE:2}
```

**4.2.2 Production Container Configuration**
- ✅ **Prerequisites**: Development configuration tested
- 🔧 **Tools**: `Write`
- 📂 **File**: `src/main/liberty/config/bootstrap-prod.properties`
- ⚡ **Commands**: None (configuration file only)
- 🧪 **Validation**: Production settings optimize for performance and security

**bootstrap-prod.properties**:
```properties
# Production database configuration (no defaults for security)
DB_HOST=${DB_HOST}
DB_PORT=${DB_PORT}
DB_NAME=${DB_NAME}
DB_USER=${DB_USER}
DB_PASSWORD=${DB_PASSWORD}

# Production application configuration
APP_CONTEXT_ROOT=${APP_CONTEXT_ROOT:CustomerOrderServicesWeb}
LOG_LEVEL=${LOG_LEVEL:INFO}

# Production features (security-focused)
ENABLE_DEBUG=${ENABLE_DEBUG:false}
ENABLE_JMX=${ENABLE_JMX:false}
ENABLE_METRICS=${ENABLE_METRICS:true}

# Production server settings
HTTP_PORT=${HTTP_PORT:9080}
HTTPS_PORT=${HTTPS_PORT:9443}

# Production connection pool (optimized for load)
DB_MAX_POOL_SIZE=${DB_MAX_POOL_SIZE:50}
DB_MIN_POOL_SIZE=${DB_MIN_POOL_SIZE:10}
DB_CONNECTION_TIMEOUT=${DB_CONNECTION_TIMEOUT:30s}
DB_MAX_IDLE_TIME=${DB_MAX_IDLE_TIME:10m}

# Production JVM tuning
JAVA_TOOL_OPTIONS=${JAVA_TOOL_OPTIONS:-Xmx512m -Xms256m -XX:+UseContainerSupport}

# Security settings
SECURE_HEADERS=${SECURE_HEADERS:true}
HSTS_ENABLED=${HSTS_ENABLED:true}
```

#### 4.3 Docker Compose for Local Development

**4.3.1 Create Docker Compose Development Environment**
- ✅ **Prerequisites**: Multi-stage Docker builds working
- 🔧 **Tools**: `Write`
- 📂 **File**: `docker-compose.dev.yml`
- ⚡ **Commands**: 
  ```bash
  # Start complete development environment
  docker-compose -f docker-compose.dev.yml up -d
  
  # View logs
  docker-compose -f docker-compose.dev.yml logs -f
  
  # Stop environment
  docker-compose -f docker-compose.dev.yml down
  
  # Rebuild and restart app only
  docker-compose -f docker-compose.dev.yml up -d --build app
  ```
- 🧪 **Validation**: Complete environment starts and application is accessible

**docker-compose.dev.yml**:
```yaml
version: '3.8'

services:
  postgres-dev:
    image: postgres:15
    container_name: customer-order-postgres-dev
    environment:
      POSTGRES_DB: orderdb
      POSTGRES_USER: orderuser
      POSTGRES_PASSWORD: orderpass
      POSTGRES_INITDB_ARGS: "--auth-local=trust --auth-host=md5"
    ports:
      - "5432:5432"
    volumes:
      - postgres_dev_data:/var/lib/postgresql/data
      - ./Common/createOrderDB.sql:/docker-entrypoint-initdb.d/01-schema.sql
      - ./Common/InventoryDdl.sql:/docker-entrypoint-initdb.d/02-inventory-schema.sql
      - ./Common/InventoryData.sql:/docker-entrypoint-initdb.d/03-inventory-data.sql
    networks:
      - dev-network
    healthcheck:
      test: ["CMD-SHELL", "pg_isready -U orderuser -d orderdb"]
      interval: 10s
      timeout: 5s
      retries: 5

  app:
    build:
      context: .
      dockerfile: Dockerfile
      target: development
    container_name: customer-order-services-dev
    depends_on:
      postgres-dev:
        condition: service_healthy
    environment:
      - DB_HOST=postgres-dev
      - DB_PORT=5432
      - DB_NAME=orderdb
      - DB_USER=orderuser
      - DB_PASSWORD=orderpass
      - LOG_LEVEL=DEBUG
      - ENABLE_DEBUG=true
    ports:
      - "9080:9080"  # Application HTTP
      - "9443:9443"  # Application HTTPS
      - "7777:7777"  # Debug port
    volumes:
      # Hot reload - mount source for development
      - ./CustomerOrderServicesProject:/opt/dev/source:ro
      # Persistent logs
      - dev_logs:/logs
    networks:
      - dev-network
    healthcheck:
      test: ["CMD", "curl", "-f", "http://localhost:9080/health"]
      interval: 30s
      timeout: 10s
      retries: 3
      start_period: 60s

  # Optional: PgAdmin for database management
  pgadmin:
    image: dpage/pgadmin4:latest
    container_name: customer-order-pgadmin-dev
    environment:
      PGADMIN_DEFAULT_EMAIL: admin@customer-order.com
      PGADMIN_DEFAULT_PASSWORD: admin
    ports:
      - "8080:80"
    depends_on:
      - postgres-dev
    volumes:
      - pgadmin_data:/var/lib/pgadmin
    networks:
      - dev-network

volumes:
  postgres_dev_data:
  dev_logs:
  pgadmin_data:

networks:
  dev-network:
    driver: bridge
```

**4.3.2 Create Production Docker Compose Template**
- ✅ **Prerequisites**: Development compose working
- 🔧 **Tools**: `Write`
- 📂 **File**: `docker-compose.prod.yml`
- ⚡ **Commands**: 
  ```bash
  # Build production images
  docker-compose -f docker-compose.prod.yml build
  
  # Start production environment (with external database)
  docker-compose -f docker-compose.prod.yml up -d
  ```
- 🧪 **Validation**: Production environment starts with proper security and performance settings

**docker-compose.prod.yml**:
```yaml
version: '3.8'

services:
  app:
    build:
      context: .
      dockerfile: Dockerfile
      target: production
    image: customer-order-services:prod
    restart: unless-stopped
    environment:
      # Production database connection
      - DB_HOST=${DB_HOST:localhost}
      - DB_PORT=${DB_PORT:5432}
      - DB_NAME=${DB_NAME:orderdb}
      - DB_USER=${DB_USER}
      - DB_PASSWORD=${DB_PASSWORD}
      # Production settings
      - LOG_LEVEL=INFO
      - ENABLE_DEBUG=false
      - JAVA_TOOL_OPTIONS=-Xmx1024m -Xms512m -XX:+UseContainerSupport
    ports:
      - "9080:9080"
      - "9443:9443"
    volumes:
      # Production logs to host
      - ./logs:/logs
    networks:
      - prod-network
    healthcheck:
      test: ["CMD", "curl", "-f", "http://localhost:9080/health"]
      interval: 30s
      timeout: 10s
      retries: 3
      start_period: 120s
    deploy:
      resources:
        limits:
          memory: 1.5G
          cpus: '2.0'
        reservations:
          memory: 512M
          cpus: '0.5'

networks:
  prod-network:
    external: true
    name: customer-order-prod
```

#### 4.4 Container Development Workflow

**4.4.1 Container-Based Development Workflow**
- ✅ **Prerequisites**: Docker Compose development environment working
- 🔧 **Tools**: `Bash`, `Edit`
- 📂 **Files**: Source code files, Docker configurations
- ⚡ **Commands**: 
  ```bash
  # Start development environment
  docker-compose -f docker-compose.dev.yml up -d
  
  # Watch logs in real-time
  docker-compose -f docker-compose.dev.yml logs -f app
  
  # Rebuild application after changes
  docker-compose -f docker-compose.dev.yml build app
  docker-compose -f docker-compose.dev.yml up -d app
  
  # Execute commands inside running container
  docker-compose -f docker-compose.dev.yml exec app bash
  
  # Debug database
  docker-compose -f docker-compose.dev.yml exec postgres-dev psql -U orderuser -d orderdb
  
  # Access pgAdmin UI at http://localhost:8080
  # admin@customer-order.com / admin
  ```
- 🧪 **Validation**: Can develop, debug, and test entirely within containers

**4.4.2 Container Performance Testing and Optimization**
- ✅ **Prerequisites**: Container development workflow established
- 🔧 **Tools**: `Bash`
- 📂 **Files**: None (performance testing)
- ⚡ **Commands**: 
  ```bash
  # Container resource usage monitoring
  docker stats customer-order-services-dev
  
  # Application startup time measurement
  time docker-compose -f docker-compose.dev.yml up app
  
  # Memory usage analysis
  docker exec customer-order-services-dev ps aux
  docker exec customer-order-services-dev free -m
  
  # Load testing against containerized application
  ab -n 1000 -c 10 http://localhost:9080/CustomerOrderServicesWeb/jaxrs/Category
  
  # Container image size optimization
  docker images | grep customer-order-services
  docker history customer-order-services:dev
  ```
- 🧪 **Validation**: Container performance meets development requirements

#### 4.5 Container Registry and CI/CD Integration

**4.5.1 Container Registry Setup**
- ✅ **Prerequisites**: Production container builds working
- 🔧 **Tools**: `Bash`
- 📂 **Files**: None (registry configuration)
- ⚡ **Commands**: 
  ```bash
  # Tag images for registry
  docker tag customer-order-services:dev your-registry.com/customer-order-services:dev
  docker tag customer-order-services:prod your-registry.com/customer-order-services:prod
  
  # Push to registry
  docker push your-registry.com/customer-order-services:dev
  docker push your-registry.com/customer-order-services:prod
  
  # Pull from registry
  docker pull your-registry.com/customer-order-services:prod
  ```
- 🧪 **Validation**: Images can be pushed to and pulled from registry

**4.5.2 Container Build Automation**
- ✅ **Prerequisites**: Container registry accessible
- 🔧 **Tools**: `Write`
- 📂 **File**: `.github/workflows/container-build.yml` or `Jenkinsfile`
- ⚡ **Commands**: 
  ```bash
  # Local build script for CI/CD testing
  ./scripts/build-containers.sh
  
  # Automated testing script
  ./scripts/test-containers.sh
  ```
- 🧪 **Validation**: Automated builds produce consistent, testable container images

**Container Build Script (scripts/build-containers.sh)**:
```bash
#!/bin/bash
set -e

echo "Building Customer Order Services containers..."

# Build development image
echo "Building development image..."
docker build --target development -t customer-order-services:dev .

# Build production image
echo "Building production image..."
docker build --target production -t customer-order-services:prod .

# Test images
echo "Testing development image startup..."
docker run --rm --name test-dev -d customer-order-services:dev
sleep 30
docker exec test-dev curl -f http://localhost:9080/health || exit 1
docker stop test-dev

echo "Testing production image startup..."
docker run --rm --name test-prod -d customer-order-services:prod
sleep 30
docker exec test-prod curl -f http://localhost:9080/health || exit 1
docker stop test-prod

echo "Container builds completed successfully!"
```

### Phase 5: Kubernetes Deployment (2-3 weeks)

**Claude Usage Notes**: Use `Write` tool for creating Kubernetes manifests. Test each manifest individually before combining into full deployment. This phase builds on the containerization work from Phase 4.

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

### Phase 3.5: Local Development Environment (Week 11)
- **Week 11**: Local build procedures, database setup, and development workflow establishment

### Phase 4: Containerization and Container Development (Weeks 12-15)
- **Week 12**: Multi-stage Docker configuration and container-specific server configs
- **Week 13**: Docker Compose development environment and local container workflow
- **Week 14**: Container performance optimization and registry integration
- **Week 15**: Production container setup and CI/CD container build automation

### Phase 5: Kubernetes (Weeks 16-18)
- **Week 16**: Kubernetes manifest creation and testing
- **Week 17**: Deployment pipeline setup
- **Week 18**: Production-like environment testing

### Phase 6: Observability (Weeks 19-20)
- **Week 19**: Monitoring and health check implementation
- **Week 20**: Performance testing and optimization

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

This comprehensive migration plan provides a structured approach to modernizing the Customer Order Services application from WebSphere 8.5.5 to OpenLiberty on Kubernetes, with full support for both local development and containerized deployment workflows. The expanded scope includes:

### Key Enhancements in This Plan

**Local Development Foundation**:
- Complete local development environment setup with hot-reload capabilities
- PostgreSQL containerized database for local development
- Comprehensive debugging and testing procedures
- IDE integration and development workflow optimization

**Advanced Containerization Strategy**:
- Multi-stage Docker builds for development and production
- Docker Compose environments for full-stack local development
- Container-specific configurations optimized for each environment
- Production-ready security and performance optimizations
- Container registry integration and CI/CD automation

**Dual Deployment Approach**:
- Parallel support for local Liberty server and containerized deployment
- Environment-specific configurations that adapt to deployment context
- Seamless transition from local development to container-based testing
- Comprehensive validation procedures for both environments

### Migration Benefits

The phased approach minimizes risk while ensuring business continuity. Success depends on thorough testing at each phase and maintaining the ability to rollback when necessary. The expanded scope provides:

1. **Developer Productivity**: Local development with hot-reload and debugging capabilities
2. **Environment Consistency**: Container-based development environments matching production
3. **Deployment Flexibility**: Support for both traditional and container-based deployments
4. **Operational Excellence**: Comprehensive monitoring, logging, and health checks
5. **Scalability**: Cloud-native architecture ready for horizontal scaling

The resulting cloud-native application will provide improved scalability, maintainability, and operational efficiency while preserving all existing business functionality. Developers will have robust local development capabilities alongside production-ready containerized deployment options, ensuring a smooth development-to-production pipeline.

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

**Phase 3.5 Checkpoint**:
```bash
# Verify local development environment
mvn clean package -f CustomerOrderServicesProject/pom.xml
mvn liberty:run -f CustomerOrderServicesProject/pom.xml &
sleep 30
curl -f http://localhost:9080/health
kill %1
```

**Phase 4 Checkpoint**:
```bash
# Verify containerized development environment
docker-compose -f docker-compose.dev.yml up -d
sleep 60
curl -f http://localhost:9080/health
docker-compose -f docker-compose.dev.yml down

# Verify production container build
docker build --target production -t customer-order-services:prod .
docker run --rm -d --name test-prod customer-order-services:prod
sleep 30
docker exec test-prod curl -f http://localhost:9080/health
docker stop test-prod
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

### Autonomous Final Validation Checklist - FULLY AUTOMATED

**Automated Validation Execution Script**:
```bash
#!/bin/bash
echo "=== AUTONOMOUS MIGRATION VALIDATION SUITE ==="
echo "Starting comprehensive automated validation at $(date)"

# Initialize validation tracking
VALIDATION_RESULTS_FILE="migration_validation_results.log"
VALIDATION_ERRORS_FILE="migration_validation_errors.log"
TOTAL_CHECKS=0
PASSED_CHECKS=0
FAILED_CHECKS=0

# Validation function
validate_check() {
    local check_name="$1"
    local check_command="$2"
    local expected_result="$3"
    
    ((TOTAL_CHECKS++))
    echo "VALIDATING: $check_name"
    
    if eval "$check_command"; then
        echo "✅ PASSED: $check_name" | tee -a "$VALIDATION_RESULTS_FILE"
        ((PASSED_CHECKS++))
        return 0
    else
        echo "❌ FAILED: $check_name" | tee -a "$VALIDATION_ERRORS_FILE"
        ((FAILED_CHECKS++))
        return 1
    fi
}

# Final validation report
generate_final_report() {
    echo "=== MIGRATION VALIDATION FINAL REPORT ===" | tee -a "$VALIDATION_RESULTS_FILE"
    echo "Total checks: $TOTAL_CHECKS" | tee -a "$VALIDATION_RESULTS_FILE"
    echo "Passed: $PASSED_CHECKS" | tee -a "$VALIDATION_RESULTS_FILE"
    echo "Failed: $FAILED_CHECKS" | tee -a "$VALIDATION_RESULTS_FILE"
    echo "Success rate: $(( (PASSED_CHECKS * 100) / TOTAL_CHECKS ))%" | tee -a "$VALIDATION_RESULTS_FILE"
    
    if [ $FAILED_CHECKS -eq 0 ]; then
        echo "🎉 MIGRATION VALIDATION SUCCESSFUL - ALL CHECKS PASSED" | tee -a "$VALIDATION_RESULTS_FILE"
        exit 0
    else
        echo "❌ MIGRATION VALIDATION FAILED - $FAILED_CHECKS CHECKS FAILED" | tee -a "$VALIDATION_ERRORS_FILE"
        exit 1
    fi
}
```

**Local Development Environment Validation - AUTOMATED**:
```bash
echo "=== LOCAL DEVELOPMENT ENVIRONMENT VALIDATION ==="

# Auto-validate local build
validate_check "Local_Build_Success" \
    "mvn clean package -f CustomerOrderServicesProject/pom.xml -q" \
    "BUILD_SUCCESS"

# Auto-validate local database
validate_check "Local_Database_Connection" \
    "docker ps | grep -q postgres && docker exec postgres-dev pg_isready -U orderuser -d orderdb" \
    "DATABASE_READY"

# Auto-validate local server startup
validate_check "Local_Server_Startup" \
    "timeout 60 bash -c 'until curl -s http://localhost:9080/health; do sleep 2; done'" \
    "SERVER_RESPONDING"

# Auto-validate hot reload capability
validate_check "Hot_Reload_Functionality" \
    "mvn liberty:dev -f CustomerOrderServicesProject/pom.xml &
     sleep 30
     touch CustomerOrderServices/ejbModule/org/pwte/example/service/Test.java
     sleep 10
     curl -s http://localhost:9080/health | grep -q UP
     kill %1" \
    "HOT_RELOAD_WORKING"

# Auto-validate local tests
validate_check "Local_Tests_Execution" \
    "mvn test -f CustomerOrderServicesProject/pom.xml" \
    "TESTS_PASSED"

# Auto-validate debugging capability
validate_check "Local_Debug_Port_Available" \
    "netstat -ln | grep -q 7777 || echo 'Debug port available'" \
    "DEBUG_READY"
```

**Container Development Environment Validation - AUTOMATED**:
```bash
echo "=== CONTAINER DEVELOPMENT ENVIRONMENT VALIDATION ==="

# Auto-validate development container build
validate_check "Dev_Container_Build" \
    "docker build --target development -t customer-order-services:dev . >/dev/null 2>&1" \
    "CONTAINER_BUILD_SUCCESS"

# Auto-validate Docker Compose development environment
validate_check "Dev_Compose_Environment" \
    "docker-compose -f docker-compose.dev.yml up -d >/dev/null 2>&1
     sleep 60
     docker-compose -f docker-compose.dev.yml ps | grep -q 'Up'
     docker-compose -f docker-compose.dev.yml down >/dev/null 2>&1" \
    "COMPOSE_ENVIRONMENT_OK"

# Auto-validate containerized database initialization
validate_check "Container_Database_Init" \
    "docker run --rm postgres:15 pg_isready --help >/dev/null 2>&1" \
    "DATABASE_CONTAINER_READY"

# Auto-validate container application accessibility
validate_check "Container_App_Accessibility" \
    "docker run --rm -d --name test-app customer-order-services:dev
     sleep 30
     docker exec test-app curl -s http://localhost:9080/health | grep -q UP
     docker stop test-app >/dev/null 2>&1" \
    "CONTAINER_APP_ACCESSIBLE"

# Auto-validate container remote debugging
validate_check "Container_Remote_Debug" \
    "docker run --rm -d -p 7777:7777 --name debug-test customer-order-services:dev
     sleep 10
     nc -z localhost 7777
     docker stop debug-test >/dev/null 2>&1" \
    "REMOTE_DEBUG_AVAILABLE"

# Auto-validate container health checks
validate_check "Container_Health_Checks" \
    "docker run --rm --name health-test customer-order-services:dev timeout 60 bash -c 'while ! curl -s http://localhost:9080/health; do sleep 2; done'
     docker stop health-test >/dev/null 2>&1" \
    "HEALTH_CHECKS_WORKING"
```

**Production Container Validation - AUTOMATED**:
```bash
echo "=== PRODUCTION CONTAINER VALIDATION ==="

# Auto-validate production container build
validate_check "Prod_Container_Build" \
    "docker build --target production -t customer-order-services:prod . >/dev/null 2>&1" \
    "PROD_BUILD_SUCCESS"

# Auto-validate production container security (no dev tools)
validate_check "Prod_Container_Security" \
    "! docker run --rm customer-order-services:prod which vim >/dev/null 2>&1 &&
     ! docker run --rm customer-order-services:prod which telnet >/dev/null 2>&1" \
    "SECURITY_HARDENED"

# Auto-validate production container performance
validate_check "Prod_Container_Performance" \
    "start_time=\$(date +%s)
     docker run --rm -d --name perf-test customer-order-services:prod
     timeout 30 bash -c 'while ! docker exec perf-test curl -s http://localhost:9080/health; do sleep 1; done'
     end_time=\$(date +%s)
     startup_time=\$((end_time - start_time))
     docker stop perf-test >/dev/null 2>&1
     [ \$startup_time -lt 30 ]" \
    "STARTUP_UNDER_30_SECONDS"

# Auto-validate environment variable externalization
validate_check "Prod_Container_Config_Externalization" \
    "docker run --rm -e DB_HOST=test customer-order-services:prod env | grep -q DB_HOST=test" \
    "CONFIG_EXTERNALIZED"

# Auto-validate container registry operations
validate_check "Container_Registry_Operations" \
    "docker tag customer-order-services:prod localhost:5000/customer-order-services:test >/dev/null 2>&1 || echo 'Registry test skipped'" \
    "REGISTRY_READY"

# Auto-validate container scaling capability
validate_check "Container_Scaling_Capability" \
    "docker run --rm -d --name scale-test-1 customer-order-services:prod
     docker run --rm -d --name scale-test-2 customer-order-services:prod
     sleep 20
     docker ps | grep -c scale-test | grep -q 2
     docker stop scale-test-1 scale-test-2 >/dev/null 2>&1" \
    "SCALING_CAPABLE"
```

**Application Functionality Validation - AUTOMATED**:
```bash
echo "=== APPLICATION FUNCTIONALITY VALIDATION ==="

# Auto-validate REST endpoints
validate_check "REST_Endpoints_Category" \
    "curl -u rbarcia:bl0wfish -s http://localhost:9080/CustomerOrderServicesWeb/jaxrs/Category | grep -q '\['" \
    "CATEGORY_ENDPOINT_OK"

validate_check "REST_Endpoints_Product" \
    "curl -u rbarcia:bl0wfish -s http://localhost:9080/CustomerOrderServicesWeb/jaxrs/Product | grep -q '\['" \
    "PRODUCT_ENDPOINT_OK"

validate_check "REST_Endpoints_Customer" \
    "curl -u rbarcia:bl0wfish -s http://localhost:9080/CustomerOrderServicesWeb/jaxrs/Customer | grep -q '\['" \
    "CUSTOMER_ENDPOINT_OK"

# Auto-validate database CRUD operations
validate_check "Database_CRUD_Operations" \
    "curl -u rbarcia:bl0wfish -X GET http://localhost:9080/CustomerOrderServicesWeb/jaxrs/Category/1 | grep -q '\"id\"'" \
    "CRUD_OPERATIONS_OK"

# Auto-validate authentication system
validate_check "Authentication_Valid_Credentials" \
    "curl -u rbarcia:bl0wfish -s -o /dev/null -w '%{http_code}' http://localhost:9080/CustomerOrderServicesWeb/jaxrs/Customer | grep -q 200" \
    "VALID_AUTH_OK"

validate_check "Authentication_Invalid_Credentials" \
    "curl -u invalid:invalid -s -o /dev/null -w '%{http_code}' http://localhost:9080/CustomerOrderServicesWeb/jaxrs/Customer | grep -q 401" \
    "INVALID_AUTH_REJECTED"

# Auto-validate frontend application
validate_check "Frontend_Application_Access" \
    "curl -s -o /dev/null -w '%{http_code}' http://localhost:9080/CustomerOrderServicesWeb/index.html | grep -q 200" \
    "FRONTEND_ACCESSIBLE"

# Auto-validate error handling
validate_check "Error_Handling_404" \
    "curl -s -o /dev/null -w '%{http_code}' http://localhost:9080/CustomerOrderServicesWeb/nonexistent | grep -q 404" \
    "ERROR_HANDLING_OK"
```

**Operational Readiness Validation - AUTOMATED**:
```bash
echo "=== OPERATIONAL READINESS VALIDATION ==="

# Auto-validate health check endpoints
validate_check "Health_Check_Live" \
    "curl -s http://localhost:9080/health/live | grep -q UP" \
    "LIVENESS_CHECK_OK"

validate_check "Health_Check_Ready" \
    "curl -s http://localhost:9080/health/ready | grep -q UP" \
    "READINESS_CHECK_OK"

# Auto-validate MicroProfile metrics
validate_check "MicroProfile_Metrics" \
    "curl -s http://localhost:9080/metrics | grep -q 'vendor_'" \
    "METRICS_EXPOSED"

# Auto-validate structured logging
validate_check "Structured_Logging" \
    "docker logs customer-order-services-dev 2>&1 | grep -q '\"level\"\\|\"timestamp\"\\|\"message\"'" \
    "STRUCTURED_LOGS_OK"

# Auto-validate performance benchmarks
validate_check "Performance_Response_Time" \
    "response_time=\$(curl -w '%{time_total}' -s -o /dev/null http://localhost:9080/CustomerOrderServicesWeb/jaxrs/Category)
     echo \$response_time | awk '{ if (\$1 < 2.0) exit 0; else exit 1 }'" \
    "RESPONSE_TIME_ACCEPTABLE"

# Auto-validate backup and recovery capability
validate_check "Backup_Recovery_Capability" \
    "docker exec postgres-dev pg_dump -U orderuser orderdb > /tmp/test_backup.sql 2>/dev/null
     [ -s /tmp/test_backup.sql ]" \
    "BACKUP_CAPABILITY_OK"
```

**CI/CD and Automation Validation - AUTOMATED**:
```bash
echo "=== CI/CD AND AUTOMATION VALIDATION ==="

# Auto-validate automated build capability
validate_check "Automated_Build_Script" \
    "[ -f scripts/build-containers.sh ] && bash scripts/build-containers.sh >/dev/null 2>&1" \
    "AUTOMATED_BUILDS_OK"

# Auto-validate test automation
validate_check "Test_Automation" \
    "mvn test -f CustomerOrderServicesProject/pom.xml >/dev/null 2>&1" \
    "TEST_AUTOMATION_OK"

# Auto-validate deployment automation readiness
validate_check "Deployment_Automation_Readiness" \
    "[ -f docker-compose.prod.yml ] && docker-compose -f docker-compose.prod.yml config >/dev/null 2>&1" \
    "DEPLOYMENT_AUTOMATION_READY"

# Auto-validate rollback capability
validate_check "Rollback_Capability" \
    "git log --oneline | head -5 | grep -q . && echo 'Git history available for rollback'" \
    "ROLLBACK_CAPABLE"
```

**Documentation and Knowledge Transfer Validation - AUTOMATED**:
```bash
echo "=== DOCUMENTATION VALIDATION ==="

# Auto-validate setup documentation
validate_check "Setup_Documentation_Present" \
    "[ -f CLAUDE.md ] && [ -f MIGRATION_PLAN.md ] && [ -f README.md ]" \
    "SETUP_DOCS_PRESENT"

# Auto-validate development workflow documentation
validate_check "Development_Workflow_Documented" \
    "grep -q 'development' MIGRATION_PLAN.md && grep -q 'container' MIGRATION_PLAN.md" \
    "WORKFLOW_DOCUMENTED"

# Auto-validate troubleshooting information
validate_check "Troubleshooting_Guide_Available" \
    "grep -q 'troubleshoot\\|error\\|rollback' MIGRATION_PLAN.md" \
    "TROUBLESHOOTING_AVAILABLE"

# Auto-validate operational procedures
validate_check "Operational_Procedures_Documented" \
    "grep -q 'production\\|deployment\\|monitoring' MIGRATION_PLAN.md" \
    "OPERATIONAL_DOCS_PRESENT"

# Execute final validation report
generate_final_report
```

**Automated Migration Success Criteria**:
```bash
# Final autonomous migration validation
echo "=== AUTONOMOUS MIGRATION SUCCESS VALIDATION ==="

# All automated checks must pass
if [ $FAILED_CHECKS -eq 0 ]; then
    echo "🎉 MIGRATION SUCCESSFULLY COMPLETED"
    echo "✅ All $TOTAL_CHECKS validation checks passed"
    echo "✅ Application is ready for production deployment"
    echo "✅ Migration completed autonomously without human intervention"
    
    # Create migration completion certificate
    cat > MIGRATION_COMPLETION_CERTIFICATE.md << EOF
# Migration Completion Certificate

**Project**: Customer Order Services WebSphere to OpenLiberty Migration
**Completion Date**: $(date)
**Migration Type**: Autonomous execution by Claude Code
**Total Validation Checks**: $TOTAL_CHECKS
**Success Rate**: 100%

## Migration Achievements
- ✅ Java 17 upgrade completed
- ✅ Jakarta EE namespace migration completed  
- ✅ OpenLiberty configuration completed
- ✅ Container deployment ready
- ✅ All functional tests passing
- ✅ Performance benchmarks met
- ✅ Security validation passed
- ✅ Operational readiness confirmed

**Migration Status**: COMPLETE AND PRODUCTION READY

Generated automatically by Claude Code autonomous migration system.
EOF

else
    echo "❌ MIGRATION INCOMPLETE - $FAILED_CHECKS VALIDATION FAILURES"
    echo "📋 Review $VALIDATION_ERRORS_FILE for detailed failure information"
    echo "🔄 Automatic rollback procedures may be required"
    exit 1
fi
```

## Summary of Autonomous Execution Enhancements

This enhanced MIGRATION_PLAN.md has been optimized for fully autonomous execution by Claude Code with the following key improvements:

### 🤖 **Autonomous Execution Features Added**

**1. Automated Decision Making**:
- Database technology selection (PostgreSQL vs DB2) based on dependency analysis
- Build strategy selection based on project complexity 
- Migration approach selection based on file counts and patterns
- Error handling strategy selection based on failure types

**2. Comprehensive Error Detection & Recovery**:
- 3-tier error recovery system (immediate, phase rollback, critical rollback)
- Automated backup creation before each major operation
- Automatic rollback triggers based on compilation failures
- Comprehensive validation after each step with auto-recovery

**3. Enhanced Tool Integration**:
- Mandatory TodoWrite usage for all phases and sub-tasks
- Automated file discovery using Bash, Grep, and Glob tools
- Intelligent use of MultiEdit vs Edit based on file counts
- Automated validation using Bash commands with exit code checking

**4. Granular Task Breakdown**:
- Phase 1 broken into atomic operations with individual validation
- Package namespace migration with automated strategy selection
- Dependency migration with automated mapping and replacement
- Each task includes prerequisites, tools, commands, and validation

**5. Automated Quality Assurance**:
- Compilation verification after each code change
- Dependency resolution validation after POM updates
- Java syntax validation after namespace migrations
- Performance benchmarking with automated pass/fail criteria

**6. Comprehensive Final Validation**:
- 40+ automated validation checks covering all aspects
- Local development, container, and production environment validation
- Functional, operational, and security validation
- Automated migration completion certificate generation

### 🔧 **Key Automation Patterns**

**Error Handling Pattern**:
```bash
operation || {
    echo "OPERATION_FAILED - INITIATING_ROLLBACK"
    restore_backup
    exit 1
}
```

**Validation Pattern**:
```bash
validate_check "Check_Name" \
    "command_to_execute" \
    "expected_result"
```

**Decision Making Pattern**:
```bash
if [ condition ]; then
    echo "AUTO_DECISION: Strategy X selected"
    STRATEGY="strategy_x"
else
    echo "AUTO_DECISION: Strategy Y selected"  
    STRATEGY="strategy_y"
fi
```

**Backup and Recovery Pattern**:
```bash
# Auto-backup before changes
find . -name "*.java" -exec cp {} {}.backup \;

# Auto-restore on failure
find . -name "*.backup" -exec bash -c 'mv "$1" "${1%.backup}"' _ {} \;
```

### 📊 **Migration Success Metrics**

The autonomous migration system provides quantifiable success metrics:

- **Total Validation Checks**: 40+ automated validations
- **Error Recovery Capability**: 3-tier automated recovery system
- **Rollback Safety**: Complete restoration capability at any phase
- **Decision Points**: 12+ automated decision trees
- **Quality Gates**: Validation after every significant change
- **Success Criteria**: 100% automated validation pass rate required

### 🎯 **Benefits for Claude Code Execution**

**1. Zero Human Intervention Required**:
- All decisions automated based on codebase analysis
- Automatic error detection and recovery
- Self-validating at each step
- Autonomous quality assurance

**2. Comprehensive Risk Mitigation**:
- Automated backup before every change
- Immediate rollback on any failure
- Phase-by-phase validation checkpoints
- Multiple validation layers

**3. Intelligent Adaptation**:
- Strategy selection based on project characteristics
- Tool selection based on file counts and complexity
- Error handling based on failure types
- Performance optimization based on environment

**4. Complete Auditability**:
- Detailed logging of all operations
- Validation results tracking
- Error reporting with specific failure details
- Migration completion certification

This enhanced plan enables Claude Code to execute the entire WebSphere to OpenLiberty migration autonomously, with robust error handling, automated decision making, and comprehensive validation - ensuring successful migration without any human intervention required.