package xyz.lbres.badselectorsv2.phone.typedigits

import android.util.Log
import xyz.lbres.badselectorsv2.phone.BasePhoneViewModel
import xyz.lbres.badselectorsv2.phone.utils.digitsRange
import xyz.lbres.badselectorsv2.utils.seededShuffled

class TypeDigitsViewModel : BasePhoneViewModel() {
    private var digitsOrder = digitsRange.seededShuffled()
    // override for public set
    public override var currentIndex = -1

    /**
     * Update value of the digit at [currentIndex]
     *
     * @param value [Int]: index into digits order to set new value of digit
     */
    override fun setCurrentDigit(value: Int) {
        // invalid index
        if (currentIndex !in digitsRange || value !in digitsRange) {
            Log.w(null, "Unable to select '$value' at '$currentIndex'")
        }
        // skip update if value hasn't changed
        else if (_digits[currentIndex] != value) {
            val result = digitsOrder[value]
            _digits[currentIndex] = result
        }
    }

    override fun resetData() {
        super.resetData()
        digitsOrder = digitsRange.seededShuffled()
    }
}
