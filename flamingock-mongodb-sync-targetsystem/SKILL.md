---
name: flamingock-mongodb-sync-targetsystem
description: Use this skill to configure, wire, or connect a `MongoDBSyncTargetSystem` in an existing Flamingock project when setting up Mongo-backed migration infrastructure, including sync-driver dependency guidance and bean/builder registration.
license: Apache-2.0
metadata:
  author: Flamingock
  version: 1.0.0
---

# Flamingock MongoDB Sync TargetSystem

## Redirections
- If the TargetSystem is already configured and you need to create a migration -> `flamingock-mongodb-sync-change`
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
  - Java: `new MongoDBSyncTargetSystem(id, mongoClient, databaseName)`
  - Kotlin: `MongoDBSyncTargetSystem(id, mongoClient, databaseName)`
- **Dependencies**: 
  - For Gradle plugin-first setups: enable `mongodb()` and, if needed, `springboot()` in `flamingock { ... }`; do not add Flamingock artifacts again in the same `dependencies { ... }` block.
  - For Maven/manual setups: include `io.flamingock:flamingock-mongodb-sync-targetsystem` and, for Spring Boot, `io.flamingock:flamingock-springboot-integration` explicitly.
  - Only add `org.mongodb:mongodb-driver-sync` if the skill is also creating the `MongoClient`.

### 2. MongoClient Handling
- **Auto-detection**: Briefly check if a `MongoClient` bean or variable already exists in the project.
- **Orphan Client Rule**: If no `MongoClient` bean or variable exists, generate it immediately instead of asking the user to create it first.
- **Spring Boot**: Create a `@Bean` returning `MongoClients.create("TODO")` when the project has no existing `MongoClient` bean.
- **Standalone**: Create a local `MongoClient mongoClient = MongoClients.create("TODO");` variable and wire it into the Flamingock builder/TargetSystem constructor.
- **Driver Dependency Rule**: Only add `org.mongodb:mongodb-driver-sync` when this skill is generating the missing client.

### 3. Build Tool Configuration
- **Gradle**: If the Flamingock Gradle plugin is needed, show how to enable the `mongodb()` module (and `springboot()` if applicable).
- **Maven**: Provide standard `dependencyManagement` (BOM) and `dependencies` blocks.
- When adding Flamingock dependencies or the Gradle plugin, first try the bundled `scripts/last-version.py` or `scripts/last-version.sh` to resolve the current version.

## Placeholder Guidelines
When values are missing, use:
- **TargetSystem ID**: `"YOUR_TARGET_SYSTEM_ID"`
- **Database Name**: `"TODO"`
- **Version**: `[VERSION]` only if `scripts/last-version.py` or `scripts/last-version.sh` could not resolve the current version
- **MongoClient**: `mongoClient` (variable name)

## Response Structure
When responding, prefer this order:
1. **Assumptions**: runtime, language, build tool, and whether a `MongoClient` already exists
2. **Dependencies**: Maven or Gradle configuration
3. **Code**: the `MongoClient` and `MongoDBSyncTargetSystem` wiring
4. **Verification / Next Step**: how to verify the setup, or whether the user should continue with `flamingock-mongodb-sync-change`

## Example Output

### Java Spring Boot Configuration
```java
package com.example.config;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import io.flamingock.mongodb.sync.targetsystem.driver.MongoDBSyncTargetSystem;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class FlamingockMongoConfig {

    @Bean
    MongoClient mongoClient() {
        return MongoClients.create("TODO");
    }

    @Bean
    MongoDBSyncTargetSystem mongoTargetSystem(MongoClient mongoClient) {
        return new MongoDBSyncTargetSystem("YOUR_TARGET_SYSTEM_ID", mongoClient, "TODO");
    }
}
```

### Kotlin Spring Boot Configuration
```kotlin
package com.example.config

import com.mongodb.client.MongoClient
import com.mongodb.client.MongoClients
import io.flamingock.mongodb.sync.targetsystem.driver.MongoDBSyncTargetSystem
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class FlamingockMongoConfig {

    @Bean
    fun mongoClient(): MongoClient = MongoClients.create("TODO")

    @Bean
    fun mongoTargetSystem(mongoClient: MongoClient): MongoDBSyncTargetSystem {
        return MongoDBSyncTargetSystem("YOUR_TARGET_SYSTEM_ID", mongoClient, "TODO")
    }
}
```
