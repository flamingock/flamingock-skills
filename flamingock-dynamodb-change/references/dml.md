## Transactional write changes for `DynamoDBTargetSystem`

Use this reference when the request is clearly about DynamoDB write work such as put, update, delete, or transactional backfills.

### Preconditions

- `DynamoDBTargetSystem` registration evidence is already confirmed.
- Use `"YOUR_TARGET_SYSTEM_ID"` when the target-system id is still unknown.
- `Language` may resolve to `java` or `kotlin`; default to Java if still unspecified.
- If author, package, or order are missing, keep the canonical placeholders instead of blocking.
- The change truly performs write operations that fit DynamoDB transactions.

If details are still missing, generate the structure with placeholders and keep the request non-blocking.

### Source-backed facts

- `DynamoDBTargetSystem` injects `DynamoDbClient` into the target-system context.
- Flamingock also injects `TransactWriteItemsEnhancedRequest.Builder` for transactional write execution.
- DynamoDB transactions support write operations only. Reads do not belong in this transactional path.

### Dependency choice

Prefer the smallest dependency set that solves the task:

1. `DynamoDbClient` + `TransactWriteItemsEnhancedRequest.Builder` for transactional writes
2. Build the enhanced client inside the method only when table mapping is needed

Do not switch to unrelated persistence abstractions in this path.

### Transaction rule

Keep `transactional = true` by default for write changes. You normally do not need to write it explicitly.

### Rollback precision

- Roll back only the items created or modified by `@Apply`.
- Prefer exact partition/sort keys.
- Never use a broad delete/overwrite pattern that could destroy pre-existing application data.

### Java path

```java
@TargetSystem(id = "YOUR_TARGET_SYSTEM_ID")
@Change(order = "TODO", id = "seed-order-status", author = "TODO")
public class _ORDER__SeedOrderStatus {

    @Apply
    public void apply(DynamoDbClient client, TransactWriteItemsEnhancedRequest.Builder txBuilder) {
        // add DynamoDB write operations to txBuilder
    }

    @Rollback
    public void rollback(DynamoDbClient client, TransactWriteItemsEnhancedRequest.Builder txBuilder) {
        // undo only the items touched by apply
    }
}
```

### Kotlin path

```kotlin
@TargetSystem(id = "YOUR_TARGET_SYSTEM_ID")
@Change(order = "TODO", id = "seed-order-status", author = "TODO")
class _ORDER__SeedOrderStatus {

    @Apply
    fun apply(client: DynamoDbClient, txBuilder: TransactWriteItemsEnhancedRequest.Builder) {
        // add DynamoDB write operations to txBuilder
    }

    @Rollback
    fun rollback(client: DynamoDbClient, txBuilder: TransactWriteItemsEnhancedRequest.Builder) {
        // undo only the items touched by apply
    }
}
```

### Copy-paste asset

Use only the asset that matches the resolved language:

- Java: `assets/example-dml.java`
- Kotlin: `assets/example-dml.kt`
