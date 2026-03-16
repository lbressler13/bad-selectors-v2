package xyz.lbres.badselectorsv2.ui.calculator.randomenabled

import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.isEnabled
import androidx.test.espresso.matcher.ViewMatchers.withText
import xyz.lbres.badselectorsv2.ui.calculator.backspaceButton
import xyz.lbres.badselectorsv2.ui.calculator.clearButton
import xyz.lbres.badselectorsv2.ui.calculator.equalsButton
import xyz.lbres.badselectorsv2.ui.calculator.mainText
import xyz.lbres.badselectorsv2.ui.calculator.numberButtons
import xyz.lbres.badselectorsv2.ui.calculator.operatorButtons
import xyz.lbres.badselectorsv2.ui.calculator.operators
import xyz.lbres.badselectorsv2.ui.testutils.isDisabled
import xyz.lbres.kotlinutils.general.simpleIf
import xyz.lbres.kotlinutils.list.listOfValue

/**
 * Check current state of fragment.
 * Checks value of main text, and if all buttons are enabled or disabled appropriately.
 */
fun checkState(
    text: String,
    enabledNumbers: List<Boolean>,
    enabledOperators: List<Boolean>,
    equalsEnabled: Boolean = true,
    backspaceEnabled: Boolean = true,
) {
    mainText.check(matches(withText(text)))

    val enabledMatcher = isEnabled()
    val disabledMatcher = isDisabled()
    val buttonMatcher = { enabled: Boolean -> simpleIf(enabled, enabledMatcher, disabledMatcher) }

    numberButtons.forEachIndexed { index, button ->
        try {
            button.check(matches(buttonMatcher(enabledNumbers[index])))
        } catch (e: AssertionError) {
            println("Error checking number button at $index")
            throw e
        }
    }

    operators.forEachIndexed { index, operator ->
        val button = operatorButtons[operator]!!
        try {
            button.check(matches(buttonMatcher(enabledOperators[index])))
        } catch (e: AssertionError) {
            println("Error checking operator button for '$operator'")
            throw e
        }
    }

    equalsButton.check(matches(buttonMatcher(equalsEnabled)))
    backspaceButton.check(matches(buttonMatcher(backspaceEnabled)))
    clearButton.check(matches(buttonMatcher(true)))
}

fun checkErrorState(error: String) {
    checkState(
        error,
        listOfValue(10, false),
        listOfValue(4, false),
        equalsEnabled = false,
        backspaceEnabled = false,
    )
}
