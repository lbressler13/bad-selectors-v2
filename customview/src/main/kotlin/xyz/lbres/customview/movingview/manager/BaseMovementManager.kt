package xyz.lbres.customview.movingview.manager

import android.util.Log
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
     * Update position. If paused, position will not update unless [forcedPosition] is provided or [forceUpdate] is `true`.
     *
     * @param dimensions [Dimensions]: maximum allowed dimensions for position
     * @param forcedPosition [Double]: position to update to, defaults to `null`
     * @param forceUpdate [Boolean]: if the view position should be update even when paused. Defaults to `false`
     */
    fun updatePosition(
        dimensions: Dimensions<Int>,
        forcedPosition: Position<Double>? = null,
        forceUpdate: Boolean = false,
    ) {
        val validForced = forcedPosition != null &&
            forcedPosition.x in 0.0..dimensions.width.toDouble() &&
            forcedPosition.y in 0.0..dimensions.height.toDouble()

        val newPosition = when {
            validForced -> forcedPosition
            forcedPosition != null -> {
                Log.w(null, "Invalid forced position $forcedPosition, position not updated")
                position
            }
            forceUpdate || !paused -> getNewPosition(dimensions)
            else -> position
        }

        if (newPosition != position) {
            position = newPosition
            callOnMove()
        }
    }

    /**
     * Get the next position value
     *
     * @param dimensions [Dimensions]: maximum allowed dimensions for position
     * @return Position<Double>: next position
     */
    protected abstract fun getNewPosition(dimensions: Dimensions<Int>): Position<Double>

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
