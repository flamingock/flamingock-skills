## Non-transactional table management for `DynamoDBTargetSystem`

Use this reference when the request is clearly about DynamoDB table-management work such as create table, delete table, or other non-transactional schema-shaping operations.

### Preconditions

- `DynamoDBTargetSystem` registration evidence is already confirmed.
- Use `"YOUR_TARGET_SYSTEM_ID"` when the target-system id is still unknown.
- `Language` may resolve to `java` or `kotlin`; default to Java if still unspecified.
- If author, package, or order are missing, keep the canonical placeholders instead of blocking.
- The change truly performs table-management or read-oriented work outside DynamoDB transaction support.

If details are still missing, generate the structure with placeholders and keep the request non-blocking.

### Transaction rule

Default these changes to non-transactional execution:

```java
@Change(id = "...", author = "...", transactional = false)
```

Prefer `DynamoDbClient` only.

### Idempotency rule

Apply logic must be safe to re-run. Use guards such as:

- catch `ResourceInUseException` for `CreateTable`
- catch `ResourceNotFoundException` for `DeleteTable`
- check current table state before mutating when the request needs tighter control

### Rollback precision

- Roll back only the table or resource owned by this change.
- If ownership is unclear, prefer a conservative rollback instead of inventing a destructive compensation.

### Java path

```java
@TargetSystem(id = "YOUR_TARGET_SYSTEM_ID")
@Change(order = "TODO", id = "create-archive-table", author = "TODO", transactional = false)
public class _ORDER__CreateArchiveTable {

    @Apply
    public void apply(DynamoDbClient client) {
        // guard already-exists conditions before mutating table state
    }

    @Rollback
    public void rollback(DynamoDbClient client) {
        // compensate only the table effect introduced by apply
    }
}
```

### Kotlin path

```kotlin
@TargetSystem(id = "YOUR_TARGET_SYSTEM_ID")
@Change(order = "TODO", id = "create-archive-table", author = "TODO", transactional = false)
class _ORDER__CreateArchiveTable {

    @Apply
    fun apply(client: DynamoDbClient) {
        // guard already-exists conditions before mutating table state
    }

    @Rollback
    fun rollback(client: DynamoDbClient) {
        // compensate only the table effect introduced by apply
    }
}
```

### Copy-paste asset

Use only the asset that matches the resolved language:

- Java: `assets/example-ddl.java`
- Kotlin: `assets/example-ddl.kt`
