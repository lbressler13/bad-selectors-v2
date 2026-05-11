package xyz.lbres.badselectorsv2.phone

import androidx.lifecycle.ViewModel
import xyz.lbres.badselectorsv2.phone.utils.numDigits
import xyz.lbres.kotlinutils.array.ext.setAllValues

/**
 * ViewModel containing values needed for all phone selectors
 */
abstract class BasePhoneViewModel : ViewModel() {
    /**
     * Values stored at digits
     */
    protected val _digits: Array<Int?> = Array(numDigits) { null }
    val digits: List<Int?>
        get() = _digits.toList()

    /**
     * Current index being assigned to
     */
    var currentIndex = 0
        protected set

    /**
     * Update value of the digit at [currentIndex]
     *
     * @param value [Int]: new value of digit
     */
    fun setCurrentDigit(value: Int) {
        _digits[currentIndex] = value
    }

    /**
     * Increment value of [currentIndex]
     */
    open fun incrementCurrentIndex() {
        currentIndex++
    }

    /**
     * Clear all data related to digits
     */
    open fun resetData() {
        currentIndex = 0
        _digits.setAllValues(null)
    }
}
