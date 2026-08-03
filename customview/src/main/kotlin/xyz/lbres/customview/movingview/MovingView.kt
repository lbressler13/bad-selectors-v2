package xyz.lbres.customview.movingview

import android.view.View

/**
 * Interface for all children of a MotionLayout
 */
sealed interface MovingView {

    /**
     * If movement of view is paused
     */
    var paused: Boolean

    /**
     * Size of each movement, in pixels. Used only for linear motion.
     */
    var movementSize: Int?

    /**
     * Type of motion
     */
    val motionType: MotionType

    /**
     * Update the motion type for the view
     *
     * @param newValue [MotionType]: new motion type
     * @param movementSize [Int]?: movement size, used only for linear movement. Defaults to null
     */
    fun updateMotionType(newValue: MotionType, movementSize: Int? = null)

    /**
     * Update the position of the view
     *
     * @param parentWidth [Int]: width of parent view
     * @param parentHeight [Int]: height of parent view
     * @param forceUpdate [Boolean]: if the view position should be updated even when paused. Defaults to `false`
     */
    fun updatePosition(parentWidth: Int, parentHeight: Int, forceUpdate: Boolean = false)

    /**
     * Set position to specific values
     *
     * @param parentWidth [Int]: width of parent view
     * @param parentHeight [Int]: height of parent view
     * @param x [Double]: position on the x axis
     * @param y [Double]: position on the y axis
     */
    fun forcePosition(parentWidth: Int, parentHeight: Int, x: Double, y: Double)

    /**
     * Set position to specific values
     *
     * @param parentWidth [Int]: width of parent view
     * @param parentHeight [Int]: height of parent view
     * @param x [Int]: position on the x axis
     * @param y [Int]: position on the y axis
     */
    fun forcePosition(parentWidth: Int, parentHeight: Int, x: Int, y: Int)

    /**
     * Set initial position of view
     *
     * @param parentWidth [Int]: width of parent view
     * @param parentHeight [Int]: height of parent view
     */
    fun setInitialPosition(parentWidth: Int, parentHeight: Int)

    /**
     * Update onMove listener
     *
     * @param callback ([View], Int, Int) -> Unit: callback to call when view moves
     */
    fun setOnMoveListener(callback: (view: View, x: Int, y: Int) -> Unit)

    /**
     * Update onMove listener
     *
     * @param listener [OnMoveListener]: new listener, can be `null`
     */
    fun setOnMoveListener(listener: OnMoveListener?)

    /**
     * Update onPauseChanged listener
     *
     * @param callback ([View], Boolean) -> Unit: callback to call when paused state changes
     */
    fun setOnPauseChangedListener(callback: (view: View, paused: Boolean) -> Unit)

    /**
     * Update onPauseChanged listener
     *
     * @param listener [OnPausedChangedListener]: new listener, can be `null`
     */
    fun setOnPauseChangedListener(listener: OnPausedChangedListener?)

    /**
     * Interface for listener to bind view position updating
     */
    interface OnMoveListener {
        /**
         * Function to invoke when move event occurs
         *
         * @param view [View]: view that is moved
         * @param x [Int]: new x position
         * @param y [Int]: new y position
         */
        fun onMove(view: View, x: Int, y: Int)
    }

    /**
     * Interface for listener to bind to changes in paused state
     */
    interface OnPausedChangedListener {
        /**
         * Function to invoke when paused state changes
         *
         * @param view [View]: view whose state is changed
         * @param paused [Boolean]: new value of paused attribute
         */
        fun onChange(view: View, paused: Boolean)
    }

    /**
     * Types of motion for a view
     */
    enum class MotionType {
        /**
         * Position updates have no relation to each other
         */
        NONCONTINUOUS,
        /**
         * Position updates occur along a straight line
         */
        LINEAR,
    }
}
