package xyz.lbres.customview.circlelayout

import android.content.Context
import android.util.AttributeSet
import android.view.View
import xyz.lbres.customview.interfaces.singlechildlayout.SingleChildLayout
import xyz.lbres.customview.interfaces.singlechildlayout.SingleChildLayoutManager

/**
 * Abstract implementation of CircleLayout where all children have the same resource ID.
 * See README for information about customizing layout.
 */
sealed class AbstractSingleChildCircleLayout(context: Context, attrs: AttributeSet?, defStyleAttr: Int) :
    CircleLayout(context, attrs, defStyleAttr), SingleChildLayout {

    /**
     * Manager for children
     */
    // must be lateinit to handle property access in requestLayout
    private lateinit var manager: SingleChildLayoutManager

    /**
     * Resource ID of layout to use for children
     */
    override val childLayout: Int
        get() = manager.childLayout

    /**
     * Number of children to create
     */
    override val numChildren: Int
        get() = manager.numChildren

    init {
        manager = SingleChildLayoutManager({ this }, context, attrs)
    }

    /**
     * Initialize children, if needed, and request layout
     */
    override fun requestLayout() {
        if (this::manager.isInitialized && !manager.childrenInitialized) {
            manager.initializeChildren()
        }

        super.requestLayout()
    }

    /**
     * Call manager wrapper for all additions/removal of children
     */

    override fun addView(child: View?) {
        manager.modifyChildren { super.addView(child) }
    }

    override fun addView(child: View?, params: LayoutParams?) {
        manager.modifyChildren { super.addView(child, params) }
    }

    override fun addView(child: View?, index: Int) {
        manager.modifyChildren { super.addView(child, index) }
    }

    override fun addView(child: View?, width: Int, height: Int) {
        manager.modifyChildren { super.addView(child, width, height) }
    }

    override fun addView(child: View?, index: Int, params: LayoutParams?) {
        manager.modifyChildren { super.addView(child, index, params) }
    }

    public override fun addViewInLayout(child: View?, index: Int, params: LayoutParams?): Boolean {
        return manager.modifyChildren { super.addViewInLayout(child, index, params) }
    }

    public override fun addViewInLayout(
        child: View?,
        index: Int,
        params: LayoutParams?,
        preventRequestLayout: Boolean,
    ): Boolean {
        return manager.modifyChildren {
            super.addViewInLayout(
                child,
                index,
                params,
                preventRequestLayout,
            )
        }
    }

    override fun removeAllViews() {
        manager.modifyChildren { super.removeAllViews() }
    }

    override fun removeViews(start: Int, count: Int) {
        manager.modifyChildren { super.removeViews(start, count) }
    }

    override fun removeViewInLayout(view: View?) {
        manager.modifyChildren { super.removeViewInLayout(view) }
    }

    override fun removeViewAt(index: Int) {
        manager.modifyChildren { super.removeViewAt(index) }
    }

    override fun removeView(view: View?) {
        manager.modifyChildren { super.removeView(view) }
    }

    override fun removeAllViewsInLayout() {
        manager.modifyChildren { super.removeAllViewsInLayout() }
    }

    override fun removeViewsInLayout(start: Int, count: Int) {
        manager.modifyChildren { super.removeViewsInLayout(start, count) }
    }
}
