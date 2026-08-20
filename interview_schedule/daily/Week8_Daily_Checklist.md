# 📅 Week 8: Compose Advanced + Scenarios + Flutter Scenarios + Testing
> **Oct 8 (Thu) – Oct 14 (Wed)** | Topics: 23 files | DSA: ~18 problems
> [← Week 7](Week7_Daily_Checklist.md) | [Back to Daily Checklist](../Daily_Checklist.md) | [Week 9 →](Week9_Daily_Checklist.md)

---

## Day 50 — Thursday, 08/10/2026

**🤖 Compose Advanced (45 min):**
- [ ] 🟡 Architecture — [Architecture.md](../../jetpack_compose_questions/advanced/Architecture.md) — *MVI, unidirectional flow, state holders*
- [ ] 🟡 Custom Layouts — [CustomLayouts.md](../../jetpack_compose_questions/advanced/CustomLayouts.md) — *Layout composable, MeasurePolicy, SubcomposeLayout*

**🐦 Flutter Scenarios (45 min):**
- [ ] 🟡 Debugging Scenarios — [DebuggingScenarios.md](../../flutter_questions/scenario_based/DebuggingScenarios.md) — *Rebuild issues, memory leaks*
- [ ] 🟡 Navigation Scenarios — [NavigationScenarios.md](../../flutter_questions/scenario_based/NavigationScenarios.md) — *Deep links, nested nav, bottom nav*

**🔄 Cross-Platform Insight:**
- Compose MVI (unidirectional) ↔ Flutter BLoC (events → states) — both unidirectional data flow

**🧮 DSA Practice (30 min):**
- [ ] **[Simulation › Spiral Matrix | 🟡 Medium]** Spiral Matrix (review) (25 min) — [SpiralMatrix.kt](../../DSA/simulation/spiral_matrix/SpiralMatrix.kt)
- [ ] **[Stack › Valid Parentheses | 🟢 Easy]** Valid Parentheses (review) (15 min) — [ValidParentheses.kt](../../DSA/stack/valid_parentheses/ValidParentheses.kt)

**✅ End-of-Day Self-Check:**
- [ ] Can create a custom Layout in Compose
- [ ] Can explain MVI architecture
- [ ] Can debug Flutter rebuild issues
- [ ] Solved 2 DSA problems

---

## Day 51 — Friday, 09/10/2026

**🤖 Compose Advanced (45 min):**
- [ ] 🟡 Internals — [Internals.md](../../jetpack_compose_questions/advanced/Internals.md) — *Slot table, recomposition, applier*
- [ ] 🟡 Interop — [Interop.md](../../jetpack_compose_questions/advanced/Interop.md) — *AndroidView, ViewInterop, ComposeView*

**🐦 Flutter Scenarios (45 min):**
- [ ] 🟡 Performance Scenarios — [PerformanceScenarios.md](../../flutter_questions/scenario_based/PerformanceScenarios.md) — *Jank, rebuilds, memory*
- [ ] 🟡 State Management Scenarios — [StateManagementScenarios.md](../../flutter_questions/scenario_based/StateManagementScenarios.md) — *Complex state, persistence*

**🔄 Cross-Platform Insight:**
- Compose Internals (slot table) ↔ Flutter Internals (element tree) — both have internal tree representations

**🧮 DSA Practice (30 min):**
- [ ] **[Array › Two Pointer In-Place | 🟡 Medium]** Sorted Squares (20 min) — [SortedSquares.kt](../../DSA/array/two_pointer_inplace/sorted_squares/SortedSquares.kt)
- [ ] **[Array › Two Pointer In-Place | 🟡 Medium]** Rotate Array (20 min) — [RotateArray.kt](../../DSA/array/two_pointer_inplace/rotate_array/RotateArray.kt)

**✅ End-of-Day Self-Check:**
- [ ] Can explain Compose slot table and recomposition
- [ ] Can use AndroidView for interop
- [ ] Can optimize Flutter performance (jank, rebuilds)
- [ ] Solved 2 DSA problems

---

## Day 52 — Saturday, 10/10/2026 (Weekend — 4-5 hrs)

**🤖 Compose Advanced (1 hr):**
- [ ] 🟡 Performance — [Performance.md](../../jetpack_compose_questions/advanced/Performance.md) — *stability, skippable, key, derivedStateOf*
- [ ] 🟡 Testing — [Testing.md](../../jetpack_compose_questions/advanced/Testing.md) — *createComposeRule, onNodeWithText, assertIsDisplayed*

**🤖 Compose Scenarios (1 hr):**
- [ ] 🟡 Debugging Scenarios — [DebuggingScenarios.md](../../jetpack_compose_questions/scenario_based/DebuggingScenarios.md) — *Recomposition loops, state loss*
- [ ] 🟡 Navigation Scenarios — [NavigationScenarios.md](../../jetpack_compose_questions/scenario_based/NavigationScenarios.md) — *Nested nav, bottom nav, deep links*
- [ ] 🟡 Performance Scenarios — [PerformanceScenarios.md](../../jetpack_compose_questions/scenario_based/PerformanceScenarios.md) — *Lazy list optimization, stability*
- [ ] 🟡 State Scenarios — [StateScenarios.md](../../jetpack_compose_questions/scenario_based/StateScenarios.md) — *Complex state, restoration*
- [ ] 🟡 UI Scenarios — [UIScenarios.md](../../jetpack_compose_questions/scenario_based/UIScenarios.md) — *Custom UI, animations, theming*

**🧪 Testing (1 hr):**
- [ ] 🟡 Unit Testing — [UnitTesting.md](../../testing_questions/UnitTesting.md) — *JUnit, assertions, fakes, stubs*
- [ ] 🟡 Espresso — [Espresso.md](../../testing_questions/Espresso.md) — *UI tests, ViewMatchers, ViewActions*
- [ ] 🟡 Mockito — [Mockito.md](../../testing_questions/Mockito.md) — *Mocking, verification, argument matchers*

**🧮 DSA Practice (1 hr):**
- [ ] **[Array › Two Pointer In-Place | 🟡 Medium]** Next Permutation (25 min) — [NextPermutation.kt](../../DSA/array/two_pointer_inplace/next_permutation/NextPermutation.kt)
- [ ] **[Array › Two Pointer In-Place | 🟡 Medium]** Remove Duplicates II (20 min) — [RemoveDuplicateseTWO.kt](../../DSA/array/two_pointer_inplace/remove_duplicatese_two/RemoveDuplicateseTWO.kt)
- [ ] **[Array › Kadane's | 🟡 Medium]** Maximum Product Subarray (25 min) — [MaximumProductSubarray.kt](../../DSA/array/kadane_algorithm/maximum_product_subarray/MaximumProductSubarray.kt)
- [ ] **[Array › Kadane's | 🟡 Medium]** Maximum Sum Circular Subarray (25 min) — [MaximumSumCircularSubarray.kt](../../DSA/array/kadane_algorithm/maximum_sum_circular_subarray/MaximumSumCircularSubarray.kt)

**🎁 EXTRA TOPIC:**
- [ ] 🔄 Compose performance deep dive — Write stable/immutable annotations, use key, derivedStateOf

**✅ End-of-Day Self-Check:**
- [ ] Can optimize recomposition with key/derivedStateOf
- [ ] Can write Compose UI tests
- [ ] Can write unit tests with JUnit/Mockito
- [ ] Can write Espresso UI tests
- [ ] Solved 4 DSA problems

---

## Day 53 — Sunday, 11/10/2026 (Weekend — 4-5 hrs)

**🧪 Testing (1 hr):**
- [ ] 🟡 TDD — [TDD.md](../../testing_questions/TDD.md) — *Red-Green-Refactor, test pyramid*
- [ ] 🟡 Compose Testing — [ComposeTesting.md](../../testing_questions/ComposeTesting.md) — *createComposeRule, semantics, assertions*
- [ ] 🟡 Testing Scenarios — [TestingScenarios.md](../../testing_questions/TestingScenarios.md) — *Real-world testing problems*

**🐦 Flutter Scenarios (1 hr):**
- [ ] 🟡 UI Scenarios — [UIScenarios.md](../../flutter_questions/scenario_based/UIScenarios.md) — *Custom UI, animations, theming*
- [ ] Practice: Write tests for a Flutter app

**🧮 DSA Practice (1.5 hr):**
- [ ] **[Array › Prefix Sum | 🟡 Medium]** Contiguous Array (25 min) — [ContiguousArray.kt](../../DSA/array/prefix_sum/contiguous_array/ContiguousArray.kt)
- [ ] **[Array › Sliding Window | 🟡 Medium]** Longest Repeating Char Replacement (25 min) — [LongestRepeatingCharacterReplacement.kt](../../DSA/array/sliding_window/longest_repeating_character_replacement/LongestRepeatingCharacterReplacement.kt)
- [ ] **[Array › Sliding Window | 🟡 Medium]** Find All Anagrams in String (25 min) — [FindAllAnagramsInString.kt](../../DSA/array/sliding_window/find_all_anagrams_in_string/FindAllAnagramsInString.kt)
- [ ] **[Array › Sliding Window | 🟡 Medium]** Fruits into Baskets (20 min) — [FruitsIntoBaskets.kt](../../DSA/array/sliding_window/fruits_into_baskets/FruitsIntoBaskets.kt)

**🎁 EXTRA TOPIC:**
- [ ] 🔄 Compose performance + Flutter performance — Compare optimization techniques

**✅ End-of-Day Self-Check:**
- [ ] Can explain TDD cycle (Red-Green-Refactor)
- [ ] Can explain test pyramid (70% unit, 20% integration, 10% E2E)
- [ ] Can write Compose UI tests
- [ ] Can write Flutter widget tests
- [ ] Solved 4 DSA problems

---

## Day 54 — Monday, 12/10/2026

**🤖 Compose (45 min):**
- [ ] 🔄 Spaced Repetition: Compose State + Side Effects — write from memory
- [ ] 🔄 Spaced Repetition: Compose Navigation — write NavHost from memory

**🧪 Testing (45 min):**
- [ ] 🔄 Spaced Repetition: Mockito — write when().thenReturn() from memory
- [ ] 🔄 Spaced Repetition: TDD — write a failing test, then implement

**🧮 DSA Practice (30 min):**
- [ ] **[Array › Prefix Sum | 🟡 Medium]** Longest Subarray with Sum K (25 min) — [LongestSubarrayWithSumK.kt](../../DSA/array/prefix_sum/longest_subarray_with_sum_k/LongestSubarrayWithSumK.kt)
- [ ] **[Array › Prefix Sum | 🟢 Easy]** Running Sum of 1D Array (15 min) — [RunningSum1DArray.kt](../../DSA/array/prefix_sum/running_sum1_d_array/RunningSum1DArray.kt)

**✅ End-of-Day Self-Check:**
- [ ] Can write Compose state + side effects from memory
- [ ] Can write Mockito mocks from memory
- [ ] Solved 2 DSA problems

---

## Day 55 — Tuesday, 13/10/2026

**🤖 Compose (45 min):**
- [ ] 🔄 Spaced Repetition: Custom Layouts — write MeasurePolicy
- [ ] 🔄 Spaced Repetition: Performance — write stable/immutable annotations

**🐦 Flutter (45 min):**
- [ ] 🔄 Spaced Repetition: Flutter state management — compare Provider vs BLoC vs Riverpod
- [ ] 🔄 Spaced Repetition: Flutter scenarios — debugging, performance

**🧮 DSA Practice (30 min):**
- [ ] **[Array › Two Pointer | 🟡 Medium]** Two Sum (Sorted) (20 min) — [TwoSumLevelTwoWithSortedArray.kt](../../DSA/array/two_pointer/two_sum_level_two_with_sorted_array/TwoSumLevelTwoWithSortedArray.kt)
- [ ] **[Array › Two Pointer | 🟢 Easy]** Move Zeros to End (15 min) — [MoveZerosToEnd.kt](../../DSA/array/two_pointer/move_zeros_to_end/MoveZerosToEnd.kt)

**✅ End-of-Day Self-Check:**
- [ ] Can write Compose custom layout from memory
- [ ] Can compare Flutter state management solutions
- [ ] Solved 2 DSA problems

---

## Day 56 — Wednesday, 14/10/2026 (Weekly Review)

**🔄 Weekly Review (1 hr):**
- [ ] Re-read notes from all Week 8 topics
- [ ] Write summary: "Compose advanced + scenarios + testing + Flutter scenarios"
- [ ] Identify weak areas

**🧮 DSA Practice (1 hr):**
- [ ] **[Array › Linear Scan | 🟢 Easy]** Max Consecutive Ones (15 min) — [FindMaximumConsecutiveOnes.kt](../../DSA/array/linear_scan/find_maximum_consecutive_ones/FindMaximumConsecutiveOnes.kt)
- [ ] **[Array › Linear Scan | 🟢 Easy]** Leaders in Array (15 min) — [LeadersInArray.kt](../../DSA/array/linear_scan/leaders_in_array/LeadersInArray.kt)
- [ ] **[Array › Linear Scan | 🟢 Easy]** Finding Pivot Index (15 min) — [FindingPivotIndex.kt](../../DSA/array/linear_scan/finding_pivot_index/FindingPivotIndex.kt)
- [ ] **[Array › Linear Scan | 🟢 Easy]** First Repeating Element (15 min) — [FirstRepeatingElement.kt](../../DSA/array/linear_scan/first_repeating_element/FirstRepeatingElement.kt)

**✅ Week 8 Self-Assessment:**
- [ ] Can create a custom Layout in Compose
- [ ] Can use AndroidView for interop
- [ ] Can optimize recomposition with key/derivedStateOf
- [ ] Can write Compose UI tests
- [ ] Can implement state hoisting pattern
- [ ] Can use ViewModel + StateFlow + collectAsStateWithLifecycle
- [ ] Can survive process death with SavedStateHandle
- [ ] Can differentiate StateFlow vs SharedFlow vs LiveData
- [ ] Can write unit tests with JUnit/Mockito
- [ ] Can write Espresso UI tests
- [ ] Can write Compose UI tests
- [ ] Can explain TDD cycle
- [ ] Can explain test pyramid
- [ ] Solved 18 DSA problems this week

---

[← Week 7](Week7_Daily_Checklist.md) | [Back to Daily Checklist](../Daily_Checklist.md) | [Week 9 →](Week9_Daily_Checklist.md)
