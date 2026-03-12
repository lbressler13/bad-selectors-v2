package xyz.lbres.badselectorsv2.calculator.randomenabled

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import xyz.lbres.badselectorsv2.calculator.BaseCalculatorFragment
import xyz.lbres.badselectorsv2.calculator.BaseCalculatorViewModel
import xyz.lbres.badselectorsv2.calculator.utils.runComputation
import xyz.lbres.badselectorsv2.databinding.FragmentRandomEnabledBinding
import xyz.lbres.kotlinutils.general.simpleIf

/**
 * Fragment with a calculator that randomly enables and disables operator and number buttons, when any button is pressed.
 */
class RandomEnabledFragment : BaseCalculatorFragment() {
    private lateinit var viewModel: RandomEnabledViewModel
    override val calculatorViewModel: BaseCalculatorViewModel
        get() = viewModel

    private lateinit var binding: FragmentRandomEnabledBinding
    override val rootView: View
        get() = binding.root

    /**
     * Initialize fragment
     */
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        viewModel = ViewModelProvider(requireActivity())[RandomEnabledViewModel::class.java]
        binding = FragmentRandomEnabledBinding.inflate(layoutInflater, container, false)

        initKeypad()
        initMainText()
        // updateEnabledButtons()

        return binding.root
    }

    /**
     * Update enabled buttons when input button is pressed
     *
     * @param text [String]: text of pressed button
     */
    override fun handleInputButton(text: String) {
        super.handleInputButton(text)
        updateEnabledButtons()
    }

    /**
     * Update enabled buttons when backspace is pressed
     */
    override fun handleBackspace() {
        super.handleBackspace()
        updateEnabledButtons()
    }

    /**
     * Update enabled buttons when UI is reset
     */
    override fun resetUi() {
        super.resetUi()
        updateEnabledButtons()
    }

    /**
     * Perform computation and updated enabled buttons when equals is pressed
     */
    override fun handleEquals() {
        try {
            val computedValue: Int = runComputation(viewModel.calcData.computeText)

            viewModel.setResult(computedValue, null)
            viewModel.useComputedAsComputeText()
        } catch (e: Exception) {
            val errorMessage = e.message ?: "Err: Computation error"
            viewModel.setResult(null, errorMessage)
        }

        updateEnabledButtons()
    }

    /**
     * Update enabled values in ViewModel, and set enabled buttons based on new values
     */
    private fun updateEnabledButtons() {
        viewModel.enabler.update()

        val numberButtons = listOf(
            binding.zeroButton,
            binding.oneButton,
            binding.twoButton,
            binding.threeButton,
            binding.fourButton,
            binding.fiveButton,
            binding.sixButton,
            binding.sevenButton,
            binding.eightButton,
            binding.nineButton,
        )

        numberButtons.forEachIndexed { index, button ->
            if (viewModel.enabler.isDigitEnabled(index)) {
                enableButton(button)
            } else {
                disableButton(button)
            }
        }

        val operatorPairs = listOf(
            Pair(binding.plusButton, "+"),
            Pair(binding.minusButton, "-"),
            Pair(binding.timesButton, "x"),
            Pair(binding.divideButton, "/"),
        )

        operatorPairs.forEach { (button, operator) ->
            val enabled = viewModel.enabler.isOperatorEnabled(operator)
            simpleIf(enabled, { enableButton(button) }, { disableButton(button) })
        }
    }
}
