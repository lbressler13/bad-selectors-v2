package xyz.lbres.badselectorsv2.home

import androidx.lifecycle.ViewModel

/**
 * ViewModel to track information about expanded/collapsed views in the Home fragment UI
 */
class HomeViewModel : ViewModel() {
    /**
     * If items are expanded
     */
    private var _selectorsExpanded = BooleanArray(0)
    val selectorsExpanded: BooleanArray
        get() = _selectorsExpanded

    /**
     * If [selectorsExpanded] isn't initialized, initialize to all closed
     *
     * @param numItems [Int]: number of items to initialize with
     */
    fun initSelectorsExpanded(numItems: Int) {
        if (selectorsExpanded.size != numItems) {
            _selectorsExpanded = BooleanArray(numItems)
        }
    }
}
