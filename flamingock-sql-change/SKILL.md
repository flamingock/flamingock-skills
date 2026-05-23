---
name: flamingock-sql-change
description: Use this skill to create, write, review, or split `SqlTargetSystem` `@Change` classes for relational-database migrations, backfills, data fixes, schema changes, and enforce strict DML vs DDL separation.
license: Apache-2.0
metadata:
  author: Flamingock
  version: 1.0.0
---

# Flamingock SQL Change

## Redirections
- If the user needs to configure the SQL TargetSystem first -> `flamingock-sql-targetsystem`
- If the user still needs Flamingock core setup -> `flamingock-onboarding`

## Reference Routing
- If the request is **DML** (insert, update, delete, backfill, seed/data fix) -> read `references/dml.md`
- If the request is **DDL** (table, column, index, schema-shaping work) -> read `references/ddl.md`

## Philosophy: "Help First, Then Refine"
Always generate the best possible code with the information provided. If metadata like 'order', 'author', or 'systemId' is missing, use placeholders (e.g., `_ORDER__`, `author = "TODO"`) instead of blocking the user or asking for details first. The goal is to provide a working structure that the user can later finalize.

## Documentation & Grounding
For the most up-to-date syntax, features, and best practices, always consult the official LLM-optimized documentation:
- **Official Docs**: https://docs.flamingock.io/llms.txt

## Critical Constraints
- **NEVER mix DDL and DML in the same Flamingock change class.** If the user asks for both, refuse the single-class request and split it into two changes.
- **DML MUST stay transactional by default.** Do not set `transactional = false` for normal insert/update/delete work.
- **DDL SHOULD default to non-transactional for portability.** PostgreSQL can support transactional DDL, but multi-dialect guidance should stay conservative.
- **DDL MUST include idempotency guards** such as `IF NOT EXISTS` / `IF EXISTS` or equivalent dialect-safe checks.
- **Do not switch to ORM/repository abstractions** unless the user explicitly changes technologies.

## Technical Core Principles

### 1. DML vs DDL Distinction (Critical)
The skill must correctly distinguish between Data Manipulation and Data Definition to keep SQL migrations predictable across dialects.

- **Strict Guard**: If the request mixes operations such as "add a column and seed data", explicitly refuse to generate one class and propose two classes: one DDL change and one DML change.

- **DML (Data Manipulation)**: (Insert, Update, Delete, Merge).
  - **Transactional**: Must use `transactional = true` (default).
  - **JDBC Path**: Use `Connection` or `DataSource` with prepared statements.
  - **Reference**: See `references/dml.md`.
- **DDL (Data Definition)**: (Create/Drop Table, Alter Table, Create/Drop Index).
  - **Non-Transactional**: Prefer `@Change(..., transactional = false)` unless the user explicitly scopes the migration to a dialect with transactional DDL.
  - **Idempotency**: Always include `IF NOT EXISTS`, `IF EXISTS`, or a precise existence check.
  - **Reference**: See `references/ddl.md`.

### 2. Naming & Structure
- **File Naming**: Follow the pattern `[ORDER]__[ClassName].[ext]`. Use `_ORDER__` as a placeholder if the order is unknown.
- **Annotations**:
  - `@TargetSystem(id = "...")`: Required. Use `"YOUR_TARGET_SYSTEM_ID"` if unknown.
  - `@Change(...)`: Required. Set `order`, `author`, and `transactional`.
  - `@Apply` and `@Rollback`: Mandatory methods.

### 3. Language & Environment
- **Languages**: Support both **Java** and **Kotlin**. Default to Java if unspecified, but mention Kotlin capability.
- **Dependencies**: Use `Connection` or `DataSource`. Stay in JDBC semantics.

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
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

@TargetSystem(id = "YOUR_TARGET_SYSTEM_ID")
@Change(order = "TODO", id = "backfill-user-status", author = "TODO")
public class _ORDER__BackfillUserStatus {

    @Apply
    public void apply(Connection connection) throws SQLException {
        try (PreparedStatement stmt = connection.prepareStatement(
            "UPDATE users SET status = ? WHERE status IS NULL"
        )) {
            stmt.setString(1, "ACTIVE");
            stmt.executeUpdate();
        }
    }

    @Rollback
    public void rollback(Connection connection) throws SQLException {
        try (PreparedStatement stmt = connection.prepareStatement(
            "UPDATE users SET status = NULL WHERE status = ?"
        )) {
            stmt.setString(1, "ACTIVE");
            stmt.executeUpdate();
        }
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
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

@TargetSystem(id = "YOUR_TARGET_SYSTEM_ID")
@Change(order = "TODO", id = "create-logs-table", author = "TODO", transactional = false)
public class _ORDER__CreateLogsTable {

    @Apply
    public void apply(Connection connection) throws SQLException {
        try (Statement statement = connection.createStatement()) {
            statement.execute("CREATE TABLE IF NOT EXISTS logs (id BIGINT PRIMARY KEY, message VARCHAR(255))");
        }
    }

    @Rollback
    public void rollback(Connection connection) throws SQLException {
        try (Statement statement = connection.createStatement()) {
            statement.execute("DROP TABLE IF EXISTS logs");
        }
    }
}
```
