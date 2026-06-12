## DynamoDB migration branch

Use this reference when the legacy Mongock backend is DynamoDB.

### Origin mapping
- Keep the Flamingock migration on the DynamoDB backend family.
- Treat `origin` as the legacy DynamoDB audit location expected by the migration path.
- If the legacy audit table or equivalent location is unclear, ask for confirmation instead of inventing one.

### Audit import notes
- Import happens before pending legacy Mongock changes unless `skipImport = true`.
- Strict defaults still apply: empty origin fails unless explicitly allowed, and unknown imported entries fail unless explicitly ignored.
- **Empty/absent origin (the legacy audit table) makes the first run fail** with `FlamingockException: No audit entries found when importing from '<origin>'.` This hits clean environments (fresh table, CI, new region) even though prod/staging with real history succeed. Surface this in final notes and present `emptyOriginAllowed = "true"` (or a placeholder) as the lever. See the "Empty-origin runtime gap" section in `SKILL.md` for the full options and trade-offs.
- Keep the explanation DynamoDB-specific; do not drift into Mongo or SQL assumptions.

### Routing boundary
- Use this branch to decide migration fit, flags, and blockers for DynamoDB-backed Mongock.
- For target-system wiring after migration fit -> `flamingock-dynamodb-targetsystem`
- For new native Flamingock changes after migration fit -> `flamingock-dynamodb-change`

### Watch-outs
- If the user cannot identify the legacy DynamoDB audit location, do not hide that uncertainty behind `emptyOriginAllowed = true`.
- Keep the branch lightweight; the main workflow stays in `SKILL.md`.

### Verified package paths

```java
import io.flamingock.api.annotations.EnableFlamingock;
import io.flamingock.community.Flamingock;
import io.flamingock.store.dynamodb.DynamoDBAuditStore;
import io.flamingock.support.mongock.annotations.MongockSupport;
import io.flamingock.targetsystem.dynamodb.DynamoDBTargetSystem;
```

### Required Gradle plugin modules

```kotlin
flamingock {
    community()
    dynamodb()
    mongock()   // mandatory for @MongockSupport
}
```

### AuditStore is mandatory

`Flamingock.builder()...build()` fails with `BuilderException: AuditStore must be configured before running Flamingock` unless `setAuditStore(...)` is called. Wire `DynamoDBAuditStore` via its static `from(targetSystem)` factory — constructor is private; do NOT call `new DynamoDBAuditStore(client)`.

### Full standalone migration template (Java)

Replace `[VERSION]` with the latest Flamingock release (resolve via `<flamingock-skills-root>/flamingock-onboarding/scripts/last-version.sh`).

`build.gradle.kts`:

```kotlin
plugins {
    java
    application
    id("io.flamingock") version "[VERSION]"
}

repositories { mavenCentral() }

flamingock {
    community()
    dynamodb()
    mongock()
}

dependencies {
    implementation("software.amazon.awssdk:dynamodb-enhanced:[AWS_SDK_VERSION]")
    implementation("org.slf4j:slf4j-simple:[SLF4J_VERSION]")
}
```

`App.java`:

```java
import io.flamingock.api.annotations.EnableFlamingock;
import io.flamingock.community.Flamingock;
import io.flamingock.store.dynamodb.DynamoDBAuditStore;
import io.flamingock.support.mongock.annotations.MongockSupport;
import io.flamingock.targetsystem.dynamodb.DynamoDBTargetSystem;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;

@EnableFlamingock
@MongockSupport(targetSystem = "dynamo-demo")
public class App {
    public static void main(String[] args) {
        DynamoDbClient dynamoDbClient = DynamoDbClient.builder()
                .region(Region.of("YOUR_AWS_REGION"))
                .build();

        DynamoDBTargetSystem dynamoTargetSystem =
                new DynamoDBTargetSystem("dynamo-demo", dynamoDbClient);

        DynamoDBAuditStore auditStore = DynamoDBAuditStore.from(dynamoTargetSystem);

        Flamingock.builder()
                .setAuditStore(auditStore)
                .addTargetSystem(dynamoTargetSystem)
                .build()
                .run();
    }
}
```

### Stage rule when only legacy changes exist

If the project has zero native Flamingock `@Change` classes and only legacy Mongock `@ChangeUnit` classes:

- Use bare `@EnableFlamingock` — do NOT add `@Stage(location = "...")`.
- Legacy `@ChangeUnit` classes go to the auto-generated `flamingock-legacy-stage`; user-declared `@Stage` is treated as NATIVE-only and requires at least one native `@Change`, otherwise pipeline validation fails with `Stage[X] must contain at least one change`.
