---
name: flamingock-mongock-migration-skill
description: Use this skill when the user wants to migrate an existing Mongock integration to Flamingock, preserve deployed Mongock changes, replace Mongock runner/driver/wiring/config/bootstrap with the matching Flamingock path, enable `@MongockSupport`, import Mongock audits, keep pending legacy Mongock changes runnable, or choose the correct Flamingock target system for a real Mongock migration.
license: Apache-2.0
metadata:
  author: Flamingock
  version: 1.1.0
---

# Mongock Migration Skill

## Lead framing
Use this skill only for **real Mongock -> Flamingock migration work on an existing integration**.

Lead with this idea every time:

> Preserve the legacy Mongock changes. Replace the surrounding Mongock integration layer.

The migration is **not** “start Flamingock from scratch.”
The migration **is**:
- keep deployed Mongock `@ChangeUnit`, `@ChangeLog`, and `@ChangeSet` classes immutable,
- keep pending legacy Mongock changes runnable through the compatibility path,
- replace Mongock runner/bootstrap/wiring/configuration with the matching Flamingock target-system path,
- enable and evaluate `@MongockSupport`,
- then hand off to onboarding, target-system, or change skills only after the migration frame is explicit.

## Quick path
1. Confirm this is a real Mongock migration, not greenfield Flamingock onboarding.
2. Confirm which legacy backend/runtime is in use.
3. State what stays: deployed legacy Mongock changes.
4. State what gets replaced: runner, driver/target-system binding, wiring/bootstrap/config, and `@MongockSupport` integration.
5. Check blockers and strict defaults.
6. Route to exactly one backend reference.
7. Hand off only after migration fit, backend, and replacement scope are explicit.

## In scope vs out of scope

| Area | Rule |
|------|------|
| Preserved scope | Deployed Mongock changes remain historical artifacts and are NOT rewritten into native Flamingock changes. |
| Replacement scope | Replace Mongock runner, driver/target-system integration, bootstrap, wiring, config, and migration annotations around the preserved changes. |
| Skill role | Decide migration fit, backend path, flags/defaults, blockers, and next handoff. |
| Not this skill | Net-new Flamingock setup, full target implementation before migration fit is clear, or rewriting legacy changes. |

## Documentation & Grounding
For the most up-to-date syntax, features, and best practices, always consult the official LLM-optimized documentation:
- **Official Docs**: https://docs.flamingock.io/llms.txt

## Hard Guardrails
- **Never rewrite deployed Mongock changes.** Migration support is compatibility-oriented, not a rewriter.
- **Say explicitly that the integration layer gets replaced.** Name runner, bootstrap, wiring, config, and backend/target binding when summarizing the path.
- **Use the same backend family Mongock already used.** Do not silently switch MongoDB Sync to Spring Data, DynamoDB to Couchbase, or any other cross-target conversion.
- **Require `@MongockSupport` for the migration path.** If it is missing, explain that the migration flow starts there.
- **Treat these `@MongockSupport` flags as the supported surface:** `targetSystem`, `skipImport`, `origin`, `emptyOriginAllowed`, `ignoreUnknownEntries`.
- **All `@MongockSupport` flags are `String`-typed, even the boolean-looking ones.** `skipImport`, `emptyOriginAllowed`, and `ignoreUnknownEntries` accept the literal strings `"true"`, `"false"`, or `""` (empty == default == `false`). They are strings so they can carry property placeholders, e.g. `emptyOriginAllowed = "${flamingock.empty-origin-allowed:false}"`. Always quote the value; never pass a bare boolean.
- **Treat these defaults as strict unless the user changes them intentionally:**
  - import runs unless `skipImport = true`
  - empty origin fails unless `emptyOriginAllowed = true`
  - unknown imported entries fail unless `ignoreUnknownEntries = true`
- **Block unsupported Mongock semantics immediately:** `runAlways = true`, `failFast = false`, or non-default `systemVersion`.
- **Code and tests beat docs.** If examples or docs conflict with inspected behavior, stay with inspected behavior.
- **Do not sound like greenfield onboarding.** Do not start with dependency installation, bean wiring, or full bootstrap steps before naming the migration frame and backend.
- **Require the `mongock-support` artifact.** The `@MongockSupport` annotation lives in `io.flamingock.support.mongock.annotations`. Pull it in via the Gradle plugin module `mongock()` (i.e. `flamingock { community(); mongodb(); mongock() }`) or the Maven dependency `io.flamingock:mongock-support`. Without it, compile fails with `cannot find symbol: class MongockSupport`.
- **Do not declare an empty user `@Stage` when only legacy Mongock changes exist.** Pipeline validation fails with `Stage[X] must contain at least one change` because legacy `@ChangeUnit` classes go to the auto-generated `flamingock-legacy-stage`, NOT a user-defined `@Stage`. Use bare `@EnableFlamingock` until at least one native `@Change` class exists in the project.
- **Mongock `disableTransaction()` / `setTransactionEnabled(false)` does not map to a documented Flamingock fluent option** on `MongoDBSyncTargetSystem`. Apply this decision tree:
  - Standalone Mongo (no replica set) -> drop the flag. Safe. Mongo standalone has no transactions to disable.
  - Replica set, transactions intentionally suppressed -> blocker. No fluent equivalent on `MongoDBSyncTargetSystem`. Escalate, do not silently enable transactions.
  - Replica set, transactions desired -> drop the flag. Flamingock uses transactions by default on replica sets.
  - The supported tuning surface is `withReadConcern` / `withReadPreference` / `withWriteConcern` only.
- **AuditStore is mandatory on the Flamingock builder.** `Flamingock.builder()...build()` throws `BuilderException: AuditStore must be configured before running Flamingock` if `setAuditStore(...)` is missing. Wire the backend-matching AuditStore using its static `from(targetSystem)` factory (constructors are private). Example: `MongoDBSyncAuditStore.from(mongoTargetSystem)` then `.setAuditStore(auditStore)`. Each backend reference shows its exact wiring.

- **Flamingock Gradle plugin id is `io.flamingock`.** Pin the plugin version explicitly in the `plugins { ... }` block. The `flamingock { community(); mongodb(); mongock() }` DSL alone does not declare the plugin.

## Flamingock Version Resolution

When the user does not specify a Flamingock version, resolve it in this order:

1. If the project already declares `io.flamingock` plugin, `flamingock-bom`, or Flamingock artifacts, reuse that existing version unless the user asked to upgrade.
2. If the repository has centralized version management (`gradle.properties`, `libs.versions.toml`, parent POM, dependencyManagement), reuse the project convention.
3. Otherwise resolve the latest stable Flamingock version from both:
   - Gradle plugin marker:
     `bash <skills-root>/flamingock-onboarding/scripts/last-version.sh io.flamingock`
   - Maven artifact:
     `bash <skills-root>/flamingock-onboarding/scripts/last-version.sh io.flamingock flamingock-community`
4. Use the version only if both sources resolve to the same stable release.
5. Exclude prerelease versions (`alpha`, `beta`, `rc`, `SNAPSHOT`) unless the user explicitly asks for them.
6. If resolution fails because of network/sandbox, retry with escalated network permission.
7. If resolution still fails, do not guess. Use `[VERSION]` and explicitly report that the version could not be resolved.

## Cross-backend execution semantics

These behaviors apply to every backend reference unless noted otherwise:

- **`Flamingock.builder().build().run()` is synchronous.** Blocks until import + pending changes finish. Safe to wrap the underlying client (e.g. `MongoClient`, `DynamoDbClient`, Couchbase `Cluster`) in try-with-resources.
- **Audit migration on first run** reads the legacy Mongock audit (collection/table depends on backend) and imports each entry as `already applied` into `flamingock-legacy-stage`. The legacy audit source is NOT deleted, renamed, or written to. Subsequent runs do not re-import.
- **Expected stage count is 2** even when the user declared zero `@Stage`: Flamingock auto-creates `flamingock-system-stage` and `flamingock-legacy-stage`. A clean first run reports `0 newly applied, N already applied` for the legacy stage — that is success, not a no-op.
- **Compile-time verification:** the Gradle annotation processor prints `[Flamingock] Searching for @MongockSupport annotation: Found` and `Generated metadata: 2 stages, N changes` during `compileJava`. If those lines are missing the `mongock()` plugin DSL module is not active.

## Empty-origin runtime gap (clean environments)

This is a **runtime** outcome the skill cannot fully resolve at edit time, because it depends on the live database state — not the code. Always surface it explicitly in the final notes so the user is not surprised on first run.

**The behavior:** when `skipImport` is `false` (the default), Flamingock imports the legacy Mongock audit before running pending changes. If the configured `origin` (the legacy Mongock audit collection/table) **does not exist or is empty**, the import fails hard, because `emptyOriginAllowed` defaults to `false`.

**The symptom:** a `FlamingockException` at startup with a message of the form:

```
No audit entries found when importing from '<origin>'.
```

(`<origin>` is the resolved legacy audit source, e.g. the Mongock changelog collection.)

**Why it matters:** the migrated app works perfectly against any environment that has a real Mongock history (production, staging — the majority case). But a **fresh/clean environment** (new local DB, CI from scratch, a new region, an ephemeral test container) has no legacy audit to import, so the same binary fails on first boot unless the empty case is allowed.

**The fix — `emptyOriginAllowed`:** set it to `"true"` to let Flamingock treat an empty/absent origin as "nothing to import" and proceed to the pending changes instead of failing:

```java
@MongockSupport(
    targetSystem = "mongo-demo",
    emptyOriginAllowed = "true"
)
```

**Recommended options to give the user (let them choose — this is an environment policy decision, not a code default):**
1. **Placeholder-driven (recommended for mixed fleets):** keep it strict by default but overridable per environment, so prod stays safe while clean envs pass:
   `emptyOriginAllowed = "${flamingock.empty-origin-allowed:false}"` — then set `flamingock.empty-origin-allowed=true` only where the origin may legitimately be empty (CI, fresh local, new region).
2. **Always allow empty:** `emptyOriginAllowed = "true"` — simplest; appropriate when clean environments are expected to be the norm or when you want one binary to boot everywhere.
3. **Keep strict (default):** leave it unset/`"false"` — appropriate when every environment is guaranteed to have a real Mongock history and an empty origin should be treated as a genuine misconfiguration worth failing on.

**Guardrail:** do NOT silently flip `emptyOriginAllowed` to `"true"` as part of the migration. Strict-by-default is intentional: it protects against a wrong/empty `origin` masking a real data problem. Present it as an explicit, environment-aware choice and explain the trade-off. Note it only applies while `skipImport` is `false`.

## Shared Migration Workflow
Follow this order:

1. Confirm the request is a real Mongock migration, not a net-new Flamingock setup.
2. Identify or confirm the original Mongock backend/runtime:
   - MongoDB sync driver
   - MongoDB Spring Data / `MongoTemplate`
   - DynamoDB
   - Couchbase
3. State the preserved legacy scope: existing Mongock `@ChangeUnit`, `@ChangeLog`, and `@ChangeSet` classes remain historical artifacts.
4. State the replacement focus: runner, driver/target system, wiring/bootstrap/config, and `@MongockSupport` migration integration.
5. Evaluate `@MongockSupport` usage and the migration flags.
6. Check blockers before giving implementation advice.
7. Load exactly one target reference when backend-specific origin or routing detail matters.
8. Return migration fit, preserved scope, chosen target, replacement focus, required flags/defaults, guardrails/blockers, and the next handoff.

## Cross-target Checklist
Keep the main answer short and make this checklist explicit:
- [ ] Confirm the legacy Mongock backend before choosing a Flamingock target system
- [ ] Keep deployed Mongock changes unchanged
- [ ] Name the replacement layer explicitly: runner, target-system/driver binding, wiring/bootstrap/config, `@MongockSupport`
- [ ] Add or verify `@MongockSupport(targetSystem = "...")`
- [ ] Decide whether audit import stays strict or needs explicit relaxation
- [ ] Warn about the empty-origin runtime gap on clean environments and present the `emptyOriginAllowed` options
- [ ] Check for unsupported Mongock metadata before proceeding
- [ ] Hand off to the correct onboarding / targetsystem / change skill only after migration fit is settled

## Reference Routing
- If the legacy backend is **MongoDB sync driver / `MongoDatabase` / `MongoClient`** -> read `references/mongodb-sync.md`
- If the legacy backend is **MongoDB Spring Data / `MongoTemplate`** -> read `references/mongodb-springdata.md`
- If the legacy backend is **DynamoDB** -> read `references/dynamodb.md`
- If the legacy backend is **Couchbase** -> read `references/couchbase.md`
- If the backend is still unclear -> stop and ask the user to confirm the original Mongock target system

## Redirections
- If the user needs Flamingock bootstrap after migration fit, preserved scope, and backend are explicit -> `flamingock-onboarding`
- If the user needs target-system wiring for the chosen backend after migration fit is confirmed -> `flamingock-mongodb-sync-targetsystem`, `flamingock-mongodb-springdata-targetsystem`, `flamingock-dynamodb-targetsystem`, or `flamingock-couchbase-targetsystem`
- If the user needs a new native Flamingock change after migration is settled -> `flamingock-mongodb-sync-change`, `flamingock-mongodb-springdata-change`, `flamingock-dynamodb-change`, or `flamingock-couchbase-change`
- If the user asks to convert legacy Mongock changes into native Flamingock changes -> refuse the rewrite and redirect back to the compatibility migration path

## Response Rules
- Lead with the migration verdict, not background history.
- If the backend is ambiguous, ask for confirmation before inventing a target.
- If the request is blocked, name the exact blocker and the required follow-up.
- Explicitly separate **what stays** from **what gets replaced**.
- Keep target-specific detail in the selected reference, not duplicated in the main body.
- Do not jump into onboarding-style setup steps until migration framing is explicit.
- Always close with final notes covering the empty-origin runtime gap: the import fails on clean/empty environments with `FlamingockException: No audit entries found when importing from '<origin>'.`, and `emptyOriginAllowed = "true"` (or a placeholder) is the lever. Frame it as an environment choice, never as a silent default flip.
- Once migration-only guidance is done, stop and hand off instead of continuing into a different skill's scope.

## Minimum Output Contract
Always return at least:

```text
Migration fit: Supported | Blocked | Need target confirmation
Preserved legacy scope: <what legacy Mongock artifacts stay untouched>
Current backend/runtime: <one | pending confirmation>
Chosen target system: <one | pending confirmation>
Replacement focus: <runner, driver/target binding, wiring/bootstrap/config, @MongockSupport>
Required flags/defaults: <list>
Guardrails/blockers: <list>
Empty-origin note: <will first run hit 'No audit entries found' on a clean env? state the emptyOriginAllowed decision>
Next action/handoff: <skill or action>
```

## Examples of correct behavior
- “This migration is supported. Your deployed Mongock changes stay as-is; what we replace is the Mongock runner/bootstrap/wiring/config around them, then we enable `@MongockSupport` on the matching Flamingock target path.”
- “This is blocked because your legacy changes depend on `runAlways = true`, which Flamingock’s Mongock compatibility path does not support.”
- “I need you to confirm whether the old Mongock stack used raw MongoDB sync driver or Spring Data `MongoTemplate` before I choose the Flamingock target system and the replacement wiring path.”
- “We are not doing greenfield onboarding yet; first we lock migration fit, preserved legacy scope, backend, and the replacement layer.”
