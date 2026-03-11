package xyz.lbres.badselectorsv2.calculator.singleoperation

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import xyz.lbres.badselectorsv2.calculator.BaseCalculatorFragment
import xyz.lbres.badselectorsv2.calculator.BaseCalculatorViewModel
import xyz.lbres.badselectorsv2.calculator.common.runComputation
import xyz.lbres.badselectorsv2.calculator.common.standardOperatorFunction
import xyz.lbres.badselectorsv2.databinding.FragmentSingleOperationBinding
import xyz.lbres.badselectorsv2.ext.view.gone
import xyz.lbres.badselectorsv2.ext.view.visible
import xyz.lbres.kotlinutils.general.simpleIf
import kotlin.Exception

/**
 * Fragment with a calculator that performs computation automatically when 2 numbers and 1 operator have been typed.
 * Does not allow for repeated numbers or operators.
 * Avoids syntax errors and divide by zero errors by disabling buttons that would cause errors.
 */
class SingleOperationFragment : BaseCalculatorFragment() {
    private lateinit var viewModel: SingleOperationViewModel
    override val calculatorViewModel: BaseCalculatorViewModel
        get() = viewModel
    private lateinit var binding: FragmentSingleOperationBinding
    override lateinit var rootView: View

    /**
     * Initialize fragment
     */
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        binding = FragmentSingleOperationBinding.inflate(layoutInflater, container, false)
        viewModel = ViewModelProvider(requireActivity())[SingleOperationViewModel::class.java]
        rootView = binding.root

        // init UI
        initKeypad()
        initMainText()
        processComputeText()

        return binding.root
    }

    /**
     * Equals button does not exist
     */
    override fun handleEquals() {}

    /**
     * Update compute text and process change in text
     *
     * @param text [String]: text associated with the pressed button
     */
    override fun handleInputButton(text: String) {
        super.handleInputButton(text) // updates compute text
        processComputeText()
    }

    /**
     * Depending on the size of the compute text, update buttons UI or perform computation
     */
    private fun processComputeText() {
        val size = viewModel.calcData.computeText.size

        when (size) {
            0, 2 -> setButtonsClickability(operatorsClickable = false, numbersClickable = true)
            1 -> setButtonsClickability(operatorsClickable = true, numbersClickable = false)
            3 -> doComputation()
            else -> {
                viewModel.setResult(null, "Err: Invalid computation")
                updateMainText()
            }
        }
    }

    /**
     * Run computation on current compute text
     */
    private fun doComputation() {
        try {
            val computedValue: Int = runComputation(
                viewModel.calcData.computeText,
                listOf("x", "/"),
                listOf("+", "-"),
                standardOperatorFunction(),
            )

            viewModel.setResult(computedValue, null)
        } catch (e: Exception) {
            val errorMessage = e.message ?: "Error"
            viewModel.setResult(null, errorMessage)
        }

        updateMainText()

        if (viewModel.calcData.computedValue != null) {
            viewModel.moveComputedToComputeText()
            processComputeText()
        }
    }

    /**
     * Dim operator buttons and hide clear button
     */
    override fun resetUi() {
        super.resetUi()
        setButtonsClickability(operatorsClickable = false, numbersClickable = true)
        binding.clearButton.gone()
    }

    /**
     * Show button to clear error
     */
    override fun showErrorUi() {
        super.showErrorUi()
        binding.clearButton.visible()
        // set all to full opacity to appear consistent when disabled
        setButtonsClickability(operatorsClickable = false, numbersClickable = false)
    }

    /**
     * Set operator buttons and number buttons to be dimmed and unclickable
     *
     * @param operatorsClickable [Boolean]: if operator buttons should be clickable
     * @param numbersClickable [Boolean]: if number buttons should be clickable
     */
    private fun setButtonsClickability(operatorsClickable: Boolean, numbersClickable: Boolean) {
        setSingleButtonClickability(binding.plusButton, operatorsClickable)
        setSingleButtonClickability(binding.minusButton, operatorsClickable)
        setSingleButtonClickability(binding.timesButton, operatorsClickable)
        setSingleButtonClickability(binding.divideButton, operatorsClickable)

        setSingleButtonClickability(binding.oneButton, numbersClickable)
        setSingleButtonClickability(binding.twoButton, numbersClickable)
        setSingleButtonClickability(binding.threeButton, numbersClickable)
        setSingleButtonClickability(binding.fourButton, numbersClickable)
        setSingleButtonClickability(binding.fiveButton, numbersClickable)
        setSingleButtonClickability(binding.sixButton, numbersClickable)
        setSingleButtonClickability(binding.sevenButton, numbersClickable)
        setSingleButtonClickability(binding.eightButton, numbersClickable)
        setSingleButtonClickability(binding.nineButton, numbersClickable)
        setSingleButtonClickability(binding.zeroButton, numbersClickable)

        if (viewModel.calcData.computeText.lastOrNull() == "/") {
            setSingleButtonClickability(binding.zeroButton, false)
        }
    }

    /**
     * Update single button to be clickable/unclickable and full opacity/dimmed based on [clickable]
     *
     * @param button [View]: button to update
     * @param clickable [Boolean]: if the button should be clickable
     */
    private fun setSingleButtonClickability(button: View, clickable: Boolean) {
        val fullOpacity = 1f
        val dimmedOpacity = 0.6f
        val opacity = simpleIf(clickable, fullOpacity, dimmedOpacity)
        button.alpha = opacity
        button.isClickable = clickable
    }
}
