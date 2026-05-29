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
