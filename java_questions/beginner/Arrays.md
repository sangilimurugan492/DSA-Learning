# Arrays

## Q1: How do you declare and initialize arrays in Java?

```java
// Declaration
int[] arr1;           // Recommended (type[])
int arr2[];            // C-style (not recommended)

// Declaration + allocation
int[] nums = new int[5];           // [0, 0, 0, 0, 0] — default values
String[] names = new String[3];     // [null, null, null]

// Declaration + initialization
int[] evens = {2, 4, 6, 8, 10};
String[] days = {"Mon", "Tue", "Wed"};

// Using new keyword with values
int[] odds = new int[]{1, 3, 5, 7, 9};

// Anonymous array (passed to methods)
processArray(new int[]{1, 2, 3});
```

---

## Q2: What is the difference between arrays and ArrayList?

| Array | ArrayList |
|-------|-----------|
| Fixed size | Dynamic size (auto-resizes) |
| Can hold primitives (`int[]`) | Only objects (`List<Integer>`) |
| `length` property | `size()` method |
| Multi-dimensional: `int[][]` | `List<List<Integer>>` |
| Faster access | Slightly slower (overhead) |
| No methods (just `[]`) | Rich API (add, remove, contains) |
| Covariant (see below) | Invariant (type-safe) |

```java
// Array — fixed size
int[] arr = new int[3];
arr[0] = 1;
// arr[3] = 4;  // ❌ ArrayIndexOutOfBoundsException

// ArrayList — dynamic
List<Integer> list = new ArrayList<>();
list.add(1);
list.add(2);
list.add(3);  // Auto-resizes — no limit
```

---

## Q3: How do you iterate over an array?

```java
int[] nums = {10, 20, 30, 40, 50};

// 1. Traditional for loop — index access
for (int i = 0; i < nums.length; i++) {
    System.out.println("Index " + i + ": " + nums[i]);
}

// 2. Enhanced for-each — no index
for (int n : nums) {
    System.out.println(n);
}

// 3. Java 8+ Streams
Arrays.stream(nums).forEach(System.out::println);

// 4. Java 8+ with index (using IntStream)
IntStream.range(0, nums.length)
         .forEach(i -> System.out.println(i + ": " + nums[i]));
```

---

## Q4: How do you sort and search arrays?

```java
int[] nums = {5, 2, 8, 1, 9, 3};

// Sort
Arrays.sort(nums);  // [1, 2, 3, 5, 8, 9] — in-place

// Binary search (array must be sorted first)
int index = Arrays.binarySearch(nums, 5);  // Returns index (3)
int notFound = Arrays.binarySearch(nums, 7);  // Returns -(insertion point) - 1

// Partial sort
int[] arr = {5, 2, 8, 1, 9, 3};
Arrays.sort(arr, 1, 4);  // Sort indices 1-3 only: [5, 1, 2, 8, 9, 3]

// Parallel sort (Java 8+) — faster for large arrays
Arrays.parallelSort(nums);

// Fill
int[] filled = new int[5];
Arrays.fill(filled, 42);  // [42, 42, 42, 42, 42]

// Copy
int[] copy = Arrays.copyOf(nums, nums.length);
int[] range = Arrays.copyOfRange(nums, 1, 4);  // Elements 1, 2, 3
```

---

## Q5: How do multi-dimensional arrays work?

```java
// 2D array — rectangular
int[][] matrix = new int[3][4];  // 3 rows, 4 columns
matrix[0][0] = 1;
matrix[2][3] = 9;

// 2D array with initialization
int[][] grid = {
    {1, 2, 3},
    {4, 5, 6},
    {7, 8, 9}
};

// Jagged array — different row lengths
int[][] jagged = new int[3][];
jagged[0] = new int[]{1, 2};
jagged[1] = new int[]{3, 4, 5};
jagged[2] = new int[]{6};

// Iterating 2D array
for (int i = 0; i < grid.length; i++) {         // rows
    for (int j = 0; j < grid[i].length; j++) {   // columns
        System.out.print(grid[i][j] + " ");
    }
    System.out.println();
}

// Using deepToString for 2D array
System.out.println(Arrays.deepToString(grid));
// [[1, 2, 3], [4, 5, 6], [7, 8, 9]]
```

---

## Q6: What is array covariance?

```java
// Arrays are covariant — subtype[] is a subtype of supertype[]
Number[] numbers = new Integer[10];  // ✅ Compiles
numbers[0] = 1;                      // ✅ Integer is Number

// ⚠️ ArrayStoreException at runtime
Number[] nums = new Integer[5];
// nums[0] = 3.14;  // ❌ ArrayStoreException — Double is not Integer

// Generics (ArrayList) are invariant — type-safe
// List<Number> list = new ArrayList<Integer>();  // ❌ Compile error
List<? extends Number> list = new ArrayList<Integer>();  // ✅ Wildcard
```

> Array covariance is a design flaw in Java. Generics use invariance + wildcards for type safety.

---

## 🔗 Related Topics
- [Basics](Basics.md)
- [Control Flow](ControlFlow.md)
- [Collections](../intermediate/Collections.md)
