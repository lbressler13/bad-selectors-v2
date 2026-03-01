package xyz.lbres.badselectorsv2.abstracts

import xyz.lbres.kotlinutils.list.IntList

abstract class TabFragment : BaseFragment() {
    /**
     * Metadata about the current fragment
     */
    protected abstract var metadata: Metadata

    /**
     * Metadata about a [TabFragment].
     *
     * @param titleResId [Int]: resource ID for the string that should be displayed in the app title bar
     * @param tabTitleResIds [IntList]: list of resource IDs for strings that should be displayed as tab titles
     * @param navActionFromHomeId [Int]: resource ID for the navigation action used to launch the given fragment from the home fragment
     */
    data class Metadata(
        val titleResId: Int,
        val tabTitleResIds: IntList,
        val navActionFromHomeId: Int,
    )
}
