package xyz.lbres.badselectorsv2.calculator.singleoperation

import xyz.lbres.badselectorsv2.calculator.BaseCalculatorViewModel
import xyz.lbres.badselectorsv2.calculator.utils.CalcData

/**
 * ViewModel containing values that are specific to the single operation calculator.
 */
class SingleOperationViewModel : BaseCalculatorViewModel() {
    /**
     * Move the computed value into the compute text and clear result
     */
    fun moveComputedToComputeText() {
        val newComputeText = listOf(calcData.computedValue!!.toString())
        calcData = CalcData(newComputeText, null, null)
    }
}
