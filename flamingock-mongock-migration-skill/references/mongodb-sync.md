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
