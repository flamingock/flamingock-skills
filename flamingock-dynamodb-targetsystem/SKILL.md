---
name: flamingock-dynamodb-targetsystem
description: Use this skill to configure, wire, or connect a `DynamoDBTargetSystem` in an existing Flamingock project when setting up DynamoDB-backed migration infrastructure, including AWS SDK guidance and bean/builder registration.
license: Apache-2.0
metadata:
  author: Flamingock
  version: 1.0.0
---

# Flamingock DynamoDB TargetSystem

## Redirections
- If the TargetSystem is already configured and you need to create a migration -> `flamingock-dynamodb-change`
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
  - Java: `new DynamoDBTargetSystem(id, dynamoDbClient)`
  - Kotlin: `DynamoDBTargetSystem(id, dynamoDbClient)`
- **Dependencies**:
  - For Gradle plugin-first setups: enable `dynamodb()` and, if needed, `springboot()` in `flamingock { ... }`; keep `dependencies { ... }` for AWS SDK modules only.
  - For Maven/manual setups: include `io.flamingock:flamingock-dynamodb-targetsystem` and, for Spring Boot, `io.flamingock:flamingock-springboot-integration` explicitly.
  - Add `software.amazon.awssdk:dynamodb-enhanced` when the project does not already provide the AWS DynamoDB SDK.

### 2. DynamoDbClient Handling
- **Auto-detection**: Briefly check if a `DynamoDbClient` bean or variable already exists in the project.
- **Orphan Client Rule**: If no `DynamoDbClient` bean or variable exists, generate it immediately instead of asking the user to create it first.
- **Spring Boot**: Create a `@Bean` returning `DynamoDbClient.builder()` only when the project has no existing client bean.
- **Standalone**: Create a local `DynamoDbClient dynamoDbClient = DynamoDbClient.builder()...build();` variable and wire it into the Flamingock builder/TargetSystem constructor.
- **AWS Region Rule**: Use `Region.of("TODO")` when the region is unknown.

### 3. Build Tool Configuration
- **Gradle**: If the Flamingock Gradle plugin is needed, show how to enable the `dynamodb()` module (and `springboot()` if applicable).
- **Maven**: Provide standard `dependencyManagement` (BOM) and `dependencies` blocks.
- When adding Flamingock dependencies or the Gradle plugin, first try the bundled `scripts/last-version.py` or `scripts/last-version.sh` to resolve the current version.

## Placeholder Guidelines
When values are missing, use:
- **TargetSystem ID**: `"YOUR_TARGET_SYSTEM_ID"`
- **AWS Region**: `"TODO"`
- **Version**: `[VERSION]` only if `scripts/last-version.py` or `scripts/last-version.sh` could not resolve the current version
- **DynamoDbClient**: `dynamoDbClient` (variable name)

## Response Structure
When responding, prefer this order:
1. **Assumptions**: runtime, language, build tool, region, and whether a `DynamoDbClient` already exists
2. **Dependencies**: Maven or Gradle configuration
3. **Code**: the `DynamoDbClient` and `DynamoDBTargetSystem` wiring
4. **Verification / Next Step**: how to verify the setup, or whether the user should continue with `flamingock-dynamodb-change`

## Example Output

### Java Spring Boot Configuration
```java
package com.example.config;

import io.flamingock.dynamodb.targetsystem.driver.DynamoDBTargetSystem;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;

@Configuration
public class FlamingockDynamoConfig {

    @Bean
    DynamoDBTargetSystem dynamoTargetSystem(DynamoDbClient dynamoDbClient) {
        return new DynamoDBTargetSystem("YOUR_TARGET_SYSTEM_ID", dynamoDbClient);
    }
}
```

### Kotlin Spring Boot Configuration
```kotlin
package com.example.config

import io.flamingock.dynamodb.targetsystem.driver.DynamoDBTargetSystem
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import software.amazon.awssdk.services.dynamodb.DynamoDbClient

@Configuration
class FlamingockDynamoConfig {

    @Bean
    fun dynamoTargetSystem(dynamoDbClient: DynamoDbClient): DynamoDBTargetSystem {
        return DynamoDBTargetSystem("YOUR_TARGET_SYSTEM_ID", dynamoDbClient)
    }
}
```
