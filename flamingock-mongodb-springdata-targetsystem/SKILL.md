---
name: flamingock-mongodb-springdata-targetsystem
description: Use this skill to configure, wire, or connect a `MongoDBSpringDataTargetSystem` in a Spring Boot Flamingock project when setting up Mongo-backed migration infrastructure with `MongoTemplate`, Spring Data dependencies, and bean registration.
license: Apache-2.0
metadata:
  author: Flamingock
  version: 1.0.0
---

# Flamingock MongoDB Spring Data TargetSystem

## Redirections
- If the TargetSystem is already configured and you need to create a migration -> `flamingock-mongodb-springdata-change`
- If you need to set up Flamingock from scratch -> `flamingock-onboarding`

## Reference Routing
- If the request is **Spring Boot wiring** -> read `references/springboot.md`
- If the request is **Standalone wiring** -> read `references/standalone.md`

## Philosophy: "Immediate Value"
Never block the user with mandatory intake fields. If Runtime, Language, or Build tool is missing, use sensible defaults (for this skill: Spring Boot, Java, Maven) or placeholders, and clearly state the assumptions made. Provide working code snippets immediately.

## Documentation & Grounding
For the most up-to-date syntax, features, and best practices, always consult the official LLM-optimized documentation:
- **Official Docs**: https://docs.flamingock.io/llms.txt

## Technical Core Principles

### 1. TargetSystem Creation
- **Constructor Path**: Always use the Spring-backed creation:
  - Java: `new MongoDBSpringDataTargetSystem(id, mongoTemplate)`
  - Kotlin: `MongoDBSpringDataTargetSystem(id, mongoTemplate)`
- **Dependencies**:
  - For Gradle plugin-first setups: enable `mongodb()` plus `springboot()` in `flamingock { ... }`; keep `dependencies { ... }` for Spring Data MongoDB only.
  - For Maven/manual setups: include `io.flamingock:flamingock-mongodb-springdata-targetsystem`, `io.flamingock:flamingock-springboot-integration`, and `org.springframework.boot:spring-boot-starter-data-mongodb` explicitly.
  - This variant is Spring Boot only. Do not invent a standalone fallback inside this skill.

### 2. MongoTemplate Handling
- **Auto-detection**: Briefly check if a `MongoTemplate` bean already exists in the project.
- **Orphan Template Rule**: If no explicit `MongoTemplate` bean is present but the project already uses Spring Data MongoDB, assume Boot autoconfiguration will provide it.
- **Custom Bean Rule**: Only generate a custom `MongoTemplate` bean when the project already exposes Spring Data primitives such as `MongoDatabaseFactory` and `MappingMongoConverter`.
- **No Raw Sync Fallback**: Do not create or request raw sync-driver wiring (`MongoClient`, `ClientSession`, or a sync-driver target-system path) in this skill.

### 3. Build Tool Configuration
- **Gradle**: If the Flamingock Gradle plugin is needed, show how to enable the `mongodb()` and `springboot()` modules.
- **Maven**: Provide standard `dependencyManagement` (BOM) and `dependencies` blocks.
- When adding Flamingock dependencies or the Gradle plugin, first try the bundled `scripts/last-version.py` or `scripts/last-version.sh` to resolve the current version.

## Placeholder Guidelines
When values are missing, use:
- **TargetSystem ID**: `"YOUR_TARGET_SYSTEM_ID"`
- **Version**: `[VERSION]` only if `scripts/last-version.py` or `scripts/last-version.sh` could not resolve the current version
- **MongoTemplate bean**: `mongoTemplate`

## Response Structure
When responding, prefer this order:
1. **Assumptions**: Spring Boot runtime, language, build tool, and whether a `MongoTemplate` bean already exists
2. **Dependencies**: Maven or Gradle configuration
3. **Code**: the `MongoDBSpringDataTargetSystem` bean wiring
4. **Verification / Next Step**: how to verify the setup, or whether the user should continue with `flamingock-mongodb-springdata-change`

## Example Output

### Java Spring Boot Configuration
```java
package com.example.config;

import io.flamingock.mongodb.springdata.targetsystem.driver.MongoDBSpringDataTargetSystem;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.core.MongoTemplate;

@Configuration
public class FlamingockMongoConfig {

    @Bean
    MongoDBSpringDataTargetSystem mongoTargetSystem(MongoTemplate mongoTemplate) {
        return new MongoDBSpringDataTargetSystem("YOUR_TARGET_SYSTEM_ID", mongoTemplate);
    }
}
```

### Kotlin Spring Boot Configuration
```kotlin
package com.example.config

import io.flamingock.mongodb.springdata.targetsystem.driver.MongoDBSpringDataTargetSystem
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.data.mongodb.core.MongoTemplate

@Configuration
class FlamingockMongoConfig {

    @Bean
    fun mongoTargetSystem(mongoTemplate: MongoTemplate): MongoDBSpringDataTargetSystem {
        return MongoDBSpringDataTargetSystem("YOUR_TARGET_SYSTEM_ID", mongoTemplate)
    }
}
```
