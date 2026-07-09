# Pull Requests

## Q1: How do you create a pull request?

```bash
# Push your feature branch to remote
git push -u origin feature/login-screen

# Create PR via GitHub CLI
gh pr create --title "feat: add login screen" --body "Implements login with email/password" --base main

# Or via GitHub website:
# Push branch → Go to repo → Click "Compare & pull request"
```

### PR via CLI with body
```bash
gh pr create \
  --title "fix: crash on payment screen" \
  --body "## Changes
- Fixed NPE in PaymentViewModel
- Added null check for payment method

## Testing
- [x] Tested on emulator (API 33)
- [x] Tested on physical device (API 30)
- [x] Unit tests pass

## Ticket
PROJ-123" \
  --base main \
  --reviewer alice,bob
```

---

## Q2: What should a PR template look like?

Create `.github/PULL_REQUEST_TEMPLATE.md`:

```markdown
## Description
<!-- Brief description of what this PR does -->

## Type of Change
- [ ] Bug fix
- [ ] New feature
- [ ] Breaking change
- [ ] Refactor
- [ ] Documentation update

## Changes Made
-
-

## Testing
- [ ] Unit tests pass
- [ ] UI tested on emulator
- [ ] No new lint warnings
- [ ] Tested on API 24+ (minSdk)

## Screenshots (if UI changes)
<!-- Before/After screenshots -->

## Checklist
- [ ] Code follows project style
- [ ] Self-reviewed
- [ ] Added inline comments for complex logic
- [ ] Updated CHANGELOG if needed
- [ ] No `Log.d` left in production code
```

---

## Q3: How do you review a PR?

### Review checklist for Android PRs
| Area | What to check |
|------|--------------|
| Architecture | Follows MVVM/Clean Architecture? |
| Lifecycle | No memory leaks? Lifecycle-aware? |
| Performance | No main-thread I/O? Efficient RecyclerView? |
| Security | No hardcoded API keys? ProGuard rules? |
| Testing | Unit tests for new ViewModel logic? |
| Resources | Strings in `strings.xml`? Correct translations? |
| Dependencies | New deps needed? Version conflicts? |

### Review commands
```bash
# Checkout the PR branch locally
gh pr checkout 42

# View changed files
git diff main...feature/login-screen --stat

# View specific file diff
git diff main...feature/login-screen -- app/src/main/MainActivity.kt

# Run tests before approving
./gradlew test
./gradlew lint
```

### Review comments
```bash
# Approve PR
gh pr review 42 --approve --body "LGTM! Nice work on the login flow."

# Request changes
gh pr review 42 --request-changes --body "Please fix the memory leak in LoginViewModel."

# Comment
gh pr review 42 --comment --body "Have you considered using StateFlow instead of LiveData?"
```

---

## Q4: How do you resolve conflicts in a PR?

```bash
# Method 1: Merge main into your branch
git checkout feature/login-screen
git fetch origin
git merge origin/main
# Resolve conflicts → commit → push
git push origin feature/login-screen

# Method 2: Rebase onto main (cleaner history)
git checkout feature/login-screen
git fetch origin
git rebase origin/main
# Resolve conflicts → continue → push
git push origin feature/login-screen --force-with-lease

# Method 3: Use GitHub's web editor
# Go to PR → "Resolve conflicts" button → edit inline
```

### `--force` vs `--force-with-lease`
| Option | Safe? | When to use |
|--------|--------|-----------|
| `--force` | ❌ Risky | Overwrites remote history |
| `--force-with-lease` | ✅ Safer | Fails if someone else pushed after you |

---

## Q5: How do you squash commits before merging?

```bash
# Method 1: Interactive rebase
git checkout feature/login-screen
git rebase -i origin/main

# In the editor, change to:
pick abc1234 feat: add login layout
squash def5678 fix: fix padding
squash ghi9012 fix: fix text color
# Save → combine commit messages → push

git push origin feature/login-screen --force-with-lease

# Method 2: Squash merge on GitHub
gh pr merge 42 --squash --delete-branch
```

### When to squash
| Scenario | Squash? |
|----------|---------|
| Small feature (3-5 commits) | ✅ Yes |
| Large feature (20+ commits) | ❌ No (lose context) |
| WIP commits ("fix typo", "fix again") | ✅ Yes |
| Meaningful commits with context | ❌ No |

---

## Q6: How do you keep your PR up to date?

```bash
# Rebase onto latest main
git checkout feature/login-screen
git fetch origin
git rebase origin/main
git push --force-with-lease

# Merge latest main into your branch
git checkout feature/login-screen
git merge origin/main
git push

# Update from GitHub UI
# PR page → "Update branch" button
```

---

## Q7: What are branch protection rules?

### Setting up in GitHub
**Settings → Branches → Add rule** for `main`:

| Rule | Why |
|------|-----|
| Require PR before merging | No direct pushes to main |
| Require approvals (1-2) | Peer review mandatory |
| Require status checks pass | CI must be green |
| Require branches up to date | PR must be rebased |
| Require signed commits | GPG-signed commits only |
| Require linear history | No merge commits |
| Dismiss stale approvals | Re-review after new commits |

### Via GitHub CLI
```bash
# View branch protection
gh api repos/{owner}/{repo}/branches/main/protection

# Enforce via repo settings file
# .github/settings.yml
```

### CODEOWNERS file
```
# .github/CODEOWNERS
# Global owners
* @alice @bob

# Android-specific
app/src/main/ @android-team
app/src/test/ @qa-team

# Security-sensitive
app/src/main/java/com/app/auth/ @security-team
app/google-services.json @security-team
```

---

## 🔗 Related Topics
- [Branching](Branching.md)
- [Git Workflow](GitWorkflow.md)
- [GitHub Actions](GitHubActions.md)
