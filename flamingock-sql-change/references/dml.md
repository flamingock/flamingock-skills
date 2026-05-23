## Transactional DML for `SqlTargetSystem`

Use this reference when the request is clearly about SQL DML work such as inserts, updates, deletes, merges, or backfills.

### Preconditions

- `SqlTargetSystem` registration evidence is already confirmed.
- Use `"YOUR_TARGET_SYSTEM_ID"` when the target-system id is still unknown.
- `Language` may resolve to `java` or `kotlin`; default to Java if still unspecified.
- If author, package, or order are missing, keep the canonical placeholders instead of blocking.
- The change truly performs DML (`INSERT`, `UPDATE`, `DELETE`, `MERGE`, seed/backfill work).

If details are still missing, generate the structure with placeholders and keep the request non-blocking.

### Source-backed facts

- `SqlTargetSystem` injects `DataSource` and `Connection` into the target-system context.
- For most changes, `Connection` is the smallest dependency that solves the task.
- Keep writes parameterized with prepared statements whenever user input or variable values are involved.

### Dependency choice

Prefer the smallest dependency set that solves the task:

1. `Connection` for single-step or moderate DML work
2. `DataSource` when explicit connection acquisition or batching is clearer

Do not switch to ORM/repository abstractions in this path.

### Transaction rule

Keep `transactional = true` by default for DML. You normally do not need to write it explicitly.

### Rollback precision

- Roll back only rows inserted or modified by `@Apply`.
- Prefer exact primary keys, deterministic predicates, or a migration marker column/value when needed.
- Never use a broad rollback statement that could delete or mutate pre-existing application data.

### Java path

```java
@TargetSystem(id = "YOUR_TARGET_SYSTEM_ID")
@Change(order = "TODO", id = "backfill-order-status", author = "TODO")
public class _ORDER__BackfillOrderStatus {

    @Apply
    public void apply(Connection connection) throws SQLException {
        // use prepared statements for DML work
    }

    @Rollback
    public void rollback(Connection connection) throws SQLException {
        // undo only the rows touched by apply
    }
}
```

### Kotlin path

```kotlin
@TargetSystem(id = "YOUR_TARGET_SYSTEM_ID")
@Change(order = "TODO", id = "backfill-order-status", author = "TODO")
class _ORDER__BackfillOrderStatus {

    @Apply
    fun apply(connection: Connection) {
        // use prepared statements for DML work
    }

    @Rollback
    fun rollback(connection: Connection) {
        // undo only the rows touched by apply
    }
}
```

### Copy-paste asset

Use only the asset that matches the resolved language:

- Java: `assets/example-dml.java`
- Kotlin: `assets/example-dml.kt`
