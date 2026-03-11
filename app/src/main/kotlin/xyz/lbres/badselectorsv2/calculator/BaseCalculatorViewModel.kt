package xyz.lbres.badselectorsv2.calculator

import androidx.lifecycle.ViewModel
import xyz.lbres.badselectorsv2.calculator.utils.CalcData
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
        calcData = calcData.withText(newText)
    }

    /**
     * Delete last value from list
     */
    open fun backspaceComputeText() {
        if (calcData.computeText.isNotEmpty()) {
            val newText = calcData.computeText.copyWithoutLast()
            calcData = calcData.withText(newText)
        }
    }

    /**
     * Update most recent computed value or error
     */
    fun setResult(computedValue: Int?, error: String?) {
        calcData = CalcData(calcData.computeText, computedValue, error)
    }

    /**
<<<<<<< HEAD
     * Replace compute text list with the computed value
     */
    fun useComputedAsComputeText() {
        val computed = calcData.computedValue!!.toString()
        calcData = CalcData(listOf(computed), null, null)
    }

    /**
=======
>>>>>>> main
     * Reset data in this view model
     */
    open fun resetComputeData() {
        calcData = CalcData()
    }
}
