## Transactional document changes for `CouchbaseTargetSystem`

Use this reference when the request is clearly about Couchbase document work such as insert, replace, remove, or transactional backfills.

### Preconditions

- `CouchbaseTargetSystem` registration evidence is already confirmed.
- Use `"YOUR_TARGET_SYSTEM_ID"` when the target-system id is still unknown.
- `Language` may resolve to `java` or `kotlin`; default to Java if still unspecified.
- If author, package, or order are missing, keep the canonical placeholders instead of blocking.
- The change truly performs document operations that belong inside a Couchbase transaction.

If details are still missing, generate the structure with placeholders and keep the request non-blocking.

### Source-backed facts

- `CouchbaseTargetSystem` injects `Cluster` and `Bucket` into the target-system context.
- Flamingock also injects `TransactionAttemptContext` for transactional document execution.
- Without `TransactionAttemptContext`, document work in this path falls outside the intended transaction boundary.

### Dependency choice

Prefer the smallest dependency set that solves the task:

1. `Bucket` + `TransactionAttemptContext` for most document work
2. Add `Cluster` when the example needs cluster-level coordination or managers

Do not switch to Mongo or SQL abstractions in this path.

### Transaction rule

Keep `transactional = true` by default for document changes. You normally do not need to write it explicitly.

### Rollback precision

- Roll back only the documents created or modified by `@Apply`.
- Prefer exact document ids or deterministic keys.
- Never use a broad rollback operation that could destroy pre-existing application data.

### Java path

```java
@TargetSystem(id = "YOUR_TARGET_SYSTEM_ID")
@Change(order = "TODO", id = "seed-order-status", author = "TODO")
public class _ORDER__SeedOrderStatus {

    @Apply
    public void apply(Cluster cluster, Bucket bucket, TransactionAttemptContext txContext) {
        // use txContext for document operations
    }

    @Rollback
    public void rollback(Cluster cluster, Bucket bucket, TransactionAttemptContext txContext) {
        // undo only the documents touched by apply
    }
}
```

### Kotlin path

```kotlin
@TargetSystem(id = "YOUR_TARGET_SYSTEM_ID")
@Change(order = "TODO", id = "seed-order-status", author = "TODO")
class _ORDER__SeedOrderStatus {

    @Apply
    fun apply(cluster: Cluster, bucket: Bucket, txContext: TransactionAttemptContext) {
        // use txContext for document operations
    }

    @Rollback
    fun rollback(cluster: Cluster, bucket: Bucket, txContext: TransactionAttemptContext) {
        // undo only the documents touched by apply
    }
}
```

### Copy-paste asset

Use only the asset that matches the resolved language:

- Java: `assets/example-dml.java`
- Kotlin: `assets/example-dml.kt`
