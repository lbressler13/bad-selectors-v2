package xyz.lbres.customview.movingview.manager

import xyz.lbres.customview.data.Dimensions
import xyz.lbres.customview.data.Position

/**
 * Base functionality to track information about movement for a moving view
 *
 * @param paused [Boolean]: if movement is initially paused
 */
internal abstract class BaseMovementManager(paused: Boolean) : MovementManager {
    /**
     * Callback to invoke when movement occurs
     */
    private var onMoveCallback: ((x: Int, y: Int) -> Unit)? = null

    /**
     * Callback to invoke when movement is paused or unpaused
     */
    private var onPauseChangedCallback: ((Boolean) -> Unit)? = null

    /**
     * If movement is paused
     */
    private var _paused: Boolean = paused
    var paused: Boolean
        get() = _paused
        set(value) = updatePaused(value)

    /**
     * Position on screen
     */
    protected var position = Position(0.0, 0.0)
    val x: Double
        get() = position.x
    val y: Double
        get() = position.y

    /**
     * Perform full position update if movement is not paused
     *
     * @param dimensions [Dimensions]: maximum allowed dimensions for position
     * @return [Position]: updated position
     */
    fun updatePosition(dimensions: Dimensions<Int>) {
        if (!paused) {
            val previousPosition = position
            doPositionUpdate(dimensions)

            if (position != previousPosition) {
                callOnMove()
            }
        }
    }

    /**
     * Set position to specific values
     *
     * @param dimensions [Dimensions]: maximum allowed dimensions for position
     * @param forcedPosition [Double]: position to update to
     */
    fun forcePosition(dimensions: Dimensions<Int>, forcedPosition: Position<Double>) {
        val previousPosition = position
        doPositionUpdate(dimensions, forcedPosition)

        if (position != previousPosition) {
            callOnMove()
        }
    }

    /**
     * Update the [position] variable
     *
     * @param dimensions [Dimensions]: maximum allowed dimensions for position
     */
    protected abstract fun doPositionUpdate(dimensions: Dimensions<Int>, forcedPosition: Position<Double>? = null)

    /**
     * Invoke onPauseChanged callback, if not null, and set new paused value
     */
    private fun updatePaused(newValue: Boolean) {
        if (newValue != paused) {
            onPauseChangedCallback?.invoke(newValue)
        }
        _paused = newValue
    }

    /**
     * Invoke onMove callback, if not null
     */
    protected fun callOnMove() {
        onMoveCallback?.invoke(position.x.toInt(), position.y.toInt())
    }

    /**
     * Update onMove callback
     *
     * @param callback (Int, Int) -> Unit: callback to call when movement occurs
     */
    fun setOnMoveCallback(callback: ((Int, Int) -> Unit)?) {
        onMoveCallback = callback
    }

    /**
     * Update onPauseChanged callback
     *
     * @param callback (Boolean) -> Unit: callback to call when paused value is changed
     */
    fun setOnPauseChangedCallback(callback: ((Boolean) -> Unit)?) {
        onPauseChangedCallback = callback
    }
}
