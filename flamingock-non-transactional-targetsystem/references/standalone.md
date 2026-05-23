## Standalone `NonTransactionalTargetSystem`

Use this reference when the request is clearly about standalone wiring for `NonTransactionalTargetSystem`, or when standalone is the best default fit.

### Preconditions

- The external-system client or service object already exists, or this skill can generate an example for it.
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

### Registration example

#### Java

```java
NonTransactionalTargetSystem s3TargetSystem =
    new NonTransactionalTargetSystem("YOUR_TARGET_SYSTEM_ID")
        .addDependency(s3Client)
        .setProperty("bucket.name", "TODO");

Flamingock.builder()
    .addTargetSystem(s3TargetSystem)
    .build()
    .run();
```

#### Kotlin

```kotlin
val s3TargetSystem =
    NonTransactionalTargetSystem("YOUR_TARGET_SYSTEM_ID")
        .addDependency(s3Client)
        .setProperty("bucket.name", "TODO")

Flamingock.builder()
    .addTargetSystem(s3TargetSystem)
    .build()
    .run()
```

### AuditStore warning

Do not present this target system as AuditStore-capable. Community edition still needs a separate persistence-capable target system for the AuditStore.
