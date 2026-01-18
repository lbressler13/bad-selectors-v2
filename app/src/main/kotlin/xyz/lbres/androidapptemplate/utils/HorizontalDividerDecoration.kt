package xyz.lbres.androidapptemplate.utils

import android.graphics.Canvas
import android.graphics.drawable.Drawable
import androidx.core.view.children
import androidx.recyclerview.widget.RecyclerView

/**
 * Horizontal divider decoration with custom drawable
 */
class HorizontalDividerDecoration(private val divider: Drawable) : RecyclerView.ItemDecoration() {
    /**
     * Draw divider on canvas
     */
    override fun onDraw(canvas: Canvas, parent: RecyclerView, state: RecyclerView.State) {
        parent.children.forEach { child ->
            val left = child.left
            val right = child.right
            val top = child.bottom
            val bottom = top + divider.intrinsicHeight

            divider.setBounds(left, top, right, bottom)
            divider.draw(canvas)
        }
    }
}
