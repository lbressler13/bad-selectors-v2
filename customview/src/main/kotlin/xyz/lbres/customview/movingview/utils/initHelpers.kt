package xyz.lbres.customview.movingview.utils

import android.content.Context
import android.util.AttributeSet
import androidx.core.content.withStyledAttributes
import xyz.lbres.customview.R
import xyz.lbres.customview.ext.typedarray.getIntOrNull
import xyz.lbres.customview.movingview.MovingView
import xyz.lbres.customview.movingview.manager.BaseMovementManager

/**
 * Parse context and attributes set to identify the motion type and create a movement manager
 */
internal fun parseAttributes(context: Context, attrs: AttributeSet?): Pair<MovingView.MotionType, BaseMovementManager> {
    var attrPaused = false
    var attrMovementSize = 0
    var attrMotionType: MovingView.MotionType? = null

    context.withStyledAttributes(attrs, R.styleable.Movement) {
        attrPaused = getBoolean(R.styleable.Movement_paused, false)
        attrMovementSize = getInt(R.styleable.Movement_movementSize, 0)
        val motionTypeValue = getIntOrNull(R.styleable.Movement_motionType)
        attrMotionType = when (motionTypeValue) {
            0 -> MovingView.MotionType.NONCONTINUOUS
            1 -> MovingView.MotionType.LINEAR
            else -> throw IllegalStateException("Valid motionType is required to construct a MovingView")
        }
    }

    val manager = BaseMovementManager.create(attrMotionType!!, attrPaused, attrMovementSize)
    return Pair(attrMotionType, manager)
}
