package xyz.lbres.customview.movingview

import android.content.Context
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatTextView
import xyz.lbres.customview.movingview.manager.BaseMovementManager
import xyz.lbres.customview.movingview.manager.MovementManager

/**
 * Moving [AppCompatTextView], to be used as a child of a MotionLayout.
 * See README for information about customizing view.
 */
class MovingTextView(context: Context, attrs: AttributeSet?, defStyleAttr: Int) :
    AppCompatTextView(context, attrs, defStyleAttr), IMovingView {

    private var _manager: BaseMovementManager
    override val manager: MovementManager
        get() = _manager

    /**
     * Type of motion
     */
    private var _motionType: MovingView.MotionType
    override val motionType: MovingView.MotionType
        get() = _motionType

    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, android.R.attr.textViewStyle)
    constructor(context: Context) : this(context, null)

    init {
        val (motionType, manager) = parseAttributes(context, attrs)
        this._motionType = motionType
        this._manager = manager
    }

    /**
     * Update the motion type for the view
     *
     * @param newValue [MotionType]: new motion type
     * @param movementSize [Int]?: movement size, used only for linear movement. Defaults to null
     */
    override fun updateMotionType(newValue: MovingView.MotionType, movementSize: Int?) {
        if (newValue != motionType) {
            _manager = BaseMovementManager.create(newValue, paused, movementSize, previous = _manager)
            _motionType = newValue
        }
    }
}
