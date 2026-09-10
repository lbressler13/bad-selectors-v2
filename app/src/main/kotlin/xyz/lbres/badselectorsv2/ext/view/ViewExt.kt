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

/**
 * Make a view visible if a specific condition is met
 *
 * @param visible [Boolean]
 * @param hiddenVisibility: visibility to use if [visible] is false, defaults to [View.GONE]
 */
fun View.visibleIf(visible: Boolean, hiddenVisibility: Int = View.GONE) {
    visibility = if (visible) View.VISIBLE else hiddenVisibility
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
