package xyz.lbres.customview.motionlayout

import android.content.Context
import android.os.Handler
import android.os.Looper
import android.util.AttributeSet
import android.util.Log
import android.view.InflateException
import android.view.View
import android.view.ViewGroup
import androidx.core.content.withStyledAttributes
import androidx.core.view.children
import xyz.lbres.customview.R
import xyz.lbres.customview.movingview.MovingView
import kotlin.math.max

/**
 * Layout that updates the positions of child views at a specified interval.
 * All children must implement the [MovingView] interface.
 * See README for information about customizing layout.
 */
open class MotionLayout(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : ViewGroup(
    context,
    attrs,
    defStyleAttr,
) {
    /**
     * Runnable and handler for moving children
     */
    private val loopHandler: Handler
    private val runnable: Runnable

    private var intervalCompleted: Boolean = false // prevents all views from updating if one view requests layout
    private var forceUpdate: Boolean = false // if position update should be forced, even if layout is paused
    private var forceChildUpdates: Boolean = false // if child positions should be updated even when children are paused

    /**
     * If movement of layout is paused
     */
    private var _paused: Boolean
    var paused
        get() = _paused
        set(value) = updatePaused(value)

    /**
     * Interval between child position updates
     */
    private var _motionInterval: Long
    var motionInterval: Long
        get() = _motionInterval
        set(value) = updateMotionInterval(value)

    constructor(context: Context) : this(context, null, 0)
    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)

    init {
        // parse values from attributes
        var attrPaused = false
        var attrMotionInterval = 0

        context.withStyledAttributes(attrs, R.styleable.Movement) {
            attrPaused = getBoolean(R.styleable.Movement_paused, false)
            attrMotionInterval = getInt(R.styleable.Movement_motionInterval, 0)
        }

        _paused = attrPaused
        _motionInterval = max(attrMotionInterval, 0).toLong()

        // start runnable
        loopHandler = Handler(Looper.myLooper()!!)
        runnable = Runnable {
            if (forceUpdate || !paused) {
                intervalCompleted = true
                requestLayout()
                postRunnableWithDelay()
                // TODO look at postDelayed
                // loopHandler.postDelayed(this, motionInterval)
            }
        }

        if (!paused) {
            postRunnableWithDelay()
        }
    }

    /**
     * Set new paused value and start or stop runnable
     */
    private fun updatePaused(newValue: Boolean) {
        val valueUpdated = paused != newValue
        _paused = newValue

        if (valueUpdated && paused) {
            cancelRunnable()
        } else if (valueUpdated) {
            postRunnableWithDelay()
        }
    }

    /**
     * Set new motion interval and restart runnable with new interval if not paused
     */
    private fun updateMotionInterval(newValue: Long) {
        val valueUpdated = motionInterval != newValue

        if (valueUpdated && newValue >= 0) {
            _motionInterval = newValue

            if (!paused) {
                cancelRunnable()
                postRunnableWithDelay()
            }
        } else if (newValue < 0) {
            Log.w(null, "MotionLayout motionInterval must be non-negative")
        }
    }

    /**
     * Immediately move all children, even if layout is paused
     *
     * @param forceChildUpdates [Boolean]: if child updates should be forced even when children are paused
     */
    fun forceUpdate(forceChildUpdates: Boolean = false) {
        intervalCompleted = true
        forceUpdate = true
        this.forceChildUpdates = forceChildUpdates
        cancelRunnable()
        postRunnable()
    }

    /**
     * Display children on screen.
     *
     * @param changed [Boolean] if this is a new size/position for this layout
     * @param left [Int] left position, relative to parent
     * @param top [Int] top position, relative to parent
     * @param right [Int] right position, relative to parent
     * @param bottom [Int] bottom position, relative to parent
     */
    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        val horizontalPadding = paddingRight + paddingLeft
        val verticalPadding = paddingTop + paddingBottom
        val widthBound = width - horizontalPadding
        val heightBound = height - verticalPadding
        val widthSpec = MeasureSpec.makeMeasureSpec(widthBound, MeasureSpec.AT_MOST)
        val heightSpec = MeasureSpec.makeMeasureSpec(heightBound, MeasureSpec.AT_MOST)

        children.forEachIndexed { index, child ->
            child as MovingView
            child.updatePosition(widthBound, heightBound, forceUpdate = forceChildUpdates)

            child.measure(widthSpec, heightSpec)
            child.layout(child.left, child.top, child.left + child.measuredWidth, child.top + child.measuredHeight)
        }
        intervalCompleted = false
        forceChildUpdates = false
        forceUpdate = false
    }

    /**
     * Throw inflate exception if any child is not a moving view
     */
    override fun onFinishInflate() {
        if (children.any { it !is MovingView }) {
            throw InflateException("Child of MotionLayout must implement MovingView")
        }
        super.onFinishInflate()
    }

    override fun addView(child: View?) {
        addMovingView(child) { super.addView(child) }
    }

    override fun addView(child: View?, params: LayoutParams?) {
        addMovingView(child) { super.addView(child, params) }
    }

    override fun addView(child: View?, index: Int) {
        addMovingView(child) { super.addView(child, index) }
    }

    override fun addView(child: View?, width: Int, height: Int) {
        addMovingView(child) { super.addView(child, width, height) }
    }

    override fun addView(child: View?, index: Int, params: LayoutParams?) {
        addMovingView(child) { super.addView(child, index, params) }
    }

    override fun addViewInLayout(child: View?, index: Int, params: LayoutParams?): Boolean {
        return addMovingView(child) { super.addViewInLayout(child, index, params) }
    }

    override fun addViewInLayout(
        child: View?,
        index: Int,
        params: LayoutParams?,
        preventRequestLayout: Boolean,
    ): Boolean {
        return addMovingView(child) { super.addViewInLayout(child, index, params, preventRequestLayout) }
    }

    /**
     * Validate that view implements [MovingView] before adding
     *
     * @param child [View]?: view to add
     * @param addView () -> T: function to add view to layout
     * @return T: return value from [addView]
     */
    private fun <T> addMovingView(child: View?, addView: () -> T): T {
        if (child !is MovingView) {
            throw IllegalStateException("Child of MotionLayout must implement MovingView")
        }
        return addView()
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        if (!paused) {
            postRunnable()
        }
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        cancelRunnable()
    }

    /**
     * Start runnable
     */
    private fun postRunnable() {
        if (!loopHandler.hasCallbacks(runnable)) {
            loopHandler.post(runnable)
        }
    }

    /**
     * Start runnable after one motion interval has passed
     */
    private fun postRunnableWithDelay() {
        if (!loopHandler.hasCallbacks(runnable)) {
            loopHandler.postDelayed(runnable, motionInterval)
        }
    }

    /**
     * Cancel current runnable
     */
    private fun cancelRunnable() {
        if (loopHandler.hasCallbacks(runnable)) {
            loopHandler.removeCallbacks(runnable)
        }
    }
}
