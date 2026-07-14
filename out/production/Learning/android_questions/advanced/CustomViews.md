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

---

## 🔗 Related Topics
- [UI Layouts & Views](../beginner/UILayouts.md)
- [Performance Optimization](Performance.md)
