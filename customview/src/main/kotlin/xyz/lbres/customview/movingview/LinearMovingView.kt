package xyz.lbres.customview.movingview

/**
 * Functionality for a view whose position updates are continuous and linear
 */
interface LinearMovingView : MovingView {
    /**
     * Size of each movement, in pixels
     */
    var movementSize: Int
}
