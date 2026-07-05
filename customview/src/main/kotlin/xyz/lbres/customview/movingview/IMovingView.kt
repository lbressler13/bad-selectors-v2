package xyz.lbres.customview.movingview

import android.view.View
import xyz.lbres.customview.data.Dimensions
import xyz.lbres.customview.movingview.MovingView.OnMoveListener
import xyz.lbres.customview.movingview.MovingView.OnPausedChangedListener
import xyz.lbres.customview.movingview.manager.BaseMovementManager
import xyz.lbres.customview.movingview.manager.MovementManager

internal interface IMovingView : MovingView {
    val manager: MovementManager

    /**
     * If movement of view is paused
     */
    override var paused: Boolean
        get() = (manager as BaseMovementManager).paused
        set(value) { (manager as BaseMovementManager).paused = value }

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
     * Update onMove listener
     *
     * @param callback ([View], Int, Int) -> Unit: callback to call when view moves
     */
    override fun setOnMoveListener(callback: (view: View, x: Int, y: Int) -> Unit) {
        this as View
        (manager as BaseMovementManager).setOnMoveCallback { x, y -> callback(this, x, y) }
    }

    /**
     * Update onMove listener
     *
     * @param listener [OnMoveListener]: new listener, can be `null`
     */
    override fun setOnMoveListener(listener: OnMoveListener?) {
        this as View
        if (listener == null) {
            (manager as BaseMovementManager).setOnMoveCallback(null)
        } else {
            (manager as BaseMovementManager).setOnMoveCallback { x, y -> listener.onMove(this, x, y) }
        }
    }

    /**
     * Update onPauseChanged listener
     *
     * @param callback ([View], Boolean) -> Unit: callback to call when paused state changes
     */
    override fun setOnPauseChangedListener(callback: (view: View, paused: Boolean) -> Unit) {
        this as View
        (manager as BaseMovementManager).setOnPauseChangedCallback { callback(this, paused) }
    }

    /**
     * Update onPauseChanged listener
     *
     * @param listener [OnPausedChangedListener]: new listener, can be `null`
     */
    override fun setOnPauseChangedListener(listener: OnPausedChangedListener?) {
        this as View
        if (listener == null) {
            (manager as BaseMovementManager).setOnPauseChangedCallback(null)
        } else {
            (manager as BaseMovementManager).setOnPauseChangedCallback { paused -> listener.onChange(this, paused) }
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
}
