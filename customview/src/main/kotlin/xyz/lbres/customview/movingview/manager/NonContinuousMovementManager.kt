package xyz.lbres.customview.movingview.manager

import xyz.lbres.customview.data.Dimensions
import xyz.lbres.customview.data.Position

/**
 * Manage and update information about movement for a view whose position updates are not continuous
 *
 * @param paused [Boolean]: if movement is initially paused
 */
internal class NonContinuousMovementManager(paused: Boolean) : BaseMovementManager(paused) {
    /**
     * Get the next position value
     *
     * @param dimensions [Dimensions]: maximum allowed dimensions for position
     * @return Position<Double>: next position
     */
    override fun getNewPosition(dimensions: Dimensions<Int>): Position<Double> {
        val newX = random.nextDouble(0.0, dimensions.width.toDouble())
        val newY = random.nextDouble(0.0, dimensions.height.toDouble())
        return Position(newX, newY)
    }
}
