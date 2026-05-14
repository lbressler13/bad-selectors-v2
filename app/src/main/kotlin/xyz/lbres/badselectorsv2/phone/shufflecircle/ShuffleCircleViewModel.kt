package xyz.lbres.badselectorsv2.phone.shufflecircle

import xyz.lbres.badselectorsv2.phone.BasePhoneViewModel
import xyz.lbres.badselectorsv2.phone.utils.PhoneNumberGenerator
import xyz.lbres.badselectorsv2.phone.utils.digitsRange
import xyz.lbres.badselectorsv2.utils.createRandom
import xyz.lbres.kotlinutils.general.simpleIf
import xyz.lbres.kotlinutils.list.IntList
import xyz.lbres.kotlinutils.random.ext.nextBoolean

class ShuffleCircleViewModel : BasePhoneViewModel() {
    var russianRoulette = false

    private val generator = PhoneNumberGenerator(1..3)

    /**
     * Last generated number. Must be private to account for russian roulette
     */
    private var generatedNumber: IntList = digitsRange.toList()

    /**
     * Last value returned by [getGeneratedAtIndex]
     */
    var generatedDigit: Int? = -1
        private set

    /**
     * Initialize shuffled numbers
     */
    init {
        updateDigits()
    }

    /**
     * Get the value of the generated number at a specific index.
     * Guaranteed to never return null twice in a row.
     *
     * @param index [Int]: index to retrieve number for
     * @return [Int]?: number at [index], with some probability of null if [russianRoulette] is true
     */
    fun getGeneratedAtIndex(index: Int): Int? {
        val canUseNull = russianRoulette && currentIndex != 0 && generatedDigit != null && generatedDigit != -1

        val probabilityNull = 0.01f // 1 / 100
        val nextNull = createRandom().nextBoolean(probabilityNull)
        generatedDigit = simpleIf(canUseNull && nextNull, null, generatedNumber[index])

        return generatedDigit
    }

    /**
     * Update count to next shuffle, and shuffle digits if necessary
     */
    fun updateDigits() {
        generatedNumber = generator.generateNumber()
    }

    /**
     * Increment value of [currentIndex] and force-update digits
     */
    override fun incrementCurrentIndex() {
        super.incrementCurrentIndex()
        generatedNumber = generator.generateNumber(forceRegenerate = true)
        generatedDigit = null
    }

    /**
     * Reset all data
     */
    override fun resetData() {
        super.resetData()
        generatedDigit = -1
        generator.reset()
        updateDigits()
    }
}
