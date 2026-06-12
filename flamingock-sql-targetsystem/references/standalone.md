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

### AuditStore is mandatory

`Flamingock.builder()...build()` fails with `BuilderException: AuditStore must be configured before running Flamingock` unless `setAuditStore(...)` is called. Wire `SqlAuditStore` via its static `from(targetSystem)` factory — the constructor is private; do NOT call `new SqlAuditStore(dataSource)`.

Import: `io.flamingock.store.sql.SqlAuditStore`.

### Java setup path

Use only the constructor proven by the Flamingock source:

```java
SqlTargetSystem sqlTargetSystem =
    new SqlTargetSystem("YOUR_TARGET_SYSTEM_ID", dataSource);

SqlAuditStore auditStore = SqlAuditStore.from(sqlTargetSystem);

Flamingock.builder()
    .setAuditStore(auditStore)
    .addTargetSystem(sqlTargetSystem)
    .build()
    .run();
```

If the project already has a builder chain, add `.setAuditStore(auditStore)` and `.addTargetSystem(sqlTargetSystem)` to that existing setup.

### Kotlin setup path

Use the same source-backed constructor, but keep the output Kotlin-only:

```kotlin
val sqlTargetSystem =
    SqlTargetSystem("YOUR_TARGET_SYSTEM_ID", dataSource)

val auditStore = SqlAuditStore.from(sqlTargetSystem)

Flamingock.builder()
    .setAuditStore(auditStore)
    .addTargetSystem(sqlTargetSystem)
    .build()
    .run()
```

If the project already has a builder chain, add `.setAuditStore(auditStore)` and `.addTargetSystem(sqlTargetSystem)` to that existing setup.
