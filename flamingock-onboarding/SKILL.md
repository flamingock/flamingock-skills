---
name: flamingock-onboarding
description: Use this skill to set up, bootstrap, wire, or integrate Flamingock from scratch in a Java or Kotlin project (standalone or Spring Boot), including core dependencies, activation, and initial migration infrastructure.
license: Apache-2.0
metadata:
  author: Flamingock
  version: 1.0.0
---

# Flamingock Onboarding

## Redirections
- If Flamingock is already set up and you need to configure a TargetSystem for a specific technology -> `flamingock-*-targetsystem`
- If a TargetSystem is already configured and you need a migration class for a specific technology -> `flamingock-*-change`

## Reference Routing
- If the request is **Spring Boot + Community** -> read `references/springboot-community.md`
- If the request is **Spring Boot + Cloud** -> read `references/springboot-cloud.md`
- If the request is **Standalone + Community** -> read `references/standalone-community.md`
- If the request is **Standalone + Cloud** -> read `references/standalone-cloud.md`

## Philosophy: "Frictionless Start"
The goal is to get the user up and running in seconds. Do not block the process with questions. Default to **Community Edition**, **Spring Boot**, and **Java** if information is missing, but clearly label these as defaults and explain how to change them.

## Documentation & Grounding
For the most up-to-date syntax, features, and best practices, always consult the official LLM-optimized documentation:
- **Official Docs**: https://docs.flamingock.io/llms.txt

## Technical Core Principles

### 1. Version Management
- Always prioritize using the Flamingock BOM (`flamingock-bom`).
- When adding Flamingock dependencies or the Gradle plugin, first try the bundled `scripts/last-version.py` or `scripts/last-version.sh` to resolve the current version.
- If version resolution is not possible, use `[VERSION]` as a placeholder and call out the assumption clearly.

### 2. Activation
- **Spring Boot**: Use `@EnableFlamingock` on the main application class or a configuration class.
- **Standalone**: Show the `Flamingock.builder()` setup.
- **@Stage**: If the changes package is unknown, omit it or use `"com.example.changes"` as a placeholder.

### 3. Community AuditStore
- Derive the AuditStore from the TargetSystem using the pattern: `XxxAuditStore.from(targetSystem)`.
- If no TargetSystem exists yet, explain that the AuditStore can be added later.

### 4. Cloud Integration
- If the user mentions "Cloud", focus on `apiToken`, `environment`, and `service` values.
- Use placeholders for these values if they are not provided.

## Build Tool Integration
- **Gradle**: Show the `io.flamingock` plugin and module configuration (`community()`, `springboot()`, etc.).
- **Maven**: Provide the `dependencyManagement` and `dependencies` sections.

## Response Structure
When responding, prefer this order:
1. **Assumptions**: edition, runtime, language, build tool, and whether a TargetSystem already exists
2. **Dependencies / Plugin Setup**: Maven or Gradle configuration
3. **Activation / Bootstrap Code**: `@EnableFlamingock`, `Flamingock.builder()`, or equivalent startup wiring
4. **AuditStore / Cloud Wiring**: only if the chosen path needs it
5. **Verification / Next Step**: how to verify the setup, or whether the user should continue with a `flamingock-*-targetsystem` skill

## Example Output

### Maven + Spring Boot Quick Start
```xml
<dependencyManagement>
  <dependencies>
    <dependency>
      <groupId>io.flamingock</groupId>
      <artifactId>flamingock-bom</artifactId>
      <version>[VERSION]</version>
      <type>pom</type>
      <scope>import</scope>
    </dependency>
  </dependencies>
</dependencyManagement>

<dependencies>
  <dependency>
    <groupId>io.flamingock</groupId>
    <artifactId>flamingock-community</artifactId>
  </dependency>
  <dependency>
    <groupId>io.flamingock</groupId>
    <artifactId>flamingock-springboot-integration</artifactId>
  </dependency>
</dependencies>
```

```java
package com.example;

import io.flamingock.core.api.audit.AuditStore;
import io.flamingock.mongodb.sync.auditstore.MongoDBSyncAuditStore;
import io.flamingock.springboot.EnableFlamingock;
import io.flamingock.springboot.annotations.Stage;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
@EnableFlamingock(stages = { @Stage(location = "com.example.changes") })
public class DemoApplication {

    public static void main(String[] args) {
        SpringApplication.run(DemoApplication.class, args);
    }

    @Bean
    AuditStore flamingockAuditStore() {
        return MongoDBSyncAuditStore.from(targetSystem);
    }
}
```

## Verification Checklist
Always end with a short checklist:
1. Verify dependencies resolve.
2. Run the app and check for Flamingock startup logs.
3. Verify the AuditStore (for Community) or Cloud connectivity.
