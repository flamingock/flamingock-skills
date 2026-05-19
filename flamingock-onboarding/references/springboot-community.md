## Spring Boot + Community

Use this reference when the request is about Spring Boot + Community onboarding, or when those defaults are the best immediate fit.

### Preconditions

- The chosen AuditStore backend is `mongodb-sync`, `mongodb-springdata`, `sql/jdbc`, `dynamodb`, or `couchbase` when the AuditStore section is emitted.
- If the TargetSystem bean is not available yet, onboarding can still continue without `AuditStore` until that TargetSystem bean exists.
- `@Stage(location = "...")` is optional during onboarding. If there is no changes package yet, generate only `@EnableFlamingock`.

### Gradle

```kotlin
plugins {
    id("io.flamingock") version "[VERSION]"
}

flamingock {
    community()
    springboot()
    // Add the required module for the existing TargetSystem:
    // mongodb(), sql(), dynamodb(), or couchbase()
}
```

Replace `[VERSION]` with the resolved version, or keep the placeholder temporarily if the version could not be resolved yet.

### Maven

Keep `[VERSION]` only when the exact version is still unresolved and call it out to the user:

#### Java

```xml
<dependencyManagement>
  <dependencies>
    <dependency>
      <groupId>io.flamingock</groupId>
      <artifactId>flamingock-bom</artifactId>
      <version>[VERSION]</version>
      <type>pom</type>
      <scope>import</scope>
    </dependency>
  </dependencies>
</dependencyManagement>

<dependency>
  <groupId>io.flamingock</groupId>
  <artifactId>flamingock-community</artifactId>
</dependency>

<dependency>
  <groupId>io.flamingock</groupId>
  <artifactId>flamingock-springboot-integration</artifactId>
</dependency>

<build>
  <plugins>
    <plugin>
      <groupId>org.apache.maven.plugins</groupId>
      <artifactId>maven-compiler-plugin</artifactId>
      <version>3.11.0</version>
      <configuration>
        <annotationProcessorPaths>
          <path>
            <groupId>io.flamingock</groupId>
            <artifactId>flamingock-processor</artifactId>
            <version>[VERSION]</version>
          </path>
        </annotationProcessorPaths>
      </configuration>
    </plugin>
  </plugins>
</build>
```

#### Kotlin (kapt)

```xml
<plugin>
    <groupId>org.jetbrains.kotlin</groupId>
    <artifactId>kotlin-maven-plugin</artifactId>
    <executions>
        <execution>
            <id>kapt</id>
            <goals><goal>kapt</goal></goals>
            <configuration>
                <annotationProcessorPaths>
                    <annotationProcessorPath>
                        <groupId>io.flamingock</groupId>
                        <artifactId>flamingock-processor</artifactId>
                        <version>[VERSION]</version>
                    </annotationProcessorPath>
                </annotationProcessorPaths>
            </configuration>
        </execution>
    </executions>
</plugin>
```

If the TargetSystem bean does not exist yet, continue onboarding without `AuditStore` and leave that TargetSystem creation for a separate step.

### `@EnableFlamingock` activation

Place the annotation on the user-selected main class or dedicated config class.

If the user chose a dedicated config class, keep `@EnableFlamingock` there and keep the `@SpringBootApplication` main class unchanged.

When a changes package already exists:

#### Java

```java
@EnableFlamingock(
    stages = { @Stage(location = "com.company.myapp.changes") }
)
@SpringBootApplication
public class MyApplication {
    public static void main(String[] args) {
        SpringApplication.run(MyApplication.class, args);
    }
}
```

#### Kotlin

```kotlin
@EnableFlamingock(
    stages = [Stage(location = "com.company.myapp.changes")]
)
@SpringBootApplication
class MyApplication

fun main(args: Array<String>) {
    runApplication<MyApplication>(*args)
}
```

#### Java — dedicated config class

```java
@Configuration
@EnableFlamingock(
    stages = { @Stage(location = "com.company.myapp.changes") }
)
public class FlamingockConfiguration {
}
```

#### Kotlin — dedicated config class

```kotlin
@Configuration
@EnableFlamingock(
    stages = [Stage(location = "com.company.myapp.changes")]
)
class FlamingockConfiguration
```

When no changes package exists yet, omit the stage definition:

#### Java

```java
@EnableFlamingock
@SpringBootApplication
public class MyApplication {
    public static void main(String[] args) {
        SpringApplication.run(MyApplication.class, args);
    }
}
```

#### Kotlin

```kotlin
@EnableFlamingock
@SpringBootApplication
class MyApplication

fun main(args: Array<String>) {
    runApplication<MyApplication>(*args)
}
```

#### Java — dedicated config class

```java
@Configuration
@EnableFlamingock
public class FlamingockConfiguration {
}
```

#### Kotlin — dedicated config class

```kotlin
@Configuration
@EnableFlamingock
class FlamingockConfiguration
```

### AuditStore bean

If the TargetSystem bean is already resolved elsewhere in the project, derive the AuditStore from it here:

#### Java

MongoDB example:

```java
@Bean
public AuditStore auditStore(MongoDBSyncTargetSystem mongoTargetSystem) {
    return MongoDBSyncAuditStore.from(mongoTargetSystem);
}
```

SQL example:

```java
@Bean
public AuditStore auditStore(SqlTargetSystem sqlTargetSystem) {
    return SqlAuditStore.from(sqlTargetSystem);
}
```

Use the corresponding `XxxAuditStore.from(...)` factory for DynamoDB or Couchbase when those are the chosen backends.

For `mongodb-springdata`, follow the same pattern: inject the existing `MongoDBSpringDataTargetSystem` bean and derive the AuditStore from it with the matching `XxxAuditStore.from(...)` factory.

#### Kotlin

MongoDB example:

```kotlin
@Bean
fun auditStore(mongoTargetSystem: MongoDBSyncTargetSystem): AuditStore =
    MongoDBSyncAuditStore.from(mongoTargetSystem)
```

SQL example:

```kotlin
@Bean
fun auditStore(sqlTargetSystem: SqlTargetSystem): AuditStore =
    SqlAuditStore.from(sqlTargetSystem)
```

If the TargetSystem bean is not available yet, do not emit `AuditStore` in this onboarding step. Generate the activation, Flamingock dependencies, and `application.yml` first, then derive the `AuditStore` later once that bean exists.

### `application.yml`

```yaml
flamingock:
  lockAcquiredForMillis: 1200
  managementMode: INITIALIZING_BEAN
```

If the TargetSystem bean does not exist yet, keep the onboarding output focused on Flamingock dependencies, activation, and `application.yml`.
