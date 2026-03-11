package xyz.lbres.badselectorsv2.calculator.common

import android.text.Spannable
import android.text.method.ScrollingMovementMethod
import android.widget.TextView
import xyz.lbres.kotlinutils.general.tryOrDefault

/**
 * Extends [ScrollingMovementMethod] to expose the protected functionality
 * of scrolling to top and bottom
 */
class UnprotectedScrollingMovementMethod : ScrollingMovementMethod() {
    /**
     * Scroll to bottom of widget
     */
    fun goToBottom(widget: TextView?, buffer: Spannable? = null): Boolean {
        return tryOrDefault(false) {
            bottom(widget, buffer)
        }
    }

    /**
     * Scroll to top of widget
     */
    fun goToTop(widget: TextView?, buffer: Spannable? = null): Boolean {
        return tryOrDefault(false) {
            top(widget, buffer)
        }
    }
}
