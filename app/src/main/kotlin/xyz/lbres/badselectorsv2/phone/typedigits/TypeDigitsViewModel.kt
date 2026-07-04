package xyz.lbres.badselectorsv2.phone.typedigits

import android.util.Log
import xyz.lbres.badselectorsv2.phone.BasePhoneViewModel
import xyz.lbres.badselectorsv2.phone.utils.digitsRange
import xyz.lbres.badselectorsv2.utils.seededShuffled

class TypeDigitsViewModel : BasePhoneViewModel() {
    private var digitsOrder = digitsRange.seededShuffled()

    fun selectValue(index: Int, value: Int): Int? {
        if (index !in digitsRange || value !in digitsRange) {
            Log.w(null, "Unable to select '$value' at '$index'")
            return null
        }

        val result = digitsOrder[value]
        _digits[index] = result
        return result
    }

    override fun resetData() {
        super.resetData()
        digitsOrder = digitsRange.seededShuffled()
    }
}
