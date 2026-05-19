# flamingock-skills

A collection of AI agent skills for the [Flamingock](https://flamingock.io) ecosystem. Each skill teaches an AI coding assistant how to work correctly with Flamingock APIs, conventions, and patterns.

## Available skills

| Skill | Description |
|-------|-------------|
| [`flamingock-onboarding`](./flamingock-onboarding/) | Configure Flamingock's entry point in a project — standalone Java or Spring Boot, Community or Cloud edition — adding the correct dependencies and writing the initialization code |
| [`flamingock-mongodb-sync-targetsystem`](./flamingock-mongodb-sync-targetsystem/) | Configure `MongoDBSyncTargetSystem` for standalone or Spring Boot Java/Kotlin projects using the MongoDB sync driver, with language-gated single-path output and TargetSystem-only scope |
| [`flamingock-mongodb-sync-change`](./flamingock-mongodb-sync-change/) | Create or review Java/Kotlin `@Change` classes for `MongoDBSyncTargetSystem`, with explicit language gating, strict registration verification, DML-vs-DDL routing, rollback-safe guidance, and hard redirects for setup or Spring Data concerns |

---

## What is a skill?

A skill is a folder containing a `SKILL.md` file (and optionally `references/` and `assets/` directories) that an AI agent loads when it detects a relevant context. Once installed, the agent uses the skill's rules and examples instead of guessing from general knowledge.

This matters for Flamingock because the API has specific contracts that a general-purpose model is likely to get wrong. Skills encode the correct patterns so the agent produces accurate, production-ready code from the start.

---

## Installation

Skills are installed per project. Copy the skill folder into your project's `.agents/skills/` directory.

### Step 1 — Create the skills directory (if it doesn't exist)

```bash
mkdir -p your-project/.agents/skills/
```

### Step 2 — Copy the skill

```bash
cp -r flamingock-onboarding/ your-project/.agents/skills/
# or
cp -r flamingock-mongodb-sync-targetsystem/ your-project/.agents/skills/
# or
cp -r flamingock-mongodb-sync-change/ your-project/.agents/skills/
```

Your project structure should look like this:

```
your-project/
└── .agents/
    └── skills/
        ├── flamingock-onboarding/
        │   ├── SKILL.md
        │   └── references/
        │       └── ...
        ├── flamingock-mongodb-sync-targetsystem/
        │   ├── SKILL.md
        │   └── references/
        │       └── ...
        └── flamingock-mongodb-sync-change/
            ├── SKILL.md
            └── references/
                └── ...
```

### Step 3 — Commit it

```bash
cd your-project
git add .agents/skills/flamingock-*/
git commit -m "chore: add flamingock agent skill"
```

The skill travels with the repo, so every developer (and every CI agent) on the project gets it automatically.

---

## Contributing

Found an error or a missing pattern? Open an issue or a PR. All examples are verified against the official [Flamingock documentation](https://docs.flamingock.io).
