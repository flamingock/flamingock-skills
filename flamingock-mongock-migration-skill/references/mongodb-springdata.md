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
