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

### Routing boundary
- Use this branch only for MongoDB sync migration fit and origin reasoning.
- For target-system wiring after migration fit -> `flamingock-mongodb-sync-targetsystem`
- For new native MongoDB sync changes after migration fit -> `flamingock-mongodb-sync-change`

### Watch-outs
- If the codebase actually uses `MongoTemplate` or Spring Data repositories as the primary Mongock surface, switch to `mongodb-springdata.md` instead.
- Do not rewrite legacy Mongock classes into native Flamingock `@Change` classes.
- Legacy `disableTransaction()` / `setTransactionEnabled(false)` has no direct fluent equivalent on `MongoDBSyncTargetSystem`. Raise it explicitly with the user if the source Mongo deployment is a replica set and transactions must stay off.

### Verified package paths (Flamingock 1.4.x)

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

### Stage rule when only legacy changes exist

If the project has zero native Flamingock `@Change` classes and only legacy Mongock `@ChangeUnit` classes:

- Use bare `@EnableFlamingock` — do NOT add `@Stage(location = "...your legacy package...")`.
- Reason: legacy `@ChangeUnit` classes are placed in the auto-generated `flamingock-legacy-stage`. Any user-declared `@Stage` is treated as a NATIVE-only stage and requires at least one native `@Change` class, otherwise pipeline validation fails with `Stage[X] must contain at least one change`.
- Once the project adds a real native `@Change`, reintroduce `@Stage(location = "...")` pointing at the package containing the native changes.

### Known transitive-dep gap

Some 1.4.x community builds pull a transitive `mongock-importer-dynamodb:<version>` that is not always published to Maven Central (observed: `1.4.2`). If gradle/maven resolution fails on this artifact, force the previous patch:

```kotlin
configurations.all {
    resolutionStrategy {
        force("io.flamingock:mongock-importer-dynamodb:1.4.1")
    }
}
```
