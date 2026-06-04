## MongoDB Spring Data migration branch

Use this reference when the legacy Mongock setup is Spring Boot + Spring Data MongoDB and migration reasoning should stay on `MongoTemplate` semantics.

### Origin mapping
- Keep the Flamingock migration on the MongoDB Spring Data path, not the raw sync-driver path.
- Treat `origin` with the same Mongo-backed audit-location model used by the legacy setup.
- If the target system already points to the same legacy Mongo source, `origin` can stay implicit.
- If the legacy audit location differs, make `origin` explicit instead of guessing.

### Audit import notes
- Import still runs first unless `skipImport = true`.
- Strict defaults remain in force: empty origin fails by default and unknown imported entries fail by default.
- Keep the answer Spring Data-oriented; do not fall back to raw `MongoClient` / `ClientSession` guidance here.

### Routing boundary
- This branch is Spring Boot only.
- For target-system wiring after migration fit -> `flamingock-mongodb-springdata-targetsystem`
- For new native Spring Data changes after migration fit -> `flamingock-mongodb-springdata-change`

### Watch-outs
- If the user asks for standalone wiring or low-level sync-driver code, do not invent a Spring Data workaround. Re-check whether the correct branch is actually MongoDB sync.
- Do not duplicate the whole migration workflow here; keep it migration-only and target-specific.

### Verified package paths

```java
import io.flamingock.api.annotations.EnableFlamingock;
import io.flamingock.store.mongodb.sync.MongoDBSyncAuditStore;   // Spring Data reuses the sync AuditStore
import io.flamingock.support.mongock.annotations.MongockSupport;
import io.flamingock.targetsystem.mongodb.springdata.MongoDBSpringDataTargetSystem;
```

### Required Gradle plugin modules

```kotlin
flamingock {
    community()
    mongodb()
    springboot()
    mongock()   // mandatory for @MongockSupport
}
```

### AuditStore is mandatory

Spring Boot autoconfig (`flamingock-springboot-integration`) injects an existing `CommunityAuditStore` bean but does NOT create one. Without a declared bean the Flamingock builder fails with `BuilderException: AuditStore must be configured before running Flamingock`. Wire `MongoDBSyncAuditStore.from(targetSystem)` as a `@Bean` — constructor is private; do NOT call `new MongoDBSyncAuditStore(...)`.

### Full Spring Boot migration template (Java)

Replace `[VERSION]` placeholders with the latest available releases (resolve Flamingock via `<flamingock-skills-root>/flamingock-onboarding/scripts/last-version.sh`).

`build.gradle.kts`:

```kotlin
plugins {
    java
    id("org.springframework.boot") version "[SPRING_BOOT_VERSION]"
    id("io.spring.dependency-management") version "[SPRING_DEPMGMT_VERSION]"
    id("io.flamingock") version "[VERSION]"
}

repositories { mavenCentral() }

flamingock {
    community()
    mongodb()
    springboot()
    mongock()
}

dependencies {
    implementation("org.springframework.boot:spring-boot-starter-data-mongodb")
}
```

`Application.java`:

```java
import io.flamingock.api.annotations.EnableFlamingock;
import io.flamingock.support.mongock.annotations.MongockSupport;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@EnableFlamingock
@MongockSupport(targetSystem = "mongo-demo")
public class Application {
    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }
}
```

`FlamingockConfig.java`:

```java
import io.flamingock.store.mongodb.sync.MongoDBSyncAuditStore;
import io.flamingock.targetsystem.mongodb.springdata.MongoDBSpringDataTargetSystem;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.core.MongoTemplate;

@Configuration
public class FlamingockConfig {

    @Bean
    public MongoDBSpringDataTargetSystem mongoTargetSystem(MongoTemplate mongoTemplate) {
        return new MongoDBSpringDataTargetSystem("mongo-demo", mongoTemplate);
    }

    @Bean
    public MongoDBSyncAuditStore auditStore(MongoDBSpringDataTargetSystem mongoTargetSystem) {
        return MongoDBSyncAuditStore.from(mongoTargetSystem);
    }
}
```

### Stage rule when only legacy changes exist

If the project has zero native Flamingock `@Change` classes and only legacy Mongock `@ChangeUnit` classes:

- Use bare `@EnableFlamingock` — do NOT add `@Stage(location = "...")`.
- Legacy `@ChangeUnit` classes go to the auto-generated `flamingock-legacy-stage`; user-declared `@Stage` is treated as NATIVE-only and requires at least one native `@Change`, otherwise pipeline validation fails with `Stage[X] must contain at least one change`.
