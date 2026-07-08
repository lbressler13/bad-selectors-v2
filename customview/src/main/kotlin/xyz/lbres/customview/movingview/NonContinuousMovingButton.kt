package xyz.lbres.customview.movingview

import android.content.Context
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatButton
import androidx.core.content.withStyledAttributes
import xyz.lbres.customview.R
import xyz.lbres.customview.movingview.manager.MovementManager
import xyz.lbres.customview.movingview.manager.NonContinuousMovementManager

/**
 * Button with non-continuous movement, to be used as a child of a MotionLayout.
 * See README for information about customizing view.
 */
class NonContinuousMovingButton(context: Context, attrs: AttributeSet?, defStyleAttr: Int) :
    AppCompatButton(context, attrs, defStyleAttr), IMovingView {

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
}
