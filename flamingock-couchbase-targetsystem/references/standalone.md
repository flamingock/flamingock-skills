## Standalone `CouchbaseTargetSystem`

Use this reference when the request is clearly about standalone wiring for `CouchbaseTargetSystem`, or when standalone is the best default fit.

### Preconditions

- A `Cluster` variable is available, or this skill generates it using the orphan-cluster rule.
- Use `"YOUR_TARGET_SYSTEM_ID"` when the target system id is still unknown.
- Use `"TODO"` when the bucket name or connection values are still unknown.

### Dependency guidance

#### Gradle

If the project uses the Flamingock Gradle plugin, make sure the Flamingock block includes the Couchbase module. In this plugin-first path, keep the `dependencies {}` block for Couchbase SDK modules only:

```kotlin
flamingock {
    couchbase()
}

dependencies {
    implementation("com.couchbase.client:java-client:[VERSION]")
}
```

#### Maven

Add the Couchbase target-system module and the Couchbase SDK:

```xml
<dependency>
    <groupId>io.flamingock</groupId>
    <artifactId>flamingock-couchbase-targetsystem</artifactId>
</dependency>
<dependency>
    <groupId>com.couchbase.client</groupId>
    <artifactId>java-client</artifactId>
    <version>[VERSION]</version>
</dependency>
```

### Cluster creation (if needed)

#### Java

```java
Cluster cluster = Cluster.connect("TODO", "TODO", "TODO");
```

#### Kotlin

```kotlin
val cluster = Cluster.connect("TODO", "TODO", "TODO")
```

### Java setup path

Use only the constructor proven by the Flamingock source:

```java
CouchbaseTargetSystem couchbaseTargetSystem =
    new CouchbaseTargetSystem("YOUR_TARGET_SYSTEM_ID", cluster, "TODO");

Flamingock.builder()
    .addTargetSystem(couchbaseTargetSystem)
    .build()
    .run();
```

If the project already has a builder chain, add only the `.addTargetSystem(couchbaseTargetSystem)` step to that existing setup.

### Kotlin setup path

Use the same source-backed constructor, but keep the output Kotlin-only:

```kotlin
val couchbaseTargetSystem =
    CouchbaseTargetSystem("YOUR_TARGET_SYSTEM_ID", cluster, "TODO")

Flamingock.builder()
    .addTargetSystem(couchbaseTargetSystem)
    .build()
    .run()
```

If the project already has a builder chain, add only the `.addTargetSystem(couchbaseTargetSystem)` step to that existing setup.
