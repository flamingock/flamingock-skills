## Non-transactional schema changes for `CouchbaseTargetSystem`

Use this reference when the request is clearly about Couchbase schema work such as collection creation, collection drop, or other non-transactional setup operations.

### Preconditions

- `CouchbaseTargetSystem` registration evidence is already confirmed.
- Use `"YOUR_TARGET_SYSTEM_ID"` when the target-system id is still unknown.
- `Language` may resolve to `java` or `kotlin`; default to Java if still unspecified.
- If author, package, or order are missing, keep the canonical placeholders instead of blocking.
- The change truly performs schema-shaping work outside Couchbase transaction support.

If details are still missing, generate the structure with placeholders and keep the request non-blocking.

### Transaction rule

Default these changes to non-transactional execution:

```java
@Change(id = "...", author = "...", transactional = false)
```

Prefer `Bucket` or `Cluster` without `TransactionAttemptContext`.

### Idempotency rule

Apply logic must be safe to re-run. Use guards such as:

- catch `CollectionExistsException` for `createCollection`
- inspect current collection/index state before mutating when needed

### Rollback precision

- Roll back only the collection/index owned by this change.
- If ownership is unclear, prefer a conservative rollback instead of inventing a destructive compensation.

### Java path

```java
@TargetSystem(id = "YOUR_TARGET_SYSTEM_ID")
@Change(order = "TODO", id = "create-archive-collection", author = "TODO", transactional = false)
public class _ORDER__CreateArchiveCollection {

    @Apply
    public void apply(Cluster cluster, Bucket bucket) {
        // guard already-exists conditions before mutating schema
    }

    @Rollback
    public void rollback(Cluster cluster, Bucket bucket) {
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
    fun apply(cluster: Cluster, bucket: Bucket) {
        // guard already-exists conditions before mutating schema
    }

    @Rollback
    fun rollback(cluster: Cluster, bucket: Bucket) {
        // compensate only the schema effect introduced by apply
    }
}
```

### Copy-paste asset

Use only the asset that matches the resolved language:

- Java: `assets/example-ddl.java`
- Kotlin: `assets/example-ddl.kt`
