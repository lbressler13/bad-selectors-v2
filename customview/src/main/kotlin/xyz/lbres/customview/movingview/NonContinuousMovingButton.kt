package xyz.lbres.customview.movingview

import NonContinuousMovingView
import android.content.Context
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatButton
import androidx.core.content.withStyledAttributes
import xyz.lbres.customview.R
import xyz.lbres.customview.data.Position
import xyz.lbres.customview.movingview.manager.MovementManager
import xyz.lbres.customview.movingview.manager.NonContinuousMovementManager

class NonContinuousMovingButton(context: Context, attrs: AttributeSet?, defStyleAttr: Int) :
    AppCompatButton(context, attrs, defStyleAttr), NonContinuousMovingView {

    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, android.R.attr.buttonStyle)
    constructor(context: Context) : this(context, null)

    private var _manager: NonContinuousMovementManager
    override val manager: MovementManager
        get() = _manager

    /**
     * Extract values from attributes
     */
    init {
        var attrPaused = false

        context.withStyledAttributes(attrs, R.styleable.Movement) {
            attrPaused = getBoolean(R.styleable.Movement_paused, false)
        }

        _manager = NonContinuousMovementManager(attrPaused)
    }

    /**
     * Set position to specific values
     *
     * @param x [Double]: position on the x axis
     * @param y [Double]: position on the y axis
     */
    override fun forcePosition(parentWidth: Int, parentHeight: Int, x: Double, y: Double) {
        _manager.forcePosition(getBounds(parentWidth, parentHeight), Position(x, y))
    }

    /**
     * Update the position of the view
     *
     * @param parentWidth [Int]: width of parent view
     * @param parentHeight [Int]: height of parent view
     */
    override fun updatePosition(parentWidth: Int, parentHeight: Int) {
        _manager.updatePosition(getBounds(parentWidth, parentHeight))

        left = _manager.x.toInt()
        top = _manager.y.toInt()
    }
}
