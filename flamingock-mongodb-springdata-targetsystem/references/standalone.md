## Standalone `MongoDBSpringDataTargetSystem`

Use this reference when the request is framed as standalone wiring.

### Preconditions

- `MongoDBSpringDataTargetSystem` depends on `MongoTemplate`, so a Spring context is required.
- Use `"YOUR_TARGET_SYSTEM_ID"` when the target system id is still unknown.

### Dependency guidance

#### Gradle

Do not present a standalone Gradle recipe for this target system. This variant is Spring Boot only.

#### Maven

Do not present a standalone Maven recipe for this target system. This variant is Spring Boot only.

### MongoTemplate creation (if needed)

Do not fabricate a standalone `MongoTemplate` setup inside this skill. Ask the user to switch the request to Spring Boot wiring if they want to use this target system.

### Java setup path

Stop and explain that `MongoDBSpringDataTargetSystem` is only valid in a Spring Boot / Spring context where `MongoTemplate` can be injected.

### Kotlin setup path

Use the same guidance in Kotlin: this skill does not generate standalone code for `MongoDBSpringDataTargetSystem`.

### Optional concern tuning

If the user truly needs standalone MongoDB migrations, recommend choosing a standalone-capable target system instead of forcing this Spring Data variant.
