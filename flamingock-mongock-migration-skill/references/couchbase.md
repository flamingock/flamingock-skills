## Couchbase migration branch

Use this reference when the legacy Mongock backend is Couchbase.

### Origin mapping
- Keep the Flamingock migration on the Couchbase backend family.
- Couchbase origin syntax is target-specific:
  - default scope: use the legacy collection name
  - non-default scope: use `scope.collection`
- If the legacy Couchbase origin is unclear, ask for the real scope/collection instead of guessing.

### Audit import notes
- Import runs before pending legacy Mongock changes unless `skipImport = true`.
- Strict defaults remain the same: empty origin fails unless explicitly allowed, and unknown imported entries fail unless explicitly ignored.
- Keep audit reasoning Couchbase-specific and explicit about scope/collection shape.

### Routing boundary
- Use this branch only for migration fit, origin shape, and blocker analysis.
- For target-system wiring after migration fit -> `flamingock-couchbase-targetsystem`
- For new native Flamingock changes after migration fit -> `flamingock-couchbase-change`

### Watch-outs
- Do not flatten `scope.collection` into a plain collection name when the legacy setup used a non-default scope.
- Do not rewrite historical Mongock changes into native Flamingock changes.

### Verified package paths

```java
import io.flamingock.api.annotations.EnableFlamingock;
import io.flamingock.community.Flamingock;
import io.flamingock.store.couchbase.CouchbaseAuditStore;
import io.flamingock.support.mongock.annotations.MongockSupport;
import io.flamingock.targetsystem.couchbase.CouchbaseTargetSystem;
```

### Required Gradle plugin modules

```kotlin
flamingock {
    community()
    couchbase()
    mongock()   // mandatory for @MongockSupport
}
```

### AuditStore is mandatory

`Flamingock.builder()...build()` fails with `BuilderException: AuditStore must be configured before running Flamingock` unless `setAuditStore(...)` is called. Wire `CouchbaseAuditStore` via its static `from(targetSystem)` factory — constructor is private; do NOT call `new CouchbaseAuditStore(cluster, bucket)`.

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
    couchbase()
    mongock()
}

dependencies {
    implementation("com.couchbase.client:java-client:[COUCHBASE_SDK_VERSION]")
    implementation("org.slf4j:slf4j-simple:[SLF4J_VERSION]")
}
```

`App.java`:

```java
import com.couchbase.client.java.Cluster;
import io.flamingock.api.annotations.EnableFlamingock;
import io.flamingock.community.Flamingock;
import io.flamingock.store.couchbase.CouchbaseAuditStore;
import io.flamingock.support.mongock.annotations.MongockSupport;
import io.flamingock.targetsystem.couchbase.CouchbaseTargetSystem;

@EnableFlamingock
@MongockSupport(targetSystem = "couchbase-demo")
public class App {
    public static void main(String[] args) {
        // Cluster.connect(connectionString, username, password)
        Cluster cluster = Cluster.connect("YOUR_CONNECTION_STRING", "YOUR_USERNAME", "YOUR_PASSWORD");

        // CouchbaseTargetSystem(id, cluster, bucketName)
        CouchbaseTargetSystem couchbaseTargetSystem =
                new CouchbaseTargetSystem("couchbase-demo", cluster, "YOUR_BUCKET_NAME");

        CouchbaseAuditStore auditStore = CouchbaseAuditStore.from(couchbaseTargetSystem);

        Flamingock.builder()
                .setAuditStore(auditStore)
                .addTargetSystem(couchbaseTargetSystem)
                .build()
                .run();
    }
}
```

### Stage rule when only legacy changes exist

If the project has zero native Flamingock `@Change` classes and only legacy Mongock `@ChangeUnit` classes:

- Use bare `@EnableFlamingock` — do NOT add `@Stage(location = "...")`.
- Legacy `@ChangeUnit` classes go to the auto-generated `flamingock-legacy-stage`; user-declared `@Stage` is treated as NATIVE-only and requires at least one native `@Change`, otherwise pipeline validation fails with `Stage[X] must contain at least one change`.
