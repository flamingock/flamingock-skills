## Transactional DML for `MongoDBSyncTargetSystem`

Use this reference when the request is clearly about MongoDB DML work such as inserts, updates, deletes, or backfills.

### Preconditions

- `MongoDBSyncTargetSystem` registration evidence is already confirmed.
- Use `"YOUR_TARGET_SYSTEM_ID"` when the target-system id is still unknown.
- `Language` may resolve to `java` or `kotlin`; default to Java if still unspecified.
- If author, package, or order are missing, keep the canonical placeholders instead of blocking.
- The change truly performs DML (`insert`, `update`, `delete`, `findOneAndUpdate`, bulk writes, seed/backfill work).

If details are still missing, generate the structure with placeholders and keep the request non-blocking.

### Source-backed facts

- `MongoDBSyncTargetSystem` injects `MongoClient` and `MongoDatabase` into the target-system context.
- Flamingock creates a `ClientSession` from the configured `MongoClient` for transactional execution.
- Without the `ClientSession` parameter, MongoDB operations execute outside the transaction.

### Dependency choice

Prefer the smallest dependency set that solves the task:

1. `MongoDatabase` for most collection work
2. `MongoDatabase` + `ClientSession` for atomic transactional writes
3. Add `MongoClient` only when the user truly needs direct client APIs

Do not switch to `MongoTemplate` in this path.

### Transaction rule

Keep `transactional = true` by default for DML. You normally do not need to write it explicitly.

If the change must be atomic, every MongoDB write that belongs to the transaction must use the overload that receives `ClientSession`.

### Rollback precision

- Roll back only documents created or modified by `@Apply`.
- Prefer exact ids, deterministic filters, or an explicit migration marker written by apply.
- Never use a broad rollback filter that could delete or mutate pre-existing application data.

### Java path

```java
@TargetSystem(id = "YOUR_TARGET_SYSTEM_ID")
@Change(order = "TODO", id = "backfill-order-status", author = "TODO")
public class _ORDER__BackfillOrderStatus {

    @Apply
    public void apply(MongoDatabase mongoDatabase, ClientSession session) {
        // pass session to every write that must participate in the transaction
    }

    @Rollback
    public void rollback(MongoDatabase mongoDatabase, ClientSession session) {
        // undo only the rows/documents touched by apply
    }
}
```

### Kotlin path

```kotlin
@TargetSystem(id = "YOUR_TARGET_SYSTEM_ID")
@Change(order = "TODO", id = "backfill-order-status", author = "TODO")
class _ORDER__BackfillOrderStatus {

    @Apply
    fun apply(mongoDatabase: MongoDatabase, session: ClientSession) {
        // pass session to every write that must participate in the transaction
    }

    @Rollback
    fun rollback(mongoDatabase: MongoDatabase, session: ClientSession) {
        // undo only the rows/documents touched by apply
    }
}
```

### Copy-paste asset

Use only the asset that matches the resolved language:

- Java: `assets/example-dml.java`
- Kotlin: `assets/example-dml.kt`
