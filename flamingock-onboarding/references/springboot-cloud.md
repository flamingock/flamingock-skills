## Spring Boot + Cloud

Use this reference after intake resolves to `springboot + cloud`.

### Preconditions

- `apiToken`, environment name, and service name may use explicit placeholders such as `[FLAMINGOCK_API_TOKEN]`, `[FLAMINGOCK_ENVIRONMENT]`, and `[FLAMINGOCK_SERVICE]` during onboarding if the real values are not known yet.
- Do not create an AuditStore bean.

### Gradle

```kotlin
plugins {
    id("io.flamingock") version "[FLAMINGOCK_VERSION]"
}

flamingock {
    springboot()
}
```

Replace `[FLAMINGOCK_VERSION]` with the resolved version, or keep the placeholder temporarily if the version could not be resolved yet.

### Maven

Use the same structure as Spring Boot Community, but use the Cloud edition artifact explicitly. Keep `${flamingockVersion}` only when the exact version is still unresolved and call it out to the user:

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
  <artifactId>flamingock-cloud</artifactId>
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

### `@EnableFlamingock` placement

When a changes package already exists:

#### Java

```java
@EnableFlamingock(
    stages = { @Stage(location = "com.company.myapp.changes") }
)
@Configuration
public class FlamingockConfiguration {
}
```

#### Kotlin

```kotlin
@EnableFlamingock(
    stages = [Stage(location = "com.company.myapp.changes")]
)
@Configuration
class FlamingockConfiguration
```

When no changes package exists yet, omit the stage definition:

#### Java

```java
@EnableFlamingock
@Configuration
public class FlamingockConfiguration {
}
```

#### Kotlin

```kotlin
@EnableFlamingock
@Configuration
class FlamingockConfiguration
```

Place the annotation on the user-selected main class or dedicated config class.

### `application.yml`

```yaml
flamingock:
  apiToken: "[FLAMINGOCK_API_TOKEN]"
  environmentName: "[FLAMINGOCK_ENVIRONMENT]"
  serviceName: "[FLAMINGOCK_SERVICE]"
```

### No-AuditStore rule

- Do not register an `AuditStore` bean.
- Do not create `XxxAuditStore.from(...)`.
- Keep the setup focused on Spring Boot integration, `@EnableFlamingock`, and Cloud properties.
