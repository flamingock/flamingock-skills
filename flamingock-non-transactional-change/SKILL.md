---
name: flamingock-non-transactional-change
description: Use this skill to create, write, review, or split `NonTransactionalTargetSystem` `@Change` classes for systems without native transactions, emphasizing compensation, idempotency, and recovery strategy.
license: Apache-2.0
metadata:
  author: Flamingock
  version: 1.0.0
---

# Flamingock Non-Transactional Change

## Redirections
- If the user needs to configure the non-transactional TargetSystem first -> `flamingock-non-transactional-targetsystem`
- If the user still needs Flamingock core setup -> `flamingock-onboarding`

## Reference Routing
- For any non-transactional change request (resource setup, remote mutation, seeding, backfill, external configuration) -> read `references/operations.md`

## Philosophy: "Help First, Then Refine"
Always generate the best possible code with the information provided. If metadata like 'order', 'author', or 'systemId' is missing, use placeholders (e.g., `_ORDER__`, `author = "TODO"`) instead of blocking the user or asking for details first. The goal is to provide a working structure that the user can later finalize.

## Documentation & Grounding
For the most up-to-date syntax, features, and best practices, always consult the official LLM-optimized documentation:
- **Official Docs**: https://docs.flamingock.io/llms.txt

## Critical Constraints
- **All changes in this skill are non-transactional.** Always set `@Change(..., transactional = false)`.
- **Compensation is mandatory thinking.** Every change must provide a realistic `@Rollback` or a clearly scoped compensation path.
- **Recovery strategy matters.** Use `@Recovery(strategy = RecoveryStrategy.ALWAYS_RETRY)` only when the operation is truly idempotent.
- **Do not imply database-style rollback semantics.** There is no automatic transactional context here.
- **Use registered dependencies only.** The change must consume objects/properties previously registered through the target system or builder.

## Technical Core Principles

### 1. Non-Transactional Operation Model (Critical)
The skill must reason in terms of compensation, retry safety, and idempotency instead of ACID rollback.

- **Strict Guard**: If the requested rollback would destroy unrelated state, refuse that rollback and replace it with a narrower compensation plan.
- **Idempotent path**: If the operation can be repeated safely, mention or add `@Recovery(strategy = RecoveryStrategy.ALWAYS_RETRY)`.
- **Non-idempotent path**: Keep the default manual intervention strategy and explain why.

### 2. Naming & Structure
- **File Naming**: Follow the pattern `[ORDER]__[ClassName].[ext]`. Use `_ORDER__` as a placeholder if the order is unknown.
- **Annotations**:
  - `@TargetSystem(id = "...")`: Required. Use `"YOUR_TARGET_SYSTEM_ID"` if unknown.
  - `@Change(..., transactional = false)`: Required.
  - `@Apply` and `@Rollback`: Mandatory methods.

### 3. Language & Environment
- **Languages**: Support both **Java** and **Kotlin**. Default to Java if unspecified, but mention Kotlin capability.
- **Dependencies**: Use the exact types or named values registered in the target system (`.addDependency()` / `.setProperty()`).

## Placeholder Guidelines
When information is missing, use these tokens:
- **Order**: `_ORDER__` in filename, `"TODO"` in annotation.
- **Author**: `"TODO"`
- **Package**: `com.example.migrations`
- **TargetSystem ID**: `"YOUR_TARGET_SYSTEM_ID"`

## Example Output

### Idempotent External Mutation Change
```java
package com.example.migrations;

import io.flamingock.api.annotations.Apply;
import io.flamingock.api.annotations.Change;
import io.flamingock.api.annotations.Recovery;
import io.flamingock.api.annotations.RecoveryStrategy;
import io.flamingock.api.annotations.Rollback;
import io.flamingock.api.annotations.TargetSystem;
import software.amazon.awssdk.services.s3.S3Client;

@TargetSystem(id = "YOUR_TARGET_SYSTEM_ID")
@Recovery(strategy = RecoveryStrategy.ALWAYS_RETRY)
@Change(order = "TODO", id = "tag-product-bucket", author = "TODO", transactional = false)
public class _ORDER__TagProductBucket {

    @Apply
    public void apply(S3Client s3Client) {
        // read-modify-write pattern
    }

    @Rollback
    public void rollback(S3Client s3Client) {
        // remove only the tag introduced by apply
    }
}
```

### Named Dependency Change
```java
package com.example.migrations;

import io.flamingock.api.annotations.Apply;
import io.flamingock.api.annotations.Change;
import io.flamingock.api.annotations.Rollback;
import io.flamingock.api.annotations.TargetSystem;
import jakarta.inject.Named;

@TargetSystem(id = "YOUR_TARGET_SYSTEM_ID")
@Change(order = "TODO", id = "configure-notification-templates", author = "TODO", transactional = false)
public class _ORDER__ConfigureNotificationTemplates {

    @Apply
    public void apply(@Named("email") NotificationClient emailClient) {
        emailClient.registerTemplate("welcome", "Welcome!");
    }

    @Rollback
    public void rollback(@Named("email") NotificationClient emailClient) {
        emailClient.deleteTemplate("welcome");
    }
}
```
