package xyz.lbres.customview.testutils

import android.view.View

/**
 * Log the positions of a list of views
 */
internal fun logViewPositions(views: List<View>) {
    views.forEach { println("(${it.left}, ${it.top})") }
    println()
}
