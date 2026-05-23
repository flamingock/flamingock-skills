## Spring Boot `NonTransactionalTargetSystem`

Use this reference when the request is clearly about Spring Boot wiring for `NonTransactionalTargetSystem`, or when Spring Boot is the best default fit.

### Preconditions

- The external-system client bean(s) or service object(s) either already exist or can be created in the project.
- Use `"YOUR_TARGET_SYSTEM_ID"` when the target system id is still unknown.
- Use `"TODO"` for named property values that are still unknown.

### Dependency guidance

#### Gradle

Keep Flamingock on the Community core path and add only the external SDKs needed by the user’s system:

```kotlin
flamingock {
    community()
}

dependencies {
    implementation("software.amazon.awssdk:s3:[VERSION]")
}
```

#### Maven

Add Flamingock core and the external SDK that matches the managed system:

```xml
<dependency>
    <groupId>io.flamingock</groupId>
    <artifactId>flamingock-community</artifactId>
</dependency>
<dependency>
    <groupId>software.amazon.awssdk</groupId>
    <artifactId>s3</artifactId>
    <version>[VERSION]</version>
</dependency>
```

### Dependency registration (example)

#### Java

```java
@Bean
public NonTransactionalTargetSystem s3TargetSystem(S3Client s3Client) {
    return new NonTransactionalTargetSystem("YOUR_TARGET_SYSTEM_ID")
        .addDependency(s3Client)
        .setProperty("bucket.name", "TODO");
}
```

#### Kotlin

```kotlin
@Bean
fun s3TargetSystem(s3Client: S3Client): NonTransactionalTargetSystem {
    return NonTransactionalTargetSystem("YOUR_TARGET_SYSTEM_ID")
        .addDependency(s3Client)
        .setProperty("bucket.name", "TODO")
}
```

### AuditStore warning

Do not present this target system as AuditStore-capable. If the app uses Community edition, a separate MongoDB, SQL, DynamoDB, or Couchbase target system is still needed for the AuditStore.
