---
name: flamingock-onboarding
description: Guide Flamingock onboarding for standalone or Spring Boot Java/Kotlin projects across Community and Cloud editions. Trigger: use this skill whenever the user wants to install, initialize, wire, or activate Flamingock, choose dependencies, configure AuditStore or Cloud properties, place @EnableFlamingock, set up the standalone builder, or configure Spring Boot integration before writing any @Change classes.
license: Apache-2.0
metadata:
  author: Flamingock
  version: 1.0.0
---

# Flamingock Onboarding

## Critical rules

1. Resolve all routing fields before code generation: `Runtime`, `Language`, `Edition`, and `Build tool` are mandatory blockers because they change the output path.
2. Value fields are not blockers after one clarification pass. If a value cannot be confirmed or auto-resolved, generate the selected path with clear placeholders and tell the user what to replace.
3. Keep `SKILL.md` procedural. Read exactly one matching file from `references/` after the routing fields are complete.
4. For Community edition, derive the AuditStore from an existing AuditStore-capable TargetSystem: `mongodb-sync`, `mongodb-springdata` (Spring Boot only), `sql/jdbc`, `dynamodb`, or `couchbase`.
5. If the user does not identify the TargetSystem, inspect the codebase. If exactly one matching TargetSystem exists, use it; if several exist, ask the user which one to use; if none exist, continue onboarding without AuditStore and explain the next step.
6. If no changes package exists yet, do not block onboarding. Omit `@Stage(...)` / `stages = ...` and explain that it can be added later.
7. Do not invent an in-memory AuditStore. It is unsupported by the canonical guide.

## Intake flow

Collect the routing fields first. If any routing field is missing, ask only for the unresolved routing fields and stop. After the routing fields are known, collect the remaining value fields. Ask once for missing value fields; if the user does not know them or the codebase does not reveal them, continue with placeholders or by omitting the dependent section.

### Routing fields

1. **Runtime** — `standalone` or `springboot`
2. **Language** — `java` or `kotlin`
3. **Edition** — `community` or `cloud`
4. **Build tool** — `gradle` or `maven`

### Value fields

5. **Flamingock version** — explicit version or omit it so the agent tries Maven Central first and falls back to a placeholder if needed
6. **Changes package** — exact package for `@Stage(location = "...")` or `none yet`
7. **Activation class** — `main class`, `dedicated config class`, or the exact class name where `@EnableFlamingock` should live
8. **Community AuditStore backend** — only when edition is `community`: `mongodb-sync`, `mongodb-springdata` (Spring Boot only), `sql/jdbc`, `dynamodb`, `couchbase`, `auto-detect-from-targetsystem`, or `none yet`
9. **Community AuditStore TargetSystem** — only when edition is `community`: explicit TargetSystem name, `auto-detect`, or `none yet`
10. **Cloud values** — only when edition is `cloud`: `apiToken`, environment name, and service name

Use this reply template when the user has not already provided everything:

```text
Runtime: standalone | springboot
Language: java | kotlin
Edition: community | cloud
Build tool: gradle | maven
Flamingock version: <x.y.z> | latest | unknown
Changes package: <package> | none yet
Activation class: main class | dedicated config class | exact class name
Community AuditStore backend: mongodb-sync | mongodb-springdata | sql/jdbc | dynamodb | couchbase | auto-detect-from-targetsystem | none yet
Community AuditStore TargetSystem: explicit name | auto-detect | none yet
Cloud values: apiToken=... | environment=... | service=... | use placeholders
```

If `Changes package` is missing, generate `@EnableFlamingock` without `@Stage(...)`. If the user does not know `Activation class`, use a placeholder class name in the selected runtime style and tell the user to move the annotation later if needed.

## Guards before generation

### Language guard

If `Language` is missing or unknown, ask exactly `Language: java | kotlin` and stop. Do not emit code, dependencies, beans, builder setup, or `application.yml`, and never assume Java as the default.

### Routing guard

If any of `Runtime`, `Edition`, or `Build tool` is missing or ambiguous, ask only for the unresolved routing fields and stop. Do not guess between `standalone` vs `springboot`, `community` vs `cloud`, or `gradle` vs `maven`.

### Version guard

Before dependency output:

- if the user supplied a Flamingock version, use it
- otherwise try Maven Central for the latest published Flamingock version
- if lookup fails or remains ambiguous, keep generating the resolved path with placeholders such as `[FLAMINGOCK_VERSION]` or `${flamingockVersion}` and explicitly warn the user to replace them

### Community edition guard

If the user chooses `community`, verify both conditions when the AuditStore section is being emitted:

- the selected AuditStore backend is one of `mongodb-sync`, `mongodb-springdata`, `sql/jdbc`, `dynamodb`, or `couchbase`
- an AuditStore-capable TargetSystem is available for that backend

If an AuditStore-capable TargetSystem is available, derive the AuditStore from that TargetSystem using the canonical `XxxAuditStore.from(...)` factory.

If the user did not identify a Community AuditStore TargetSystem, inspect the codebase to locate existing TargetSystem definitions or registrations that can back the selected AuditStore backend. Apply this rule:

- one matching TargetSystem found → use it
- several found → ask the user which one to use and stop before the AuditStore section
- none found → continue generating onboarding without AuditStore and explain that the AuditStore can be added later after a TargetSystem is created

If the only available option is `non-transactional`, explain that Community still needs a separate AuditStore-capable TargetSystem and omit the AuditStore section.

### Cloud edition guard

If the user chooses `cloud`, do not configure an AuditStore. Ask once for `apiToken`, environment name, and service name. If the user does not know them, continue with placeholders and clearly mark them for replacement.

### Changes package guard

If `Changes package` is known, generate `@EnableFlamingock` with `@Stage(location = "...")`.

If `Changes package` is missing, unknown, or `none yet`, generate only `@EnableFlamingock` and add a short note telling the user to add `@Stage(...)` when the first changes package exists.

## Routing

After intake is complete and guards pass, read exactly one reference file:

| Runtime | Edition | Reference |
| --- | --- | --- |
| `standalone` | `community` | `references/standalone-community.md` |
| `standalone` | `cloud` | `references/standalone-cloud.md` |
| `springboot` | `community` | `references/springboot-community.md` |
| `springboot` | `cloud` | `references/springboot-cloud.md` |

Inside the chosen file, follow the subsection for the selected build tool:

- `Gradle` subsection when build tool is `gradle`
- `Maven` subsection when build tool is `maven`

After selecting the reference file, locate the `Java` or `Kotlin` subsection matching the resolved language inside each relevant section of that file, and follow only that subsection's examples.

## Output rules

When generation is allowed:

1. Briefly restate the resolved path: runtime, language, edition, build tool, Flamingock version source, AuditStore choice if Community, Community AuditStore TargetSystem status when applicable, and changes package status.
2. Generate only the setup relevant to that path.
3. For dependencies, use the user version, the Maven Central result, or placeholders if resolution failed. Never silently leave placeholders without calling them out.
4. If `Changes package` is known, use `@EnableFlamingock` with `@Stage(location = "...")` on the user-selected class.
5. If `Changes package` is not known yet, generate only `@EnableFlamingock` on the user-selected class.
6. For standalone, show builder-based setup.
7. For Spring Boot, show activation, `application.yml` properties, and an `AuditStore` bean only when an AuditStore-capable TargetSystem bean already exists.
8. For Community, build the AuditStore from the existing TargetSystem using the canonical `XxxAuditStore.from(...)` pattern only when the TargetSystem is actually resolved.
9. If no Community TargetSystem is resolved yet, omit `setAuditStore(...)` / `AuditStore` beans and explain that this can be added later after the TargetSystem exists.
10. For Cloud, use builder token/environment/service configuration in standalone or `application.yml` cloud properties in Spring Boot; placeholders are allowed when values are unknown.
11. If the chosen TargetSystem can support multiple concrete implementations, stay aligned with the user’s existing client/bean.
12. Emit language-appropriate syntax for the resolved language: Java syntax for `java`, Kotlin syntax for `kotlin`.
13. Do not mix languages: never emit Java syntax in Kotlin output blocks, and never emit Kotlin syntax in Java output blocks.
14. Before sending the final answer, verify that every Flamingock annotation, class name, method call, artifact coordinate, and placeholder matches the selected reference path exactly. Do not paraphrase or rename canonical Flamingock names.

## Verification

After generating setup guidance, tell the user to verify in this order:

1. **Dependency resolution** — confirm the chosen Flamingock dependencies and external driver/client dependencies resolve cleanly.
2. **First run** — start the app with its normal run command and confirm Flamingock initializes. If `@Stage(location = "...")` was included, confirm it discovers the configured changes package and applies pending changes.
3. **Second run** — restart the app and confirm the same changes are not re-executed.
4. **Audit artifacts** — when a Community AuditStore was actually configured, inspect the audit storage and verify execution records exist; default names from the canonical guide are `flamingockAuditLog` and `flamingockLock` where applicable.

For Community, point the inspection to the chosen backend:

- MongoDB/Couchbase: collections created and populated
- SQL/JDBC: tables created and populated
- DynamoDB: audit and lock tables created and populated

For Cloud, verify cloud configuration is loaded and no AuditStore bean or builder AuditStore setup was created.

If the answer used placeholders, add a final reminder to replace them before considering the setup complete.

## Commands

Use this short workflow every time:

```text
1. Ask for missing intake answers.
2. Resolve all routing fields.
3. Ask once for missing value fields, then fall back to placeholders or omission when allowed.
4. Apply guards.
5. Read one reference file.
6. Generate only the selected setup path.
7. Finish with the Verification checklist.
```
