package xyz.lbres.badselectorsv2.phone.utils

import android.util.Log
import xyz.lbres.badselectorsv2.utils.seededRandom
import xyz.lbres.kotlinutils.array.ext.setAllValues
import xyz.lbres.kotlinutils.closedrange.intrange.ext.size
import xyz.lbres.kotlinutils.intarray.ext.mapInPlaceIndexed
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
class PhoneNumberGenerator(
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

    /**
     * Fixed digits that will not be changed when new number is generated
     */
    private val frozenDigits: Array<Int?> = arrayOfNulls(digitsRange.size())

    /**
     * The next generated number
     */
    private var generatedNumber: IntArray = intArrayOfValue(numDigits, -1)

    init {
        // start of repeats range must be >= 1, and first must be before last
        this.fullNumberRepeats = when {
            fullNumberRepeats.first > fullNumberRepeats.last -> 1..1
            fullNumberRepeats.last < 1 -> 1..1
            fullNumberRepeats.first < 1 -> 1..fullNumberRepeats.last
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
     * Pulls from the remaining values list if [allowRepeatDigits] is false
     *
     * @return [IntList]: new generated number
     */
    fun generateNumber(): IntList {
        if (repeatsRemaining <= 0) {
            generatedNumber.mapInPlaceIndexed { index, _ ->
                val remaining = remainingValues[index]
                when {
                    frozenDigits[index] != null -> frozenDigits[index]!!
                    allowRepeatDigits -> remaining.random()
                    else -> {
                        if (remaining.isEmpty()) {
                            resetRemainingAt(index)
                        }
                        remaining.popRandom()!!
                    }
                }
            }

            repeatsRemaining = fullNumberRepeats.seededRandom()
        }

        repeatsRemaining--
        return generatedNumber.toList()
    }

    /**
     * Freeze value at a given position.
     * Future generated numbers will have the same values at this position until [reset] is called.
     *
     * @param index [Int]: position to freeze value
     */
    fun freezeAtIndex(index: Int) {
        if (generatedNumber[index] != -1) {
            frozenDigits[index] = generatedNumber[index]
        }
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
        frozenDigits.setAllValues(null)
        repeatsRemaining = 0
    }
}
