package xyz.lbres.badselectorsv2.date.nestedcircles

import android.util.Log
import xyz.lbres.badselectorsv2.date.BaseDateViewModel

/**
 * ViewModel containing values that are specific to the date circle selector
 */
class NestedCirclesViewModel : BaseDateViewModel() {
    val enabler = DateEnabler()

    override var day: Int?
        get() = enabler.day
        set(value) { enabler.day = value }

    override var month: Int?
        get() = enabler.month
        set(value) { enabler.month = value }

    override var year: Int?
        get() = enabler.year
        set(_) {
            Log.e(null, "Cannot set year directly. Must use setYearFromIndex")
        }
}
