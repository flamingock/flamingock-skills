## Spring Boot `DynamoDBTargetSystem`

Use this reference when the request is clearly about Spring Boot wiring for `DynamoDBTargetSystem`, or when Spring Boot is the best default fit.

### Preconditions

- A Spring-managed `DynamoDbClient` bean is available, or this skill generates it using the orphan-client rule.
- Use `"YOUR_TARGET_SYSTEM_ID"` when the target system id is still unknown.
- Use `"TODO"` when the AWS region is still unknown.

### Dependency guidance

#### Gradle

If the project uses the Flamingock Gradle plugin, make sure Spring Boot and DynamoDB modules are enabled. In this plugin-first path, keep the `dependencies {}` block for AWS SDK modules only:

```kotlin
flamingock {
    springboot()
    dynamodb()
}

dependencies {
    implementation("software.amazon.awssdk:dynamodb-enhanced:[VERSION]")
}
```

#### Maven

Add Spring Boot integration, the DynamoDB target-system module, and the AWS DynamoDB SDK:

```xml
<dependency>
    <groupId>io.flamingock</groupId>
    <artifactId>flamingock-springboot-integration</artifactId>
</dependency>
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
@Bean
public DynamoDbClient dynamoDbClient() {
    return DynamoDbClient.builder()
        .region(Region.of("TODO"))
        .build();
}
```

#### Kotlin

```kotlin
@Bean
fun dynamoDbClient(): DynamoDbClient {
    return DynamoDbClient.builder()
        .region(Region.of("TODO"))
        .build()
}
```

### Java setup path

Register the target system as a bean and inject the existing `DynamoDbClient` bean:

```java
@Bean
public DynamoDBTargetSystem dynamoTargetSystem(DynamoDbClient dynamoDbClient) {
    return new DynamoDBTargetSystem("YOUR_TARGET_SYSTEM_ID", dynamoDbClient);
}
```

### Kotlin setup path

Register the same target system as a Spring bean, but keep the code Kotlin-only:

```kotlin
@Bean
fun dynamoTargetSystem(dynamoDbClient: DynamoDbClient): DynamoDBTargetSystem {
    return DynamoDBTargetSystem("YOUR_TARGET_SYSTEM_ID", dynamoDbClient)
}
```

Do not switch to builder registration in the Spring Boot path.
