---
name: flamingock-mongodb-springdata-change
description: Use this skill to create, write, review, or split `MongoDBSpringDataTargetSystem` `@Change` classes for Spring Data MongoDB migrations, backfills, data fixes, collection/index changes, and enforce strict DML vs DDL separation.
license: Apache-2.0
metadata:
  author: Flamingock
  version: 1.0.0
---

# Flamingock MongoDB Spring Data Change

## Redirections
- If the user needs to configure the Spring Data MongoDB TargetSystem first -> `flamingock-mongodb-springdata-targetsystem`
- If the user still needs Flamingock core setup -> `flamingock-onboarding`

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
- **DML MUST stay in Spring Data semantics.** Use `MongoTemplate`; do not inject `ClientSession` or fall back to sync-driver transaction patterns.
- **DDL MUST be non-transactional.** Set `@Change(..., transactional = false)` for collection/index work.
- **DDL MUST include idempotency guards** such as `collectionExists`, `indexOps().ensureIndex(...)`, or conservative existence checks.
- **Do not switch to raw MongoDB sync APIs** unless the user explicitly changes technologies.

## Technical Core Principles

### 1. DML vs DDL Distinction (Critical)
The skill must correctly distinguish between Data Manipulation and Data Definition while staying inside Spring Data MongoDB semantics.

- **Strict Guard**: If the request mixes operations such as "create an index and seed data", explicitly refuse to generate one class and propose two classes: one DDL change and one DML change.

- **DML (Data Manipulation)**: (Insert, Update, Delete).
  - **Transactional**: Must use `transactional = true` (default).
  - **Spring Data Path**: Use `MongoTemplate` operations such as `save`, `updateMulti`, `remove`, and `findAndModify`.
  - **Reference**: See `references/dml.md`.
- **DDL (Data Definition)**: (Create/Drop Collection, Index).
  - **Non-Transactional**: Must use `@Change(..., transactional = false)`.
  - **No Session Injection**: Do not inject `ClientSession`.
  - **Idempotency**: Always include "already exists" guards such as `collectionExists` or `ensureIndex`.
  - **Reference**: See `references/ddl.md`.

### 2. Naming & Structure
- **File Naming**: Follow the pattern `[ORDER]__[ClassName].[ext]`. Use `_ORDER__` as a placeholder if the order is unknown.
- **Annotations**:
  - `@TargetSystem(id = "...")`: Required. Use `"YOUR_TARGET_SYSTEM_ID"` if unknown.
  - `@Change(...)`: Required. Set `order`, `author`, and `transactional`.
  - `@Apply` and `@Rollback`: Mandatory methods.

### 3. Language & Environment
- **Languages**: Support both **Java** and **Kotlin**. Default to Java if unspecified, but mention Kotlin capability.
- **Dependencies**: Use `MongoTemplate`. Keep the implementation Spring Boot / Spring Data aligned.

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

import io.flamingock.api.annotations.Apply;
import io.flamingock.api.annotations.Change;
import io.flamingock.api.annotations.Rollback;
import io.flamingock.api.annotations.TargetSystem;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;

@TargetSystem(id = "YOUR_TARGET_SYSTEM_ID")
@Change(order = "TODO", id = "backfill-user-status", author = "TODO")
public class _ORDER__BackfillUserStatus {

    @Apply
    public void apply(MongoTemplate mongoTemplate) {
        mongoTemplate.updateMulti(
            Query.query(Criteria.where("status").exists(false)),
            new Update().set("status", "ACTIVE"),
            "users"
        );
    }

    @Rollback
    public void rollback(MongoTemplate mongoTemplate) {
        mongoTemplate.updateMulti(
            Query.query(Criteria.where("status").is("ACTIVE")),
            new Update().unset("status"),
            "users"
        );
    }
}
```

### Non-Transactional DDL Change
```java
package com.example.migrations;

import io.flamingock.api.annotations.Apply;
import io.flamingock.api.annotations.Change;
import io.flamingock.api.annotations.Rollback;
import io.flamingock.api.annotations.TargetSystem;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.index.Index;

@TargetSystem(id = "YOUR_TARGET_SYSTEM_ID")
@Change(order = "TODO", id = "create-logs-collection", author = "TODO", transactional = false)
public class _ORDER__CreateLogsCollection {

    @Apply
    public void apply(MongoTemplate mongoTemplate) {
        if (!mongoTemplate.collectionExists("logs")) {
            mongoTemplate.createCollection("logs");
        }
        mongoTemplate.indexOps("logs").ensureIndex(new Index().on("timestamp", Sort.Direction.DESC));
    }

    @Rollback
    public void rollback(MongoTemplate mongoTemplate) {
        if (mongoTemplate.collectionExists("logs")) {
            mongoTemplate.dropCollection("logs");
        }
    }
}
```
