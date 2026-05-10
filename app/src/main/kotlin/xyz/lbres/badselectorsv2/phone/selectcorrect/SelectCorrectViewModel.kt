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
    val initialized: Boolean
        get() = generatedNumber.none { it == -1 }

    /**
     * Save the value of a digit, based on its current value in the current generated number
     *
     * @param index [Int]: index of value to save
     */
    fun setDigitAt(index: Int) {
        generator.freezeAtIndex(index)
        digits[index] = generatedNumber[index]
    }

    /**
     * Generate a new number
     */
    fun generateNumber() {
        _generatedNumber = generator.generateNumber()
    }

    /**
     * Reset all data
     */
    override fun resetData() {
        super.resetData()
        generator.reset()
        generateNumber()
    }
}
