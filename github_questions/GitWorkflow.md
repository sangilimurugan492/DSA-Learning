# Git Workflow

## Q1: What are common Git workflows for mobile teams?

| Workflow | Branches | Best For |
|----------|----------|---------|
| GitHub Flow | `main` + feature branches | Small teams, continuous deployment |
| Git Flow | `main`, `develop`, `feature/`, `release/`, `hotfix/` | Versioned releases (mobile apps) |
| Trunk-Based | `main` + short-lived branches | Fast-moving teams, CI/CD heavy |

---

## Q2: How does GitHub Flow work?

Simplest workflow — perfect for small teams.

```
main ────────────────────────────────────
  \                                     /
   └─ feature/login ──→ PR ──→ merge ──┘
```

### Steps
1. Create branch from `main`: `git checkout -b feature/login`
2. Commit changes
3. Push: `git push -u origin feature/login`
4. Open PR to `main`
5. Review + merge
6. Delete branch

### Rules
- `main` is always deployable
- Never commit directly to `main`
- PR must pass CI before merge
- Delete branch after merge

---

## Q3: How does Git Flow work for Android apps?

Best for apps with versioned releases (Play Store).

```
main ─────────────●─────────────●────────── (tags: v1.0, v1.1)
                   \           /
develop ───●───●───●───●───●───●─────────── (always latest dev)
            \              /
feature/      ●──●──●─────┘
                              \
release/v1.1                    ●──●──●──→ merge to main + develop
```

### Branches
| Branch | Purpose | Merges to |
|--------|---------|-----------|
| `main` | Production-ready code | Tagged with version |
| `develop` | Latest development | Integration branch |
| `feature/*` | New features | `develop` |
| `release/*` | Release prep | `main` + `develop` |
| `hotfix/*` | Production fixes | `main` + `develop` |

### Example flow
```bash
# 1. Start feature from develop
git checkout develop
git pull
git checkout -b feature/payment-screen

# 2. Work and commit
git add . && git commit -m "feat: add payment screen"

# 3. Finish feature → merge to develop
git checkout develop
git merge --no-ff feature/payment-screen
git branch -d feature/payment-screen

# 4. Prepare release
git checkout -b release/v2.0.0
# Bump version, fix release-specific bugs
git commit -m "chore: bump version to 2.0.0"

# 5. Finish release → merge to main + develop
git checkout main
git merge --no-ff release/v2.0.0
git tag v2.0.0
git push --tags

git checkout develop
git merge --no-ff release/v2.0.0
git branch -d release/v2.0.0
```

---

## Q4: How do you handle hotfixes?

```bash
# Production bug! Fix from main, not develop
git checkout main
git pull
git checkout -b hotfix/crash-on-startup

# Fix the bug
git add . && git commit -m "fix: crash on startup (NPE in Application.onCreate)"

# Merge to main
git checkout main
git merge --no-ff hotfix/crash-on-startup
git tag v2.0.1
git push --tags

# Also merge to develop (so fix is in next release)
git checkout develop
git merge --no-ff hotfix/crash-on-startup

# Clean up
git branch -d hotfix/crash-on-startup
```

### Hotfix checklist
- [ ] Branch from `main` (not `develop`)
- [ ] Minimal fix — no new features
- [ ] Merge to both `main` and `develop`
- [ ] Tag new version (`v2.0.1`)
- [ ] Update Play Store rollout
- [ ] Notify team

---

## Q5: How do you manage version tags?

```bash
# Create a tag
git tag v2.0.0
git tag -a v2.0.0 -m "Release 2.0.0: Dark mode + Payment feature"

# Push tags
git push origin v2.0.0
git push origin --tags

# List tags
git tag -l

# List tags sorted by version
git tag -l --sort=-v:refname

# Checkout a specific version
git checkout v2.0.0

# Delete a tag (local + remote)
git tag -d v2.0.0
git push origin --delete v2.0.0
```

### Semantic versioning for Android
```
vMAJOR.MINOR.PATCH
  │      │     └── Bug fixes (hotfix)
  │      └──────── New features (backward compatible)
  └─────────────── Breaking changes
```

| Change | Version bump | Example |
|--------|-------------|---------|
| Bug fix | PATCH | 2.0.0 → 2.0.1 |
| New feature | MINOR | 2.0.1 → 2.1.0 |
| Breaking change | MAJOR | 2.1.0 → 3.0.0 |

---

## Q6: How do you use Git hooks for Android?

```bash
# Pre-commit hook: run lint before commit
# .git/hooks/pre-commit
#!/bin/sh
echo "Running lint..."
./gradlew lintDebug
if [ $? -ne 0 ]; then
    echo "❌ Lint failed. Fix issues before committing."
    exit 1
fi
echo "✅ Lint passed."
```

### Pre-push hook: run tests
```bash
# .git/hooks/pre-push
#!/bin/sh
echo "Running unit tests..."
./gradlew testDebugUnitTest
if [ $? -ne 0 ]; then
    echo "❌ Tests failed. Fix before pushing."
    exit 1
fi
```

### Using Husky (shared hooks)
```bash
# Install Husky
npm install husky --save-dev
npx husky init

# Add pre-commit hook
echo "./gradlew lintDebug" > .husky/pre-commit
echo "./gradlew testDebugUnitTest" > .husky/pre-push
```

---

## Q7: How do you choose a workflow for your team?

| Team Size | Release Cycle | Recommended |
|-----------|--------------|-------------|
| 1-3 devs | Continuous | GitHub Flow |
| 3-10 devs | Bi-weekly releases | Git Flow |
| 10+ devs | Feature flags + daily | Trunk-Based |
| Open source | Community PRs | GitHub Flow + fork model |

### Questions to ask
1. How often do you release? (daily → GitHub Flow, monthly → Git Flow)
2. Do you need hotfix branches? (yes → Git Flow)
3. How many devs? (small → GitHub Flow, large → Trunk-Based)
4. Do you support multiple versions? (yes → Git Flow with release branches)

---

## 🔗 Related Topics
- [Branching](Branching.md)
- [Pull Requests](PullRequests.md)
- [GitHub Actions](GitHubActions.md)
