# TextJustification — Detailed Explanation

> **LeetCode** | https://leetcode.com/problems/text-justification/  
> **Topic:** simulation — text_justification

---

## 📋 Problem Statement

Given an array of strings `words` and a width `maxWidth`, format the text such that each line has exactly `maxWidth` characters and is fully (left and right) justified.

**Rules:**
- Greedily pack as many words as possible in each line.
- Pad extra spaces `' '` when needed so each line has exactly `maxWidth` characters.
- Distribute extra spaces as evenly as possible between words on the same line. If the number of spaces does not divide evenly, the left slots get more spaces.
- Last line is left-justified (no extra space between words, pad spaces at end).
- A line with only one word is left-justified.

**Example 1:**
- Input: `words = ["This", "is", "an", "example", "of", "text", "justification."]`, `maxWidth = 16`
- Output: `["This    is    an","example  of text","justification.  "]`

**Example 2:**
- Input: `words = ["What","must","be","acknowledgment","shall","be"]`, `maxWidth = 16`
- Output: `["What   must   be","acknowledgment  ","shall be        "]`

---

## 🎯 The Problem in Simple Terms

You're given a list of words and a `maxWidth` (say 16). You need to arrange the words into lines where **every line is exactly `maxWidth` characters long**. You do this by adding extra spaces between words.

**Example:**
```
words = ["This", "is", "an", "example", "of", "text", "justification."]
maxWidth = 16
```

**Output:**
```
"This    is    an"      ← 16 chars
"example  of text"      ← 16 chars
"justification.  "      ← 16 chars (last line, left-justified)
```

---

## 📋 The 3 Rules

1. **Pack greedily**: Fit as many words as possible on each line (separated by at least 1 space).
2. **Middle lines**: Distribute extra spaces **evenly** between words. If they don't divide evenly, **left slots get more**.
3. **Last line (or single-word line)**: Left-justified — just 1 space between words, pad the rest at the end.

---

## 🔍 Step-by-Step Walkthrough of the Code

### Step 1: Pick words for a line (lines 35–42)

```kotlin
var j = i + 1
var lineLength = words[i].length   // start with first word's length
```

We try to add words one by one. Each word needs **at least 1 space** before it. So the check is:

```kotlin
lineLength + 1 + words[j].length <= maxWidth
```

**Example with first line:**
- Start: `lineLength = 4` ("This")
- Add "is": `4 + 1 + 2 = 7 ≤ 16` ✅ → `lineLength = 7`
- Add "an": `7 + 1 + 2 = 10 ≤ 16` ✅ → `lineLength = 10`
- Add "example": `10 + 1 + 7 = 18 > 16` ❌ → stop!

So words `i=0` to `j=3` (i.e., "This", "is", "an") go on line 1.

### Step 2: Calculate how many spaces to distribute (line 46)

```kotlin
val numWords = j - i          // 3 words
val numSpaces = maxWidth - (lineLength - (numWords - 1))
```

**What does `lineLength - (numWords - 1)` mean?**
- `lineLength` includes the words **plus** the minimum 1 space between each pair.
- `numWords - 1` = number of gaps between words (each gap has 1 space).
- So `lineLength - (numWords - 1)` = **total characters of just the words** (no spaces).

**Example:**
- `lineLength = 10` (includes "This" + " " + "is" + " " + "an")
- `numWords - 1 = 2` (2 gaps)
- Pure word chars = `10 - 2 = 8`
- `numSpaces = 16 - 8 = 8` → we have **8 spaces** to distribute across **2 gaps**

### Step 3: Distribute spaces (lines 58–68)

```kotlin
val spaceSlots = numWords - 1              // 2 gaps
val baseSpaces = numSpaces / spaceSlots     // 8 / 2 = 4
val extraSpaces = numSpaces % spaceSlots    // 8 % 2 = 0
```

- Each gap gets `baseSpaces` = 4 spaces
- The first `extraSpaces` gaps get **1 extra** space

**Result:** `"This    is    an"` (4 spaces between each word) ✅

**Another example where it doesn't divide evenly:**
- Say `numSpaces = 9`, `spaceSlots = 2`
- `baseSpaces = 4`, `extraSpaces = 1`
- Gap 0 gets `4 + 1 = 5` spaces, Gap 1 gets `4` spaces
- Left gap gets more — that's the rule!

The key line:
```kotlin
val spacesToApply = baseSpaces + (if (k - i < extraSpaces) 1 else 0)
```
- `k - i` is the gap index (0, 1, 2, ...)
- If gap index < `extraSpaces`, add 1 extra space

### Step 4: Last line or single word (lines 49–55)

```kotlin
if (j == words.size || numWords == 1)
```

- `j == words.size` → we've reached the last line
- `numWords == 1` → only one word on this line

In both cases: **left-justify** — put 1 space between words, pad the end with spaces.

**Example (last line):**
```
"justification."  →  "justification.  "  (padded to 16)
```

---

## 🧠 Visual Summary

```
Line 1: "This    is    an"
         This + 4spaces + is + 4spaces + an = 16 ✅

Line 2: "example  of text"
         Words: example(7) + of(2) + text(4) = 13 chars
         Spaces needed: 16 - 13 = 3, across 2 gaps
         baseSpaces = 1, extraSpaces = 1
         Gap 0: 1+1=2 spaces, Gap 1: 1 space
         "example  of text" ✅

Line 3: "justification.  "  (last line → left-justified, pad end)
```

---

## 💡 Key Intuition

Think of it like a **word processor**:
1. **Fill** a line with as many words as possible
2. **Count** how many extra spaces you need to fill the line
3. **Spread** those spaces between words (left-heavy if uneven)
4. **Exception**: last line = just left-align, pad the right side

The trickiest part is the space math. Just remember:
- `numSpaces = maxWidth - (total word characters only)`
- Divide `numSpaces` by number of gaps → `baseSpaces`
- Remainder → first few gaps get +1 extra

---

## 🧩 Method 1: Brute Force

### Core Idea

See implementation in `TextJustification.kt` for details.

### Complexity

| Metric | Value |
|--------|-------|
| **Time** | O(N) |
| **Space** | O(N) |

---

## 🧩 Method 2: Optimal

### Core Idea

See implementation in `TextJustification.kt` for details.

### Complexity

| Metric | Value |
|--------|-------|
| **Time** | O(N) |
| **Space** | O(1) |

---

## ⏱️ Complexity Summary

| Metric | Value | Why |
|--------|-------|-----|
| **Time** | O(N) | Each word is visited once |
| **Space** | O(N) | Output list stores all lines |

---

## 🔑 Key Takeaways

1. See the `.kt` file for full implementation and inline comments.
2. Refer to the LeetCode problem for detailed examples.
3. The space distribution logic is the core challenge — divide evenly, left-heavy on remainder.
4. Last line and single-word lines are always left-justified.

---

## 📚 Related Problems

| Problem | LeetCode | Difficulty |
|---------|----------|------------|
| TextJustification | [https://leetcode.com/problems/text-justification/](https://leetcode.com/problems/text-justification/) | Hard |
