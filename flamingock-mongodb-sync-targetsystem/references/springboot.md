## Spring Boot `MongoDBSyncTargetSystem`

Use this reference only when intake resolves to `runtime = springboot`.

### Preconditions

- A Spring-managed `MongoClient` bean is available (either existing or to be created).
- A concrete target system id is known (or use a placeholder).
- A concrete database name is known (or use a placeholder).

### Dependency guidance

#### Gradle

If the project uses the Flamingock Gradle plugin, make sure Spring Boot and MongoDB modules are enabled:

```kotlin
flamingock {
    springboot()
    mongodb()
}

dependencies {
    implementation("io.flamingock:flamingock-springboot-integration")
    implementation("io.flamingock:flamingock-mongodb-sync-targetsystem")
}
```

Add `org.mongodb:mongodb-driver-sync` only when you are also creating the `MongoClient` in this setup path.

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
    <version>4.0.0</version>
</dependency>
```

### MongoClient creation (if needed)

#### Java

```java
@Bean
public MongoClient mongoClient() {
    return MongoClients.create("[MONGODB_CONNECTION_STRING]");
}
```

#### Kotlin

```kotlin
@Bean
fun mongoClient(): MongoClient {
    return MongoClients.create("[MONGODB_CONNECTION_STRING]")
}
```

### Java setup path

Register the target system as a bean and inject the existing `MongoClient` bean:

```java
@Bean
public MongoDBSyncTargetSystem mongoTargetSystem(MongoClient mongoClient) {
    return new MongoDBSyncTargetSystem("user-database-id", mongoClient, "userDb");
}
```

### Kotlin setup path

Register the same target system as a Spring bean, but keep the code Kotlin-only:

```kotlin
@Bean
fun mongoTargetSystem(mongoClient: MongoClient): MongoDBSyncTargetSystem {
    return MongoDBSyncTargetSystem("user-database-id", mongoClient, "userDb")
}
```

### Optional concern tuning

Only add concern tuning when the user asks for it or already provides the values.

#### Java

The supported fluent options are only `withReadConcern`, `withReadPreference`, and `withWriteConcern`:

```java
@Bean
public MongoDBSyncTargetSystem mongoTargetSystem(MongoClient mongoClient) {
    return new MongoDBSyncTargetSystem("user-database-id", mongoClient, "userDb")
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
    return MongoDBSyncTargetSystem("user-database-id", mongoClient, "userDb")
        .withReadConcern(ReadConcern.MAJORITY)
        .withReadPreference(ReadPreference.primary())
        .withWriteConcern(WriteConcern.MAJORITY.withJournal(true))
}
```

Do not switch to builder registration in the Spring Boot path.
