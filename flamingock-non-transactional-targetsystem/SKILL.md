---
name: flamingock-non-transactional-targetsystem
description: Use this skill to configure, wire, or connect a `NonTransactionalTargetSystem` in an existing Flamingock project when setting up migration infrastructure for systems without native transactions, including dependency/property registration and bean/builder registration.
license: Apache-2.0
metadata:
  author: Flamingock
  version: 1.0.0
---

# Flamingock Non-Transactional TargetSystem

## Redirections
- If the TargetSystem is already configured and you need to create a migration -> `flamingock-non-transactional-change`
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
  - Java: `new NonTransactionalTargetSystem(id)`
  - Kotlin: `NonTransactionalTargetSystem(id)`
- **Dependencies**:
  - No extra Flamingock module is required beyond `flamingock-community`; this target system is part of the base Community setup.
  - Only add the external SDK dependencies that match the system the user wants to manage (Kafka, S3, Redis, REST client, etc.).

### 2. Dependency and Property Registration
- **Auto-detection**: Briefly check which objects and properties already exist in the project and can be registered.
- **Registration Rule**: Register injectable objects with `.addDependency(...)` and configuration values with `.setProperty(...)`.
- **No Native Transaction Claims**: Do not imply transactional support or AuditStore capability for this target system.
- **AuditStore Rule**: Warn clearly that `NonTransactionalTargetSystem` cannot back the Community AuditStore.

### 3. Build Tool Configuration
- **Gradle**: If the Flamingock Gradle plugin is needed, show the Community module setup and only the external SDKs relevant to the user’s system.
- **Maven**: Provide standard `dependencyManagement` (BOM) and dependency blocks for core Flamingock plus the external SDKs.
- When adding Flamingock dependencies or the Gradle plugin, first try the bundled `scripts/last-version.py` or `scripts/last-version.sh` to resolve the current version.

## Placeholder Guidelines
When values are missing, use:
- **TargetSystem ID**: `"YOUR_TARGET_SYSTEM_ID"`
- **Named property values**: `"TODO"`
- **Version**: `[VERSION]` only if `scripts/last-version.py` or `scripts/last-version.sh` could not resolve the current version

## Response Structure
When responding, prefer this order:
1. **Assumptions**: runtime, language, build tool, external system, and which dependencies/properties are available
2. **Dependencies**: Flamingock core plus external SDK guidance
3. **Code**: the `NonTransactionalTargetSystem` wiring with `.addDependency()` / `.setProperty()`
4. **Verification / Next Step**: how to verify the setup, or whether the user should continue with `flamingock-non-transactional-change`

## Example Output

### Java Spring Boot Configuration
```java
package com.example.config;

import io.flamingock.targetsystem.nontransactional.NonTransactionalTargetSystem;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.services.s3.S3Client;

@Configuration
public class FlamingockS3Config {

    @Bean
    NonTransactionalTargetSystem s3TargetSystem(S3Client s3Client) {
        return new NonTransactionalTargetSystem("YOUR_TARGET_SYSTEM_ID")
            .addDependency(s3Client)
            .setProperty("bucket.name", "TODO");
    }
}
```

### Kotlin Spring Boot Configuration
```kotlin
package com.example.config

import io.flamingock.targetsystem.nontransactional.NonTransactionalTargetSystem
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import software.amazon.awssdk.services.s3.S3Client

@Configuration
class FlamingockS3Config {

    @Bean
    fun s3TargetSystem(s3Client: S3Client): NonTransactionalTargetSystem {
        return NonTransactionalTargetSystem("YOUR_TARGET_SYSTEM_ID")
            .addDependency(s3Client)
            .setProperty("bucket.name", "TODO")
    }
}
```
