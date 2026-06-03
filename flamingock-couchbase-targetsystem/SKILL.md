---
name: flamingock-couchbase-targetsystem
description: Use this skill to configure, wire, or connect a `CouchbaseTargetSystem` in an existing Flamingock project when setting up Couchbase-backed migration infrastructure, including SDK guidance and bean/builder registration.
license: Apache-2.0
metadata:
  author: Flamingock
  version: 1.0.0
---

# Flamingock Couchbase TargetSystem

## Redirections
- If the TargetSystem is already configured and you need to create a migration -> `flamingock-couchbase-change`
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
  - Java: `new CouchbaseTargetSystem(id, cluster, bucketName)`
  - Kotlin: `CouchbaseTargetSystem(id, cluster, bucketName)`
- **Dependencies**:
  - For Gradle plugin-first setups: enable `couchbase()` and, if needed, `springboot()` in `flamingock { ... }`; keep `dependencies { ... }` for Couchbase SDK modules only.
  - For Maven/manual setups: include `io.flamingock:flamingock-couchbase-targetsystem` and, for Spring Boot, `io.flamingock:flamingock-springboot-integration` explicitly.
  - Add `com.couchbase.client:java-client` when the project does not already provide the Couchbase SDK.

### 2. Cluster Handling
- **Auto-detection**: Briefly check if a `Cluster` bean or variable already exists in the project.
- **Orphan Cluster Rule**: If no `Cluster` bean or variable exists, generate it immediately instead of asking the user to create it first.
- **Spring Boot**: Create a `@Bean` returning `Cluster.connect(...)` only when the project has no existing cluster bean.
- **Standalone**: Create a local `Cluster cluster = Cluster.connect("TODO", "TODO", "TODO");` variable and wire it into the Flamingock builder/TargetSystem constructor.
- **Bucket Rule**: Keep the bucket name explicit; use `"TODO"` when unknown.

### 3. Build Tool Configuration
- **Gradle**: If the Flamingock Gradle plugin is needed, show how to enable the `couchbase()` module (and `springboot()` if applicable).
- **Maven**: Provide standard `dependencyManagement` (BOM) and `dependencies` blocks.
- When adding Flamingock dependencies or the Gradle plugin, first try the bundled `scripts/last-version.py` or `scripts/last-version.sh` to resolve the current version.

## Placeholder Guidelines
When values are missing, use:
- **TargetSystem ID**: `"YOUR_TARGET_SYSTEM_ID"`
- **Connection String / Host**: `"TODO"`
- **Username / Password**: `"TODO"`
- **Bucket Name**: `"TODO"`
- **Version**: `[VERSION]` only if `scripts/last-version.py` or `scripts/last-version.sh` could not resolve the current version
- **Cluster**: `cluster` (variable name)

## Response Structure
When responding, prefer this order:
1. **Assumptions**: runtime, language, build tool, bucket, and whether a `Cluster` already exists
2. **Dependencies**: Maven or Gradle configuration
3. **Code**: the `Cluster` and `CouchbaseTargetSystem` wiring
4. **Verification / Next Step**: how to verify the setup, or whether the user should continue with `flamingock-couchbase-change`

## Example Output

### Java Spring Boot Configuration
```java
package com.example.config;

import com.couchbase.client.java.Cluster;
import io.flamingock.targetsystem.couchbase.CouchbaseTargetSystem;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class FlamingockCouchbaseConfig {

    @Bean
    CouchbaseTargetSystem couchbaseTargetSystem(Cluster cluster) {
        return new CouchbaseTargetSystem("YOUR_TARGET_SYSTEM_ID", cluster, "TODO");
    }
}
```

### Kotlin Spring Boot Configuration
```kotlin
package com.example.config

import com.couchbase.client.java.Cluster
import io.flamingock.targetsystem.couchbase.CouchbaseTargetSystem
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class FlamingockCouchbaseConfig {

    @Bean
    fun couchbaseTargetSystem(cluster: Cluster): CouchbaseTargetSystem {
        return CouchbaseTargetSystem("YOUR_TARGET_SYSTEM_ID", cluster, "TODO")
    }
}
```
