package xyz.lbres.customview.circlelayout

import android.annotation.SuppressLint
import android.content.Context
import android.util.AttributeSet
import android.view.View
import xyz.lbres.customview.interfaces.singlechildlayout.SingleChildLayout

class SingleChildCircleLayout(context: Context, attrs: AttributeSet?, defStyleAttr: Int) :
    CircleLayout(context, attrs, defStyleAttr), SingleChildLayout {

    override val numChildren: Int
        get() = abstractLayout.numChildren

    override val childLayout: Int
        get() = abstractLayout.childLayout

    private val abstractLayout: AbstractSingleChildCircleLayout =
        AbstractSingleChildCircleLayout(context, attrs, defStyleAttr)

    constructor(context: Context) : this(context, null, 0)
    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)

    @SuppressLint("MissingSuperCall")
    override fun requestLayout() = abstractLayout.requestLayout()
    override fun addView(child: View?) = abstractLayout.addView(child)
    override fun addView(child: View?, params: LayoutParams?) =
        abstractLayout.addView(child, params)

    override fun addView(child: View?, index: Int) = abstractLayout.addView(child, index)
    override fun addView(child: View?, width: Int, height: Int) =
        abstractLayout.addView(child, width, height)

    override fun addView(child: View?, index: Int, params: LayoutParams?) =
        abstractLayout.addView(child, index, layoutParams)

    override fun addViewInLayout(child: View?, index: Int, params: LayoutParams?) =
        abstractLayout.addViewInLayout(child, index, params)

    override fun addViewInLayout(child: View?, index: Int, params: LayoutParams?, preventRequestLayout: Boolean) =
        abstractLayout.addViewInLayout(
            child,
            index,
            params,
            preventRequestLayout,
        )

    override fun removeAllViews() = abstractLayout.removeAllViews()
    override fun removeViews(start: Int, count: Int) = abstractLayout.removeViews(start, count)
    override fun removeViewInLayout(view: View?) = abstractLayout.removeViewInLayout(view)
    override fun removeViewAt(index: Int) = abstractLayout.removeViewAt(index)
    override fun removeView(view: View?) = abstractLayout.removeView(view)
    override fun removeAllViewsInLayout() = abstractLayout.removeAllViewsInLayout()
    override fun removeViewsInLayout(start: Int, count: Int) = abstractLayout.removeViewsInLayout(start, count)
}
