package xyz.lbres.customview.interfaces.singlechildlayout

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.ViewGroup
import xyz.lbres.customview.IManager
import xyz.lbres.customview.R
import xyz.lbres.customview.ext.typedarray.getIntOrNull
import xyz.lbres.customview.ext.typedarray.getResourceIdOrNull
import androidx.core.content.withStyledAttributes
import androidx.core.view.isNotEmpty

/**
 * Manager for views in a single child layout
 *
 * @param getLayout () -> [ViewGroup]: function to retrieve the layout to manage
 * @param context [Context]: activity context
 * @param attrs [AttributeSet]: attributes for layout, can be `null`
 */
internal class SingleChildLayoutManager(
    private val getLayout: () -> ViewGroup,
    private val context: Context,
    attrs: AttributeSet?,
) : IManager() {

    enum class ChildInitializationState { NOT_STARTED, IN_PROGRESS, COMPLETE }

    /**
     * State of child initialization.
     */
    var childInitializationState: ChildInitializationState = ChildInitializationState.NOT_STARTED
        private set

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
        if (childInitializationState == ChildInitializationState.COMPLETE) {
            throw UnsupportedOperationException("Cannot modify children of SingleChildLayout")
        }

        return modificationFn()
    }

    /**
     * Add children to layout using values from attributes
     */
    fun initializeChildren() {
        if (getLayout().isNotEmpty()) {
            throw IllegalStateException("SingleChildLayout cannot be created with children")
        }

        childInitializationState = ChildInitializationState.IN_PROGRESS

        val layoutInflater = LayoutInflater.from(context)
        repeat(numChildren) {
            val view = layoutInflater.inflate(childLayout, getLayout(), false)
            getLayout().addView(view)
        }

        childInitializationState = ChildInitializationState.COMPLETE
    }
}
