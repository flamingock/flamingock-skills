## Spring Boot `MongoDBSyncTargetSystem`

Use this reference when the request is clearly about Spring Boot wiring for `MongoDBSyncTargetSystem`, or when Spring Boot is the best default fit.

### Preconditions

- A Spring-managed `MongoClient` bean is available, or this skill generates it using the orphan-client rule.
- Use `"YOUR_TARGET_SYSTEM_ID"` when the target system id is still unknown.
- Use `"TODO"` when the database name or connection string is still unknown.

### Dependency guidance

#### Gradle

If the project uses the Flamingock Gradle plugin, make sure Spring Boot and MongoDB modules are enabled. In this plugin-first path, do not add Flamingock artifacts again in `dependencies { ... }`:

```kotlin
flamingock {
    springboot()
    mongodb()
}
```

Add `org.mongodb:mongodb-driver-sync` only when you are also creating the `MongoClient` in this setup path:

```kotlin
dependencies {
    implementation("org.mongodb:mongodb-driver-sync:[VERSION]")
}
```

#### Maven

Add Spring Boot integration and the MongoDB sync target-system module:

```xml
<dependency>
    <groupId>io.flamingock</groupId>
    <artifactId>flamingock-springboot-integration</artifactId>
</dependency>
<dependency>
    <groupId>io.flamingock</groupId>
    <artifactId>flamingock-mongodb-sync-targetsystem</artifactId>
</dependency>
```

Add the MongoDB sync driver only when you are also creating the `MongoClient` in this setup path:

```xml
<dependency>
    <groupId>org.mongodb</groupId>
    <artifactId>mongodb-driver-sync</artifactId>
    <version>[VERSION]</version>
</dependency>
```

### MongoClient creation (if needed)

#### Java

```java
@Bean
public MongoClient mongoClient() {
    return MongoClients.create("TODO");
}
```

#### Kotlin

```kotlin
@Bean
fun mongoClient(): MongoClient {
    return MongoClients.create("TODO")
}
```

### Java setup path

Register the target system as a bean and inject the existing `MongoClient` bean:

```java
@Bean
public MongoDBSyncTargetSystem mongoTargetSystem(MongoClient mongoClient) {
    return new MongoDBSyncTargetSystem("YOUR_TARGET_SYSTEM_ID", mongoClient, "TODO");
}
```

### Kotlin setup path

Register the same target system as a Spring bean, but keep the code Kotlin-only:

```kotlin
@Bean
fun mongoTargetSystem(mongoClient: MongoClient): MongoDBSyncTargetSystem {
    return MongoDBSyncTargetSystem("YOUR_TARGET_SYSTEM_ID", mongoClient, "TODO")
}
```

### Optional concern tuning

Only add concern tuning when the user asks for it or already provides the values.

#### Java

The supported fluent options are only `withReadConcern`, `withReadPreference`, and `withWriteConcern`:

```java
@Bean
public MongoDBSyncTargetSystem mongoTargetSystem(MongoClient mongoClient) {
    return new MongoDBSyncTargetSystem("YOUR_TARGET_SYSTEM_ID", mongoClient, "TODO")
        .withReadConcern(ReadConcern.MAJORITY)
        .withReadPreference(ReadPreference.primary())
        .withWriteConcern(WriteConcern.MAJORITY.withJournal(true));
}
```

#### Kotlin

Use the same three supported fluent options with Kotlin syntax:

```kotlin
@Bean
fun mongoTargetSystem(mongoClient: MongoClient): MongoDBSyncTargetSystem {
    return MongoDBSyncTargetSystem("YOUR_TARGET_SYSTEM_ID", mongoClient, "TODO")
        .withReadConcern(ReadConcern.MAJORITY)
        .withReadPreference(ReadPreference.primary())
        .withWriteConcern(WriteConcern.MAJORITY.withJournal(true))
}
```

Do not switch to builder registration in the Spring Boot path.
