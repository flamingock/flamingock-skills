## DynamoDB migration branch

Use this reference when the legacy Mongock backend is DynamoDB.

### Origin mapping
- Keep the Flamingock migration on the DynamoDB backend family.
- Treat `origin` as the legacy DynamoDB audit location expected by the migration path.
- If the legacy audit table or equivalent location is unclear, ask for confirmation instead of inventing one.

### Audit import notes
- Import happens before pending legacy Mongock changes unless `skipImport = true`.
- Strict defaults still apply: empty origin fails unless explicitly allowed, and unknown imported entries fail unless explicitly ignored.
- Keep the explanation DynamoDB-specific; do not drift into Mongo or SQL assumptions.

### Routing boundary
- Use this branch to decide migration fit, flags, and blockers for DynamoDB-backed Mongock.
- For target-system wiring after migration fit -> `flamingock-dynamodb-targetsystem`
- For new native Flamingock changes after migration fit -> `flamingock-dynamodb-change`

### Watch-outs
- If the user cannot identify the legacy DynamoDB audit location, do not hide that uncertainty behind `emptyOriginAllowed = true`.
- Keep the branch lightweight; the main workflow stays in `SKILL.md`.
