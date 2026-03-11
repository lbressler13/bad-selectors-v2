package xyz.lbres.badselectorsv2.calculator.common

import xyz.lbres.kotlinutils.list.StringList

/**
 * All data about the current state of a calculation.
 *
 * @param computeText [StringList]: values to be computed with
 * @param computedValue [Int]?: result of running computation
 * @param error [String]?: error thrown when running computation
 */
data class CalcData(val computeText: StringList, val computedValue: Int?, val error: String?) {
    constructor() : this(emptyList(), null, null)

    /**
     * Create a duplicate of this CalcData with different text
     *
     * @param newText [StringList]: text to use for new CalcData
     */
    fun withText(newText: StringList): CalcData {
        return CalcData(newText, computedValue, error)
    }

    /**
     * If all value are empty or null
     */
    fun isEmpty(): Boolean = computeText.isEmpty() && computedValue == null && error == null
}
