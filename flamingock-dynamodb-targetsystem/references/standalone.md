## Standalone `DynamoDBTargetSystem`

Use this reference when the request is clearly about standalone wiring for `DynamoDBTargetSystem`, or when standalone is the best default fit.

### Preconditions

- A `DynamoDbClient` variable is available, or this skill generates it using the orphan-client rule.
- Use `"YOUR_TARGET_SYSTEM_ID"` when the target system id is still unknown.
- Use `"TODO"` when the AWS region is still unknown.

### Dependency guidance

#### Gradle

If the project uses the Flamingock Gradle plugin, make sure the Flamingock block includes the DynamoDB module. In this plugin-first path, keep the `dependencies {}` block for AWS SDK modules only:

```kotlin
flamingock {
    dynamodb()
}

dependencies {
    implementation("software.amazon.awssdk:dynamodb-enhanced:[VERSION]")
}
```

#### Maven

Add the DynamoDB target-system module and the AWS DynamoDB SDK:

```xml
<dependency>
    <groupId>io.flamingock</groupId>
    <artifactId>flamingock-dynamodb-targetsystem</artifactId>
</dependency>
<dependency>
    <groupId>software.amazon.awssdk</groupId>
    <artifactId>dynamodb-enhanced</artifactId>
    <version>[VERSION]</version>
</dependency>
```

### DynamoDbClient creation (if needed)

#### Java

```java
DynamoDbClient dynamoDbClient = DynamoDbClient.builder()
    .region(Region.of("TODO"))
    .build();
```

#### Kotlin

```kotlin
val dynamoDbClient = DynamoDbClient.builder()
    .region(Region.of("TODO"))
    .build()
```

### Java setup path

Use only the constructor proven by the Flamingock source:

```java
DynamoDBTargetSystem dynamoTargetSystem =
    new DynamoDBTargetSystem("YOUR_TARGET_SYSTEM_ID", dynamoDbClient);

Flamingock.builder()
    .addTargetSystem(dynamoTargetSystem)
    .build()
    .run();
```

If the project already has a builder chain, add only the `.addTargetSystem(dynamoTargetSystem)` step to that existing setup.

### Kotlin setup path

Use the same source-backed constructor, but keep the output Kotlin-only:

```kotlin
val dynamoTargetSystem =
    DynamoDBTargetSystem("YOUR_TARGET_SYSTEM_ID", dynamoDbClient)

Flamingock.builder()
    .addTargetSystem(dynamoTargetSystem)
    .build()
    .run()
```

If the project already has a builder chain, add only the `.addTargetSystem(dynamoTargetSystem)` step to that existing setup.
