package xyz.lbres.badselectorsv2.date

import androidx.lifecycle.ViewModel

/**
 * ViewModel containing values that are needed for all date selectors
 */
abstract class BaseDateViewModel : ViewModel() {
    /**
     * Components that make up a date.
     * Child classes can override values to use public setters.
     */
    open var day: Int? = null
        protected set
    open var month: Int? = null
        protected set
    open var year: Int? = null
        protected set

    /**
     * Reset data in this view model
     */
    open fun resetData() {
        day = null
        month = null
        year = null
    }
}
