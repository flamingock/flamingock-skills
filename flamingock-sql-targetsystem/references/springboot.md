## Spring Boot `SqlTargetSystem`

Use this reference when the request is clearly about Spring Boot wiring for `SqlTargetSystem`, or when Spring Boot is the best default fit.

### Preconditions

- A Spring-managed `DataSource` bean is available, or this skill generates it using the orphan-datasource rule.
- Use `"YOUR_TARGET_SYSTEM_ID"` when the target system id is still unknown.
- Use `"TODO"` when the JDBC URL or credentials are still unknown.

### Dependency guidance

#### Gradle

If the project uses the Flamingock Gradle plugin, make sure Spring Boot and SQL modules are enabled. In this plugin-first path, keep the `dependencies {}` block for the JDBC driver only:

```kotlin
flamingock {
    springboot()
    sql()
}

dependencies {
    implementation("org.postgresql:postgresql:[VERSION]")
}
```

Swap the JDBC driver if the user is on MySQL, Oracle, SQL Server, or another dialect.

#### Maven

Add Spring Boot integration, the SQL target-system module, and the JDBC driver:

```xml
<dependency>
    <groupId>io.flamingock</groupId>
    <artifactId>flamingock-springboot-integration</artifactId>
</dependency>
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
@Bean
public DataSource dataSource() {
    HikariConfig config = new HikariConfig();
    config.setJdbcUrl("TODO");
    config.setUsername("TODO");
    config.setPassword("TODO");
    return new HikariDataSource(config);
}
```

#### Kotlin

```kotlin
@Bean
fun dataSource(): DataSource {
    val config = HikariConfig()
    config.jdbcUrl = "TODO"
    config.username = "TODO"
    config.password = "TODO"
    return HikariDataSource(config)
}
```

### Java setup path

Register the target system as a bean and inject the existing `DataSource` bean:

```java
@Bean
public SqlTargetSystem sqlTargetSystem(DataSource dataSource) {
    return new SqlTargetSystem("YOUR_TARGET_SYSTEM_ID", dataSource);
}
```

### Kotlin setup path

Register the same target system as a Spring bean, but keep the code Kotlin-only:

```kotlin
@Bean
fun sqlTargetSystem(dataSource: DataSource): SqlTargetSystem {
    return SqlTargetSystem("YOUR_TARGET_SYSTEM_ID", dataSource)
}
```

Do not switch to builder registration in the Spring Boot path.
