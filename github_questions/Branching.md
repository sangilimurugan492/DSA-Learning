# Branching

## Q1: How do you create and switch branches?

```bash
# Create a new branch
git branch feature/login-screen

# Switch to it
git checkout feature/login-screen
# OR (newer syntax)
git switch feature/login-screen

# Create and switch in one command
git checkout -b feature/login-screen
# OR
git switch -c feature/login-screen

# List all branches
git branch -a

# List remote branches only
git branch -r
```

---

## Q2: What are branch naming conventions for Android projects?

| Prefix | Use Case | Example |
|--------|----------|---------|
| `feature/` | New feature | `feature/dark-mode` |
| `fix/` | Bug fix | `fix/crash-on-startup` |
| `hotfix/` | Production hotfix | `hotfix/payment-crash` |
| `release/` | Release prep | `release/v2.0.0` |
| `chore/` | Maintenance | `chore/update-deps` |
| `refactor/` | Code refactoring | `refactor/auth-module` |

### Rules
- Use lowercase with hyphens: `feature/dark-mode` not `Feature/DarkMode`
- Keep it short but descriptive: `fix/login-crash` not `fix/bug`
- Include ticket number if applicable: `feature/PROJ-123-offline-sync`

---

## Q3: How do you merge branches?

```bash
# Switch to target branch (e.g., main)
git checkout main

# Merge feature branch
git merge feature/login-screen

# Merge with no fast-forward (preserves branch history)
git merge --no-ff feature/login-screen

# Merge with squash (combines all commits into one)
git merge --squash feature/login-screen
git commit -m "feat: add login screen"

# Abort a merge (if conflicts are too complex)
git merge --abort
```

### Merge types
| Type | History | Use Case |
|------|---------|----------|
| Fast-forward (default) | Linear | Solo dev, clean history |
| `--no-ff` | Merge commit visible | Team, preserves feature context |
| `--squash` | Single commit | Small features, WIP cleanup |

---

## Q4: How do you rebase a feature branch?

```bash
# Rebase feature branch onto latest main
git checkout feature/login-screen
git fetch origin
git rebase origin/main

# Resolve conflicts during rebase
# After fixing conflicts:
git add .
git rebase --continue

# Skip a commit (if needed)
git rebase --skip

# Abort rebase
git rebase --abort

# Interactive rebase (squash, reorder, edit commits)
git rebase -i HEAD~3
```

### Interactive rebase options
| Command | Action |
|---------|--------|
| `pick` | Keep commit as-is |
| `squash` | Combine with previous commit |
| `reword` | Change commit message |
| `drop` | Delete commit |
| `edit` | Pause and amend |

---

## Q5: How do you delete branches?

```bash
# Delete local branch (safe — fails if not merged)
git branch -d feature/login-screen

# Force delete (even if not merged)
git branch -D feature/login-screen

# Delete remote branch
git push origin --delete feature/login-screen

# Clean up deleted remote branches locally
git fetch --prune
# OR
git remote prune origin
```

---

## Q6: How do you handle merge conflicts?

```bash
# When merge fails, Git marks conflicted files
git status
# both modified: app/src/main/res/values/strings.xml

# Open the file — you'll see:
<<<<<<< HEAD
<string name="welcome">Welcome back!</string>
=======
<string name="welcome">Hello there!</string>
>>>>>>> feature/new-welcome

# Resolve: pick one or combine, remove markers
<string name="welcome">Welcome back!</string>

# Stage the resolved file
git add app/src/main/res/values/strings.xml

# Continue the merge
git commit -m "Merge feature/new-welcome: resolve strings.xml conflict"

# For rebase conflicts
git rebase --continue
```

### Conflict resolution tools
```bash
# Use a visual merge tool
git mergetool

# Use Android Studio's merge tool
# Git → Resolve Conflicts → Merge

# Abort and start over
git merge --abort
```

### Common Android conflict files
| File | Why conflicts happen |
|------|---------------------|
| `strings.xml` | Multiple devs adding strings |
| `AndroidManifest.xml` | Adding activities/permissions |
| `build.gradle` | Adding dependencies |
| `nav_graph.xml` | Adding navigation destinations |

---

## Q7: How do you cherry-pick a commit?

```bash
# Get the commit hash from another branch
git log --oneline feature/hotfix

# Cherry-pick it to current branch
git cherry-pick <commit-hash>

# Cherry-pick multiple commits
git cherry-pick <hash1> <hash2> <hash3>

# Cherry-pick a range
git cherry-pick <start-hash>..<end-hash>

# Cherry-pick without committing (stage only)
git cherry-pick --no-commit <commit-hash>
```

### When to cherry-pick
- Hotfix needs to go to both `main` and `develop`
- A specific commit from a feature branch is needed urgently
- Picking a bug fix without merging the whole branch

---

## 🔗 Related Topics
- [Git Basics](GitBasics.md)
- [Pull Requests](PullRequests.md)
- [Git Workflow](GitWorkflow.md)
