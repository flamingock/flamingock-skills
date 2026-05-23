## Standalone `SqlTargetSystem`

Use this reference when the request is clearly about standalone wiring for `SqlTargetSystem`, or when standalone is the best default fit.

### Preconditions

- A `DataSource` variable is available, or this skill generates it using the orphan-datasource rule.
- Use `"YOUR_TARGET_SYSTEM_ID"` when the target system id is still unknown.
- Use `"TODO"` when the JDBC URL or credentials are still unknown.

### Dependency guidance

#### Gradle

If the project uses the Flamingock Gradle plugin, make sure the Flamingock block includes the SQL module. In this plugin-first path, keep the `dependencies {}` block for the JDBC driver only:

```kotlin
flamingock {
    sql()
}

dependencies {
    implementation("org.postgresql:postgresql:[VERSION]")
}
```

#### Maven

Add the SQL target-system module and the JDBC driver:

```xml
<dependency>
    <groupId>io.flamingock</groupId>
    <artifactId>flamingock-sql-targetsystem</artifactId>
</dependency>
<dependency>
    <groupId>org.postgresql</groupId>
    <artifactId>postgresql</artifactId>
    <version>[VERSION]</version>
</dependency>
```

### DataSource creation (if needed)

#### Java

```java
HikariConfig config = new HikariConfig();
config.setJdbcUrl("TODO");
config.setUsername("TODO");
config.setPassword("TODO");
DataSource dataSource = new HikariDataSource(config);
```

#### Kotlin

```kotlin
val config = HikariConfig().apply {
    jdbcUrl = "TODO"
    username = "TODO"
    password = "TODO"
}
val dataSource: DataSource = HikariDataSource(config)
```

### Java setup path

Use only the constructor proven by the Flamingock source:

```java
SqlTargetSystem sqlTargetSystem =
    new SqlTargetSystem("YOUR_TARGET_SYSTEM_ID", dataSource);

Flamingock.builder()
    .addTargetSystem(sqlTargetSystem)
    .build()
    .run();
```

If the project already has a builder chain, add only the `.addTargetSystem(sqlTargetSystem)` step to that existing setup.

### Kotlin setup path

Use the same source-backed constructor, but keep the output Kotlin-only:

```kotlin
val sqlTargetSystem =
    SqlTargetSystem("YOUR_TARGET_SYSTEM_ID", dataSource)

Flamingock.builder()
    .addTargetSystem(sqlTargetSystem)
    .build()
    .run()
```

If the project already has a builder chain, add only the `.addTargetSystem(sqlTargetSystem)` step to that existing setup.
