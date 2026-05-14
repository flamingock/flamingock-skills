---
name: flamingock-mongodb-sync-targetsystem
description: Guide setup of Flamingock `MongoDBSyncTargetSystem` for standalone or Spring Boot Java/Kotlin projects using the MongoDB sync driver, with strict intake gating, explicit runtime+language routing, and single-language output. Trigger: use this skill whenever the user wants to configure, register, or wire `MongoDBSyncTargetSystem`, asks for MongoDB sync TargetSystem setup in standalone or Spring Boot Java/Kotlin projects, or needs dependency/setup guidance for Flamingock MongoDB sync target registration.
license: Apache-2.0
metadata:
  author: Flamingock
  version: 1.0.0
---

# Flamingock MongoDB Sync TargetSystem

## Critical rules

1. Run one routing preflight before code generation or setup guidance. If any routing field is unresolved after supported normalization, ask only for the unresolved routing field(s) and stop.
2. Only normalize documented aliases. Never guess unsupported aliases, near-matches, or implied defaults.
3. Accept only the source-backed creation path: `new MongoDBSyncTargetSystem(id, mongoClient, databaseName)` in Java or `MongoDBSyncTargetSystem(id, mongoClient, databaseName)` in Kotlin.
4. Keep friction low: when routing is resolved and the user explicitly wants to move forward, placeholders are allowed for operational values such as `TargetSystem id`, `Database name`, `MongoClient` creation details, and dependency versions.
5. Add `mongodb-driver-sync` only when this skill creates the `MongoClient`. If the user already has a working `MongoClient`, do not add or modify that driver dependency unless the user explicitly asks for dependency help.
6. Emit exactly one runtime path and exactly one language path per answer by default. If the user explicitly asks for comparison between runtimes or languages, provide both.
7. In Gradle projects, if Flamingock plugin/module setup is needed for the generated path, ensure the required Flamingock modules are present and resolve the plugin version with the bundled scripts when the user did not provide one.

## Source priority

Use sources in this order:

1. `flamingock-java/core/target-systems/flamingock-mongodb-sync-targetsystem/src/main/java/io/flamingock/targetsystem/mongodb/sync/MongoDBSyncTargetSystem.java`
2. `docs/skills-implementation-guide.md` for dependency and registration shape.
3. `flamingock.github.io/docs/target-systems/mongodb-target-system.md` and Spring Boot docs only when the source alone is not enough

## Intake flow

Before generation, normalize only the documented aliases below, then run one routing preflight. If any routing blocker remains, reply ONLY with the unresolved intake fields and stop. Do not emit assumptions, dependencies, code, setup steps, or extra recommendations until routing is resolved.

### Supported alias normalization

Normalize only these inputs:

| Field | Accepted forms | Canonical value |
| --- | --- | --- |
| Runtime | `springboot`, `spring-boot`, `spring boot`, `spring-service` (case-insensitive) | `springboot` |
| Runtime | `standalone` (case-insensitive) | `standalone` |
| Language | `java`, `kotlin` (case-insensitive) | lowercase input |
| Build tool | `gradle`, `maven` (case-insensitive) | lowercase input |
| `mongodb-driver-sync present` | `yes`, `no`, `unknown` (case-insensitive) | lowercase input |
| `MongoClient available/source` | `existing bean`, `existing variable`, `auto-detect` (case-insensitive) | lowercase input |

If the user supplies anything outside this table, treat that field as unresolved and ask for clarification. Do not guess a closest match.

### Blocker preflight

Treat only these routing fields as hard blockers:

1. `Runtime`
2. `Language`
3. `Build tool`

Treat these as operational fields:

4. `mongodb-driver-sync present`
5. `TargetSystem id`
6. `MongoClient available/source`
7. `Database name`
8. `MongoDB driver version` (only if this skill creates the `MongoClient`)
9. Flamingock plugin version (only if Gradle plugin/module guidance must be emitted and the user did not provide a version)

Rules for operational fields:

- If the user wants a precise project-specific answer, ask for missing operational values when needed.
- If the user explicitly wants to move forward, allows placeholders, asks for a new client setup, or asks for an example, continue with clearly marked placeholders and assumptions.
- If `MongoClient` is already available, focus on creating and registering `MongoDBSyncTargetSystem` and do not expand scope into unrelated dependency repair.

If any routing blocker is unresolved, ask only for the missing routing field(s) using the intake labels and allowed options, then stop.

### Routing fields

1. **Runtime** — `standalone` or `springboot`
2. **Language** — `java` or `kotlin`
3. **Build tool** — `gradle` or `maven`

### Value fields

4. **mongodb-driver-sync present** — `yes`, `no`, or `unknown` (only relevant when this skill creates the `MongoClient`, or when the user explicitly asks for dependency help)
5. **TargetSystem id** — exact id or use a placeholder
6. **MongoClient available/source** — existing bean, existing variable, `auto-detect`, or create new with placeholders
7. **Database name** — exact name or use a placeholder
8. **MongoDB driver version** — only needed if this skill creates the `MongoClient`. Explicit version or omit it so the agent tries to resolve it using the provided scripts (falling back to a placeholder if resolution fails).
9. **Flamingock plugin version** — only needed for Gradle plugin/module guidance when the version is not already known

Use this reply template when the user has not already provided everything:

```text
Runtime: standalone | springboot
Language: java | kotlin
Build tool: gradle | maven
mongodb-driver-sync present: yes | no | unknown
TargetSystem id: ...
MongoClient available/source: existing bean | existing variable | auto-detect | create new with placeholders
Database name: ...
MongoDB driver version: <x.y.z> | latest | unknown (only if creating MongoClient)
Flamingock plugin version: <x.y.z> | latest | unknown (only for Gradle plugin guidance)
```

If `MongoClient` is missing, try to locate it in the code using `grep`. If not found and the user wants a precise project-specific answer, ask once. If not found and the user already asked for placeholders, an example, or a new client setup, generate setup with `MongoClient` creation using placeholders.

## Guards

### Language guard

If `Language` is missing or unknown, ask exactly `Language: java | kotlin` and stop. Do not emit code, dependencies, or setup, and never assume Java as the default.

### Routing guard

If `Runtime` or `Build tool` is missing or ambiguous, ask only for the unresolved routing fields and stop.

Treat `Runtime` as ambiguous whenever the prompt gives conflicting runtime signals, even if one path looks more plausible from surrounding context. Do not resolve that conflict from clues such as Spring beans, framework mentions, or what seems like the better fit. In that case, ask exactly `Runtime: standalone | springboot` and stop.

### Blocker-only reply guard

Whenever any routing blocker is unresolved, the reply must contain only the routing clarification lines. Do not add code, dependency guidance, setup paths, source references, fallback recommendations, or “once you answer” plans in the same message.

### MongoClient resolution guard

When `MongoClient` source is unresolved:
1. Search the codebase for `MongoClient` beans (Spring) or variables (standalone).
2. If exactly one match is found, use it.
3. If multiple matches are found, ask exactly `MongoClient available/source: existing bean | existing variable | auto-detect` plus a short clarifier if needed, then stop.
4. If the user says `MongoClient` exists but does not clarify whether it is an existing bean, an existing variable, or something to auto-detect, treat that as unresolved and ask exactly `MongoClient available/source: existing bean | existing variable | auto-detect`, then stop.
5. If no matches are found and the user explicitly wants placeholders, a new client setup, or an example that moves forward without more back-and-forth, continue by generating a `MongoClient` creation snippet using placeholders for the connection string.
6. If no matches are found and the user expects a precise project-specific answer, ask once for the `MongoClient` details before continuing.

Treat user inputs such as “I don't have the MongoClient yet”, “create it for me”, or “use placeholders” as permission to generate a new `MongoClient` setup path.

### MongoDB driver dependency and version guard

Apply this guard only when this skill creates the `MongoClient`.

Before dependency output for a newly created `MongoClient`:

- if the user supplied a driver version, use it
- otherwise, try to resolve the latest version using `scripts/last-version.py` or `scripts/last-version.sh`:
  - use `org.mongodb` as group and `mongodb-driver-sync` as artifact
- if script execution fails, lookup fails, or remains ambiguous, use placeholders such as `[MONGODB_DRIVER_VERSION]` or `${mongodbDriverVersion}` and explicitly warn the user to replace them

If the user already has an existing `MongoClient`, do not add `mongodb-driver-sync` by default. Only confirm or discuss that dependency if the user explicitly asks for dependency help.

If `mongodb-driver-sync present` itself is still unresolved and the user wants dependency help for a newly created `MongoClient`, ask exactly `mongodb-driver-sync present: yes | no | unknown` and stop.

### Flamingock Gradle plugin guard

Apply this guard only for Gradle projects.

If the generated setup needs Flamingock Gradle plugin/module guidance:

- standalone path → ensure `mongodb()` is present
- springboot path → ensure both `springboot()` and `mongodb()` are present
- if a `flamingock` block already exists, modify it instead of duplicating it
- if the plugin version is not provided, try to resolve it using `scripts/last-version.py` or `scripts/last-version.sh`:
  - use `io.flamingock` as group/plugin_id
  - do not pass an artifact
- if script execution fails or lookup remains ambiguous, use `[FLAMINGOCK_VERSION]` or `${flamingockVersion}` and explicitly warn the user to replace it

Do not emit Gradle plugin guidance for Maven projects.

### Language purity guard

Before sending the final answer, verify the output stays on exactly one language path by default:

- **Java path**: allow Java syntax only; do not emit Kotlin markers such as `fun`, `val`, `object`, or `@Bean fun`
- **Kotlin path**: allow Kotlin syntax only; do not emit Java markers such as `new MongoDBSyncTargetSystem`, `public class`, `public static void main`, or `@Bean public`

Do not show “equivalent Java” and “equivalent Kotlin” in the same answer unless the user explicitly requests a comparison. Pick the resolved language and stay there by default.

## Routing

After intake is complete, read exactly one runtime reference file:

| Runtime | Reference |
| --- | --- |
| `standalone` | `references/standalone.md` |
| `springboot` | `references/springboot.md` |

Inside that file, follow only:

1. the subsection for the selected build tool
2. the subsection for the selected language

## Output contract

When generation is allowed, respond in this order:

1. **Assumptions** — restate runtime, language, build tool, target id, `MongoClient` source, and database name. Include MongoDB driver version only if this skill creates the `MongoClient`. Include Flamingock plugin version only when Gradle plugin guidance is part of the generated path.
2. **Dependency guidance** —
   - if this skill creates the `MongoClient`, include `mongodb-driver-sync`
   - if the path is Gradle and Flamingock plugin/module guidance is needed, show the required `flamingock` plugin/module configuration for that runtime
   - if the path is Maven, show only the relevant Maven dependency path
3. **MongoClient setup** — only if a new client must be created; show the creation snippet with placeholders when exact values are unknown.
4. **Setup path** — emit exactly one registration style by default (or both if comparison is requested):
   - standalone → builder registration only
   - springboot → `@Bean` registration only
5. **Optional concern tuning** — only if the user asks or already gives values; limit tuning to `withReadConcern`, `withReadPreference`, and `withWriteConcern`.

Emit language-appropriate syntax for the resolved language only.

## Commands

```text
1. Ask for missing intake answers.
2. Resolve all routing fields.
3. Ask once for missing value fields, then fall back to placeholders when allowed.
4. Apply guards, including MongoClient auto-detection.
5. Read exactly one runtime reference file.
6. Generate one runtime-specific, language-pure setup path (or both if requested for comparison).
7. Include MongoClient creation if no existing client was found.
```
