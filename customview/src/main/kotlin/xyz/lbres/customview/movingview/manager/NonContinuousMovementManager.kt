package xyz.lbres.customview.movingview.manager

import android.util.Log
import xyz.lbres.customview.data.Dimensions
import xyz.lbres.customview.data.Position
import xyz.lbres.customview.utils.createRandom

/**
 * Manage and update information about movement for a view whose position updates are not continuous
 *
 * @param paused [Boolean]: if movement is initially paused
 */
internal class NonContinuousMovementManager(paused: Boolean) : BaseMovementManager(paused) {
    /**
     * Update the position value based on the allowed dimensions
     *
     * @param dimensions [Dimensions]: maximum allowed dimensions for position
     */
    override fun doPositionUpdate(dimensions: Dimensions<Int>, forcedPosition: Position<Double>?) {
        val xRange = 0.0..dimensions.width.toDouble()
        val yRange = 0.0..dimensions.height.toDouble()

        if (forcedPosition != null && forcedPosition.x in xRange && forcedPosition.y in yRange) {
            position = forcedPosition
        } else if (forcedPosition == null) {
            val newX = createRandom().nextDouble(xRange.start, xRange.endInclusive)
            val newY = createRandom().nextDouble(yRange.start, yRange.endInclusive)
            position = Position(newX, newY)
        } else {
            Log.w(null, "Invalid forced position $forcedPosition, position not updated")
        }
    }
}
