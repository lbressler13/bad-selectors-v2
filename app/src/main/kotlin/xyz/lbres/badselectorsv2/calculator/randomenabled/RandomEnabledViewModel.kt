package xyz.lbres.badselectorsv2.calculator.randomenabled

import xyz.lbres.badselectorsv2.calculator.BaseCalculatorViewModel

/**
 * ViewModel containing values that are specific to the random enabled calculator.
 */
class RandomEnabledViewModel : BaseCalculatorViewModel() {
    val enabler = RandomEnabler()

    override fun resetComputeData() {
        super.resetComputeData()
        enabler.update()
    }
}
