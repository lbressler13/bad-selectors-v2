package xyz.lbres.badselectorsv2.date.guessrange.rangesearcher

import xyz.lbres.badselectorsv2.utils.seededRandom
import xyz.lbres.kotlinutils.closedrange.intrange.ext.size
import xyz.lbres.kotlinutils.closedrange.intrange.rangeOfInt
import xyz.lbres.kotlinutils.list.IntList
import xyz.lbres.kotlinutils.pair.TypePair

/**
 * Implementation of binary search that uses randomized and larger-than-necessary ranges.
 * For example, if remaining values are [2, 3, 4, 10, 11, 12], the next range could be 1..9, which is binary but large.
 *
 * @param fullRange [IntRange]: initial list of values in the searcher
 */
internal class BinaryRangeSearcher(private val fullRange: IntRange): RangeSearcher(fullRange) {
    /**
     * Values that may still be correct
     */
    private var remainingValues: IntList = fullRange.toList()

    /**
     * Last range returned by [getRange]
     */
    private var lastRange: Range? = null

    /**
     * Mark last range correct, by keeping only values that are in the last range
     */
    override fun markLastRangeCorrect() {
        if (lastRange != null) {
            remainingValues = remainingValues.slice(lastRange!!.indexRange)
            lastRange = null
        }
    }

    /**
     * Mark last range incorrect, by keeping only values that are not in the last range
     */
    override fun markLastRangeIncorrect() {
        if (lastRange != null) {
            // values before range
            val startList: IntList = remainingValues.slice(0 until lastRange!!.indexRange.first)

            // values after range
            val endIndexRange = lastRange!!.indexRange.last + 1..remainingValues.lastIndex
            val endList: IntList = remainingValues.slice(endIndexRange)

            remainingValues = startList + endList
            lastRange = null
        }
    }

    /**
     * Get the single remaining value in the range, or null if the range does not consist of a single value
     */
    override fun getSingleValue(): Int? {
        return if (remainingValues.size == 1) {
            remainingValues[0]
        } else {
            null
        }
    }

    /**
     * Generate the next range
     *
     * @return [IntRange]: range that contains at least one remaining value and excludes at least one remaining value
     */
    override fun getRange(): IntRange {
        if (lastRange != null) {
            return lastRange!!.containedValuesRange
        }

        // all values have been marked incorrect, need to restart before creating new range
        if (remainingValues.isEmpty()) {
            restart()
        }

        lastRange = when (remainingValues.size) {
            1 -> {
                val range = rangeOfInt(remainingValues[0])
                Range(range, 0..0)
            }
            2 -> getRangeOf2()
            else -> createRange()
        }

        return lastRange!!.containedValuesRange
    }

    /**
     * Construct range when there are more than 2 remaining values
     *
     * @return [Range]: range that contains half of remaining values
     */
    private fun createRange(): Range {
        val size = remainingValues.size

        // range that is fully within remaining values, with randomized start index
        val internalStartIndex = (0 until size / 2).seededRandom()
        val internalEndIndex = internalStartIndex + size / 2 - 1
        val internalRange: IntRange = remainingValues[internalStartIndex]..remainingValues[internalEndIndex]

        // use internal range for initial range, prevents always returning larger half for odd range size
        if (remainingValues.size == fullRange.size()) {
            return Range(internalRange, internalStartIndex..internalEndIndex)
        }

        val startEnd = getStartEnd()
        val start = startEnd.first
        val end = startEnd.second

        val middleStart: Int = remainingValues[size / 2 - 1]
        val middleEnd: Int = remainingValues[size / 2]

        // range starts before remaining values but includes half of remaining
        val startRange: IntRange = start..middleStart
        // range starts after remaining values but includes half of remaining
        val endRange: IntRange = middleEnd..end

        val maxRange: IntRange? = listOf(internalRange, startRange, endRange).maxByOrNull { it.size() }

        return when (maxRange) {
            internalRange -> Range(internalRange, internalStartIndex..internalEndIndex)
            startRange -> Range(startRange, 0 until (size / 2))
            endRange -> Range(endRange, size / 2..remainingValues.lastIndex)
            else -> Range(internalRange, internalStartIndex..internalEndIndex)
        }
    }

    /**
     * Get start and end values for a new range
     *
     * @return [TypePair]<Int>: pair where first value is start and second value is end
     */
    private fun getStartEnd(): TypePair<Int> {
        val first = remainingValues.first()
        val last = remainingValues.last()

        val start: Int = if (first == fullRange.first) {
            first
        } else {
            (fullRange.first until first).seededRandom() // random val before range start
        }

        val end: Int = if (last == fullRange.last) {
            last
        } else {
            (last..fullRange.last).seededRandom() // random val after range end
        }

        return Pair(start, end)
    }

    /**
     * Get valid range when [remainingValues] has size 2
     *
     * @return [IntRange]
     */
    private fun getRangeOf2(): Range {
        val first: Int = remainingValues[0]
        val second: Int = remainingValues[1]

        if (first + 1 == second) {
            return getRangeOf2Adjacent()
        }

        // construct range surrounding each value, then pick the larger range
        val start: Int = if (remainingValues.first() == fullRange.first) {
            remainingValues.first()
        } else {
            (fullRange.first until remainingValues.first()).seededRandom() // random val before range start
        }

        val end: Int = if (remainingValues.last() == fullRange.last) {
            remainingValues.last()
        } else {
            (remainingValues.last()..fullRange.last).seededRandom() // random val after range end
        }

        val middle: Int = (first + 1 until second).seededRandom()
        val startRange = Range(start..middle, 0..0)
        val endRange = Range(middle..end, 1..1)

        return maxOfRanges(startRange, endRange)
    }

    /**
     * Get range when [remainingValues] has size 2, and values are adjacent
     *
     * @return [Range]: range containing exactly one of the two remaining values
     */
    private fun getRangeOf2Adjacent(): Range {
        val first: Int = remainingValues[0]
        val second: Int = remainingValues[1]
        val isFirst: Boolean = first == fullRange.first
        val isLast: Boolean = second == fullRange.last

        // values border either start or end of full range
        when {
            isFirst && isLast -> {
                val firstRange = Range(first..first, 0..0)
                val secondRange = Range(second..second, 1..1)
                return listOf(firstRange, secondRange).seededRandom()
            }
            isFirst -> {
                val end = (second + 1..fullRange.last).seededRandom()
                return Range(second..end, 1..1)
            }
            isLast -> {
                val start = (fullRange.first until first).seededRandom()
                return Range(start..first, 0..0)
            }
        }

        // construct ranges before start and after end, then pick the larger range
        val start = (fullRange.first until first).seededRandom()
        val end = (second + 1..fullRange.last).seededRandom()
        // TODO fix this so end can be between first and second
        val startRange = Range(start..first, 0..0)
        val endRange = Range(second..end, 1..1)

        return maxOfRanges(startRange, endRange)
    }

    /**
     * Reset searcher to initial range
     */
    override fun restart() {
        lastRange = null
        remainingValues = remainingValues.toList()
    }
}
