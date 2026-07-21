package xyz.lbres.customview.movingview.manager

import android.util.Log
import xyz.lbres.customview.data.Dimensions
import xyz.lbres.customview.data.Position
import xyz.lbres.customview.movingview.utils.getAllowedAngles
import xyz.lbres.customview.utils.seededRandom
import java.lang.Math.toRadians
import kotlin.math.cos
import kotlin.math.sin

internal class ContinuousLinearMovementManager(paused: Boolean, movementSize: Int) : BaseMovementManager(paused) {

    /**
     * Size of each movement
     */
    private var _movementSize: Int = 0
    var movementSize: Int
        get() = _movementSize
        set(value) = updateMovementSize(value)

    /**
     * Angle of movement, in radians
     */
    var angle: Double = toRadians((0..360).seededRandom().toDouble())
        private set

    /**
     * Amount to increment position on each update, based on [angle] and [movementSize]
     */
    private var dx: Double = 0.0
    private var dy: Double = 0.0

    init {
        updateMovementSize(movementSize)
        updateDxDy()
    }

    /**
     * Get the next position value
     *
     * @param dimensions [Dimensions]: maximum allowed dimensions for position
     * @return Position<Double>: next position
     */
    override fun getNewPosition(dimensions: Dimensions<Int>): Position<Double> {
        val newX = position.x + dx
        val newY = position.y + dy
        val newPosition = Position(newX, newY)
        println("Old position: $position")
        println("New position: $newPosition")
        if (!isValidPosition(newPosition, dimensions) || (dx == 0.0 && dy == 0.0)) {
            updateAngle(newPosition, dimensions)
        }
        return newPosition
    }

    /**
     * Set new value for [movementSize]
     */
    private fun updateMovementSize(newValue: Int) {
        if (newValue >= 0) {
            _movementSize = newValue
            updateDxDy()
        } else {
            Log.w(null, "Cannot set movement size to $newValue. Movement size must be non-negative.")
        }
    }

    /**
     * Update the angle to a random value that will not take the position off the screen.
     *
     * @param position [Position]: position of view
     * @param dimensions [Dimensions]: dimensions of allowed positions for child view
     */
    private fun updateAngle(position: Position<Double>, dimensions: Dimensions<Int>) {
        val degrees: Int = getAllowedAngles(position, dimensions).seededRandom()
        angle = toRadians(degrees.toDouble())
        println("New angle: $degrees")
        updateDxDy()
    }

    /**
     * Update [dx] and [dy] based on the current angle and movement size
     */
    private fun updateDxDy() {
        dx = cos(angle) * movementSize
        dy = sin(angle) * movementSize
    }
}
