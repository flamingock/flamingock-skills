---
name: flamingock-mongock-migration-skill
description: Use this skill when the user wants to migrate an existing Mongock integration to Flamingock, preserve deployed Mongock changes, replace Mongock runner/driver/wiring/config/bootstrap with the matching Flamingock path, enable `@MongockSupport`, import Mongock audits, keep pending legacy Mongock changes runnable, or choose the correct Flamingock target system for a real Mongock migration.
license: Apache-2.0
metadata:
  author: Flamingock
  version: 1.0.0
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
- **Treat these defaults as strict unless the user changes them intentionally:**
  - import runs unless `skipImport = true`
  - empty origin fails unless `emptyOriginAllowed = true`
  - unknown imported entries fail unless `ignoreUnknownEntries = true`
- **Block unsupported Mongock semantics immediately:** `runAlways = true`, `failFast = false`, or non-default `systemVersion`.
- **Code and tests beat docs.** If examples or docs conflict with inspected behavior, stay with inspected behavior.
- **Do not sound like greenfield onboarding.** Do not start with dependency installation, bean wiring, or full bootstrap steps before naming the migration frame and backend.

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
Next action/handoff: <skill or action>
```

## Examples of correct behavior
- “This migration is supported. Your deployed Mongock changes stay as-is; what we replace is the Mongock runner/bootstrap/wiring/config around them, then we enable `@MongockSupport` on the matching Flamingock target path.”
- “This is blocked because your legacy changes depend on `runAlways = true`, which Flamingock’s Mongock compatibility path does not support.”
- “I need you to confirm whether the old Mongock stack used raw MongoDB sync driver or Spring Data `MongoTemplate` before I choose the Flamingock target system and the replacement wiring path.”
- “We are not doing greenfield onboarding yet; first we lock migration fit, preserved legacy scope, backend, and the replacement layer.”
