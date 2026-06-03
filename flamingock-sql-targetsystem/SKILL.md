---
name: flamingock-sql-targetsystem
description: Use this skill to configure, wire, or connect a `SqlTargetSystem` in an existing Flamingock project when setting up relational-database migration infrastructure, including JDBC driver guidance and bean/builder registration.
license: Apache-2.0
metadata:
  author: Flamingock
  version: 1.0.0
---

# Flamingock SQL TargetSystem

## Redirections
- If the TargetSystem is already configured and you need to create a migration -> `flamingock-sql-change`
- If you need to set up Flamingock from scratch -> `flamingock-onboarding`

## Reference Routing
- If the request is **Spring Boot wiring** -> read `references/springboot.md`
- If the request is **Standalone wiring** -> read `references/standalone.md`

## Philosophy: "Immediate Value"
Never block the user with mandatory intake fields. If Runtime, Language, or Build tool is missing, use sensible defaults (e.g., Spring Boot, Java, Maven) or placeholders, and clearly state the assumptions made. Provide working code snippets immediately.

## Documentation & Grounding
For the most up-to-date syntax, features, and best practices, always consult the official LLM-optimized documentation:
- **Official Docs**: https://docs.flamingock.io/llms.txt

## Technical Core Principles

### 1. TargetSystem Creation
- **Constructor Path**: Always use the source-backed creation:
  - Java: `new SqlTargetSystem(id, dataSource)`
  - Kotlin: `SqlTargetSystem(id, dataSource)`
- **Dependencies**:
  - For Gradle plugin-first setups: enable `sql()` and, if needed, `springboot()` in `flamingock { ... }`; keep `dependencies { ... }` for the JDBC driver only.
  - For Maven/manual setups: include `io.flamingock:flamingock-sql-targetsystem` and, for Spring Boot, `io.flamingock:flamingock-springboot-integration` explicitly.
  - Add the JDBC driver that matches the target database when the project does not already provide it.

### 2. DataSource Handling
- **Auto-detection**: Briefly check if a `DataSource` bean or variable already exists in the project.
- **Orphan DataSource Rule**: If no `DataSource` bean or variable exists, generate it immediately instead of asking the user to create it first.
- **Spring Boot**: Create a `@Bean` returning a configured `DataSource` only when the project has no existing one.
- **Standalone**: Create a local `DataSource dataSource = ...;` variable and wire it into the Flamingock builder/TargetSystem constructor.
- **Driver Dependency Rule**: Ensure the driver dependency matches the SQL dialect the user mentions.

### 3. Build Tool Configuration
- **Gradle**: If the Flamingock Gradle plugin is needed, show how to enable the `sql()` module (and `springboot()` if applicable).
- **Maven**: Provide standard `dependencyManagement` (BOM) and `dependencies` blocks.
- When adding Flamingock dependencies or the Gradle plugin, first try the bundled `scripts/last-version.py` or `scripts/last-version.sh` to resolve the current version.

## Placeholder Guidelines
When values are missing, use:
- **TargetSystem ID**: `"YOUR_TARGET_SYSTEM_ID"`
- **JDBC URL**: `"TODO"`
- **Username / Password**: `"TODO"`
- **Version**: `[VERSION]` only if `scripts/last-version.py` or `scripts/last-version.sh` could not resolve the current version
- **DataSource**: `dataSource` (variable name)

## Response Structure
When responding, prefer this order:
1. **Assumptions**: runtime, language, build tool, dialect, and whether a `DataSource` already exists
2. **Dependencies**: Maven or Gradle configuration
3. **Code**: the `DataSource` and `SqlTargetSystem` wiring
4. **Verification / Next Step**: how to verify the setup, or whether the user should continue with `flamingock-sql-change`

## Example Output

### Java Spring Boot Configuration
```java
package com.example.config;

import io.flamingock.targetsystem.sql.SqlTargetSystem;
import javax.sql.DataSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class FlamingockSqlConfig {

    @Bean
    SqlTargetSystem sqlTargetSystem(DataSource dataSource) {
        return new SqlTargetSystem("YOUR_TARGET_SYSTEM_ID", dataSource);
    }
}
```

### Kotlin Spring Boot Configuration
```kotlin
package com.example.config

import io.flamingock.targetsystem.sql.SqlTargetSystem
import javax.sql.DataSource
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class FlamingockSqlConfig {

    @Bean
    fun sqlTargetSystem(dataSource: DataSource): SqlTargetSystem {
        return SqlTargetSystem("YOUR_TARGET_SYSTEM_ID", dataSource)
    }
}
```
