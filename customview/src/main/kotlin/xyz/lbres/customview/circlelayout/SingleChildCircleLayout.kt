package xyz.lbres.customview.circlelayout

import android.content.Context
import android.util.AttributeSet
import android.view.View
import xyz.lbres.customview.Manager
import xyz.lbres.customview.interfaces.singlechildlayout.SingleChildLayout
import xyz.lbres.customview.interfaces.singlechildlayout.SingleChildLayoutManager
import xyz.lbres.customview.interfaces.singlechildlayout.SingleChildLayoutManager.ChildInitializationState

/**
 * [CircleLayout] where all children have the same resource ID.
 * See README for information about customizing layout.
 */
class SingleChildCircleLayout(context: Context, attrs: AttributeSet?, defStyleAttr: Int) :
    CircleLayout(context, attrs, defStyleAttr), SingleChildLayout {

    constructor(context: Context) : this(context, null, 0)
    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)

    /**
     * Manager for children
     */
    // must be lateinit to handle property access in requestLayout
    private lateinit var singleChildManager: SingleChildLayoutManager
    override lateinit var manager: Manager

    init {
        singleChildManager = SingleChildLayoutManager({ this }, context, attrs)
        manager = singleChildManager
    }

    /**
     * Initialize children, if needed, and request layout
     */
    override fun requestLayout() {
        if (
            this::singleChildManager.isInitialized &&
            singleChildManager.childInitializationState == ChildInitializationState.NOT_STARTED
        ) {
            singleChildManager.initializeChildren()
        }

        super.requestLayout()
    }

    /**
     * Call manager wrapper for all additions/removal of children
     */

    override fun addView(child: View?) {
        singleChildManager.modifyChildren { super.addView(child) }
    }

    override fun addView(child: View?, params: LayoutParams?) {
        singleChildManager.modifyChildren { super.addView(child, params) }
    }

    override fun addView(child: View?, index: Int) {
        singleChildManager.modifyChildren { super.addView(child, index) }
    }

    override fun addView(child: View?, width: Int, height: Int) {
        singleChildManager.modifyChildren { super.addView(child, width, height) }
    }

    override fun addView(child: View?, index: Int, params: LayoutParams?) {
        singleChildManager.modifyChildren { super.addView(child, index, params) }
    }

    override fun addViewInLayout(child: View?, index: Int, params: LayoutParams?): Boolean {
        return singleChildManager.modifyChildren { super.addViewInLayout(child, index, params) }
    }

    override fun addViewInLayout(
        child: View?,
        index: Int,
        params: LayoutParams?,
        preventRequestLayout: Boolean,
    ): Boolean {
        return singleChildManager.modifyChildren {
            super.addViewInLayout(
                child,
                index,
                params,
                preventRequestLayout,
            )
        }
    }

    override fun removeAllViews() {
        singleChildManager.modifyChildren { super.removeAllViews() }
    }

    override fun removeViews(start: Int, count: Int) {
        singleChildManager.modifyChildren { super.removeViews(start, count) }
    }

    override fun removeViewInLayout(view: View?) {
        singleChildManager.modifyChildren { super.removeViewInLayout(view) }
    }

    override fun removeViewAt(index: Int) {
        singleChildManager.modifyChildren { super.removeViewAt(index) }
    }

    override fun removeView(view: View?) {
        singleChildManager.modifyChildren { super.removeView(view) }
    }

    override fun removeAllViewsInLayout() {
        singleChildManager.modifyChildren { super.removeAllViewsInLayout() }
    }

    override fun removeViewsInLayout(start: Int, count: Int) {
        singleChildManager.modifyChildren { super.removeViewsInLayout(start, count) }
    }
}
