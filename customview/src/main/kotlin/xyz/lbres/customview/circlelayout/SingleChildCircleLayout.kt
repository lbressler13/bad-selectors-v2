package xyz.lbres.customview.circlelayout

import android.content.Context
import android.util.AttributeSet

/**
 * [CircleLayout] where all children have the same resource ID.
 * See README for information about customizing layout.
 */
class SingleChildCircleLayout(context: Context, attrs: AttributeSet?, defStyleAttr: Int) :
    AbstractSingleChildCircleLayout(context, attrs, defStyleAttr) {

    constructor(context: Context) : this(context, null, 0)
    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)
}
