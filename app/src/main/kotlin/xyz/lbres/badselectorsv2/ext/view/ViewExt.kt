package xyz.lbres.badselectorsv2.ext.view

import android.view.View

/**
 * Enable view
 */
fun View.enable() {
    isEnabled = true
}

/**
 * Disable view
 */
fun View.disable() {
    isEnabled = false
}

/**
 * Make view visible
 */
fun View.visible() {
    visibility = View.VISIBLE
}

/**
 * Make view invisible
 */
fun View.invisible() {
    visibility = View.INVISIBLE
}

/**
 * Make view gone
 */
fun View.gone() {
    visibility = View.GONE
}

fun View.visibleIf(visible: Boolean, defaultVisibility: Int = View.GONE) {
    visibility = if (visible) View.VISIBLE else defaultVisibility
}

/**
 * Set view opacity to 50%.
 */
fun View.halfOpacity() {
    alpha = 0.5f
}

/**
 * Set view opacity to 100%.
 */
fun View.fullOpacity() {
    alpha = 1f
}
