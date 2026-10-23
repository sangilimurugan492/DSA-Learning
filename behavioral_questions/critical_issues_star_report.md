flow# Critical Issues We Fixed — And How We Did It

**Samsung Shop App | June 19–23, 2026**  
*A quick walkthrough for our parent company on the tough problems we tackled this week*

---

## What Happened This Week

Last week was intense. We had **5 serious issues** hit us — from the app literally crashing on launch in China, to customers in Austria paying for products but getting stuck in the browser, to our new AI Chat feature freezing the entire app. Each one had real customers affected, real revenue at stake, and real pressure to fix fast.

Here's the honest story of what went wrong, what we did about it, and what we learned — told in the **STAR** format (Situation → Task → Action → Result).

---

## 🔴 Issue 1: "The app won't even open" — Firebase Crash in China

**PRs:** #7754, #7743, #7741 | **Merged:** June 22–23 | **Impact:** China, Global, SEA

### What was happening (Situation)

Imagine being a customer in China, tapping the Samsung Shop app icon, and... nothing. The app crashes before it even shows the splash screen. Not sometimes — **every single time**. The app was completely unusable for our China users.

The culprit? Our code was trying to start Firebase Performance monitoring right at launch — but Firebase doesn't work in China because Google services aren't available there. The app would try to create a Firebase instance, hit a wall, and just crash. No fallback, no error handling, no graceful exit. Just a dead app.

And it wasn't just China — Global and SEA flavors were also getting initialization errors on splash, though not as severe.

### What we needed to do (Task)

We had to stop the bleeding immediately. The app needed to work in China. We also needed to make sure this kind of thing never happens again — not just for Firebase, but for any service that might not be available in every region.

### What we actually did (Action)

We attacked this from three angles at once:

1. **Upgraded the Firebase plugins** across all our wrapper packages for both Global and SEA — the newer versions had better initialization handling (PRs #7743, #7741)

2. **Made Firebase Performance conditional** — instead of blindly starting it on every launch, we now check: "Is this China? Skip it. Is the config flag turned on? No? Skip it." Firebase Performance only starts after the splash screen completes, only when the remote config says it's okay, and never in China (PR #7754)

3. **Added proper error handling** — even if something goes wrong during initialization, the app won't crash. It logs the error and keeps going

### What happened after (Result)

China users can open the app again. Period. No more crashes. Global and SEA are clean too. And here's the bonus — we can now turn Firebase Performance on or off remotely through our config, without needing an app update. If something goes wrong with Firebase in the future, we can kill it with a config switch.

**Time to fix: Under 24 hours** from when we first saw the crash reports to having the fix merged.

---

## 🔴 Issue 2: "The app is stuck on loading forever" + "Nobody's reading my messages"

**PR:** #7744 | **Merged:** June 23 | **Impact:** Global (AI Chat users)

### What was happening (Situation)

This one was two bugs that hit our AI Chat feature:

**Bug 1 — The Infinite Spinner:** When you opened the AI Chat, sometimes the app would just... freeze. Stuck on a loading spinner forever. No error message, no retry button, no way out. You'd have to kill the app and start over. And sometimes even that didn't help.

What was happening behind the scenes: the chat needed to verify something with the server (a "challenge" API call) before showing you the chat. If that call failed — bad network, server hiccup, timeout — the app just stayed in "loading" state. Forever. There was no code to handle the failure. It was like waiting for a bus that's never coming.

**Bug 2 — The Phantom Unread Messages:** Even when the chat worked, something subtle was broken. You'd read a message from the AI assistant, but the server still thought you hadn't. The little "unread" badge wouldn't go away. Why? Our code was checking if you'd scrolled to the **top** of the chat (where old messages are) instead of the **bottom** (where new messages appear) to decide whether to mark messages as read. It was backwards.

### What we needed to do (Task)

Fix the stuck loading screen — no user should ever be trapped on a spinner. Fix the read receipt logic so messages actually get marked as read. And while we're at it, add a feature toggle for a background connection (Agent Monitor SSE) that was running unnecessarily for some users.

### What we actually did (Action)

**For the stuck loading:**
- We taught the app what to do when things go wrong. If the server call fails, instead of staying in "loading" forever, the app now transitions to an error state and shows the user what happened
- We fixed the logic that decides whether the chat is "loaded" — it was ignoring error states entirely
- If the terms-and-conditions dialog needs to show up immediately, it now does — instead of being blocked by the loading state

**For the read receipts:**
- We flipped the scroll check from `minScrollExtent` (top) to `maxScrollExtent` (bottom) — now the app correctly knows when you've scrolled down to see new messages
- We added a smart check: if a message is short enough to fit on screen without needing to scroll, the read receipt triggers automatically. No scroll needed

**For the background connection:**
- Added a config toggle (`aiChatAgentMonitorEnabled`, default: off) so the background SSE connection only starts when we actually want it to

**Tests — we didn't just fix the bugs, we proved the fixes work:**
- 51 lines of new unit tests for the chat bloc
- 93 lines of new widget tests for the splash page
- Tests for the challenge handling and auth interceptor too

### What happened after (Result)

No more infinite spinners. If something fails, the app tells you and moves on. Read receipts work correctly — when you read a message, it's actually marked as read. The unnecessary background connection is off by default, saving battery and data. And we have the tests to prove all of this works.

**Time to fix: Under 24 hours.** And we shipped it with confidence because of the test coverage.

---

## 🟠 Issue 3: "I paid but the app disappeared" — Klarna Payment in Austria

**PRs:** #7745, #7749 | **Merged:** June 23 | **Impact:** Austria (AT market)

### What was happening (Situation)

Here's a scary user experience: You're in Austria, you've picked out a Samsung product, you choose Klarna as your payment method, you get redirected to Klarna's payment page in the browser, you enter your details, you confirm the payment... and then you're stuck in the browser. The app never opens back up. You just paid, but you can't see your order confirmation. You don't even know if the order went through.

This was our reality for AT market customers using Klarna. After completing payment, the browser had no way to send the user back to our app. The deep link redirect was broken. Customers were calling support asking "where's my order?" — even though the order was actually placed successfully. Support tickets spiked. Checkout conversion dropped. Trust eroded.

### What we needed to do (Task)

Make the payment flow work end-to-end: customer pays with Klarna → Klarna finishes → customer lands back in our app → sees the confirmation page. Simple in concept, critical in execution.

### What we actually did (Action)

1. **Added the missing Android intent-filter** — our app wasn't registered to handle the Klarna callback URL. We added the proper intent-filter declarations in `AndroidManifest.xml` for both production and staging builds. Now when Klarna says "go back to the app," Android knows how to open us.

2. **Removed some broken deep link code** — there was old code in `deeplink_util.dart` that was intercepting the Klarna redirect and mishandling it. Better to remove it and let the clean intent-filter do its job.

3. **Fixed the confirmation page redirect** (PR #7749) — after payment, the app now correctly opens the web browser confirmation page for Klarna orders.

### What happened after (Result)

The payment flow works as customers expect: pay with Klarna → come back to the app → see your confirmation. Support tickets for "lost orders" should drop significantly. AT market checkout conversion should recover. And we applied the fix to both production and staging, so testing future payment changes will be accurate too.

**Time to fix: Under 48 hours** — a bit longer because we needed to test the actual Klarna flow carefully, but we got it done before the next business day.

---

## 🟠 Issue 4: "Tapping the widget does nothing" — iOS Deeplink Regression

**PR:** #7740 | **Merged:** June 22 | **Impact:** India (SIEL)

### What was happening (Situation)

Sometimes the fix is the problem. A few days earlier, we had merged a PR to fix deeplink navigation for guest users on iOS — people who hadn't logged in yet but tapped a home screen widget to go to a product page. The original bug was that guest users couldn't navigate via widgets.

The fix worked for guest users... but broke it for **everyone else**. After that PR was merged, tapping any iOS widget — whether you were logged in or not — would just open the app to the home screen instead of taking you to the product. Marketing campaigns that relied on deeplinks to drive traffic? Broken. Widget-driven engagement? Gone.

We had accidentally made things worse while trying to make them better.

### What we needed to do (Task)

Stop the bleeding. Revert the problematic change immediately to restore deeplink navigation for the majority of users. Then plan a proper fix for the guest user issue that doesn't break everything else.

### What we actually did (Action)

1. **Reverted the entire PR** — we traced the regression back to changes in `app_entry.dart` and a new sync task in `sync_sub_tasks_users.dart`. The revert was clean: just 2 lines added, 34 lines removed.

2. **Verified deeplinks worked again** for authenticated users after the revert

3. **Flagged the guest user issue** for a proper re-implementation — the next attempt needs integration tests specifically for guest vs. authenticated deeplink flows

### What happened after (Result)

Deeplink navigation is back to normal for all iOS users. Marketing campaigns work again. Widget taps go to the right place. The guest user issue is still open but tracked — we'll fix it properly with tests next time instead of rushing a fix that creates a bigger problem.

**Time to fix: Under 24 hours** from regression detection to revert. Lesson learned: always test both guest and authenticated paths when changing navigation logic.

---

## 🟡 Issue 5: "Why does my tablet look like a phone?" — Inconsistent Layout Detection

**PR:** #7739 | **Merged:** June 22 | **Impact:** Global (tablet & foldable users)

### What was happening (Situation)

If you opened the Samsung Shop app on a tablet, you might see the tablet layout on the home screen... but then the phone layout on the product page. Or vice versa. Same device, different layouts depending on which screen you were on.

For foldable device users (Galaxy Z Fold), it was even weirder — the layout might not update when you unfolded or folded the phone, because the app was using a stale cached screen size instead of checking the actual current size.

The problem? Our tablet detection code was looking at a saved/cached device size from when the app first started, not the real-time size. For foldables and orientation changes, that cached value was wrong.

### What we needed to do (Task)

Make tablet detection use the real, current device size — not a stale cached value. Keep it simple, keep it safe.

### What we actually did (Action)

Changed one line. Instead of referencing the cached device size, we now use `currentDeviceSize` — which always reflects the actual screen dimensions right now, whether you just unfolded your phone, rotated your tablet, or resized a window.

### What happened after (Result)

Tablets show tablet layouts consistently across all screens. Foldable devices switch layouts correctly when you fold/unfold. The fix is one line — minimal risk, maximum correctness. This also unblocks our new breakpoint design work (GSA-3981) which relies on accurate device size detection.

**Time to fix: Under 12 hours.** Sometimes the best fix is a small one.

---

## The Big Picture

| | |
|---|---|
| **Issues fixed** | 5 |
| **PRs merged** | 8 |
| **Code changed** | 527 lines added, 184 lines removed |
| **Regions affected** | Global, SEA, China, Austria, India |
| **Average fix time** | Under 24 hours |
| **New tests added** | 144+ lines |
| **App crashes eliminated** | 2 |
| **Payment flows restored** | 1 |
| **Navigation issues fixed** | 2 |

---

## What We're Taking Away From This

**1. Region-specific code needs region-specific handling.**  
The China crash taught us that we can't assume every service is available everywhere. We're going to build a centralized "flavor-aware service registry" — a way to say "this service is available in these regions" so we never accidentally try to start Firebase in China again.

**2. Every "loading" state needs an escape hatch.**  
The AI Chat spinner taught us that any async operation that can fail must have error handling. If the server doesn't respond, the user should know — not stare at a spinner forever. We're adding this as a code review checkpoint.

**3. Test both paths, not just the one you're fixing.**  
The iOS deeplink regression happened because we tested the guest path but didn't verify the authenticated path still worked. Our upcoming Patrol integration testing framework (PR #7746) will help catch these automatically.

**4. Payment flows need end-to-end testing.**  
The Klarna issue should have been caught before it reached customers. We're going to add automated tests that simulate the full payment redirect flow so broken redirects never make it to production again.

**5. Don't cache things that change.**  
The tablet layout bug was caused by caching a value that's different on foldables and during orientation changes. Simple rule: if it can change, don't cache it — or at least invalidate the cache when it does change.

---

*This report was put together on June 23, 2026 by the Samsung Shop App development team. We're proud of how quickly we responded to these issues, and we're committed to learning from each one so our customers never have to experience them again.*