## Spring Boot `CouchbaseTargetSystem`

Use this reference when the request is clearly about Spring Boot wiring for `CouchbaseTargetSystem`, or when Spring Boot is the best default fit.

### Preconditions

- A Spring-managed `Cluster` bean is available, or this skill generates it using the orphan-cluster rule.
- Use `"YOUR_TARGET_SYSTEM_ID"` when the target system id is still unknown.
- Use `"TODO"` when the bucket name or connection values are still unknown.

### Dependency guidance

#### Gradle

If the project uses the Flamingock Gradle plugin, make sure Spring Boot and Couchbase modules are enabled. In this plugin-first path, keep the `dependencies {}` block for Couchbase SDK modules only:

```kotlin
flamingock {
    springboot()
    couchbase()
}

dependencies {
    implementation("com.couchbase.client:java-client:[VERSION]")
}
```

#### Maven

Add Spring Boot integration, the Couchbase target-system module, and the Couchbase SDK:

```xml
<dependency>
    <groupId>io.flamingock</groupId>
    <artifactId>flamingock-springboot-integration</artifactId>
</dependency>
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
@Bean
public Cluster cluster() {
    return Cluster.connect("TODO", "TODO", "TODO");
}
```

#### Kotlin

```kotlin
@Bean
fun cluster(): Cluster {
    return Cluster.connect("TODO", "TODO", "TODO")
}
```

### Java setup path

Register the target system as a bean and inject the existing `Cluster` bean:

```java
@Bean
public CouchbaseTargetSystem couchbaseTargetSystem(Cluster cluster) {
    return new CouchbaseTargetSystem("YOUR_TARGET_SYSTEM_ID", cluster, "TODO");
}
```

### Kotlin setup path

Register the same target system as a Spring bean, but keep the code Kotlin-only:

```kotlin
@Bean
fun couchbaseTargetSystem(cluster: Cluster): CouchbaseTargetSystem {
    return CouchbaseTargetSystem("YOUR_TARGET_SYSTEM_ID", cluster, "TODO")
}
```

Do not switch to builder registration in the Spring Boot path.
