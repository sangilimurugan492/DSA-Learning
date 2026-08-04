# UI Layouts & Views

## 📖 Explanation

Android UIs are built using layouts (ViewGroups) and views (widgets). Layouts define how child views are arranged on screen.

### Common Layouts
| Layout         | Description                                          |
|----------------|------------------------------------------------------|
| `LinearLayout`  | Arranges children in a single direction (horizontal/vertical) |
| `ConstraintLayout` | Positions children using constraints (most flexible) |
| `RelativeLayout` | Positions children relative to each other or parent  |
| `FrameLayout`   | Stacks children (single child typically)            |
| `TableLayout`   | Arranges children in rows and columns                |
| `GridLayout`    | Arranges children in a grid                           |

### Common Views
| View            | Description                          |
|-----------------|--------------------------------------|
| `TextView`      | Displays text                       |
| `EditText`      | Text input field                    |
| `Button`        | Clickable button                    |
| `ImageView`     | Displays an image                   |
| `RecyclerView`  | Scrollable list (efficient)         |
| `CheckBox`      | Toggle checkbox                     |
| `RadioButton`   | Radio selection                     |
| `ProgressBar`   | Loading indicator                   |
| `Spinner`       | Dropdown selector                   |

### `match_parent` vs `wrap_content`
- `match_parent` — Fill the parent's size.
- `wrap_content` — Size to fit content.

### Common Attributes
| Attribute           | Description                          |
|---------------------|--------------------------------------|
| `android:id`         | Unique view ID                      |
| `android:layout_width` | Width of the view                  |
| `android:layout_height` | Height of the view               |
| `android:padding`    | Inner spacing                       |
| `android:layout_margin` | Outer spacing                    |
| `android:gravity`    | Alignment of content within view    |
| `android:layout_gravity` | Alignment of view within parent |
| `android:visibility` | visible, invisible, gone            |

### ConstraintLayout
The most powerful and recommended layout. Uses constraints to position views.

```xml
<Button
    android:id="@+id/button"
    android:layout_width="wrap_content"
    android:layout_height="wrap_content"
    app:layout_constraintTop_toTopOf="parent"
    app:layout_constraintStart_toStartOf="parent" />
```

### `include` & `merge`
Reuse layouts with `<include>`. Use `<merge>` to eliminate redundant view groups.

```xml
<include layout="@layout/common_header" />
```

---

## 🧪 Code Example

```xml
<!-- activity_main.xml — ConstraintLayout example -->
<?xml version="1.0" encoding="utf-8"?>
<androidx.constraintlayout.widget.ConstraintLayout
    xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:app="http://schemas.android.com/apk/res-auto"
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    android:padding="16dp">

    <!-- Title -->
    <TextView
        android:id="@+id/titleText"
        android:layout_width="0dp"
        android:layout_height="wrap_content"
        android:text="Login"
        android:textSize="28sp"
        android:textStyle="bold"
        app:layout_constraintTop_toTopOf="parent"
        app:layout_constraintStart_toStartOf="parent"
        app:layout_constraintEnd_toEndOf="parent" />

    <!-- Email input -->
    <EditText
        android:id="@+id/emailInput"
        android:layout_width="0dp"
        android:layout_height="wrap_content"
        android:hint="Email"
        android:inputType="textEmailAddress"
        android:layout_marginTop="24dp"
        app:layout_constraintTop_toBottomOf="@id/titleText"
        app:layout_constraintStart_toStartOf="parent"
        app:layout_constraintEnd_toEndOf="parent" />

    <!-- Password input -->
    <EditText
        android:id="@+id/passwordInput"
        android:layout_width="0dp"
        android:layout_height="wrap_content"
        android:hint="Password"
        android:inputType="textPassword"
        android:layout_marginTop="12dp"
        app:layout_constraintTop_toBottomOf="@id/emailInput"
        app:layout_constraintStart_toStartOf="parent"
        app:layout_constraintEnd_toEndOf="parent" />

    <!-- Login button -->
    <Button
        android:id="@+id/loginButton"
        android:layout_width="0dp"
        android:layout_height="wrap_content"
        android:text="Login"
        android:layout_marginTop="24dp"
        app:layout_constraintTop_toBottomOf="@id/passwordInput"
        app:layout_constraintStart_toStartOf="parent"
        app:layout_constraintEnd_toEndOf="parent" />

    <!-- Loading indicator (hidden by default) -->
    <ProgressBar
        android:id="@+id/progressBar"
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        android:visibility="gone"
        app:layout_constraintTop_toTopOf="parent"
        app:layout_constraintBottom_toBottomOf="parent"
        app:layout_constraintStart_toStartOf="parent"
        app:layout_constraintEnd_toEndOf="parent" />

</androidx.constraintlayout.widget.ConstraintLayout>
```

```kotlin
// MainActivity.kt — Wiring up the views
package com.example.app

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val emailInput = findViewById<EditText>(R.id.emailInput)
        val passwordInput = findViewById<EditText>(R.id.passwordInput)
        val loginButton = findViewById<Button>(R.id.loginButton)
        val progressBar = findViewById<ProgressBar>(R.id.progressBar)

        loginButton.setOnClickListener {
            val email = emailInput.text.toString()
            val password = passwordInput.text.toString()

            if (email.isBlank() || password.isBlank()) {
                Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Show loading
            progressBar.visibility = View.VISIBLE
            loginButton.isEnabled = false

            // Simulate login
            loginButton.postDelayed({
                progressBar.visibility = View.GONE
                loginButton.isEnabled = true
                Toast.makeText(this, "Login successful!", Toast.LENGTH_SHORT).show()
            }, 2000)
        }
    }
}
```

---

## ❓ Interview Questions

1. **What is the difference between `LinearLayout` and `ConstraintLayout`?**
   - `LinearLayout` arranges children in a single direction (either horizontal or vertical, set via `android:orientation`). It's simple but limited — to create complex layouts, you need to nest multiple LinearLayouts, which creates a deep view hierarchy. Each nesting level adds a measure/layout pass, degrading performance. `ConstraintLayout` uses constraints (relationships between views) to position children in a flat hierarchy. A view can be constrained to parent edges, other views, guidelines, or barriers. This eliminates the need for nesting — you can build the same complex UI with a single ConstraintLayout that would require 3-4 nested LinearLayouts. ConstraintLayout also supports advanced features: chains (spread/packed/weighted), guidelines (percentage-based positioning), barriers (position relative to a group of views), and constraints with bias (positioning between two constraints). Always prefer ConstraintLayout for complex layouts.

2. **What is the difference between `padding` and `margin`?**
   - `padding` is space **inside** the view — between the view's content (text, image) and its border/background. It's set via `android:padding`, `android:paddingStart`, `android:paddingTop`, etc. Increasing padding makes the view's content area smaller but the view itself stays the same size. `margin` is space **outside** the view — between this view and adjacent views or the parent. It's set via `android:layout_margin`, `android:layout_marginStart`, etc. Increasing margin pushes the view away from neighbors. Key difference: padding affects the view's internal content area (and is drawn within the view's background), while margin affects the view's position relative to siblings. Both use `dp` units. For touch targets, padding increases the tappable area, while margin creates dead space.

3. **What is the difference between `gravity` and `layout_gravity`?**
   - `android:gravity` controls how the **content** of a view is positioned within the view's own bounds. For a TextView, it controls text alignment (left, center, right). For a LinearLayout, it controls how all children are positioned within the layout. For example, `android:gravity="center"` on a vertical LinearLayout centers all children horizontally. `android:layout_gravity` controls how the **view itself** is positioned within its **parent**. For example, a Button inside a vertical LinearLayout with `android:layout_gravity="center_horizontal"` will be centered horizontally within the parent. Note: `layout_gravity` only works if the parent has extra space — if the view is `match_parent`, there's no room to move. Also, `layout_gravity` behavior depends on the parent layout type (LinearLayout respects it differently than FrameLayout).

4. **What is the difference between `invisible` and `gone`?**
   - `android:visibility="invisible"` — the view is not drawn but still occupies space in the layout. It participates in measure/layout passes, so sibling views are positioned as if it were visible. Use this when you want to hide a view temporarily without causing layout shifts. `android:visibility="gone"` — the view is completely removed from the layout. It doesn't participate in measure/layout, and sibling views reposition to fill the space. Use this when you want the view to not affect the layout at all. `gone` is more performant for permanently hidden views (no measure/layout overhead), but causes layout recalculations when toggled. `invisible` is better for frequently toggled views (no layout shift). Default is `visible`.

5. **Why is `ConstraintLayout` recommended over nested `LinearLayout`?**
   - ConstraintLayout flattens the view hierarchy, which has multiple benefits: (1) **Performance** — each nested layout level adds a measure/layout pass. A 4-level deep hierarchy requires 4 passes; a flat ConstraintLayout requires 1. This is especially important for complex list items in RecyclerView. (2) **Fewer views** — no need for wrapper LinearLayouts just for grouping, reducing view count and memory. (3) **Flexibility** — constraints can express relationships that LinearLayout can't (e.g., a view centered between two other views, or a barrier that adjusts to the tallest view). (4) **Animation support** — ConstraintLayout works with MotionLayout for complex transitions. (5) **Visual editor** — Android Studio's layout editor has first-class ConstraintLayout support with drag-and-drop constraint creation. The only downside is a slight learning curve for the constraint model.

6. **What is `match_parent` vs `wrap_content` vs `0dp` in ConstraintLayout?**
   - `match_parent` — fills the parent's dimension (not recommended in ConstraintLayout — use `0dp` with constraints instead). `wrap_content` — sizes to fit the view's content. `0dp` (match_constraints) — in ConstraintLayout, `0dp` means "match constraints" — the view expands to fill the space defined by its constraints. For example, if a view is constrained to both start and end of the parent with `0dp` width, it fills the full width. If constrained between two other views, it fills the space between them. This is the most powerful sizing option in ConstraintLayout and should be used instead of `match_parent`. You can also set width/height to `0dp` and use `app:layout_constraintWidth_percent="0.5"` for percentage-based sizing.

7. **What is the difference between `include` and `merge` tags?**
   - `<include layout="@layout/common_header" />` lets you reuse a layout in multiple places — it inflates the referenced layout and inserts it at the include location. This reduces duplication. However, if the included layout's root is a ViewGroup (like LinearLayout), it adds an extra view to the hierarchy. `<merge>` solves this — it's a pseudo-root element that tells the inflater to directly add the children to the parent, skipping the root ViewGroup. Use `<merge>` as the root of a layout that will be included in another layout when the root ViewGroup is unnecessary. For example, if you include a set of buttons into a LinearLayout, use `<merge>` as the root — the buttons are added directly to the parent LinearLayout without an extra wrapper. This reduces view count and improves performance.

8. **What is `ViewStub` and when do you use it?**
   - `ViewStub` is a lazy inflation view — it's a zero-size, invisible placeholder that inflates another layout only when needed (when `setVisibility(VISIBLE)` or `inflate()` is called). Use it for layouts that are rarely shown (e.g., error messages, empty states, progress overlays). Benefits: (1) Faster initial layout — the stub doesn't participate in measure/layout until inflated. (2) Lower memory — the inflated layout's views aren't created until needed. (3) Better startup time — complex hidden views don't slow down initial render. After inflation, the ViewStub is replaced by the inflated layout in the hierarchy. You can get a reference to the inflated root via `viewStub.inflate()`. Note: once inflated, the ViewStub is gone — you can't re-deflate it.

9. **What are chains in ConstraintLayout?**
   - Chains are a ConstraintLayout feature for distributing a group of views along an axis. A chain is formed when views are bidirectionally constrained to each other (A→B and B→A). The chain style is set on the chain head (first view in the chain). Three styles: (1) **Spread** (default) — views are evenly distributed with equal spacing. (2) **Spread inside** — first and last views are at the edges, remaining views spread between. (3) **Packed** — views are grouped together, positioned with a bias value. (4) **Weighted** — use `layout_constraintHorizontal_weight` to distribute space proportionally (like LinearLayout weights). Chains replace nested LinearLayouts for row/column layouts. Example: three buttons in a row can be a horizontal chain with spread style, all in one ConstraintLayout.

10. **What is the difference between `dp` (or `dip`) and `px`?**
    - `px` (pixels) — physical screen pixels. A 100px button looks different sizes on different screen densities. Never use `px` for layout. `dp` (density-independent pixels) — an abstract unit that maps to physical pixels based on screen density. 1dp = 1px on a 160dpi (mdpi) screen. On a 320dpi (xhdpi) screen, 1dp = 2px. This ensures consistent physical size across devices. The formula: `px = dp * (density / 160)`. Android automatically scales dp values to the correct pixel count. Always use `dp` for layout dimensions (widths, heights, margins, padding). For text, use `sp` (scale-independent pixels) which also respects the user's font size accessibility setting.

11. **What is `ConstraintSet` and how is it used?**
    - `ConstraintSet` is a class that captures and applies constraints to a ConstraintLayout. It's used for: (1) **Animations** — capture the current constraints, modify them, and apply with `TransitionManager.beginDelayedTransition()` to animate the layout change. (2) **Layout switching** — define multiple constraint sets in XML and switch between them programmatically. (3) **Dynamic constraint modification** — change constraints at runtime without XML. Usage: `val constraintSet = ConstraintSet(); constraintSet.clone(constraintLayout); constraintSet.connect(viewId, start, targetId, end, margin); constraintSet.applyTo(constraintLayout)`. This is the foundation of MotionLayout animations.

12. **What is `MotionLayout` and how does it relate to `ConstraintLayout`?**
    - `MotionLayout` is a subclass of `ConstraintLayout` that adds rich animation capabilities. It animates between constraint sets (start and end states) with fine-grained control: keyframes, attribute changes, visibility transitions, and custom attributes. You define motion in a `MotionScene` XML file with `<Transition>`, `<ConstraintSet>` (start/end), and `<KeyFrameSet>` (intermediate states). MotionLayout supports: (1) **Swipe/drag handling** — built-in touch response with `onSwipe`. (2) **Keyframes** — position and attribute keyframes for complex paths. (3) **Key triggers** — fire callbacks at specific progress points. (4) **Custom attributes** — animate background color, alpha, rotation, etc. Use MotionLayout for: shared element transitions, collapsible headers, swipe-to-dismiss, and any complex coordinated animation that would be difficult with standard Android animations.

13. **What are guidelines, barriers, and groups in ConstraintLayout?**
    - **Guideline** — an invisible guideline positioned by `dp` or percentage (`app:layout_constraintGuide_percent="0.5"`). Views can constrain to it — great for aligning views at 50% width. **Barrier** — positions itself based on the furthest edge of a set of views. If you have two TextViews of varying lengths, a barrier at `end` will always be positioned after the longer one. Useful when content is dynamic. **Group** — groups multiple views together to control their visibility with a single reference: `app:constraint_referenced_ids="view1,view2,view3"`. Setting the group's visibility to `gone` hides all referenced views. These tools help create responsive layouts without nesting.

14. **What is `CoordinatorLayout` and how does it work with `AppBarLayout`?**
    - `CoordinatorLayout` is a super-powered FrameLayout that coordinates dependent child views. It's the foundation for Material Design scrolling behaviors. `AppBarLayout` (a vertical LinearLayout) uses `CoordinatorLayout` to implement collapsing/expanding toolbar patterns. When a `RecyclerView` (with `app:layout_behavior="@string/appbar_scrolling_view_behavior"`) scrolls, the `AppBarLayout` responds with scroll/exit/enter behaviors defined by `ScrollingViewBehavior`. Common flags: `scroll` (scrolls off), `enterAlways` (returns immediately), `enterAlwaysCollapsed`, `exitUntilCollapsed`. Use `CollapsingToolbarLayout` inside `AppBarLayout` for parallax images and flexible toolbars.

15. **What is the difference between `LinearLayout` weight and ConstraintLayout chains?**
    - `LinearLayout` weight distributes remaining space proportionally — `layout_weight="1"` and `layout_width="0dp"` means the view takes equal remaining space. It only works in one direction and requires nested LinearLayouts for 2D layouts. ConstraintLayout chains are more powerful: (1) **Spread chain** — equal spacing between views (like weights but with gaps). (2) **Packed chain** — views grouped together with adjustable bias. (3) **Weighted chain** — `layout_constraintHorizontal_weight` distributes space proportionally (like LinearLayout weights but in a flat hierarchy). Chains eliminate the need for nested LinearLayouts, reducing view hierarchy depth and improving performance.

---

## 🔗 Related Topics
- [RecyclerView & Adapter Patterns](RecyclerView.md)
- [Android Basics](Basics.md)
