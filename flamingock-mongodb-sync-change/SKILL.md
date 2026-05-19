---
name: flamingock-mongodb-sync-change
description: Use this skill to create, write, review, or split MongoDBSyncTargetSystem `@Change` classes for MongoDB migrations, backfills, data fixes, collection/index changes, and enforce strict DML vs DDL separation.
license: Apache-2.0
metadata:
  author: Flamingock
  version: 1.0.0
---

# Flamingock MongoDB Sync Change

## Redirections
- If the user needs to configure the MongoDB connection or TargetSystem first -> `flamingock-mongodb-sync-targetsystem`
- If the user still needs Flamingock core setup -> `flamingock-onboarding`
- If the request is specifically for Spring Data / `MongoTemplate` migrations -> `flamingock-mongodb-springdata-change`

## Reference Routing
- If the request is **DML** (insert, update, delete, backfill, seed/data fix) -> read `references/dml.md`
- If the request is **DDL** (collection, index, schema-shaping work) -> read `references/ddl.md`

## Philosophy: "Help First, Then Refine"
Always generate the best possible code with the information provided. If metadata like 'order', 'author', or 'systemId' is missing, use placeholders (e.g., `_ORDER__`, `author = "TODO"`) instead of blocking the user or asking for details first. The goal is to provide a working structure that the user can later finalize.

## Documentation & Grounding
For the most up-to-date syntax, features, and best practices, always consult the official LLM-optimized documentation:
- **Official Docs**: https://docs.flamingock.io/llms.txt

## Critical Constraints
- **NEVER mix DDL and DML in the same Flamingock change class.** If the user asks for both, refuse the single-class request and split it into two changes.
- **DML MUST be transactional.** Do not set `transactional = false` for insert/update/delete changes.
- **DML MUST inject `ClientSession session` and MUST pass that session to every MongoDB write/read operation that participates in the migration.**
- **DDL MUST be non-transactional.** Set `@Change(..., transactional = false)` and do not inject `ClientSession`.
- **DDL MUST include idempotency guards** such as existence checks before creating or dropping collections/indexes.
- **Do not use Spring Data, `MongoTemplate`, or repository abstractions** unless the user explicitly asks for that stack.

## Technical Core Principles

### 1. DML vs DDL Distinction (Critical)
The skill must correctly distinguish between Data Manipulation and Data Definition to ensure MongoDB stability and performance.

- **Strict Guard**: If the request mixes operations such as "create an index and seed data", explicitly refuse to generate one class and propose two classes: one DDL change and one DML change.

- **DML (Data Manipulation)**: (Insert, Update, Delete).
  - **Transactional**: Must use `transactional = true` (default).
  - **Session Propagation**: It is MANDATORY to inject `ClientSession session` in `@Apply` and pass it to every MongoDB operation to ensure it participates in the Flamingock transaction.
  - **Usage Rule**: Use APIs such as `insertOne(session, ...)`, `updateMany(session, ...)`, `deleteMany(session, ...)`, and equivalents. A DML example without session propagation is invalid.
  - **Reference**: See `references/dml.md`.
- **DDL (Data Definition)**: (Create/Drop Collection, Index).
  - **Non-Transactional**: Must use `@Change(..., transactional = false)`.
  - **No Session Injection**: Do not inject `ClientSession` into DDL-only changes.
  - **Idempotency**: Always include "already exists" guards (e.g., check if collection/index exists) to ensure the change is safely re-executable.
  - **Reference**: See `references/ddl.md`.

### 2. Naming & Structure
- **File Naming**: Follow the pattern `[ORDER]__[ClassName].[ext]`. Use `_ORDER__` as a placeholder if the order is unknown.
- **Annotations**:
  - `@TargetSystem(id = "...")`: Required. Use `"YOUR_TARGET_SYSTEM_ID"` if unknown.
  - `@Change(...)`: Required. Set `order`, `author`, and `transactional`.
  - `@Apply` and `@Rollback`: Mandatory methods.

### 3. Language & Environment
- **Languages**: Support both **Java** and **Kotlin**. Default to Java if unspecified, but mention Kotlin capability.
- **Dependencies**: Use `MongoDatabase` and `ClientSession`. **DO NOT** use Spring Data, `MongoTemplate`, or high-level abstractions unless specifically requested.

## Placeholder Guidelines
When information is missing, use these tokens:
- **Order**: `_ORDER__` in filename, `"TODO"` in annotation.
- **Author**: `"TODO"`
- **Package**: `com.example.migrations`
- **TargetSystem ID**: `"YOUR_TARGET_SYSTEM_ID"`

## Example Output

### Transactional DML Change
```java
package com.example.migrations;

import com.mongodb.client.ClientSession;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Updates;
import io.flamingock.api.annotations.Apply;
import io.flamingock.api.annotations.Change;
import io.flamingock.api.annotations.Rollback;
import io.flamingock.api.annotations.TargetSystem;

@TargetSystem(id = "YOUR_TARGET_SYSTEM_ID")
@Change(order = "TODO", id = "backfill-user-status", author = "TODO")
public class _ORDER__BackfillUserStatus {

    @Apply
    public void apply(MongoDatabase database, ClientSession session) {
        database.getCollection("users")
            .updateMany(
                session,
                Filters.exists("status", false),
                Updates.set("status", "ACTIVE")
            );
    }

    @Rollback
    public void rollback(MongoDatabase database, ClientSession session) {
        database.getCollection("users")
            .updateMany(
                session,
                Filters.eq("status", "ACTIVE"),
                Updates.unset("status")
            );
    }
}
```

### Non-Transactional DDL Change
```java
package com.example.migrations;

import com.mongodb.client.MongoDatabase;
import io.flamingock.api.annotations.Apply;
import io.flamingock.api.annotations.Change;
import io.flamingock.api.annotations.Rollback;
import io.flamingock.api.annotations.TargetSystem;
import java.util.stream.StreamSupport;

@TargetSystem(id = "YOUR_TARGET_SYSTEM_ID")
@Change(order = "TODO", id = "create-logs-collection", author = "TODO", transactional = false)
public class _ORDER__CreateLogsCollection {

    @Apply
    public void apply(MongoDatabase database) {
        boolean exists = StreamSupport.stream(database.listCollectionNames().spliterator(), false)
            .anyMatch("logs"::equals);

        if (!exists) {
            database.createCollection("logs");
        }
    }

    @Rollback
    public void rollback(MongoDatabase database) {
        boolean exists = StreamSupport.stream(database.listCollectionNames().spliterator(), false)
            .anyMatch("logs"::equals);

        if (exists) {
            database.getCollection("logs").drop();
        }
    }
}
```
