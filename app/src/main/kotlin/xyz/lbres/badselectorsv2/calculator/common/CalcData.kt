package xyz.lbres.badselectorsv2.calculator.common

import xyz.lbres.kotlinutils.list.StringList

// TODO might delete this?

/**
 * All data about the current state of a calculation.
 *
 * @param computeText [StringList]: values to be computed with
 * @param computedValue [Int]?: result of running computation
 * @param error [String]?: error thrown when running computation
 */
data class CalcData(val computeText: StringList, val computedValue: Int?, val error: String?) {
    constructor() : this(emptyList(), null, null)
}