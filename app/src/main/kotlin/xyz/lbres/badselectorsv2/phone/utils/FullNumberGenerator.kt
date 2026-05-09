package xyz.lbres.badselectorsv2.phone.utils

import android.util.Log
import xyz.lbres.badselectorsv2.utils.seededRandom
import xyz.lbres.kotlinutils.intarray.ext.setAllValues
import xyz.lbres.kotlinutils.intarray.intArrayOfValue
import xyz.lbres.kotlinutils.list.IntList
import xyz.lbres.kotlinutils.set.mutableset.ext.popRandom

/**
 * Generator for creating a randomized phone number
 *
 * @param allowRepeatDigits [Boolean]: If values can be repeated at a position. If true, each generated number will have a new value at every position.
 * When all 10 values have been used, the list of remaining values will reset.
 * Defaults to false.
 * @param fullNumberRepeats [IntRange]: The number of times that a generated number can occur before a new number is generated.
 * The repeat count will be chosen randomly from this range each time that a number is generated.
 * Defaults to 1..1, which indicates that a new number will be generated each time.
 */
class FullNumberGenerator(
    private val allowRepeatDigits: Boolean = true,
    fullNumberRepeats: IntRange = 1..1,
) {
    /**
     * Remaining values for each digit
     */
    private val remainingValues: Array<MutableSet<Int>> = Array(numDigits) { digitsRange.toMutableSet() }

    /**
     * Number of times that a generated number can occur before a new number is generated
     */
    private val fullNumberRepeats: IntRange

    /**
     * Number of generations remaining before new number is generated
     */
    private var repeatsRemaining = 0

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
            /* ktlint-disable max-line-length */
            val warningMessage = "Invalid full number repeats range provided: $fullNumberRepeats. Using alternate range ${this.fullNumberRepeats} instead."
            /* ktlint-enable max-line-length */
            Log.w(null, warningMessage)
        }
    }

    /**
     * Update the value of the generated number.
     * Pulls from [remainingValues] if [allowRepeatDigits] is false
     *
     * @return [IntList]: new generated number
     */
    fun generateNumber(): IntList {
        if (repeatsRemaining <= 0) {
            // reset digits that have no remaining options
            for (digit in digitsRange) {
                if (!allowRepeatDigits && remainingValues[digit].isEmpty()) {
                    resetRemainingAt(digit)
                }
            }

            // generate number
            remainingValues.forEachIndexed { index, remaining ->
                val result = if (allowRepeatDigits) {
                    remaining.random()
                } else {
                    remaining.popRandom()!!
                }
                generatedNumber[index] = result
            }

            repeatsRemaining = fullNumberRepeats.seededRandom()
        }

        repeatsRemaining--
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
        repeatsRemaining = 0
    }
}
