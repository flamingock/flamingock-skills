---
name: flamingock-couchbase-change
description: Use this skill to create, write, review, or split `CouchbaseTargetSystem` `@Change` classes for Couchbase document migrations, collection/index changes, and strict transactional-vs-schema separation.
license: Apache-2.0
metadata:
  author: Flamingock
  version: 1.0.0
---

# Flamingock Couchbase Change

## Redirections
- If the user needs to configure the Couchbase TargetSystem first -> `flamingock-couchbase-targetsystem`
- If the user still needs Flamingock core setup -> `flamingock-onboarding`

## Reference Routing
- If the request is **document work** (insert, replace, remove, backfill, seed/data fix) -> read `references/dml.md`
- If the request is **schema work** (collection, scope, index, schema-shaping work) -> read `references/ddl.md`

## Philosophy: "Help First, Then Refine"
Always generate the best possible code with the information provided. If metadata like 'order', 'author', or 'systemId' is missing, use placeholders (e.g., `_ORDER__`, `author = "TODO"`) instead of blocking the user or asking for details first. The goal is to provide a working structure that the user can later finalize.

## Documentation & Grounding
For the most up-to-date syntax, features, and best practices, always consult the official LLM-optimized documentation:
- **Official Docs**: https://docs.flamingock.io/llms.txt

## Critical Constraints
- **NEVER mix schema and document operations in the same Flamingock change class.** If the user asks for both, refuse the single-class request and split it into two changes.
- **Transactional document changes MUST use `TransactionAttemptContext`.** Do not bypass it for document operations that must stay transactional.
- **Schema changes MUST be non-transactional.** Set `@Change(..., transactional = false)` for collection/index work.
- **Schema changes MUST include idempotency guards** such as catching `CollectionExistsException` or using safe existence checks.
- **Do not fall back to MongoDB or SQL APIs.** Keep the guidance Couchbase SDK specific.

## Technical Core Principles

### 1. Document vs Schema Distinction (Critical)
The skill must correctly distinguish transactional document operations from non-transactional schema operations.

- **Strict Guard**: If the request mixes operations such as "create a collection and seed documents", explicitly refuse to generate one class and propose two classes: one schema change and one transactional document change.

- **Document Work**: (insert, replace, remove, transactional updates).
  - **Transactional**: Must use `transactional = true` (default).
  - **Transaction Context**: Inject `TransactionAttemptContext` and use it for document operations.
  - **Reference**: See `references/dml.md`.
- **Schema Work**: (Create/Drop Collection, Index, Scope-level setup).
  - **Non-Transactional**: Must use `@Change(..., transactional = false)`.
  - **Idempotency**: Always include existence checks or safe exception handling.
  - **Reference**: See `references/ddl.md`.

### 2. Naming & Structure
- **File Naming**: Follow the pattern `[ORDER]__[ClassName].[ext]`. Use `_ORDER__` as a placeholder if the order is unknown.
- **Annotations**:
  - `@TargetSystem(id = "...")`: Required. Use `"YOUR_TARGET_SYSTEM_ID"` if unknown.
  - `@Change(...)`: Required. Set `order`, `author`, and `transactional`.
  - `@Apply` and `@Rollback`: Mandatory methods.

### 3. Language & Environment
- **Languages**: Support both **Java** and **Kotlin**. Default to Java if unspecified, but mention Kotlin capability.
- **Dependencies**: Use `Cluster`, `Bucket`, and `TransactionAttemptContext` according to the operation type.

## Placeholder Guidelines
When information is missing, use these tokens:
- **Order**: `_ORDER__` in filename, `"TODO"` in annotation.
- **Author**: `"TODO"`
- **Package**: `com.example.migrations`
- **TargetSystem ID**: `"YOUR_TARGET_SYSTEM_ID"`

## Example Output

### Transactional Document Change
```java
package com.example.migrations;

import com.couchbase.client.java.Bucket;
import com.couchbase.client.java.Cluster;
import com.couchbase.client.java.json.JsonObject;
import com.couchbase.client.java.transactions.TransactionAttemptContext;
import io.flamingock.api.annotations.Apply;
import io.flamingock.api.annotations.Change;
import io.flamingock.api.annotations.Rollback;
import io.flamingock.api.annotations.TargetSystem;

@TargetSystem(id = "YOUR_TARGET_SYSTEM_ID")
@Change(order = "TODO", id = "seed-app-config", author = "TODO")
public class _ORDER__SeedAppConfig {

    @Apply
    public void apply(Cluster cluster, Bucket bucket, TransactionAttemptContext txContext) {
        txContext.insert(bucket.defaultCollection(), "config::app", JsonObject.create().put("enabled", true));
    }

    @Rollback
    public void rollback(Cluster cluster, Bucket bucket, TransactionAttemptContext txContext) {
        txContext.remove(bucket.defaultCollection().get("config::app"));
    }
}
```

### Non-Transactional Schema Change
```java
package com.example.migrations;

import com.couchbase.client.core.error.CollectionExistsException;
import com.couchbase.client.java.Bucket;
import com.couchbase.client.java.Cluster;
import com.couchbase.client.java.manager.collection.CollectionSpec;
import io.flamingock.api.annotations.Apply;
import io.flamingock.api.annotations.Change;
import io.flamingock.api.annotations.Rollback;
import io.flamingock.api.annotations.TargetSystem;

@TargetSystem(id = "YOUR_TARGET_SYSTEM_ID")
@Change(order = "TODO", id = "create-orders-collection", author = "TODO", transactional = false)
public class _ORDER__CreateOrdersCollection {

    @Apply
    public void apply(Cluster cluster, Bucket bucket) {
        try {
            bucket.collections().createCollection(CollectionSpec.create("orders", "_default"));
        } catch (CollectionExistsException ignored) {
            // already exists
        }
    }

    @Rollback
    public void rollback(Cluster cluster, Bucket bucket) {
        bucket.collections().dropCollection(CollectionSpec.create("orders", "_default"));
    }
}
```
