package xyz.lbres.badselectorsv2.phone.shufflecircle

import xyz.lbres.badselectorsv2.phone.BasePhoneViewModel
import xyz.lbres.badselectorsv2.phone.utils.PhoneNumberGenerator
import xyz.lbres.badselectorsv2.phone.utils.digitsRange
import xyz.lbres.badselectorsv2.utils.createRandom
import xyz.lbres.kotlinutils.general.simpleIf
import xyz.lbres.kotlinutils.list.IntList
import xyz.lbres.kotlinutils.random.ext.nextBoolean

class ShuffleCircleViewModel : BasePhoneViewModel() {
    val russianRoulette = false

    // private val generator = PhoneNumberGenerator(fullNumberRepeats = 1..3)
    private val generator = PhoneNumberGenerator(1..3)

    /**
     * Current order
     */
    private var digitsOrder: IntList = digitsRange.toList()

    /**
     * Last value returned by [getDigitAtIndex]
     */
    var currentDigit: Int? = -1
        private set

    /**
     * Initialize shuffled numbers
     */
    init {
        updateDigits()
    }

    /**
     * Get the value of a specific index.
     * Guaranteed to never return null twice in a row.
     *
     * @param index [Int]: index to retrieve number for
     * @param nullable [Boolean]: if a null value can be returned, equivalent to the russian roulette setting.
     * Defaults to `false`.
     * @return [Int]?: number at [index], with some probability of null if [nullable] is true
     */
    // TODO rename this, too similar to getDigit
    fun getDigitAtIndex(index: Int, nullable: Boolean = false): Int? {
        val canUseNull = nullable && currentDigit != null && currentDigit != -1

        val probabilityNull = 0.001f // 1 / 1000
        val nextNull = createRandom().nextBoolean(probabilityNull)
        currentDigit = simpleIf(canUseNull && nextNull, null, digitsOrder[index])

        return currentDigit
    }

    /**
     * Update count to next shuffle, and shuffle digits if necessary
     */
    fun updateDigits() {
        digitsOrder = generator.generateNumber()
    }

    /**
     * Increment value of [currentIndex] and force-update digits
     */
    override fun incrementCurrentIndex() {
        super.incrementCurrentIndex()
        digitsOrder = generator.generateNumber(forceRegenerate = true)
    }

    /**
     * Reset all data
     */
    override fun resetData() {
        super.resetData()
        currentDigit = -1
        generator.reset()
        updateDigits()
    }
}
