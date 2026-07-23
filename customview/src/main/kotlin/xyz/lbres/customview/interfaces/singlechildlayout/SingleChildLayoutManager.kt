package xyz.lbres.customview.interfaces.singlechildlayout

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.withStyledAttributes
import androidx.core.view.isNotEmpty
import xyz.lbres.customview.R
import xyz.lbres.customview.ext.typedarray.getIntOrNull
import xyz.lbres.customview.ext.typedarray.getResourceIdOrNull

/**
 * Manager for views in a SingleChildLayout.
 * Required because interfaces cannot have private properties or methods.
 *
 * @param context [Context]: activity context
 * @param attrs [AttributeSet]: attributes for layout, can be `null`
 */
// values are used in constructor only, not stored for later use
internal class SingleChildLayoutManager(context: Context, attrs: AttributeSet?) {

    private enum class ChildInitializationState { NOT_STARTED, IN_PROGRESS, COMPLETE }

    /**
     * State of child initialization.
     */
    private var childInitializationState: ChildInitializationState = ChildInitializationState.NOT_STARTED
    val childrenInitialized: Boolean
        get() = childInitializationState == ChildInitializationState.COMPLETE

    /**
     * Resource ID of layout to use for children
     */
    val childLayout: Int

    /**
     * Number of children to create
     */
    val numChildren: Int

    /**
     * Extract values from attributes
     */
    init {
        var childLayoutAttr: Int? = null
        var numChildrenAttr: Int? = null

        context.withStyledAttributes(attrs, R.styleable.SingleChild) {
            childLayoutAttr = getResourceIdOrNull(R.styleable.SingleChild_childLayout)
            numChildrenAttr = getIntOrNull(R.styleable.SingleChild_numChildren)
        }

        if (childLayoutAttr == null || numChildrenAttr == null) {
            throw IllegalStateException("SingleChildLayout requires numChildren and childLayout")
        }

        childLayout = childLayoutAttr
        numChildren = numChildrenAttr
    }

    /**
     * Throw exception when modifying children if layout has been initialized
     *
     * @param modificationFn () -> T: function to modify children
     * @return T: return value from [modificationFn]
     */
    fun <T> modifyChildren(modificationFn: () -> T): T {
        if (childInitializationState != ChildInitializationState.IN_PROGRESS) {
            throw UnsupportedOperationException("Cannot modify children of SingleChildLayout")
        }

        return modificationFn()
    }

    /**
     * Add children to layout using values from attributes
     *
     * @param layout [ViewGroup]: layout to use for initialization
     */
    fun initializeChildren(layout: ViewGroup) {
        if (childInitializationState == ChildInitializationState.NOT_STARTED) {
            if (layout.isNotEmpty()) {
                throw IllegalStateException("SingleChildLayout cannot be created with children")
            }

            childInitializationState = ChildInitializationState.IN_PROGRESS

            val layoutInflater = LayoutInflater.from(layout.context)
            repeat(numChildren) {
                val view = layoutInflater.inflate(childLayout, layout, false)
                layout.addView(view)
            }

            childInitializationState = ChildInitializationState.COMPLETE
        }
    }
}
