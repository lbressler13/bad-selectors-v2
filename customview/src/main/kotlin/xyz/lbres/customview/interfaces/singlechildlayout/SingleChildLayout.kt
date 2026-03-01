package xyz.lbres.customview.interfaces.singlechildlayout

/**
 * Functionality that must be implemented by all layouts which generate children from a single resource file.
 */
interface SingleChildLayout {
    /**
     * Number of children in the layout
     */
    val numChildren: Int

    /**
     * Resource ID that is used to generate children
     */
    val childLayout: Int
}
