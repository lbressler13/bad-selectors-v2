package xyz.lbres.badselectorsv2.phone.selectcorrect

import xyz.lbres.badselectorsv2.phone.BasePhoneViewModel
import xyz.lbres.badselectorsv2.phone.utils.PhoneNumberGenerator

/**
 * ViewModel containing values that are specific to the phone choose correct selector
 */
class SelectCorrectViewModel : BasePhoneViewModel() {
    private val generator: PhoneNumberGenerator = PhoneNumberGenerator(allowRepeatDigits = false)

    private var _generatedNumber: List<Int> = generator.generateNumber()
    val generatedNumber: List<Int>
        get() = _generatedNumber


    /**
     * If all digits of the phone number have been set
     */
    val completedNumber: Boolean
        get() = digits.none { it == null }

    /**
     * Save the value of a digit, based on its current value in the current generated number
     *
     * @param index [Int]: index of value to save
     */
    fun setDigitAt(index: Int) {
        generator.freezeAtIndex(index)
        _digits[index] = generatedNumber[index]
    }

    /**
     * Generate a new number
     */
    fun updateNumber(): List<Int> {
        _generatedNumber = generator.generateNumber()
        return generatedNumber
    }

    /**
     * Reset all data
     */
    override fun resetData() {
        super.resetData()
        generator.reset()
        updateNumber()
    }
}
