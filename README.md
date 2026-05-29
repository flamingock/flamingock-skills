# flamingock-skills

A collection of AI agent skills for the [Flamingock](https://flamingock.io) ecosystem. Each skill teaches an AI coding assistant how to work correctly with Flamingock APIs, conventions, and patterns.

## Available skills

| Skill | Description |
|-------|-------------|
| [`flamingock-onboarding`](./flamingock-onboarding/) | Configure Flamingock's entry point in a project — standalone Java or Spring Boot, Community or Cloud edition — adding the correct dependencies and writing the initialization code |
| [`flamingock-mongodb-sync-targetsystem`](./flamingock-mongodb-sync-targetsystem/) | Configure `MongoDBSyncTargetSystem` for standalone or Spring Boot Java/Kotlin projects using the MongoDB sync driver, with language-gated single-path output and TargetSystem-only scope |
| [`flamingock-mongodb-sync-change`](./flamingock-mongodb-sync-change/) | Create or review Java/Kotlin `@Change` classes for `MongoDBSyncTargetSystem`, with explicit language gating, strict registration verification, DML-vs-DDL routing, rollback-safe guidance, and hard redirects for setup or Spring Data concerns |
| [`flamingock-mongodb-springdata-targetsystem`](./flamingock-mongodb-springdata-targetsystem/) | Configure `MongoDBSpringDataTargetSystem` for Spring Boot projects using `MongoTemplate`, Spring Data MongoDB, and bean registration |
| [`flamingock-mongodb-springdata-change`](./flamingock-mongodb-springdata-change/) | Create or review `@Change` classes for `MongoDBSpringDataTargetSystem`, covering Spring Data MongoDB migrations, backfills, data fixes, and DML-vs-DDL routing |
| [`flamingock-sql-targetsystem`](./flamingock-sql-targetsystem/) | Configure `SqlTargetSystem` for relational databases, including JDBC driver guidance and bean or builder registration |
| [`flamingock-sql-change`](./flamingock-sql-change/) | Create or review `@Change` classes for `SqlTargetSystem`, covering schema changes, backfills, data fixes, and strict DML-vs-DDL separation |
| [`flamingock-dynamodb-targetsystem`](./flamingock-dynamodb-targetsystem/) | Configure `DynamoDBTargetSystem`, including AWS SDK guidance and bean or builder registration |
| [`flamingock-dynamodb-change`](./flamingock-dynamodb-change/) | Create or review `@Change` classes for `DynamoDBTargetSystem`, covering table management, transactional writes, backfills, and schema-vs-write separation |
| [`flamingock-couchbase-targetsystem`](./flamingock-couchbase-targetsystem/) | Configure `CouchbaseTargetSystem`, including Couchbase SDK guidance and bean or builder registration |
| [`flamingock-couchbase-change`](./flamingock-couchbase-change/) | Create or review `@Change` classes for `CouchbaseTargetSystem`, covering document migrations, collection or index changes, and transactional-vs-schema separation |
| [`flamingock-non-transactional-targetsystem`](./flamingock-non-transactional-targetsystem/) | Configure `NonTransactionalTargetSystem` for systems without native transactions, including dependency and property registration |
| [`flamingock-non-transactional-change`](./flamingock-non-transactional-change/) | Create or review `@Change` classes for `NonTransactionalTargetSystem`, emphasizing compensation, idempotency, recovery strategy, and non-transactional safety |
| [`flamingock-mongock-migration-skill`](./flamingock-mongock-migration-skill/) | Guide real Mongock -> Flamingock migrations without rewriting deployed changes, choose the matching target system, explain `@MongockSupport`, and hand off to onboarding/targetsystem/change skills only after migration fit is settled |

---

## What is a skill?

A skill is a folder containing a `SKILL.md` file (and optionally `references/` and `assets/` directories) that an AI agent loads when it detects a relevant context. Once installed, the agent uses the skill's rules and examples instead of guessing from general knowledge.

This matters for Flamingock because the API has specific contracts that a general-purpose model is likely to get wrong. Skills encode the correct patterns so the agent produces accurate, production-ready code from the start.

---

## Installation

Skills are installed per project. Copy the skill folder into your project's `.agents/skills/` directory.

> For automatic install yoy can also run `flamingock install-skills` if you have the Flamingock CLI installed.

### Step 1 — Create the skills directory (if it doesn't exist)

```bash
mkdir -p your-project/.agents/skills/
```

### Step 2 — Copy the skill

```bash
cp -r flamingock-*/ your-project/.agents/skills/
```

Your project structure should look like this:

```
your-project/
└── .agents/
    └── skills/
        ├── flamingock-onboarding/
        ├── flamingock-mongodb-sync-targetsystem/
        ├── flamingock-mongodb-sync-change/
        ├── flamingock-sql-targetsystem/
        ├── flamingock-sql-change/
        ├── flamingock-dynamodb-targetsystem/
        ├── flamingock-dynamodb-change/
        ├── flamingock-couchbase-targetsystem/
        ├── flamingock-couchbase-change/
        ├── flamingock-mongodb-springdata-targetsystem/
        ├── flamingock-mongodb-springdata-change/
        ├── flamingock-non-transactional-targetsystem/
        ├── flamingock-non-transactional-change/
        └── flamingock-mongock-migration-skill/
```

### Step 3 — Commit it

```bash
cd your-project
git add .agents/skills/flamingock-*/
git commit -m "chore: add flamingock agent skills"
```

The skill travels with the repo, so every developer (and every CI agent) on the project gets it automatically.

---

## Contributing

Found an error or a missing pattern? Open an issue or a PR. All examples are verified against the official [Flamingock documentation](https://docs.flamingock.io).
