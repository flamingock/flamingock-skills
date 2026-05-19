## Standalone + Cloud

Use this reference when the request is about standalone + Cloud onboarding, or when those defaults are the best immediate fit.

### Preconditions

- `apiToken`, `environment`, and `service` values may use `"TODO"` placeholders during onboarding if the real values are not known yet.
- Do not create an AuditStore.

### Gradle

Use the Flamingock plugin without `community()`. Replace `[VERSION]` with the resolved version, or keep the placeholder temporarily if the version could not be resolved yet:

```kotlin
plugins {
    id("io.flamingock") version "[VERSION]"
}

flamingock {
    // Cloud edition
}
```

### Maven

Use the same BOM import and compiler plugin pattern as Community, but use the Cloud edition artifact explicitly. Keep `[VERSION]` only when the exact version is still unresolved and call it out to the user:

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
  <artifactId>flamingock-cloud</artifactId>
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

### Builder example

#### Java

```java
Flamingock.builder()
    .setApiToken("TODO")
    .setEnvironment("TODO")
    .setService("TODO")
    .build()
    .run();
```

#### Kotlin

```kotlin
Flamingock.builder()
    .setApiToken("TODO")
    .setEnvironment("TODO")
    .setService("TODO")
    .build()
    .run()
```

### `@EnableFlamingock` and `@Stage`

Place the annotation on the user-selected main class or dedicated config class.

If the user chose a dedicated config class, keep the annotation on that dedicated class and keep `main(...)` or app bootstrap separate.

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

#### Java — dedicated config class

```java
@EnableFlamingock(
    stages = { @Stage(location = "com.company.myapp.changes") }
)
public class FlamingockConfiguration {
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

#### Kotlin — dedicated config class

```kotlin
@EnableFlamingock(
    stages = [Stage(location = "com.company.myapp.changes")]
)
class FlamingockConfiguration
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

#### Java — dedicated config class

```java
@EnableFlamingock
public class FlamingockConfiguration {
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

#### Kotlin — dedicated config class

```kotlin
@EnableFlamingock
class FlamingockConfiguration
```

### No-AuditStore rule

- Do not call `setAuditStore(...)`.
- Do not create `XxxAuditStore.from(...)`.
- Keep the Cloud setup focused on token, environment, and service.
