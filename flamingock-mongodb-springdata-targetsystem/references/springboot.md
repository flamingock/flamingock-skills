## Spring Boot `MongoDBSpringDataTargetSystem`

Use this reference when the request is clearly about Spring Boot wiring for `MongoDBSpringDataTargetSystem`, or when Spring Boot is the best default fit.

### Preconditions

- A Spring-managed `MongoTemplate` bean is available, or Spring Boot autoconfiguration can provide it.
- Use `"YOUR_TARGET_SYSTEM_ID"` when the target system id is still unknown.

### Dependency guidance

#### Gradle

If the project uses the Flamingock Gradle plugin, make sure Spring Boot and MongoDB modules are enabled. In this plugin-first path, keep the `dependencies {}` block for Spring Data MongoDB only:

```kotlin
flamingock {
    springboot()
    mongodb()
}

dependencies {
    implementation("org.springframework.boot:spring-boot-starter-data-mongodb")
}
```

#### Maven

Add Spring Boot integration, the Spring Data target-system module, and Spring Data MongoDB:

```xml
<dependency>
    <groupId>io.flamingock</groupId>
    <artifactId>flamingock-springboot-integration</artifactId>
</dependency>
<dependency>
    <groupId>io.flamingock</groupId>
    <artifactId>flamingock-mongodb-springdata-targetsystem</artifactId>
</dependency>
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-data-mongodb</artifactId>
</dependency>
```

### MongoTemplate creation (if needed)

Prefer Spring Boot autoconfiguration when the starter is present. Only add an explicit bean when the project already exposes Spring Data primitives.

#### Java

```java
@Bean
public MongoTemplate mongoTemplate(
    MongoDatabaseFactory mongoDatabaseFactory,
    MappingMongoConverter mappingMongoConverter
) {
    return new MongoTemplate(mongoDatabaseFactory, mappingMongoConverter);
}
```

#### Kotlin

```kotlin
@Bean
fun mongoTemplate(
    mongoDatabaseFactory: MongoDatabaseFactory,
    mappingMongoConverter: MappingMongoConverter
): MongoTemplate {
    return MongoTemplate(mongoDatabaseFactory, mappingMongoConverter)
}
```

### Java setup path

Register the target system as a bean and inject the existing `MongoTemplate` bean:

```java
@Bean
public MongoDBSpringDataTargetSystem mongoTargetSystem(MongoTemplate mongoTemplate) {
    return new MongoDBSpringDataTargetSystem("YOUR_TARGET_SYSTEM_ID", mongoTemplate);
}
```

### Kotlin setup path

Register the same target system as a Spring bean, but keep the code Kotlin-only:

```kotlin
@Bean
fun mongoTargetSystem(mongoTemplate: MongoTemplate): MongoDBSpringDataTargetSystem {
    return MongoDBSpringDataTargetSystem("YOUR_TARGET_SYSTEM_ID", mongoTemplate)
}
```

### Optional concern tuning

If the user asks for audit-store wiring, keep it Spring Data based:

```java
@Bean
public AuditStore auditStore(MongoDBSpringDataTargetSystem mongoTargetSystem) {
    return MongoDBSyncAuditStore.from(mongoTargetSystem);
}
```

Do not switch this path to raw sync-driver setup.
