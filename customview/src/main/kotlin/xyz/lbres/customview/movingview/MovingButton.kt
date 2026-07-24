package xyz.lbres.customview.movingview

import android.content.Context
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatButton
import xyz.lbres.customview.R
import xyz.lbres.customview.movingview.manager.BaseMovementManager
import xyz.lbres.customview.movingview.manager.MovementManager
import xyz.lbres.customview.movingview.utils.parseAttributes

/**
 * Moving button, to be used as a child of a MotionLayout.
 * See README for information about customizing view.
 */
class MovingButton(context: Context, attrs: AttributeSet?, defStyleAttr: Int) :
    AppCompatButton(context, attrs, defStyleAttr), IMovingView {

    private var _manager: BaseMovementManager
    override val manager: MovementManager
        get() = _manager

    private var _motionType: MovingView.MotionType
    override val motionType: MovingView.MotionType
        get() = _motionType

    constructor(context: Context, attrs: AttributeSet?) : this(
        context,
        attrs,
        android.R.attr.buttonStyle,
    )

    constructor(context: Context) : this(context, null)

    init {
        val (motionType, manager) = parseAttributes(context, attrs)
        this._motionType = motionType
        this._manager = manager
    }

    override fun updateMotionType(newValue: MovingView.MotionType, movementSize: Int?) {
        if (newValue != motionType) {
            _manager = BaseMovementManager.create(motionType, paused, movementSize, previous = _manager)
            _motionType = newValue
        }
    }
}
