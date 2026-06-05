## MongoDB Sync migration branch

Use this reference when the legacy Mongock setup is based on the MongoDB sync driver, `MongoDatabase`, or `MongoClient` semantics.

### Origin mapping
- Keep the Flamingock migration on the same MongoDB sync backend family.
- Treat `origin` as the legacy Mongo-backed audit source used by Mongock.
- If the current target-system configuration already points at that same legacy source, `origin` can usually stay implicit.
- If the legacy audit source differs from the new target-system defaults, call out that `origin` must be set explicitly.

### Audit import notes
- Import runs before pending legacy Mongock changes unless `skipImport = true`.
- `emptyOriginAllowed = false` and `ignoreUnknownEntries = false` stay strict by default.
- Do not relax those defaults silently. If the user needs relaxed behavior, show the exact flag they are changing and why.
- **Empty/absent origin (default `mongockChangeLog`) makes the first run fail** with `FlamingockException: No audit entries found when importing from '<origin>'.` This hits clean environments (fresh local DB, CI, new region) even though prod/staging with real history succeed. Surface this in final notes and present `emptyOriginAllowed = "true"` (or a placeholder) as the lever. See the "Empty-origin runtime gap" section in `SKILL.md` for the full options and trade-offs.

### Audit migration semantics (what happens on first run)
- Flamingock reads the legacy Mongock audit collection (default `mongockChangeLog`) from the same database as the target system.
- Each legacy entry is imported into Flamingock's audit collection as an `already applied` record assigned to `flamingock-legacy-stage`.
- The legacy collection is NOT deleted, renamed, or written to. It stays in place as a read-only source. Safe to keep or drop manually after migration is verified.
- Subsequent runs do not re-import. Flamingock detects existing audit records and skips the import phase.
- Pending legacy `@ChangeUnit` classes (deployed in code but not yet run) execute through the compatibility path after import completes.

### Routing boundary
- Use this branch only for MongoDB sync migration fit and origin reasoning.
- For target-system wiring after migration fit -> `flamingock-mongodb-sync-targetsystem`
- For new native MongoDB sync changes after migration fit -> `flamingock-mongodb-sync-change`

### Watch-outs
- If the codebase actually uses `MongoTemplate` or Spring Data repositories as the primary Mongock surface, switch to `mongodb-springdata.md` instead.
- Do not rewrite legacy Mongock classes into native Flamingock `@Change` classes.
- Legacy `disableTransaction()` / `setTransactionEnabled(false)` has no direct fluent equivalent on `MongoDBSyncTargetSystem`. Decision tree:
  - Standalone Mongo (no replica set) -> drop the flag. Safe. No transactions exist to disable.
  - Replica set, transactions intentionally suppressed -> blocker. Escalate, do not silently enable transactions.
  - Replica set, transactions desired -> drop the flag. Flamingock uses transactions by default on replica sets.

### Verified package paths

The following imports are confirmed against the shipped jars; do not invent alternative package roots:

```java
import io.flamingock.api.annotations.EnableFlamingock;
import io.flamingock.community.Flamingock;
import io.flamingock.store.mongodb.sync.MongoDBSyncAuditStore;
import io.flamingock.targetsystem.mongodb.sync.MongoDBSyncTargetSystem;
import io.flamingock.support.mongock.annotations.MongockSupport;
```

### Required Gradle plugin modules

For a MongoDB-sync Mongock migration with Community audit store:

```kotlin
flamingock {
    community()
    mongodb()
    mongock()   // mandatory for @MongockSupport — pulls mongock-support artifact
}
```

### AuditStore is mandatory

`Flamingock.builder()` fails at runtime with `AuditStore must be configured before running Flamingock` if no audit store is set. Wire `MongoDBSyncAuditStore` via the static factory `from(targetSystem)`, then pass to `setAuditStore(...)`.

The constructor is private — do NOT call `new MongoDBSyncAuditStore(client, db)`.

```java
// Constructor signature: MongoDBSyncTargetSystem(String id, MongoClient client, String databaseName)
MongoDBSyncTargetSystem mongoTargetSystem =
    new MongoDBSyncTargetSystem("YOUR_TARGET_SYSTEM_ID", mongoClient, "YOUR_DATABASE_NAME");

MongoDBSyncAuditStore auditStore = MongoDBSyncAuditStore.from(mongoTargetSystem);

// run() is synchronous — blocks until the full pipeline (import + pending changes) finishes.
// Safe to wrap in try-with-resources around the MongoClient.
Flamingock.builder()
    .setAuditStore(auditStore)
    .addTargetSystem(mongoTargetSystem)
    .build()
    .run();
```

### Full standalone migration template (Java)

End-to-end shape after the Mongock → Flamingock migration, MongoDB sync, Community audit. Replace `[VERSION]` with the latest Flamingock release (resolve via `scripts/last-version.sh` if available):

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
    mongodb()
    mongock()   // pulls mongock-support; required for @MongockSupport
}

dependencies {
    implementation("org.mongodb:mongodb-driver-sync:[MONGO_DRIVER_VERSION]")
    implementation("org.slf4j:slf4j-simple:[SLF4J_VERSION]")
}
```

`App.java`:

```java
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import io.flamingock.api.annotations.EnableFlamingock;
import io.flamingock.community.Flamingock;
import io.flamingock.store.mongodb.sync.MongoDBSyncAuditStore;
import io.flamingock.support.mongock.annotations.MongockSupport;
import io.flamingock.targetsystem.mongodb.sync.MongoDBSyncTargetSystem;

@EnableFlamingock
@MongockSupport(targetSystem = "mongo-demo")
public class App {

    // Preserve env-var configuration from the legacy Mongock app so deploy config stays intact.
    private static final String CONNECTION_STRING = System.getenv()
            .getOrDefault("MONGODB_URI", "mongodb://localhost:27017");
    private static final String DATABASE_NAME = System.getenv()
            .getOrDefault("MONGODB_DATABASE", "YOUR_DATABASE_NAME");

    public static void main(String[] args) {
        try (MongoClient mongoClient = MongoClients.create(CONNECTION_STRING)) {

            // (targetSystemId, client, databaseName)
            MongoDBSyncTargetSystem mongoTargetSystem =
                new MongoDBSyncTargetSystem("mongo-demo", mongoClient, DATABASE_NAME);

            MongoDBSyncAuditStore auditStore =
                MongoDBSyncAuditStore.from(mongoTargetSystem);

            // Synchronous — blocks until pipeline finishes, then MongoClient closes.
            Flamingock.builder()
                .setAuditStore(auditStore)
                .addTargetSystem(mongoTargetSystem)
                .build()
                .run();
        }
    }
}
```

### Expected successful-run output

First successful run after migration. Read carefully — "0 newly applied" is NOT a failure, it means import already accounted for the legacy work:

```
INFO FK-Builder           - Generated runner id:  ...
INFO FK-PipelineRunner    - Flamingock execution started [stages=2 changes=N]
INFO FK-Report            -
========================================================================
 Flamingock execution report — NO CHANGES
========================================================================
 Stages:    2 total — 0 completed, 0 failed, 2 up to date, 0 not reached
 Changes:   N total — 0 newly applied, N already applied, 0 failed, 0 not reached

 Per-stage breakdown:
   [UP TO DATE] flamingock-system-stage  (1 changes already applied)
   [UP TO DATE] flamingock-legacy-stage  (K changes already applied)
========================================================================
```

Notes on what to verify in this output:
- `stages=2` is expected even when the user declared zero `@Stage`: Flamingock auto-creates `flamingock-system-stage` and `flamingock-legacy-stage`.
- `flamingock-legacy-stage` count should match the number of deployed legacy `@ChangeUnit` classes already executed under Mongock.
- If pending legacy changes existed, they show under `newly applied` in `flamingock-legacy-stage` on first run, then move to `already applied` on subsequent runs.

### Compile-time verification

The Gradle annotation processor prints discovery results during `compileJava`. Use this to confirm legacy `@ChangeUnit` classes were picked up before running:

```
Note: 	 [Flamingock] Searching for @MongockSupport annotation: Found
Note: 	 [Flamingock] Using io.flamingock.support.mongock.processor.MongockAnnotationProcessorPlugin for discover changes
Note: 	 [Flamingock] Searching for code-based changes (Java classes annotated with @ChangeUnit or @ChangeLog annotations)
Note: 	 [Flamingock] Generated metadata: 2 stages, N changes
```

If the legacy plugin line is missing or the change count is wrong, the `mongock()` Gradle DSL module is not active or `@MongockSupport` is missing.

### Stage rule when only legacy changes exist

If the project has zero native Flamingock `@Change` classes and only legacy Mongock `@ChangeUnit` classes:

- Use bare `@EnableFlamingock` — do NOT add `@Stage(location = "...your legacy package...")`.
- Reason: legacy `@ChangeUnit` classes are placed in the auto-generated `flamingock-legacy-stage`. Any user-declared `@Stage` is treated as a NATIVE-only stage and requires at least one native `@Change` class, otherwise pipeline validation fails with `Stage[X] must contain at least one change`.
- Once the project adds a real native `@Change`, reintroduce `@Stage(location = "...")` pointing at the package containing the native changes.

