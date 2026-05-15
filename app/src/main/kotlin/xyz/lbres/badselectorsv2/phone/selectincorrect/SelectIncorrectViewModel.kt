package xyz.lbres.badselectorsv2.phone.selectincorrect

import xyz.lbres.badselectorsv2.phone.BasePhoneViewModel
import xyz.lbres.badselectorsv2.phone.utils.PhoneNumberGenerator
import xyz.lbres.badselectorsv2.phone.utils.digitsRange
import xyz.lbres.badselectorsv2.utils.seededShuffled

class SelectIncorrectViewModel : BasePhoneViewModel() {
    private val generator: PhoneNumberGenerator = PhoneNumberGenerator()

    private var digitsOrder: List<Int> = digitsRange.seededShuffled()
    private var _generatedNumber: List<Int> = generator.generateNumber()
    val generatedNumber: List<Int>
        get() = _generatedNumber

    val completedNumber
        get() = digits.none { it == null }

    fun setDigitAt(index: Int) {
        val digit = generatedNumber[index]
        _digits[index] = digitsOrder[digit]
    }

    /**
     * Generate a new number
     */
    fun updateNumber(): List<Int> {
        _generatedNumber = generator.generateNumber()
        digitsOrder = digitsRange.seededShuffled()
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
