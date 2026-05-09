## Standalone + Community

Use this reference after intake resolves to `standalone + community`.

### Preconditions

- Community AuditStore requires an AuditStore-capable TargetSystem: `mongodb-sync`, `sql/jdbc`, `dynamodb`, or `couchbase`.
- If the TargetSystem is not available yet, onboarding can still continue without `setAuditStore(...)` and without `addTargetSystem(...)` until that TargetSystem exists.
- `@Stage(location = "...")` is optional during onboarding. If there is no changes package yet, generate only `@EnableFlamingock`.

### Gradle

Use the Flamingock plugin so BOM, core, and annotation processor are handled by the plugin. Replace `[FLAMINGOCK_VERSION]` with the resolved version, or keep the placeholder temporarily if the version could not be resolved yet:

```kotlin
plugins {
    id("io.flamingock") version "[FLAMINGOCK_VERSION]"
}

flamingock {
    community()
    // Add the required module for the existing TargetSystem:
    // mongodb(), sql(), dynamodb(), or couchbase()
}
```

### Maven

Use BOM import, `flamingock-community`, and the annotation processor in `maven-compiler-plugin`. Keep `${flamingockVersion}` only when the exact version is still unresolved and call it out to the user:

#### Java

```xml
<dependencyManagement>
  <dependencies>
    <dependency>
      <groupId>io.flamingock</groupId>
      <artifactId>flamingock-bom</artifactId>
      <version>${flamingockVersion}</version>
      <type>pom</type>
      <scope>import</scope>
    </dependency>
  </dependencies>
</dependencyManagement>

<dependency>
  <groupId>io.flamingock</groupId>
  <artifactId>flamingock-community</artifactId>
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
            <version>${flamingockVersion}</version>
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
                        <version>${flamingockVersion}</version>
                    </annotationProcessorPath>
                </annotationProcessorPaths>
            </configuration>
        </execution>
    </executions>
</plugin>
```

If the TargetSystem does not exist yet, continue onboarding without AuditStore and leave TargetSystem creation for a separate step.

### Builder example

If the Community TargetSystem is already resolved, derive the AuditStore from it:

#### Java

```java
var auditStore = MongoDBSyncAuditStore.from(mongoTarget);

Flamingock.builder()
    .setAuditStore(auditStore)
    .addTargetSystem(mongoTarget)
    .build()
    .run();
```

#### Kotlin

```kotlin
val auditStore = MongoDBSyncAuditStore.from(mongoTarget)

Flamingock.builder()
    .setAuditStore(auditStore)
    .addTargetSystem(mongoTarget)
    .build()
    .run()
```

If the TargetSystem is not available yet, generate the onboarding skeleton first and add the TargetSystem plus AuditStore later:

#### Java

```java
Flamingock.builder()
    .build()
    .run();
```

#### Kotlin

```kotlin
Flamingock.builder()
    .build()
    .run()
```

### `@EnableFlamingock` and `@Stage`

When a changes package already exists:

#### Java

```java
@EnableFlamingock(
    stages = { @Stage(location = "com.company.myapp.changes") }
)
public class App {
    public static void main(String[] args) {
        // app startup
    }
}
```

#### Kotlin

```kotlin
@EnableFlamingock(
    stages = [Stage(location = "com.company.myapp.changes")]
)
class App {
    companion object {
        @JvmStatic
        fun main(args: Array<String>) {
            // app startup
        }
    }
}
```

When no changes package exists yet, omit the stage definition:

#### Java

```java
@EnableFlamingock
public class App {
    public static void main(String[] args) {
        // app startup
    }
}
```

#### Kotlin

```kotlin
@EnableFlamingock
class App {
    companion object {
        @JvmStatic
        fun main(args: Array<String>) {
            // app startup
        }
    }
}
```

### Community AuditStore guidance

Use the AuditStore factory that matches the chosen backend:

- MongoDB: `MongoDBSyncAuditStore.from(mongoTarget)`
- SQL/JDBC: `SqlAuditStore.from(sqlTarget)`
- DynamoDB: `DynamoDBAuditStore.from(dynamoTarget)`
- Couchbase: `CouchbaseAuditStore.from(couchbaseTarget)`

If the TargetSystem is not available yet, generate the onboarding skeleton without `setAuditStore(...)` and without `addTargetSystem(...)`, then tell the user to add both once the TargetSystem exists.

If the project only has a `NonTransactionalTargetSystem`, explain that Community still needs a separate AuditStore-capable TargetSystem and omit the AuditStore section.
