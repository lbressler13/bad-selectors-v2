package xyz.lbres.customview.circlelayout

import android.content.Context
import android.util.AttributeSet

/**
 * Layout that displays children in a circle. See README for information about customizing layout.
 */
open class CircleLayout(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : AbstractCircleLayout(
    context,
    attrs,
    defStyleAttr,
) {
    /**
     * Modes for method of calculating radius. See README for additional documentation.
     */
    enum class RadiusMode { FIT_CHILDREN, FIXED, PERCENT }

    /**
     * Modes for method of calculating angle between children. See README for additional documentation.
     */
    enum class AngleMode { DISTRIBUTED, FIXED }

    constructor(context: Context) : this(context, null, 0)
    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)
}
