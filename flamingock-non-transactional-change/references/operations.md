## Unified operations guidance for `NonTransactionalTargetSystem`

Use this reference for any non-transactional change request against external systems without native transaction support, whether the work is resource setup, resource shaping, remote mutation, config seeding, or state backfill.

### Preconditions

- `NonTransactionalTargetSystem` registration evidence is already confirmed.
- Use `"YOUR_TARGET_SYSTEM_ID"` when the target-system id is still unknown.
- `Language` may resolve to `java` or `kotlin`; default to Java if still unspecified.
- If author, package, or order are missing, keep the canonical placeholders instead of blocking.
- The change operates on external state where compensation, retry safety, and idempotency matter more than ACID rollback.

If details are still missing, generate the structure with placeholders and keep the request non-blocking.

### Operational invariants

- `NonTransactionalTargetSystem` injects only what the user registered through `.addDependency()` / `.setProperty()`.
- There is no automatic transaction or session support.
- Every change in this skill must keep `transactional = false`.
- Recovery strategy must match idempotency reality, not wishful thinking.

### Dependency choice

Prefer the smallest dependency set that solves the task:

1. Registered dependency types by constructor/bean type
2. Registered named dependencies or properties via `@Named`

Do not invent undeclared dependencies in this path.

### Compensation and recovery rules

- Roll back only the resource or state introduced by `@Apply`.
- Prefer read-modify-write so existing remote state is preserved.
- If the operation is idempotent, consider `@Recovery(strategy = RecoveryStrategy.ALWAYS_RETRY)`.
- If the operation is not idempotent, keep the default manual intervention behavior.
- If ownership is unclear or rollback would destroy unrelated state, replace destructive rollback with a narrower compensation plan.

### Copy-paste asset

Choose the asset that matches the resolved language:

- Java: `assets/example.java`
- Kotlin: `assets/example.kt`

Treat that unified asset as the base template for setup, mutation, config seeding, or backfill work. Adapt the `@Apply`, `@Rollback`, and optional `@Recovery` logic to the specific operation while keeping the same non-transactional safeguards.

### Java setup-style example

```java
@TargetSystem(id = "YOUR_TARGET_SYSTEM_ID")
@Change(order = "TODO", id = "create-product-bucket", author = "TODO", transactional = false)
public class _ORDER__CreateProductBucket {

    @Apply
    public void apply(S3Client s3Client) {
        // guard already-exists conditions before provisioning
    }

    @Rollback
    public void rollback(S3Client s3Client) {
        // compensate only the resource introduced by apply
    }
}
```

### Java mutation-style example

```java
@TargetSystem(id = "YOUR_TARGET_SYSTEM_ID")
@Recovery(strategy = RecoveryStrategy.ALWAYS_RETRY)
@Change(order = "TODO", id = "tag-order-bucket", author = "TODO", transactional = false)
public class _ORDER__TagOrderBucket {

    @Apply
    public void apply(S3Client s3Client) {
        // mutate external state carefully
    }

    @Rollback
    public void rollback(S3Client s3Client) {
        // compensate only the state introduced by apply
    }
}
```

### Kotlin setup-style example

```kotlin
@TargetSystem(id = "YOUR_TARGET_SYSTEM_ID")
@Change(order = "TODO", id = "create-product-bucket", author = "TODO", transactional = false)
class _ORDER__CreateProductBucket {

    @Apply
    fun apply(s3Client: S3Client) {
        // guard already-exists conditions before provisioning
    }

    @Rollback
    fun rollback(s3Client: S3Client) {
        // compensate only the resource introduced by apply
    }
}
```

### Kotlin mutation-style example

```kotlin
@TargetSystem(id = "YOUR_TARGET_SYSTEM_ID")
@Recovery(strategy = RecoveryStrategy.ALWAYS_RETRY)
@Change(order = "TODO", id = "tag-order-bucket", author = "TODO", transactional = false)
class _ORDER__TagOrderBucket {

    @Apply
    fun apply(s3Client: S3Client) {
        // mutate external state carefully
    }

    @Rollback
    fun rollback(s3Client: S3Client) {
        // compensate only the state introduced by apply
    }
}
```
