package xyz.lbres.badselectorsv2.phone.utils

import android.util.Log
import xyz.lbres.badselectorsv2.utils.seededRandom
import xyz.lbres.kotlinutils.intarray.ext.setAllValues
import xyz.lbres.kotlinutils.intarray.intArrayOfValue
import xyz.lbres.kotlinutils.list.IntList
import xyz.lbres.kotlinutils.set.mutableset.ext.popRandom

/**
 * Generate a randomized phone number
 */
class FullNumberGenerator(
    private val allowRepeatDigits: Boolean = true,
    fullNumberRepeats: IntRange = 1..1,
) {

    private val fullNumberRepeats: IntRange

    /**
     * Remaining values for each digit
     */
    private val remainingValues: Array<MutableSet<Int>> = Array(numDigits) { digitsRange.toMutableSet() }

    private var nextShuffle = 0

    // TODO
    // private val frozenDigits: Array<Int?> = arrayOfNulls(digitsRange.size())

    /**
     * The next suggested number
     */
    private var generatedNumber: IntArray = intArrayOfValue(numDigits, -1)

    init {
        this.fullNumberRepeats = when {
            fullNumberRepeats.first > fullNumberRepeats.last -> 1..1
            fullNumberRepeats.last < 1 -> 1..1
            fullNumberRepeats.first <= 1 -> 1..fullNumberRepeats.last
            else -> fullNumberRepeats
        }

        if (this.fullNumberRepeats != fullNumberRepeats) {
            Log.w(null, "Invalid full number repeats range provided: $fullNumberRepeats. Using alternate range ${this.fullNumberRepeats} instead.")
        }
    }

    /**
     * Update the value of the generated number.
     * Pulls from [remainingValues] if [allowRepeatDigits] is false
     *
     * @return [IntList]: new generated number
     */
    fun generateNumber(): IntList {
        if (nextShuffle <= 0) {
            // reset digits that have no remaining options
            for (digit in digitsRange) {
                if (!allowRepeatDigits && remainingValues[digit].isEmpty()) {
                    resetRemainingAt(digit)
                }
            }

            // generate number
            remainingValues.forEachIndexed { index, remaining ->
                val result = if (allowRepeatDigits) {
                    remaining.random() // TODO seeded
                } else {
                    remaining.popRandom()!!
                }
                generatedNumber[index] = result
            }

            nextShuffle = fullNumberRepeats.seededRandom() - 1
        } else {
            nextShuffle--
        }

        return generatedNumber.toList()
    }

    /**
     * Reset the list of remaining values for a single digit
     *
     * @param index [Int]: index of digit to reset
     */
    private fun resetRemainingAt(index: Int) {
        remainingValues[index].addAll(digitsRange)
    }

    /**
     * Reset all data
     */
    fun reset() {
        remainingValues.indices.forEach { resetRemainingAt(it) }
        generatedNumber.setAllValues(-1)
        nextShuffle = 0
    }
}