## Non-transactional DDL for `SqlTargetSystem`

Use this reference when the request is clearly about SQL DDL work such as tables, columns, indexes, or other schema-shaping operations.

### Preconditions

- `SqlTargetSystem` registration evidence is already confirmed.
- Use `"YOUR_TARGET_SYSTEM_ID"` when the target-system id is still unknown.
- `Language` may resolve to `java` or `kotlin`; default to Java if still unspecified.
- If author, package, or order are missing, keep the canonical placeholders instead of blocking.
- The change truly performs DDL (`CREATE TABLE`, `ALTER TABLE`, `CREATE INDEX`, `DROP TABLE`, `DROP COLUMN`, schema-shaping operations).

If details are still missing, generate the structure with placeholders and keep the request non-blocking.

### Transaction rule

Default portable DDL changes to non-transactional execution:

```java
@Change(id = "...", author = "...", transactional = false)
```

Prefer `Connection` only unless the user has a strong reason to use `DataSource`.

### Idempotency rule

Apply logic must be safe to re-run. Use guards such as:

- `CREATE TABLE IF NOT EXISTS`
- `ALTER TABLE ... ADD COLUMN IF NOT EXISTS`
- `DROP ... IF EXISTS`
- equivalent dialect-safe existence checks when the database lacks direct syntax

### Rollback precision

- Roll back only the schema object owned by this change.
- If ownership is unclear, prefer a conservative rollback or a scoped compensation note instead of inventing a destructive rollback.
- Do not drop unrelated tables, columns, or indexes just because they match a broad naming pattern.

### Java path

```java
@TargetSystem(id = "YOUR_TARGET_SYSTEM_ID")
@Change(order = "TODO", id = "add-archive-column", author = "TODO", transactional = false)
public class _ORDER__AddArchiveColumn {

    @Apply
    public void apply(Connection connection) throws SQLException {
        // guard already-exists conditions before mutating schema
    }

    @Rollback
    public void rollback(Connection connection) throws SQLException {
        // compensate only the schema effect introduced by apply
    }
}
```

### Kotlin path

```kotlin
@TargetSystem(id = "YOUR_TARGET_SYSTEM_ID")
@Change(order = "TODO", id = "add-archive-column", author = "TODO", transactional = false)
class _ORDER__AddArchiveColumn {

    @Apply
    fun apply(connection: Connection) {
        // guard already-exists conditions before mutating schema
    }

    @Rollback
    fun rollback(connection: Connection) {
        // compensate only the schema effect introduced by apply
    }
}
```

### Copy-paste asset

Use only the asset that matches the resolved language:

- Java: `assets/example-ddl.java`
- Kotlin: `assets/example-ddl.kt`
