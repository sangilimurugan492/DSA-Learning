# Custom Views & Canvas

## 📖 Explanation

Custom views allow you to create UI components that aren't available in the standard library. You draw directly on a `Canvas` using `Paint` objects.

### When to Create Custom Views
- Unique visual components (charts, gauges, signatures)
- Complex animations
- Custom touch handling

### Custom View Lifecycle
```
Constructor → onMeasure() → onSizeChanged() → onLayout() → onDraw()
```

### Key Methods
| Method           | Purpose                                      |
|------------------|----------------------------------------------|
| `onMeasure()`     | Determine view size (width/height)          |
| `onSizeChanged()` | Called when size changes                     |
| `onLayout()`      | Position children (for ViewGroup)           |
| `onDraw()`        | Draw content on Canvas                       |
| `onTouchEvent()`  | Handle touch events                          |
| `invalidate()`    | Trigger redraw (main thread)                 |
| `requestLayout()` | Trigger re-measure and re-layout             |

### Canvas Drawing
| Method              | Description                          |
|---------------------|--------------------------------------|
| `drawRect()`         | Draw rectangle                      |
| `drawCircle()`       | Draw circle                         |
| `drawLine()`         | Draw line                           |
| `drawText()`         | Draw text                           |
| `drawPath()`         | Draw custom path                    |
| `drawBitmap()`       | Draw image                          |
| `drawArc()`          | Draw arc/wedge                      |
| `save()` / `restore()` | Save/restore canvas state         |

### Paint
```kotlin
val paint = Paint().apply {
    color = Color.RED
    style = Paint.Style.FILL
    strokeWidth = 4f
    isAntiAlias = true
    textSize = 48f
    textAlign = Paint.Align.CENTER
}
```

### Custom Attributes
```xml
<!-- res/values/attrs.xml -->
<declare-styleable name="CircleView">
    <attr name="circleColor" format="color" />
    <attr name="circleRadius" format="dimension" />
</declare-styleable>
```

---

## 🧪 Code Example

```kotlin
package com.example.app

import android.content.Context
import android.graphics.*
import android.os.Bundle
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import androidx.appcompat.app.AppCompatActivity

// --- Custom Circle View ---
class CircleView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    private var circleColor = Color.BLUE
    private var radius = 100f

    private val paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.FILL
    }

    init {
        // Read custom attributes
        context.obtainStyledAttributes(attrs, R.styleable.CircleView).apply {
            circleColor = getColor(R.styleable.CircleView_circleColor, Color.BLUE)
            radius = getDimension(R.styleable.CircleView_circleRadius, 100f)
            recycle()
        }
        paint.color = circleColor
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val desired = (radius * 2 + paddingLeft + paddingRight).toInt()
        setMeasuredDimension(
            resolveSize(desired, widthMeasureSpec),
            resolveSize(desired, heightMeasureSpec)
        )
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        val cx = width / 2f
        val cy = height / 2f
        canvas.drawCircle(cx, cy, radius, paint)
    }
}

// --- Custom Progress Bar ---
class CircularProgressView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    var progress: Float = 0f
        set(value) {
            field = value.coerceIn(0f, 1f)
            invalidate()  // Trigger redraw
        }

    private val backgroundPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.STROKE
        strokeWidth = 20f
        color = Color.LTGRAY
        isAntiAlias = true
    }

    private val progressPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.STROKE
        strokeWidth = 20f
        color = Color.parseColor("#6200EE")
        isAntiAlias = true
        strokeCap = Paint.Cap.ROUND
    }

    private val textPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.BLACK
        textSize = 60f
        textAlign = Paint.Align.CENTER
    }

    private val oval = RectF()

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        val inset = backgroundPaint.strokeWidth / 2
        oval.set(inset, inset, w - inset, h - inset)
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        // Background circle
        canvas.drawArc(oval, 0f, 360f, false, backgroundPaint)
        // Progress arc
        canvas.drawArc(oval, -90f, 360f * progress, false, progressPaint)
        // Percentage text
        val percent = "${(progress * 100).toInt()}%"
        canvas.drawText(percent, width / 2f, height / 2f + 20f, textPaint)
    }
}

// --- Drawing Pad (Touch Events) ---
class DrawingPad @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    private val paths = mutableListOf<Pair<Path, Paint>>()
    private var currentPath: Path? = null

    private val paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.STROKE
        strokeWidth = 8f
        color = Color.BLACK
        strokeCap = Paint.Cap.ROUND
        strokeJoin = Paint.Join.ROUND
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        paths.forEach { (path, p) -> canvas.drawPath(path, p) }
        currentPath?.let { canvas.drawPath(it, paint) }
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        val x = event.x
        val y = event.y

        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                currentPath = Path().apply { moveTo(x, y) }
            }
            MotionEvent.ACTION_MOVE -> {
                currentPath?.lineTo(x, y)
            }
            MotionEvent.ACTION_UP -> {
                currentPath?.let { paths.add(it to Paint(paint)) }
                currentPath = null
            }
        }
        invalidate()
        return true
    }

    fun clear() {
        paths.clear()
        invalidate()
    }
}

// --- Activity ---
class CustomViewActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_custom)

        val progressView = findViewById<CircularProgressView>(R.id.progressView)
        val drawingPad = findViewById<DrawingPad>(R.id.drawingPad)

        // Animate progress
        var p = 0f
        progressView.postDelayed(object : Runnable {
            override fun run() {
                p += 0.02f
                progressView.progress = p
                if (p < 1f) progressView.postDelayed(this, 50)
            }
        }, 500)

        findViewById<Button>(R.id.btnClear).setOnClickListener {
            drawingPad.clear()
        }
    }
}
```

```xml
<!-- res/values/attrs.xml -->
<resources>
    <declare-styleable name="CircleView">
        <attr name="circleColor" format="color" />
        <attr name="circleRadius" format="dimension" />
    </declare-styleable>
</resources>
```

```xml
<!-- activity_custom.xml -->
<LinearLayout xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:app="http://schemas.android.com/apk/res-auto"
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    android:orientation="vertical"
    android:padding="16dp">

    <com.example.app.CircularProgressView
        android:id="@+id/progressView"
        android:layout_width="200dp"
        android:layout_height="200dp"
        android:layout_gravity="center" />

    <com.example.app.DrawingPad
        android:id="@+id/drawingPad"
        android:layout_width="match_parent"
        android:layout_height="0dp"
        android:layout_weight="1"
        android:layout_marginTop="16dp"
        android:background="#F0F0F0" />

    <Button
        android:id="@+id/btnClear"
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        android:text="Clear Drawing" />
</LinearLayout>
```

---

## ❓ Interview Questions

1. **What is the difference between `invalidate()` and `requestLayout()`?**
   - `invalidate()` triggers `onDraw()` only (redraw without re-measuring). `requestLayout()` triggers `onMeasure()` → `onLayout()` → `onDraw()` (full re-layout). Use `invalidate` for appearance changes, `requestLayout` for size changes.

2. **What is the purpose of `onMeasure()` and what are `MeasureSpec` modes?**
   - `onMeasure()` determines the view's size. `MeasureSpec` has three modes: `EXACTLY` (fixed size), `AT_MOST` (wrap content), `UNSPECIFIED` (no limit). Use `resolveSize()` to compute final size.

3. **How do you handle touch events in a custom view?**
   - Override `onTouchEvent()`. Handle `ACTION_DOWN`, `ACTION_MOVE`, `ACTION_UP`. Return `true` to consume the event. Use `GestureDetector` for complex gestures (double-tap, fling, long press).

4. **What is the role of `Canvas` and `Paint` in custom views?**
   - `Canvas` is the drawing surface — you call `drawRect()`, `drawCircle()`, `drawText()` on it. `Paint` defines how to draw — color, stroke width, style, anti-aliasing, text size.

5. **How do you create and read custom attributes?**
   - Define in `res/values/attrs.xml` with `<declare-styleable>`. Read in the view constructor using `context.obtainStyledAttributes()`. Always call `recycle()` on the typed array.

6. **What is `onMeasure` and how do `MeasureSpec` modes work?**
   - `onMeasure(widthMeasureSpec, heightMeasureSpec)` is called by the parent to determine the view's size. Each `MeasureSpec` encodes a size and a mode: (1) `EXACTLY` — the parent sets an exact size (e.g., `match_parent` with known size, or `100dp`). The view should be this exact size. (2) `AT_MOST` — the view can be any size up to the specified maximum (e.g., `wrap_content`). The view should not exceed this. (3) `UNSPECIFIED` — no constraint, the view can be any size it wants (rare, used in scroll views). Use `MeasureSpec.getMode(spec)` and `MeasureSpec.getSize(spec)` to extract mode and size. Use `resolveSize(desiredSize, measureSpec)` to compute the final size respecting the spec. A typical `onMeasure` calculates the desired size, then calls `setMeasuredDimension(width, height)` with the resolved sizes. Always handle all three modes.

7. **How do you optimize custom view drawing performance?**
   - (1) **Avoid object allocation in `onDraw()`** — `onDraw` is called on every frame. Create `Paint`, `Path`, `Rect` objects in the constructor, not in `onDraw`. (2) **Use hardware layers** — `setLayerType(LAYER_TYPE_HARDWARE, null)` caches the view as a GPU texture. Good for animated views that don't change shape. (3) **Use `invalidate(Rect)` instead of `invalidate()`** — only redraw the dirty region (though modern Android may ignore region-based invalidation). (4) **Avoid deep nesting** — custom views that combine multiple views should use a single canvas draw instead of nested layouts. (5) **Use `clipRect`** to limit drawing area. (6) **Batch draw calls** — minimize the number of `drawX()` calls. (7) **Use `postInvalidateOnAnimation()`** for animation-driven redraws to sync with the display refresh. (8) Profile with GPU Profiler and Systrace.

8. **What is the difference between `invalidate()` and `postInvalidate()`?**
   - `invalidate()` schedules a redraw on the **main thread** — must be called from the main thread. It marks the view's drawing region as dirty, triggering `onDraw()` on the next frame. `postInvalidate()` schedules a redraw on the main thread via `Handler.post()` — safe to call from **background threads**. Use `invalidate()` when already on the main thread (most cases). Use `postInvalidate()` when updating the view from a background thread (e.g., a timer, network callback). There are also `invalidate(Rect)` and `postInvalidateOnAnimation()` variants. Note: `invalidate()` only triggers `onDraw()` — not `onMeasure()` or `onLayout()`. For size changes, use `requestLayout()`.

9. **How do you create a custom ViewGroup?**
   - Extend `ViewGroup` and override: (1) `onMeasure(widthMeasureSpec, heightMeasureSpec)` — measure all children with `measureChild(child, widthSpec, heightSpec)` or `measureChildren()`. Calculate the total size based on children and call `setMeasuredDimension()`. (2) `onLayout(changed, left, top, right, bottom)` — position each child by calling `child.layout(l, t, r, b)`. This is where you define the layout algorithm (e.g., flow layout, circular layout). (3) Optionally override `generateLayoutParams()` to support custom layout params (margins, weights). Example: a `FlowLayout` that wraps children to the next line when they exceed the width. Measure pass: iterate children, accumulate widths, wrap when exceeding available width. Layout pass: position children left-to-right, wrapping to the next row. Custom ViewGroups are powerful for unique layouts that standard layouts can't express.

10. **How do you handle gesture detection in custom views?**
    - Use `GestureDetector` for common gestures: single tap, double tap, long press, fling, scroll. Create a `GestureDetector.SimpleOnGestureListener` and override the methods you need: `onSingleTapConfirmed`, `onDoubleTap`, `onLongPress`, `onScroll`, `onFling`. In `onTouchEvent()`, delegate to `gestureDetector.onTouchEvent(event)`. For multi-touch (pinch-to-zoom), use `ScaleGestureDetector`. For complex custom gestures, track `MotionEvent` history and use velocity trackers (`VelocityTracker`). Always return `true` from `onTouchEvent()` to receive subsequent events. Handle `ACTION_DOWN`, `ACTION_MOVE`, `ACTION_UP`, `ACTION_CANCEL`. Use `event.actionMasked` for multi-touch. For nested scrolling, implement `NestedScrollingChild` or use `NestedScrollView`.

11. **What is `Canvas.save()` and `Canvas.restore()` and why are they important?**
    - `canvas.save()` saves the current state of the canvas (transformation matrix and clip region) onto a stack. `canvas.restore()` restores the most recent saved state. This is crucial when you apply transformations (rotate, translate, scale) and want to undo them for subsequent draws. Example: `canvas.save(); canvas.rotate(45f, cx, cy); canvas.drawRect(...); canvas.restore();` — the rotation only affects the rect draw, not subsequent draws. Without save/restore, transformations accumulate and affect everything drawn afterwards. You can save multiple states and restore them in reverse order (LIFO). `saveLayer()` creates a separate offscreen bitmap for compositing (expensive — use sparingly). Always pair `save()` with `restore()` — unbalanced calls cause drawing artifacts. The save count can be checked with `canvas.saveCount`.

12. **How do you animate custom views?**
    - Several approaches: (1) **ValueAnimator** — animate a value from start to end, call `invalidate()` in the update listener: `ValueAnimator.ofFloat(0f, 1f).apply { duration = 1000; addUpdateListener { progress = it.animatedValue as Float; invalidate() }; start() }`. (2) **ObjectAnimator** — animate a property directly: `ObjectAnimator.ofFloat(view, "progress", 0f, 1f).start()`. The property must have a setter that calls `invalidate()`. (3) **Custom property with `Property`** — use `FloatProperty<T>` for type safety. (4) **`ViewPropertyAnimator`** — `view.animate().rotation(360f).setDuration(1000).start()`. Simple for built-in properties. (5) **`android.animation.AnimatorSet`** — combine multiple animators. (6) **Canvas-based animation** — in `onDraw`, compute positions based on `System.currentTimeMillis()` and call `postInvalidateOnAnimation()` for the next frame. Always cancel animators in `onDetachedFromWindow()` to prevent leaks.

---

## 🔗 Related Topics
- [UI Layouts & Views](../beginner/UILayouts.md)
- [Performance Optimization](Performance.md)
