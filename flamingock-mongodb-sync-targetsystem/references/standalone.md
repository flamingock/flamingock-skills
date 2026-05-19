## Standalone `MongoDBSyncTargetSystem`

Use this reference when the request is clearly about standalone wiring for `MongoDBSyncTargetSystem`, or when standalone is the best default fit.

### Preconditions

- A `MongoClient` variable is available, or this skill generates it using the orphan-client rule.
- Use `"YOUR_TARGET_SYSTEM_ID"` when the target system id is still unknown.
- Use `"TODO"` when the database name or connection string is still unknown.

### Dependency guidance

#### Gradle

If the project uses the Flamingock Gradle plugin, make sure the Flamingock block includes the MongoDB module:

```kotlin
flamingock {
    mongodb()
}

dependencies {
    implementation("io.flamingock:flamingock-mongodb-sync-targetsystem")
}
```

Add `org.mongodb:mongodb-driver-sync` only when you are also creating the `MongoClient` in this setup path.

#### Maven

Add the MongoDB sync target-system module:

```xml
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
MongoClient mongoClient = MongoClients.create("TODO");
```

#### Kotlin

```kotlin
val mongoClient = MongoClients.create("TODO")
```

### Java setup path

Use only the constructor proven by the Flamingock source:

```java
MongoDBSyncTargetSystem mongoTargetSystem =
    new MongoDBSyncTargetSystem("YOUR_TARGET_SYSTEM_ID", mongoClient, "TODO");

Flamingock.builder()
    .addTargetSystem(mongoTargetSystem)
    .build()
    .run();
```

If the project already has a builder chain, add only the `.addTargetSystem(mongoTargetSystem)` step to that existing setup.

### Kotlin setup path

Use the same source-backed constructor, but keep the output Kotlin-only:

```kotlin
val mongoTargetSystem =
    MongoDBSyncTargetSystem("YOUR_TARGET_SYSTEM_ID", mongoClient, "TODO")

Flamingock.builder()
    .addTargetSystem(mongoTargetSystem)
    .build()
    .run()
```

If the project already has a builder chain, add only the `.addTargetSystem(mongoTargetSystem)` step to that existing setup.

### Optional concern tuning

Only add concern tuning when the user asks for it or already provides the values.

#### Java

The supported fluent options are only `withReadConcern`, `withReadPreference`, and `withWriteConcern`:

```java
MongoDBSyncTargetSystem mongoTargetSystem =
    new MongoDBSyncTargetSystem("YOUR_TARGET_SYSTEM_ID", mongoClient, "TODO")
        .withReadConcern(ReadConcern.MAJORITY)
        .withReadPreference(ReadPreference.primary())
        .withWriteConcern(WriteConcern.MAJORITY.withJournal(true));
```

#### Kotlin

Use the same three supported fluent options with Kotlin syntax:

```kotlin
val mongoTargetSystem =
    MongoDBSyncTargetSystem("YOUR_TARGET_SYSTEM_ID", mongoClient, "TODO")
        .withReadConcern(ReadConcern.MAJORITY)
        .withReadPreference(ReadPreference.primary())
        .withWriteConcern(WriteConcern.MAJORITY.withJournal(true))
```

Do not invent other builder/fluent options.
