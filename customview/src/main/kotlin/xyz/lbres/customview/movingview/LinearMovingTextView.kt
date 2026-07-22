package xyz.lbres.customview.movingview

import android.content.Context
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.content.withStyledAttributes
import xyz.lbres.customview.R
import xyz.lbres.customview.movingview.manager.LinearMovementManager
import xyz.lbres.customview.movingview.manager.MovementManager

/**
 * TextView with continuous linear movement, to be used as a child of a MotionLayout.
 * See README for information about customizing view.
 */
class LinearMovingTextView(context: Context, attrs: AttributeSet?, defStyleAttr: Int) :
    AppCompatTextView(context, attrs, defStyleAttr), LinearMovingView, IMovingView {

    private val _manager: LinearMovementManager
    override val manager: MovementManager
        get() = _manager

    override var movementSize: Int
        get() = _manager.movementSize
        set(value) { _manager.movementSize = value }

    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, android.R.attr.buttonStyle)
    constructor(context: Context) : this(context, null)

    init {
        var attrPaused = false
        var attrMovementSize = 0

        context.withStyledAttributes(attrs, R.styleable.Movement) {
            attrPaused = getBoolean(R.styleable.Movement_paused, false)
        }
        context.withStyledAttributes(attrs, R.styleable.LinearMovement) {
            attrMovementSize = getInt(R.styleable.LinearMovement_movementSize, 0)
        }
        _manager = LinearMovementManager(attrPaused, attrMovementSize)
    }
}
