---
name: flamingock-dynamodb-change
description: Use this skill to create, write, review, or split `DynamoDBTargetSystem` `@Change` classes for DynamoDB table management, transactional writes, backfills, and strict write-vs-schema separation.
license: Apache-2.0
metadata:
  author: Flamingock
  version: 1.0.0
---

# Flamingock DynamoDB Change

## Redirections
- If the user needs to configure the DynamoDB TargetSystem first -> `flamingock-dynamodb-targetsystem`
- If the user still needs Flamingock core setup -> `flamingock-onboarding`

## Reference Routing
- If the request is **DML / write work** (put, update, delete, backfill, seed/data fix) -> read `references/dml.md`
- If the request is **DDL / table-management work** (create table, delete table, schema-shaping work) -> read `references/ddl.md`

## Philosophy: "Help First, Then Refine"
Always generate the best possible code with the information provided. If metadata like 'order', 'author', or 'systemId' is missing, use placeholders (e.g., `_ORDER__`, `author = "TODO"`) instead of blocking the user or asking for details first. The goal is to provide a working structure that the user can later finalize.

## Documentation & Grounding
For the most up-to-date syntax, features, and best practices, always consult the official LLM-optimized documentation:
- **Official Docs**: https://docs.flamingock.io/llms.txt

## Critical Constraints
- **NEVER mix table-management and write operations in the same Flamingock change class.** If the user asks for both, refuse the single-class request and split it into two changes.
- **Transactional write changes MUST use `TransactWriteItemsEnhancedRequest.Builder`** and build all writes through that transactional path.
- **Read or table-management operations MUST be non-transactional.** Set `@Change(..., transactional = false)` when the operation creates/deletes tables or performs scans outside transaction support.
- **DDL MUST include idempotency guards** such as catching `ResourceInUseException` or checking resource state first.
- **Do not fall back to Mongo or SQL semantics.** Keep the guidance AWS SDK and DynamoDB specific.

## Technical Core Principles

### 1. Write vs Table-Management Distinction (Critical)
The skill must correctly distinguish transactional write operations from table-management operations.

- **Strict Guard**: If the request mixes operations such as "create a table and seed rows", explicitly refuse to generate one class and propose two classes: one DDL change and one transactional write change.

- **Write Work**: (Put, Update, Delete, ConditionCheck).
  - **Transactional**: Must use `transactional = true` (default).
  - **Transactional Builder**: Inject `TransactWriteItemsEnhancedRequest.Builder` and add operations to it.
  - **Reference**: See `references/dml.md`.
- **Table Management / Non-Transactional Work**: (CreateTable, DeleteTable, Scan, DescribeTable-driven maintenance).
  - **Non-Transactional**: Must use `@Change(..., transactional = false)`.
  - **Idempotency**: Always include "already exists" / "already removed" guards.
  - **Reference**: See `references/ddl.md`.

### 2. Naming & Structure
- **File Naming**: Follow the pattern `[ORDER]__[ClassName].[ext]`. Use `_ORDER__` as a placeholder if the order is unknown.
- **Annotations**:
  - `@TargetSystem(id = "...")`: Required. Use `"YOUR_TARGET_SYSTEM_ID"` if unknown.
  - `@Change(...)`: Required. Set `order`, `author`, and `transactional`.
  - `@Apply` and `@Rollback`: Mandatory methods.

### 3. Language & Environment
- **Languages**: Support both **Java** and **Kotlin**. Default to Java if unspecified, but mention Kotlin capability.
- **Dependencies**: Use `DynamoDbClient` and `TransactWriteItemsEnhancedRequest.Builder` according to the operation type.

## Placeholder Guidelines
When information is missing, use these tokens:
- **Order**: `_ORDER__` in filename, `"TODO"` in annotation.
- **Author**: `"TODO"`
- **Package**: `com.example.migrations`
- **TargetSystem ID**: `"YOUR_TARGET_SYSTEM_ID"`

## Example Output

### Transactional Write Change
```java
package com.example.migrations;

import io.flamingock.api.annotations.Apply;
import io.flamingock.api.annotations.Change;
import io.flamingock.api.annotations.Rollback;
import io.flamingock.api.annotations.TargetSystem;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;
import software.amazon.awssdk.enhanced.dynamodb.Key;
import software.amazon.awssdk.enhanced.dynamodb.TableSchema;
import software.amazon.awssdk.enhanced.dynamodb.model.TransactWriteItemsEnhancedRequest;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;

@TargetSystem(id = "YOUR_TARGET_SYSTEM_ID")
@Change(order = "TODO", id = "seed-product-catalog", author = "TODO")
public class _ORDER__SeedProductCatalog {

    @Apply
    public void apply(DynamoDbClient client, TransactWriteItemsEnhancedRequest.Builder txBuilder) {
        DynamoDbEnhancedClient enhancedClient = DynamoDbEnhancedClient.builder().dynamoDbClient(client).build();
        DynamoDbTable<Product> table = enhancedClient.table("products", TableSchema.fromBean(Product.class));
        txBuilder.addPutItem(table, new Product("P001", "Widget"));
    }

    @Rollback
    public void rollback(DynamoDbClient client, TransactWriteItemsEnhancedRequest.Builder txBuilder) {
        DynamoDbEnhancedClient enhancedClient = DynamoDbEnhancedClient.builder().dynamoDbClient(client).build();
        DynamoDbTable<Product> table = enhancedClient.table("products", TableSchema.fromBean(Product.class));
        txBuilder.addDeleteItem(table, Key.builder().partitionValue("P001").build());
    }
}
```

### Non-Transactional Table Change
```java
package com.example.migrations;

import io.flamingock.api.annotations.Apply;
import io.flamingock.api.annotations.Change;
import io.flamingock.api.annotations.Rollback;
import io.flamingock.api.annotations.TargetSystem;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.CreateTableRequest;
import software.amazon.awssdk.services.dynamodb.model.DeleteTableRequest;
import software.amazon.awssdk.services.dynamodb.model.ResourceInUseException;

@TargetSystem(id = "YOUR_TARGET_SYSTEM_ID")
@Change(order = "TODO", id = "create-logs-table", author = "TODO", transactional = false)
public class _ORDER__CreateLogsTable {

    @Apply
    public void apply(DynamoDbClient client) {
        try {
            client.createTable(CreateTableRequest.builder().tableName("logs").build());
        } catch (ResourceInUseException ignored) {
            // already exists
        }
    }

    @Rollback
    public void rollback(DynamoDbClient client) {
        client.deleteTable(DeleteTableRequest.builder().tableName("logs").build());
    }
}
```
