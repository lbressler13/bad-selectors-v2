package xyz.lbres.badselectorsv2.calculator

import androidx.lifecycle.ViewModel
import xyz.lbres.badselectorsv2.calculator.common.CalcData
import xyz.lbres.kotlinutils.int.ext.isNegative
import xyz.lbres.kotlinutils.list.ext.copyWithFirstReplaced
import xyz.lbres.kotlinutils.list.ext.copyWithoutLast

/**
 * ViewModel containing values needed for all calculators
 */
abstract class BaseCalculatorViewModel : ViewModel() {
    /**
     * Data about current calculation
     */
    var calcData: CalcData = CalcData()
        protected set

    /**
     * Add new number or operator to end of list
     *
     * @param addition [String]: value to add
     */
    open fun appendComputeText(addition: String) {
        val newText = calcData.computeText + addition
        calcData = CalcData(newText, calcData.computedValue, calcData.error)
    }

    /**
     * Delete last value from list
     */
    open fun backspaceComputeText() {
        if (calcData.computeText.isNotEmpty()) {
            val newText = calcData.computeText.copyWithoutLast()
            calcData = CalcData(newText, calcData.computedValue, calcData.error)
        }
    }

    /**
     * Update most recent computed value or error
     */
    fun setResult(computedValue: Int?, error: String?) {
        calcData = CalcData(calcData.computeText, computedValue, error)
    }

    /**
     * Reset data in this view model
     */
    open fun resetComputeData() {
        calcData = CalcData()
    }

    /**
     * Replace compute text list with the computed value
     */
    fun useComputedAsComputeText() {
        val computed: Int = calcData.computedValue!!
        var currentState = computed.toString().map { it.toString() }

        // if negative, add minus sign to first number to prevent errors building compute text
        if (computed.isNegative()) {
            currentState = currentState.subList(1, currentState.size)
            currentState = currentState.copyWithFirstReplaced("-${currentState[0]}")
        }

        calcData = CalcData(currentState, null, null)
    }
}