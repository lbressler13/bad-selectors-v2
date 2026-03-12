package xyz.lbres.badselectorsv2.ui.calculator.singleoperation

import androidx.test.core.app.ActivityScenario
import androidx.test.espresso.assertion.ViewAssertions.doesNotExist
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.isClickable
import androidx.test.espresso.matcher.ViewMatchers.isNotClickable
import androidx.test.espresso.matcher.ViewMatchers.withText
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.experimental.categories.Category
import org.junit.runner.RunWith
import org.robolectric.Robolectric
import xyz.lbres.badselectorsv2.BaseActivity
import xyz.lbres.badselectorsv2.ui.calculator.backspaceButton
import xyz.lbres.badselectorsv2.ui.calculator.clearButton
import xyz.lbres.badselectorsv2.ui.calculator.equalsButton
import xyz.lbres.badselectorsv2.ui.calculator.mainText
import xyz.lbres.badselectorsv2.ui.calculator.numberButtons
import xyz.lbres.badselectorsv2.ui.calculator.operatorButtons
import xyz.lbres.badselectorsv2.ui.testutils.navigateToSelector
import xyz.lbres.badselectorsv2.ui.testutils.viewactions.forceClick
import xyz.lbres.badselectorsv2.ui.testutils.viewassertions.isNotPresented
import xyz.lbres.kotlinutils.general.simpleIf

@Category(Robolectric::class)
@RunWith(AndroidJUnit4::class)
class SingleOperationFragmentTest {
    private var scenario: ActivityScenario<BaseActivity>? = null

    @Before
    fun setupTest() {
        scenario = ActivityScenario.launchActivityForResult(BaseActivity::class.java)
        navigateToSelector("Calculator", "Single Operation")
    }

    @After
    fun cleanUpTest() {
        scenario = null
    }

    @Test
    fun initialUi() {
        checkState("")
        clearButton.check(isNotPresented())
        equalsButton.check(doesNotExist())
        backspaceButton.check(doesNotExist())
    }

    @Test
    fun compute() {
        numberButtons[0].perform(forceClick()) // zero
        checkState("0")

        operatorButtons["+"]!!.perform(forceClick()) // plus
        checkState("0+")

        numberButtons[1].perform(forceClick()) // one
        checkState("1")

        operatorButtons["+"]!!.perform(forceClick())
        checkState("1+")

        numberButtons[2].perform(forceClick()) // two
        checkState("3")

        operatorButtons["x"]!!.perform(forceClick()) // times
        checkState("3x")

        numberButtons[3].perform(forceClick()) // three
        checkState("9")

        operatorButtons["-"]!!.perform(forceClick()) // minus
        checkState("9-")

        numberButtons[4].perform(forceClick()) // four
        checkState("5")

        operatorButtons["/"]!!.perform(forceClick()) // div
        checkState("5/")

        numberButtons[5].perform(forceClick()) // five
        checkState("1")

        operatorButtons["+"]!!.perform(forceClick())
        checkState("1+")

        numberButtons[6].perform(forceClick()) // six
        checkState("7")

        operatorButtons["x"]!!.perform(forceClick())
        checkState("7x")

        numberButtons[7].perform(forceClick()) // seven
        checkState("49")

        operatorButtons["-"]!!.perform(forceClick())
        checkState("49-")

        numberButtons[8].perform(forceClick()) // eight
        checkState("41")

        operatorButtons["x"]!!.perform(forceClick())
        checkState("41x")

        numberButtons[9].perform(forceClick()) // nine
        checkState("369")

        operatorButtons["x"]!!.perform(forceClick())
        checkState("369x")

        // negative number
        numberButtons[0].perform(forceClick())
        operatorButtons["+"]!!.perform(forceClick())
        numberButtons[7].perform(forceClick())
        operatorButtons["-"]!!.perform(forceClick())
        numberButtons[9].perform(forceClick())
        checkState("-2")

        operatorButtons["x"]!!.perform(forceClick())
        checkState("-2x")
        numberButtons[9].perform(forceClick())
        checkState("-18")
    }

    @Test
    fun divideByZero() {
        // initial input
        numberButtons[4].perform(forceClick())
        operatorButtons["/"]!!.perform(forceClick())
        checkState("4/")

        numberButtons[2].perform(forceClick())

        // other zero op
        operatorButtons["+"]!!.perform(forceClick())
        checkState("2+")
        numberButtons[0].perform(forceClick())

        // other division
        operatorButtons["/"]!!.perform(forceClick())
        checkState("2/")
        numberButtons[1].perform(forceClick())
        checkState("2")

        // zero div zero
        operatorButtons["x"]!!.perform(forceClick())
        numberButtons[0].perform(forceClick())
        operatorButtons["/"]!!.perform(forceClick()) // divide
        checkState("0/")
    }

    @Test
    fun testRecreate() {
        // blank
        checkState("")
        scenario!!.recreate()
        checkState("")

        // number
        numberButtons[3].perform(forceClick())
        scenario!!.recreate()
        checkState("3")

        // number and operator
        operatorButtons["x"]!!.perform(forceClick())
        scenario!!.recreate()
        checkState("3x")

        // computed number
        numberButtons[7].perform(forceClick())
        scenario!!.recreate()
        checkState("21")

        // divide by zero
        operatorButtons["/"]!!.perform(forceClick())
        scenario!!.recreate()
        checkState("21/")
    }

    /**
     * Check current state of fragment based on text.
     * Checks value of main text, and if all buttons are enabled or disabled based on last character.
     */
    private fun checkState(text: String) {
        mainText.check(matches(withText(text)))
        val numbersClickable = text.isEmpty() || !text.last().isDigit()
        val zeroDisabled = text.isNotEmpty() && text.last() == '/'
        val operatorsClickable = !numbersClickable

        val operatorsMatcher = simpleIf(operatorsClickable, isClickable(), isNotClickable())
        operatorButtons.forEach { it.value.check(matches(operatorsMatcher)) }

        val numbersMatcher = simpleIf(numbersClickable, isClickable(), isNotClickable())
        numberButtons.forEach {
            if (numbersClickable && it == numberButtons[0] && zeroDisabled) {
                it.check(matches(isNotClickable()))
            } else {
                it.check(matches(numbersMatcher))
            }
        }
    }
}
