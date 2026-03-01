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

internal interface ISingleChildLayout : SingleChildLayout {
    /**
     * Child manager for layout
     */
    var manager: SingleChildLayoutManager

    /**
     * Number of children in the layout
     */
    override val numChildren: Int
        get() = manager.numChildren

    /**
     * Resource ID that is used to generate children
     */
    override val childLayout: Int
        get() = manager.childLayout
}
