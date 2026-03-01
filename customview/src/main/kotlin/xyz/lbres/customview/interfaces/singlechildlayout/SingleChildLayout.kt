package xyz.lbres.customview.interfaces.singlechildlayout

import xyz.lbres.customview.Manager

/**
 * Functionality that must be implemented by all layouts which generate children from a single resource file.
 */
internal interface SingleChildLayout {
    /**
     * Child manager for layout
     */
    var manager: Manager

    /**
     * Number of children in the layout
     */
    val numChildren: Int
        get() = (manager as SingleChildLayoutManager).numChildren

    /**
     * Resource ID that is used to generate children
     */
    val childLayout: Int
        get() = (manager as SingleChildLayoutManager).childLayout
}
