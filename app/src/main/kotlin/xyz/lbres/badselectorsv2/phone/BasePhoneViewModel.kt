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
    protected val digits: Array<Int?> = Array(numDigits) { null }

    /**
     * Current index being assigned to
     */
    var currentIndex = 0
        protected set

    /**
     * Return the value of a specific digit
     *
     * @param index [Int]: index of digit
     * @return [Int]?: the current value of the digit, or `null` if there is no current value
     */
    fun getDigitAt(index: Int): Int? {
        println(digits.contentToString())
        return digits[index]
    }

    /**
     * Update value of the digit at [currentIndex]
     *
     * @param value [Int]: new value of digit
     */
    fun setCurrentDigit(value: Int) {
        digits[currentIndex] = value
        println("Set value $value at index $currentIndex")
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
        digits.setAllValues(null)
    }
}
