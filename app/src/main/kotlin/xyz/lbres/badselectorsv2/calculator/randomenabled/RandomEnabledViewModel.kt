package xyz.lbres.badselectorsv2.calculator.randomenabled

import xyz.lbres.badselectorsv2.calculator.BaseCalculatorViewModel

/**
 * ViewModel containing values that are specific to the random enabled calculator.
 */
class RandomEnabledViewModel : BaseCalculatorViewModel() {
    private val enabler = CalculatorEnabler()

    fun updateEnabled() = enabler.update()
    fun isEnabled(digit: Int) = enabler.isEnabled(digit)
    fun isEnabled(operator: String) = enabler.isEnabled(operator)

    override fun resetComputeData() {
        super.resetComputeData()
        enabler.update()
    }
}
