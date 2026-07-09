# Git & GitHub Scenario-Based Questions

## Scenario 1: Merge Conflict in `strings.xml`

### Problem
Two developers added new strings to `strings.xml` on different branches. Merging causes a conflict.

```xml
<!-- Developer A added: -->
<string name="welcome_message">Welcome back!</string>

<!-- Developer B added: -->
<string name="logout_button">Logout</string>
```

### Resolution
```bash
git checkout main
git merge feature/welcome-screen
# CONFLICT in app/src/main/res/values/strings.xml
```

Open the file:
```xml
<resources>
    <string name="app_name">MyApp</string>
<<<<<<< HEAD
    <string name="welcome_message">Welcome back!</string>
=======
    <string name="logout_button">Logout</string>
>>>>>>> feature/welcome-screen
</resources>
```

Resolve — keep both:
```xml
<resources>
    <string name="app_name">MyApp</string>
    <string name="welcome_message">Welcome back!</string>
    <string name="logout_button">Logout</string>
</resources>
```

```bash
git add app/src/main/res/values/strings.xml
git commit -m "Merge feature/welcome-screen: resolve strings.xml conflict"
```

### Tip
Add strings at the **bottom** of `strings.xml` to reduce conflicts. Sort alphabetically.

---

## Scenario 2: Accidental Force Push

### Problem
You ran `git push --force` on `main` and overwrote a teammate's commits.

### Recovery
```bash
# 1. Check reflog for the lost commits
git reflog
# abc1234 HEAD@{0}: push origin main
# def5678 HEAD@{1}: pull origin main  ← teammate's commits were here

# 2. Reset to the commit before force push
git reset --hard def5678

# 3. Force push back (with lease)
git push --force-with-lease origin main

# 4. Notify your team to pull again
```

### Prevention
```bash
# Set up branch protection (no force push to main)
# GitHub → Settings → Branches → Protect main → Allow force pushes: OFF

# Always use --force-with-lease instead of --force
git push --force-with-lease origin feature/my-branch
```

---

## Scenario 3: Hotfix for Production Crash

### Problem
App crashes on startup for Android 12 devices. Need to fix and release immediately.

### Steps
```bash
# 1. Create hotfix branch from main (not develop)
git checkout main
git pull origin main
git checkout -b hotfix/android12-crash

# 2. Fix the crash
# Edit AndroidManifest.xml — remove exported=false for main activity
git add .
git commit -m "fix: Android 12 crash — set android:exported=true on MainActivity"

# 3. Merge to main
git checkout main
git merge --no-ff hotfix/android12-crash

# 4. Tag the hotfix
git tag v2.1.1
git push origin main --tags

# 5. Merge to develop (so fix is in next regular release)
git checkout develop
git merge --no-ff hotfix/android12-crash
git push origin develop

# 6. Clean up
git branch -d hotfix/android12-crash

# 7. Build and deploy
# GitHub Actions triggers on tag v* → builds AAB → deploys to Play Store
```

### Key points
- Branch from `main`, not `develop`
- Merge to **both** `main` and `develop`
- Tag with patch version (`v2.1.1`)
- Keep fix minimal — no new features

---

## Scenario 4: Recover a Deleted Branch

### Problem
You deleted a feature branch locally and remotely, but realized you still needed it.

### Recovery
```bash
# 1. Find the last commit hash via reflog
git reflog
# abc1234 HEAD@{5}: commit: feat: add payment screen  ← this is the branch tip

# 2. Recreate the branch at that commit
git branch feature/payment-screen abc1234

# 3. Switch to it
git checkout feature/payment-screen

# 4. Push to remote
git push -u origin feature/payment-screen
```

### If reflog is expired (>90 days)
```bash
# Use GitHub's Events API
gh api repos/{owner}/{repo}/events | jq '.[] | select(.type == "DeleteEvent")'

# Or check GitHub's branch protection / audit log
```

---

## Scenario 5: Squash WIP Commits Before PR

### Problem
Your feature branch has 8 commits including WIP, typo fixes, and debug logs. You want a clean PR with 1 commit.

```bash
git log --oneline
# h8i9j01 fix: remove debug log
# g7h8i90 fix: fix typo
# f6g7h89 wip: working on payment
# e5f6g78 wip: payment UI
# d4e5f67 feat: add payment ViewModel
# c3d4e56 feat: add payment model
# b2c3d45 feat: add payment API
# a1b2c34 feat: add payment screen layout
```

### Solution
```bash
# Interactive rebase — squash all into 1
git rebase -i origin/main

# In editor, change all but first to squash:
pick a1b2c34 feat: add payment screen layout
squash b2c3d45 feat: add payment API
squash c3d4e56 feat: add payment model
squash d4e5f67 feat: add payment ViewModel
squash e5f6g78 wip: payment UI
squash f6g7h89 wip: working on payment
squash g7h8i90 fix: fix typo
squash h8i9j01 fix: remove debug log

# Save → edit combined commit message:
# feat: add payment screen with API integration
#
# - Payment screen with card input
# - PaymentViewModel with StateFlow
# - Retrofit API integration
# - Unit tests for payment flow

# Force push the rebased branch
git push --force-with-lease origin feature/payment-screen
```

### Result
```bash
git log --oneline
# x9y8z76 feat: add payment screen with API integration  ← clean!
```

---

## Scenario 6: CI Fails on PR — How to Debug

### Problem
Your PR fails CI. The error shows:
```
> Task :app:compileDebugKotlin FAILED
e: file:///app/src/main/MainActivity.kt:42:30
  Type mismatch: inferred type is String but Int was expected.
```

### Debug steps
```bash
# 1. Reproduce locally
./gradlew compileDebugKotlin

# 2. Fix the type mismatch in MainActivity.kt:42
# val count: Int = "0"  ← wrong
# val count: Int = 0    ← fixed

# 3. Run full CI locally
./gradlew assembleDebug testDebugUnitTest lintDebug

# 4. Commit fix
git add .
git commit -m "fix: type mismatch in MainActivity"

# 5. Push — CI re-runs automatically
git push origin feature/my-branch
```

### Common CI failures
| Error | Fix |
|-------|-----|
| Compile error | Fix code locally |
| Lint error | Run `./gradlew lintDebug` locally |
| Test failure | Run `./gradlew testDebugUnitTest` |
| Out of memory | Add `org.gradle.jvmargs=-Xmx4g` to `gradle.properties` |
| SDK not found | Check `JAVA_HOME` and JDK version in workflow |

---

## Scenario 7: Two Devs Edit Same ViewModel

### Problem
Dev A and Dev B both modified `LoginViewModel.kt`. Dev A's PR is merged first. Dev B's PR now has conflicts.

### Resolution
```bash
# Dev B pulls latest main
git checkout feature/login-validation
git fetch origin
git rebase origin/main

# CONFLICT in LoginViewModel.kt
# Open the file:
<<<<<<< HEAD (main — Dev A's changes)
fun validate(email: String): Boolean {
    return email.contains("@")
}
=======
fun validate(email: String, password: String): Boolean {
    return email.contains("@") && password.length >= 8
}
>>>>>>> feature/login-validation (Dev B's changes)

# Resolve: combine both approaches
fun validate(email: String, password: String): Boolean {
    return email.contains("@") && password.length >= 8
}

# Continue rebase
git add app/src/main/.../LoginViewModel.kt
git rebase --continue

# Push
git push --force-with-lease origin feature/login-validation
```

### Prevention tips
- Communicate with your team about who's working on what
- Pull/rebase frequently (daily) to catch conflicts early
- Keep PRs small — fewer changes = fewer conflicts
- Split large files into smaller ones (Single Responsibility)

---

## 🔗 Related Topics
- [Git Basics](GitBasics.md)
- [Branching](Branching.md)
- [Pull Requests](PullRequests.md)
- [Git Workflow](GitWorkflow.md)
- [GitHub Actions](GitHubActions.md)
