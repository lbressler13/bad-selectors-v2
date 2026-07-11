package xyz.lbres.customview.movingview

import android.view.View
import xyz.lbres.customview.data.Dimensions
import xyz.lbres.customview.data.Position
import xyz.lbres.customview.movingview.MovingView.OnMoveListener
import xyz.lbres.customview.movingview.MovingView.OnPausedChangedListener
import xyz.lbres.customview.movingview.manager.BaseMovementManager
import xyz.lbres.customview.movingview.manager.MovementManager

/**
 * Internal partial implementation of [MovingView]
 */
internal interface IMovingView : MovingView {
    val manager: MovementManager

    /**
     * If movement of view is paused
     */
    override var paused: Boolean
        get() = baseManager().paused
        set(value) { baseManager().paused = value }

    /**
     * Update the position of the view
     *
     * @param parentWidth [Int]: width of parent view
     * @param parentHeight [Int]: height of parent view
     * @param forceUpdate [Boolean]: if the view position should be update even when paused. Defaults to `false`
     */
    override fun updatePosition(parentWidth: Int, parentHeight: Int, forceUpdate: Boolean) {
        this as View
        val bounds = getBounds(parentWidth, parentHeight)
        baseManager().updatePosition(bounds, forceUpdate = forceUpdate)

        left = baseManager().x.toInt()
        top = baseManager().y.toInt()
    }

    /**
     * Set position to specific values
     *
     * @param x [Double]: position on the x axis
     * @param y [Double]: position on the y axis
     */
    override fun forcePosition(parentWidth: Int, parentHeight: Int, x: Double, y: Double) {
        this as View
        val bounds = getBounds(parentWidth, parentHeight)
        baseManager().updatePosition(bounds, forcedPosition = Position(x, y))

        left = baseManager().x.toInt()
        top = baseManager().y.toInt()
    }

    /**
     * Set position to specific values
     *
     * @param parentWidth [Int]: width of parent view
     * @param parentHeight [Int]: height of parent view
     * @param x [Int]: position on the x axis
     * @param y [Int]: position on the y axis
     */
    override fun forcePosition(parentWidth: Int, parentHeight: Int, x: Int, y: Int) {
        forcePosition(parentWidth, parentHeight, x.toDouble(), y.toDouble())
    }

    /**
     * Set initial position of view
     *
     * @param parentWidth [Int]: width of parent view
     * @param parentHeight [Int]: height of parent view
     */
    override fun setInitialPosition(parentWidth: Int, parentHeight: Int) {
        this as View
        baseManager().setInitialPosition(Dimensions(parentWidth, parentHeight))

        left = baseManager().x.toInt()
        top = baseManager().y.toInt()
    }

    /**
     * Update onMove listener
     *
     * @param callback ([View], Int, Int) -> Unit: callback to call when view moves
     */
    override fun setOnMoveListener(callback: (view: View, x: Int, y: Int) -> Unit) {
        setOnMoveListener(object : OnMoveListener {
            override fun onMove(view: View, x: Int, y: Int) = callback(view, x, y)
        })
    }

    /**
     * Update onMove listener
     *
     * @param listener [OnMoveListener]: new listener, can be `null`
     */
    override fun setOnMoveListener(listener: OnMoveListener?) {
        this as View
        if (listener == null) {
            baseManager().setOnMoveCallback(null)
        } else {
            baseManager().setOnMoveCallback { x, y -> listener.onMove(this, x, y) }
        }
    }

    /**
     * Update onPauseChanged listener
     *
     * @param callback ([View], Boolean) -> Unit: callback to call when paused state changes
     */
    override fun setOnPauseChangedListener(callback: (view: View, paused: Boolean) -> Unit) {
        setOnPauseChangedListener(object : OnPausedChangedListener {
            override fun onChange(view: View, paused: Boolean) = callback(view, paused)
        })
    }

    /**
     * Update onPauseChanged listener
     *
     * @param listener [OnPausedChangedListener]: new listener, can be `null`
     */
    override fun setOnPauseChangedListener(listener: OnPausedChangedListener?) {
        this as View
        if (listener == null) {
            baseManager().setOnPauseChangedCallback(null)
        } else {
            baseManager().setOnPauseChangedCallback { paused -> listener.onChange(this, paused) }
        }
    }

    /**
     * Get x and y bounds based on parent size and view size
     *
     * @param parentWidth [Int]: width of parent view
     * @param parentHeight [Int]: height of parent view
     */
    fun getBounds(parentWidth: Int, parentHeight: Int): Dimensions<Int> {
        this as View
        val width = right - left
        val height = bottom - top

        val widthBound = parentWidth - width
        val heightBound = parentHeight - height
        return Dimensions(widthBound, heightBound)
    }

    /**
     * Cast manager to BaseMovementManager for convenience
     */
    fun baseManager(): BaseMovementManager = manager as BaseMovementManager
}
