package xyz.lbres.badselectorsv2.ui.calculator.singleoperation

import androidx.test.core.app.ActivityScenario
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
import xyz.lbres.badselectorsv2.ui.calculator.clearButton
import xyz.lbres.badselectorsv2.ui.calculator.mainText
import xyz.lbres.badselectorsv2.ui.calculator.mainTextMatches
import xyz.lbres.badselectorsv2.ui.calculator.numberButtons
import xyz.lbres.badselectorsv2.ui.calculator.operatorButtons
import xyz.lbres.badselectorsv2.ui.testutils.navigateToSelector
import xyz.lbres.badselectorsv2.ui.testutils.viewactions.forceClick
import xyz.lbres.badselectorsv2.ui.testutils.viewassertions.isNotPresented
import xyz.lbres.kotlinutils.general.simpleIf

// TODO update attr fragment, home fragment

@Category(Robolectric::class)
@RunWith(AndroidJUnit4::class)
class SingleOperationFragmentTest {
    private var scenario: ActivityScenario<BaseActivity>? = null

    @Before
    fun setupTest() {
        scenario = ActivityScenario.launchActivityForResult(BaseActivity::class.java)
        navigateToSelector(2, "Single Operation")
    }

    @After
    fun cleanUpTest() {
        scenario = null
    }

    @Test
    fun initialUi() {
        checkNumbersClickable()
        checkOperatorsNotClickable()
        clearButton.check(isNotPresented())
        mainTextMatches("")
        // TODO check equals and backspace when those exist
    }

//    @Test
//    fun allButtons() {
//        // number buttons
//        numberButtons[0].perform(forceClick())
//        numberButtons.forEachIndexed { index, button ->
//            operatorButtons["+"]!!.perform(forceClick())
//            button.perform(forceClick())
//            mainTextMatches(index.toString())
//
//            // revert main text to 0
//            operatorButtons["x"]!!.perform(forceClick())
//            numberButtons[0].perform(forceClick())
//        }
//
//        // operator button
//        operatorButtons.forEach { (operator, button) ->
//            button.perform(forceClick())
//            mainTextMatches("0$operator")
//
//            // revert main text to 0
//            val identity = simpleIf(operator == "+" || operator == "-", 0, 1)
//            numberButtons[identity].perform(forceClick())
//        }
//    }

    @Test
    fun compute() {
        numberButtons[0].perform(forceClick()) // zero
        mainTextMatches("0")
        checkNumbersNotClickable()
        checkOperatorsClickable()

        operatorButtons["+"]!!.perform(forceClick()) // +
        mainText.check(matches(withText("0+")))
        checkNumbersClickable()
        checkOperatorsNotClickable()

        numberButtons[1].perform(forceClick()) // one
        mainText.check(matches(withText("1")))
        checkNumbersNotClickable()
        checkOperatorsClickable()

        operatorButtons["+"]!!.perform(forceClick())
        mainText.check(matches(withText("1+")))
        checkNumbersClickable()
        checkOperatorsNotClickable()

        numberButtons[2].perform(forceClick()) // two
        mainText.check(matches(withText("3")))
        checkNumbersNotClickable()
        checkOperatorsClickable()

        operatorButtons["x"]!!.perform(forceClick()) // times
        mainText.check(matches(withText("3x")))
        checkNumbersClickable()
        checkOperatorsNotClickable()

        numberButtons[3].perform(forceClick()) // three
        mainText.check(matches(withText("9")))
        checkNumbersNotClickable()
        checkOperatorsClickable()

        operatorButtons["-"]!!.perform(forceClick()) // minus
        mainText.check(matches(withText("9-")))
        checkNumbersClickable()
        checkOperatorsNotClickable()

        numberButtons[4].perform(forceClick()) // four
        mainText.check(matches(withText("5")))
        checkNumbersNotClickable()
        checkOperatorsClickable()

        operatorButtons["/"]!!.perform(forceClick()) // divide
        mainText.check(matches(withText("5/")))
        checkNumbersClickable(zeroClickable = false)
        checkOperatorsNotClickable()

        numberButtons[5].perform(forceClick()) // five
        mainText.check(matches(withText("1")))
        checkNumbersNotClickable()
        checkOperatorsClickable()

        operatorButtons["+"]!!.perform(forceClick())
        mainText.check(matches(withText("1+")))
        checkNumbersClickable()
        checkOperatorsNotClickable()

        numberButtons[6].perform(forceClick()) // six
        mainText.check(matches(withText("7")))
        checkNumbersNotClickable()
        checkOperatorsClickable()

        operatorButtons["x"]!!.perform(forceClick())
        mainText.check(matches(withText("7x")))
        checkNumbersClickable()
        checkOperatorsNotClickable()

        numberButtons[7].perform(forceClick()) // seven
        mainText.check(matches(withText("49")))
        checkNumbersNotClickable()
        checkOperatorsClickable()

        operatorButtons["-"]!!.perform(forceClick())
        mainText.check(matches(withText("49-")))
        checkNumbersClickable()
        checkOperatorsNotClickable()

        numberButtons[8].perform(forceClick()) // eight
        mainText.check(matches(withText("41")))
        checkNumbersNotClickable()
        checkOperatorsClickable()

        operatorButtons["x"]!!.perform(forceClick())
        mainText.check(matches(withText("41x")))
        checkNumbersClickable()
        checkOperatorsNotClickable()

        numberButtons[9].perform(forceClick()) // nine
        mainText.check(matches(withText("369")))
        checkNumbersNotClickable()
        checkOperatorsClickable()

        operatorButtons["x"]!!.perform(forceClick())
        mainText.check(matches(withText("369x")))
        checkNumbersClickable()
        checkOperatorsNotClickable()

        // negative number
        numberButtons[0].perform(forceClick())
        operatorButtons["+"]!!.perform(forceClick())
        numberButtons[7].perform(forceClick())
        operatorButtons["-"]!!.perform(forceClick())
        numberButtons[9].perform(forceClick())
        mainText.check(matches(withText("-2")))
        checkNumbersNotClickable()
        checkOperatorsClickable()

        operatorButtons["x"]!!.perform(forceClick())
        mainText.check(matches(withText("-2x")))
        checkNumbersClickable()
        checkOperatorsNotClickable()
        numberButtons[9].perform(forceClick())
        mainText.check(matches(withText("-18")))
        checkNumbersNotClickable()
        checkOperatorsClickable()
    }

    @Test
    fun divideByZero() {
        // initial input
        numberButtons[4].perform(forceClick())
        operatorButtons["/"]!!.perform(forceClick())
        checkNumbersClickable(zeroClickable = false)

        numberButtons[2].perform(forceClick())

        // other zero op
        operatorButtons["+"]!!.perform(forceClick())
        checkNumbersClickable(zeroClickable = true)
        numberButtons[0].perform(forceClick())

        // other division
        operatorButtons["/"]!!.perform(forceClick())
        checkNumbersClickable(zeroClickable = false)
        numberButtons[1].perform(forceClick())

        // zero div zero
        operatorButtons["x"]!!.perform(forceClick())
        numberButtons[0].perform(forceClick())
        mainTextMatches("0")
        operatorButtons["/"]!!.perform(forceClick()) // divide
        checkNumbersClickable(zeroClickable = false)
    }

    @Test
    fun testRecreate() {

    }

    /**
     * Check number buttons 1-9 are clickable, and that the 0 button has the expected clickability
     *
     * @param zeroClickable [Boolean]: if the zero button should be clickable. Defaults to `true`
     */
    fun checkNumbersClickable(zeroClickable: Boolean = true) {
        numberButtons.forEach {
            if (it == numberButtons[0] && !zeroClickable) {
                it.check(matches(isNotClickable()))
            } else {
                it.check(matches(isClickable()))
            }
        }
    }

    /**
     * Check that no number buttons are clickable
     */
    fun checkNumbersNotClickable() {
        numberButtons.forEach { it.check(matches(isNotClickable())) }
    }

    /**
     * Check that all operator buttons are clickable
     */
    fun checkOperatorsClickable() {
        operatorButtons.forEach { it.value.check(matches(isClickable())) }
    }

    /**
     * Check that no operator buttons are clickable
     */
    fun checkOperatorsNotClickable() {
        operatorButtons.forEach { it.value.check(matches(isNotClickable())) }
    }
}
