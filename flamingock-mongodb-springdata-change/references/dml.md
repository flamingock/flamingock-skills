## Transactional DML for `MongoDBSpringDataTargetSystem`

Use this reference when the request is clearly about Spring Data MongoDB DML work such as inserts, updates, deletes, or backfills.

### Preconditions

- `MongoDBSpringDataTargetSystem` registration evidence is already confirmed.
- Use `"YOUR_TARGET_SYSTEM_ID"` when the target-system id is still unknown.
- `Language` may resolve to `java` or `kotlin`; default to Java if still unspecified.
- If author, package, or order are missing, keep the canonical placeholders instead of blocking.
- The change truly performs DML (`save`, `insert`, `updateFirst`, `updateMulti`, `remove`, seed/backfill work).

If details are still missing, generate the structure with placeholders and keep the request non-blocking.

### Source-backed facts

- `MongoDBSpringDataTargetSystem` injects `MongoTemplate` into the change context.
- Spring manages transaction participation for supported DML operations; this path does not expose `ClientSession`.
- `MongoTemplate` is the default dependency for this skill. Keep low-level driver detours out unless the user changes technologies.

### Dependency choice

Prefer the smallest dependency set that solves the task:

1. `MongoTemplate` for normal collection work
2. Domain classes, `Query`, `Criteria`, and `Update` only when they add clarity

Do not switch to `MongoClient`, `MongoDatabase`, or sync-driver session APIs in this path.

### Transaction rule

Keep `transactional = true` by default for DML. You normally do not need to write it explicitly.

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
    public void apply(MongoTemplate mongoTemplate) {
        // use MongoTemplate operations for the whole DML flow
    }

    @Rollback
    public void rollback(MongoTemplate mongoTemplate) {
        // undo only the documents touched by apply
    }
}
```

### Kotlin path

```kotlin
@TargetSystem(id = "YOUR_TARGET_SYSTEM_ID")
@Change(order = "TODO", id = "backfill-order-status", author = "TODO")
class _ORDER__BackfillOrderStatus {

    @Apply
    fun apply(mongoTemplate: MongoTemplate) {
        // use MongoTemplate operations for the whole DML flow
    }

    @Rollback
    fun rollback(mongoTemplate: MongoTemplate) {
        // undo only the documents touched by apply
    }
}
```

### Copy-paste asset

Use only the asset that matches the resolved language:

- Java: `assets/example-dml.java`
- Kotlin: `assets/example-dml.kt`
