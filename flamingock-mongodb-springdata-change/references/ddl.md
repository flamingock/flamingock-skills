## Non-transactional DDL for `MongoDBSpringDataTargetSystem`

Use this reference when the request is clearly about Spring Data MongoDB DDL work such as collections, indexes, or other schema-shaping operations.

### Preconditions

- `MongoDBSpringDataTargetSystem` registration evidence is already confirmed.
- Use `"YOUR_TARGET_SYSTEM_ID"` when the target-system id is still unknown.
- `Language` may resolve to `java` or `kotlin`; default to Java if still unspecified.
- If author, package, or order are missing, keep the canonical placeholders instead of blocking.
- The change truly performs DDL (`createCollection`, `dropCollection`, `ensureIndex`, `dropIndex`, schema-shaping operations).

If details are still missing, generate the structure with placeholders and keep the request non-blocking.

### Transaction rule

Default DDL changes to non-transactional execution:

```java
@Change(id = "...", author = "...", transactional = false)
```

Prefer `MongoTemplate` only.

### Idempotency rule

Apply logic must be safe to re-run. Use guards such as:

- `collectionExists()` before `createCollection()` / `dropCollection()`
- `indexOps().ensureIndex(...)` for idempotent index creation
- equivalent already-exists checks when the user asks for lower-level schema work

### Rollback precision

- Roll back only the schema object owned by this change.
- If ownership is unclear, prefer a conservative rollback such as a no-op or a narrowly scoped compensation note instead of inventing a destructive rollback.
- Do not drop unrelated collections or indexes just because they match a broad pattern.

### Java path

```java
@TargetSystem(id = "YOUR_TARGET_SYSTEM_ID")
@Change(order = "TODO", id = "create-archive-collection", author = "TODO", transactional = false)
public class _ORDER__CreateArchiveCollection {

    @Apply
    public void apply(MongoTemplate mongoTemplate) {
        // guard already-exists conditions before mutating schema
    }

    @Rollback
    public void rollback(MongoTemplate mongoTemplate) {
        // compensate only the schema effect introduced by apply
    }
}
```

### Kotlin path

```kotlin
@TargetSystem(id = "YOUR_TARGET_SYSTEM_ID")
@Change(order = "TODO", id = "create-archive-collection", author = "TODO", transactional = false)
class _ORDER__CreateArchiveCollection {

    @Apply
    fun apply(mongoTemplate: MongoTemplate) {
        // guard already-exists conditions before mutating schema
    }

    @Rollback
    fun rollback(mongoTemplate: MongoTemplate) {
        // compensate only the schema effect introduced by apply
    }
}
```

### Copy-paste asset

Use only the asset that matches the resolved language:

- Java: `assets/example-ddl.java`
- Kotlin: `assets/example-ddl.kt`
