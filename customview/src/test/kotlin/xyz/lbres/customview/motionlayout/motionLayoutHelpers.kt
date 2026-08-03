package xyz.lbres.customview.motionlayout

import android.content.Context
import android.util.AttributeSet
import android.view.View
import androidx.test.core.app.ApplicationProvider
import io.mockk.every
import io.mockk.spyk
import xyz.lbres.customview.R
import xyz.lbres.customview.movingview.MovingButton
import xyz.lbres.customview.movingview.MovingView
import xyz.lbres.customview.testutils.createMockTypedArray
import xyz.lbres.customview.testutils.setViewSize
import xyz.lbres.kotlinutils.closedrange.rangeOfInt
import xyz.lbres.customview.movingview.createMockContext as createMovingViewContext

/**
 * Create a motion layout with the given size and mock attributes
 */
internal fun createLayout(paused: Boolean, interval: Int, width: Int = 100, height: Int = 200): MotionLayout {
    val layout = MotionLayout(createMockContext(paused, interval))
    layout.left = 0
    layout.right = width
    layout.top = 0
    layout.bottom = height
    return layout
}

/**
 * Create a moving view with the given mock attributes
 */
internal fun getMovingView(motionType: Int, paused: Boolean, motionSize: Int): MovingView {
    val context = createMovingViewContext(motionType, paused, motionSize)
    val view = MovingButton(context)
    setViewSize(view, 100, 200)
    return view
}

/**
 * Create non-moving views
 */
internal fun getNonMovingViews(size: Int): List<View> {
    val context: Context = spyk(ApplicationProvider.getApplicationContext())
    return rangeOfInt(size).map { View(context) }
}

/**
 * Create mock context for MotionLayout
 */
internal fun createMockContext(paused: Boolean = false, motionInterval: Int = 0): Context {
    val mockArray = createMockTypedArray(
        setOf(
            R.styleable.Movement_motionInterval,
            R.styleable.Movement_paused,
        ),
    )
    every { mockArray.getInt(R.styleable.Movement_motionInterval, any()) } returns motionInterval
    every { mockArray.getBoolean(R.styleable.Movement_paused, any()) } returns paused

    val context: Context = spyk(ApplicationProvider.getApplicationContext())
    every {
        context.obtainStyledAttributes(any<AttributeSet>(), R.styleable.Movement, any(), any())
    } returns mockArray
    return context
}

/**
 * Log the positions of a list of views
 */
internal fun logViewPositions(views: List<View>) {
    views.forEach { println("(${it.left}, ${it.top})") }
    println()
}
