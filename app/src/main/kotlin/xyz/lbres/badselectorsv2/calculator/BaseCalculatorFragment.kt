package xyz.lbres.badselectorsv2.calculator

import android.view.View
import android.widget.TextView
import androidx.fragment.app.Fragment
import xyz.lbres.badselectorsv2.R
import xyz.lbres.badselectorsv2.calculator.common.CalcData
import xyz.lbres.badselectorsv2.calculator.common.UnprotectedScrollingMovementMethod
import xyz.lbres.badselectorsv2.ext.view.disable
import xyz.lbres.badselectorsv2.ext.view.enable
import xyz.lbres.badselectorsv2.utils.getColorOnPrimary
import xyz.lbres.badselectorsv2.utils.getDisabledForeground
import xyz.lbres.badselectorsv2.utils.setImageButtonTint

/**
 * Fragment containing functionality that is used by all calculators
 */
abstract class BaseCalculatorFragment : Fragment() {
    protected abstract val calculatorViewModel: BaseCalculatorViewModel
    protected abstract var rootView: View

    /**
     * Text size for mainText, depending on the current state
     */
    private val computeTextSize: Float = 40f
    private val errorTextSize: Float = 35f

    /**
     * Value used to separate items displayed in main text. Defaults to empty string.
     */
    protected open var computeSeparator: String = ""

    /**
     * IDs of all calculator buttons. Some may not be present in a given fragment.
     */
    private val allButtonIds: List<Int> = listOf(
        R.id.oneButton,
        R.id.twoButton,
        R.id.threeButton,
        R.id.fourButton,
        R.id.fiveButton,
        R.id.sixButton,
        R.id.sevenButton,
        R.id.eightButton,
        R.id.nineButton,
        R.id.zeroButton,
        R.id.plusButton,
        R.id.minusButton,
        R.id.timesButton,
        R.id.divideButton,
        // R.id.backspaceButton,
        // R.id.equalsButton,
        R.id.clearButton,
    )

    /**
     * Initialize main TextView
     */
    protected fun initMainText() {
        getMainText().movementMethod = UnprotectedScrollingMovementMethod()
        updateMainText()
    }

    /**
     * Initialize onClick for all numbers and operators, as well as backspace, clear, and equals
     */
    protected fun initKeypad() {
        // numbers and operators
        listOf(
            Pair(R.id.oneButton, "1"),
            Pair(R.id.twoButton, "2"),
            Pair(R.id.threeButton, "3"),
            Pair(R.id.fourButton, "4"),
            Pair(R.id.fiveButton, "5"),
            Pair(R.id.sixButton, "6"),
            Pair(R.id.sevenButton, "7"),
            Pair(R.id.eightButton, "8"),
            Pair(R.id.nineButton, "9"),
            Pair(R.id.zeroButton, "0"),
            Pair(R.id.plusButton, "+"),
            Pair(R.id.minusButton, "-"),
            Pair(R.id.timesButton, "x"),
            Pair(R.id.divideButton, "/"),
        ).forEach {
            val button = rootView.findViewById<View>(it.first)

            val text = it.second
            button?.setOnClickListener {
                handleInputButton(text)
                updateMainText()
            }
        }

        // clear button
        rootView.findViewById<View>(R.id.clearButton)?.setOnClickListener {
            calculatorViewModel.resetComputeData()
            updateMainText()
        }

        // backspace button
        // rootView.findViewById<View>(R.id.backspaceButton)?.setOnClickListener {
        // handleBackspace()
        // updateMainText()
        // }

        // equals button
        // rootView.findViewById<View>(R.id.equalsButton)?.setOnClickListener {
        // if (calculatorViewModel.calcData.computeText.isNotEmpty()) {
        // handleEquals()
        // updateMainText()
        // }
        // }
    }

    /**
     * Update main text to display compute text, computed value, or error
     */
    protected fun updateMainText() {
        val calcData = calculatorViewModel.calcData
        val mainText = getMainText()

        when {
            calcData == CalcData() -> {
                mainText.text = calcData.computeText.joinToString(computeSeparator)
                resetUi()
            }

            // scroll to top after hitting entre
            calcData.computedValue != null -> {
                mainText.text = calcData.computedValue.toString()
                getMainTextMovementMethod().goToTop(mainText)
            }

            calcData.error != null -> {
                mainText.text = calcData.error
                getMainTextMovementMethod().goToTop(mainText)

                showErrorUi()
            }

            // scroll to bottom after typing new value
            else -> {
                mainText.text = calcData.computeText.joinToString(computeSeparator)
                getMainTextMovementMethod().goToBottom(getMainText())
            }
        }
    }

    /**
     * Enable all buttons in numpad
     */
    protected open fun resetUi() {
        allButtonIds.forEach {
            val view = rootView.findViewById<View>(it)

            if (it != R.id.clearButton && view != null) {
                enableButton(view)
            }
        }

        getMainText().textSize = computeTextSize
    }

    /**
     * Update UI when error occurs.
     * Disables all buttons except clear by default.
     */
    protected open fun showErrorUi() {
        disableAllButClear()
        getMainText().textSize = errorTextSize
    }

    /**
     * Disable all buttons but clear
     */
    protected fun disableAllButClear() {
        allButtonIds.forEach {
            val view = rootView.findViewById<View>(it)
            disableButton(view)
        }

        val clearButton = rootView.findViewById<View>(R.id.clearButton)
        enableButton(clearButton)
    }

    /**
     * Disable button and update button UI
     *
     * @param button [View]?: view to disable, can be `null`
     */
    protected fun disableButton(button: View?) {
        if (button != null) {
            val disabledForeground = getDisabledForeground(requireContext())
            button.disable()
            setImageButtonTint(button, disabledForeground)
        }
    }

    /**
     * Enable button and update button UI
     *
     * @param button [View]?: view to enable, can be `null`
     */
    protected fun enableButton(button: View?) {
        if (button != null) {
            val colorOnPrimary = getColorOnPrimary(requireContext())
            button.enable()
            setImageButtonTint(button, colorOnPrimary)
        }
    }

    /**
     * Handle number or operator button being pressed.
     * Calls ViewModel appendComputeText method by default.
     */
    protected open fun handleInputButton(text: String) {
        calculatorViewModel.appendComputeText(text)
    }

    /**
     * Handle equals button being pressed
     */
    protected abstract fun handleEquals()

    /**
     * Handle backspace button being pressed.
     * Calls ViewModel backspace method by default.
     */
    protected open fun handleBackspace() {
        calculatorViewModel.backspaceComputeText()
    }

    /**
     * Get the main text box
     *
     * @return [TextView]
     */
    private fun getMainText(): TextView {
        return rootView.findViewById(R.id.mainText)
    }

    /**
     * Get the movement method for the main text box as an [UnprotectedScrollingMovementMethod]
     *
     * @return [UnprotectedScrollingMovementMethod]
     */
    private fun getMainTextMovementMethod(): UnprotectedScrollingMovementMethod {
        return getMainText().movementMethod as UnprotectedScrollingMovementMethod
    }
}
