package xyz.lbres.badselectorsv2.date.guessrange.rangesearcher

import xyz.lbres.badselectorsv2.utils.seededRandom
import xyz.lbres.kotlinutils.closedrange.intrange.ext.size

/**
 * Interface for searching for a specific value within a range, for use in the date range-based selector
 */
abstract class RangeSearcher(fullRange: IntRange) {
    /**
     * Generate the next range
     */
    abstract fun getRange(): IntRange

    /**
     * Mark the last returned range as incorrect
     */
    abstract fun markLastRangeCorrect()

    /**
     * Mark the last returned range as correct
     */
    abstract fun markLastRangeIncorrect()

    /**
     * Get the single remaining value in the range, or null if the range does not consist of a single value
     */
    abstract fun getSingleValue(): Int?

    /**
     * Reset searcher to initial range
     */
    abstract fun restart()

    /**
     * Identify range that contains more values. If ranges contain the same number of values, the selection is made randomly
     *
     * @param range1 [Range]
     * @param range2 [Range]
     * @return [Range]: range that contains larget number of values
     */
    protected fun maxOfRanges(range1: Range, range2: Range): Range {
        return when {
            range1.containedValuesRange.size() > range2.containedValuesRange.size() -> range1
            range2.containedValuesRange.size() > range1.containedValuesRange.size() -> range2
            else -> listOf(range1, range2).seededRandom()
        }
    }

    /**
     * Information about a range
     *
     * @param containedValuesRange [IntRange]: remaining values that are includes in range
     * @param indexRange [IntRange]: range of indices whose values are included in the given range
     * @param fullRange [IntRange]: range containing all values in [containedValuesRange]
     */
    protected data class Range(val containedValuesRange: IntRange, val indexRange: IntRange, val fullRange: IntRange) {
        constructor(range: IntRange, indexRange: IntRange) : this(range, indexRange, range)
    }
}
