package xyz.lbres.badselectorsv2.phone.shufflecircle

import android.util.Log
import xyz.lbres.badselectorsv2.phone.common.digitsRange
import xyz.lbres.badselectorsv2.utils.random
import xyz.lbres.badselectorsv2.utils.seededRandom
import xyz.lbres.badselectorsv2.utils.seededShuffled
import xyz.lbres.kotlinutils.list.IntList
import xyz.lbres.kotlinutils.random.ext.nextBoolean

/**
 * Tracks and shuffles order of digits, for use in the phone shuffle circle selector
 */
class DigitShuffler {
    /**
     * Current order
     */
    private var digits: IntList = digitsRange.toList()

    /**
     * Number of updates until next shuffle
     */
    private var nextShuffle: Int = 0

    /**
     * Last value returned by [getAtIndex]
     */
    var digit: Int? = -1
        private set

    /**
     * Initialize shuffled numbers
     */
    init {
        this.update()
        Log.e(null, "INIT DIGIT SHUFFLER")
    }

    /**
     * Get the value of a specific index.
     * Guaranteed to never return null twice in a row.
     *
     * @param index [Int]: index to retrieve number for
     * @param nullable [Boolean]: if a null value can be returned, equivalent to the russian roulette setting.
     * Defaults to `null`.
     * @return [Int]?: number at [index], with some probability of null if [nullable] is true
     */
    fun getAtIndex(index: Int, nullable: Boolean = false): Int? {
        val canUseNull = nullable && digit != null && digit != -1

        val probabilityNull = 0.001f // 1 / 1000
        digit = if (canUseNull && random.nextBoolean(probabilityNull)) {
            null
        } else {
            digits[index]
        }
        Log.e(null, "SET DIGIT: $digit")

        return digit
    }

    /**
     * Update count to next shuffle, and shuffle digits if necessary
     */
    fun update() {
        if (nextShuffle == 0) {
            digits = digits.seededShuffled()

            // next shuffle is between 0 and 2 updates
            nextShuffle = (0..2).seededRandom()
        } else {
            nextShuffle--
        }
    }

    /**
     * Reset all data
     */
    fun reset() {
        digits = digitsRange.toMutableList()
        digit = -1
        nextShuffle = 0
        update()
    }
}
