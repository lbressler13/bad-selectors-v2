package xyz.lbres.badselectorsv2.ui.calculator.randomenabled

import androidx.test.core.app.ActivityScenario
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.isEnabled
import androidx.test.espresso.matcher.ViewMatchers.withText
import androidx.test.ext.junit.runners.AndroidJUnit4
import io.mockk.every
import io.mockk.justRun
import io.mockk.mockkConstructor
import io.mockk.unmockkAll
import org.junit.After
import org.junit.Test
import org.junit.experimental.categories.Category
import org.junit.runner.RunWith
import org.robolectric.Robolectric
import xyz.lbres.badselectorsv2.BaseActivity
import xyz.lbres.badselectorsv2.calculator.randomenabled.RandomEnabler
import xyz.lbres.badselectorsv2.ui.calculator.backspaceButton
import xyz.lbres.badselectorsv2.ui.calculator.clearButton
import xyz.lbres.badselectorsv2.ui.calculator.clickBackspace
import xyz.lbres.badselectorsv2.ui.calculator.clickClear
import xyz.lbres.badselectorsv2.ui.calculator.clickEquals
import xyz.lbres.badselectorsv2.ui.calculator.equalsButton
import xyz.lbres.badselectorsv2.ui.calculator.mainText
import xyz.lbres.badselectorsv2.ui.calculator.numberButtons
import xyz.lbres.badselectorsv2.ui.calculator.operatorButtons
import xyz.lbres.badselectorsv2.ui.calculator.operators
import xyz.lbres.badselectorsv2.ui.calculator.typeText
import xyz.lbres.badselectorsv2.ui.testutils.isDisabled
import xyz.lbres.badselectorsv2.ui.testutils.navigateToSelector
import xyz.lbres.kotlinutils.general.simpleIf
import xyz.lbres.kotlinutils.list.listOfValue

@Category(Robolectric::class)
@RunWith(AndroidJUnit4::class)
class RandomEnabledFragmentTest {
    private val numbersLists = listOf(
        listOf(true, true, false, false, true, false, true, false, false, true),
        listOf(true, false, false, false, false, false, true, true, false, false),
        listOf(false, true, true, true, false, false, false, true, true, true),
        listOf(false, true, false, true, false, true, false, true, true, false),
        listOf(false, false, true, true, true, true, true, true, false, true),
        listOf(true, false, true, false, true, true, true, false, false, true),
        listOf(false, false, true, false, true, true, true, false, false, true),
        listOf(false, false, false, false, true, false, true, true, false, true),
    )
    val operatorsLists = listOf(
        listOf(false, false, true, true),
        listOf(true, false, true, true),
        listOf(false, true, false, true),
        listOf(true, true, true, false),
        listOf(false, true, true, false),
        listOf(false, true, true, true),
        listOf(false, true, false, true),
        listOf(true, true, true, false),
    )

    @After
    fun cleanupTest() {
        unmockkAll()
    }

    @Test
    fun initialUi() {
        mockRandomEnabler()

        launchFragment()
        checkState("", numbersLists[0], operatorsLists[0])
    }

    @Test
    fun equalsButton() {
        mockRandomEnabler()
        launchFragment()

        // doesn't change on blank
        checkState("", numbersLists[0], operatorsLists[0])
        clickEquals()
        checkState("", numbersLists[0], operatorsLists[0])

        typeText("4x7")
        checkState("4x7", numbersLists[3], operatorsLists[3])
        clickEquals()
        // updated on equals click
        checkState("28", numbersLists[4], operatorsLists[4])
    }

    @Test
    fun backspace() {
        mockRandomEnabler()
        launchFragment()

        // backspace on blank
        checkState("", numbersLists[0], operatorsLists[0])
        clickBackspace()
        checkState("", numbersLists[0], operatorsLists[0])

        // text
        typeText("4x7")
        checkState("4x7", numbersLists[3], operatorsLists[3])
        clickBackspace()
        // updated on backspace click
        checkState("4x", numbersLists[4], operatorsLists[4])

        // backspace to blank
        clickBackspace()
        checkState("4", numbersLists[5], operatorsLists[5])
        clickBackspace()
        checkState("", numbersLists[6], operatorsLists[6])
        clickBackspace()
        checkState("", numbersLists[6], operatorsLists[6])

        // backspace multidigit number
        // TODO
    }

    @Test
    fun clear() {
        mockRandomEnabler()
        launchFragment()

        // on blank
        checkState("", numbersLists[0], operatorsLists[0])
        clickClear()
        checkState("", numbersLists[0], operatorsLists[0])

        typeText("4x7")
        checkState("4x7", numbersLists[3], operatorsLists[3])
        clickClear()
        // updated on clear click
        checkState("", numbersLists[4], operatorsLists[4])
    }

    @Test
    fun computeSingleNumber() {
        val fullNumbersList = numbersLists + numbersLists
        val fullOperatorsList = operatorsLists + operatorsLists
        mockRandomEnabler(fullNumbersList.flatten(), fullOperatorsList.flatten())

        // call count on each line
        launchFragment() // 0

        // single number
        typeText("4")  // 1  four
        checkState("4", fullNumbersList[1], fullOperatorsList[1])
        clickEquals() // 2
        checkState("4", fullNumbersList[2], fullOperatorsList[2])

        typeText("6")  // 3  two
        checkState("46", fullNumbersList[3], fullOperatorsList[3])
        clickEquals() // 4
        checkState("46", fullNumbersList[4], fullOperatorsList[4])

        clickClear()  // 5
        typeText("51")  // 7  one, five
        checkState("51", fullNumbersList[7], fullOperatorsList[7])
        clickEquals() // 8
        checkState("51", fullNumbersList[8], fullOperatorsList[8])

        clickClear()  // 9
        typeText("03")  // 11  zero, three
        checkState("03", fullNumbersList[11], fullOperatorsList[11])
        clickEquals() // 12
        checkState("3", fullNumbersList[12], fullOperatorsList[12])
    }

    @Test
    fun compute() {
        // total: 36
        val fullNumbersList = List(5) { numbersLists }.flatten()
        val fullOperatorsList = List(5) { operatorsLists }.flatten()
        mockRandomEnabler(fullNumbersList.flatten(), fullOperatorsList.flatten())

        // call count on each line
        launchFragment()  // 0

        typeText("4+6")  // 3  plus
        checkState("4+6", fullNumbersList[3], fullOperatorsList[3])
        clickEquals() // 4
        checkState("10", fullNumbersList[4], fullOperatorsList[4])

        typeText("/6")  // 6  divide
        checkState("10/6", fullNumbersList[6], fullOperatorsList[6])
        clickEquals()  // 7
        checkState("1", fullNumbersList[7], fullOperatorsList[7])

        typeText("-12")  // 10  minus
        checkState("1-12", fullNumbersList[10], fullOperatorsList[10])
        clickEquals()  // 11
        checkState("-11", fullNumbersList[11], fullOperatorsList[11])

        typeText("x78")  // 14  times
        checkState("-11x78", fullNumbersList[14], fullOperatorsList[14])
        clickEquals()  // 15
        checkState("-858", fullNumbersList[15], fullOperatorsList[15])

        // multi operator
        clickClear()  // 16
        typeText("15+6/2")  // 22
        checkState("15+6/2", fullNumbersList[22], fullOperatorsList[22])
        clickEquals()  // 23
        checkState("18", fullNumbersList[23], fullOperatorsList[23])

        clickClear()  // 24
        typeText("2+4x6-93/10")  // 35
        checkState("2+4x6-93/10", fullNumbersList[35], fullOperatorsList[35])
        clickEquals()  // 36
        checkState("17", fullNumbersList[36], fullOperatorsList[36])
    }

    @Test
    fun computeError() {
        // enable all buttons for ease of error testing
        // TODO add randomization to check buttons changing :(
        mockRandomEnabler(listOf(true), listOf(true))
        launchFragment()

        fun checkErrorWithText(text: String, error: String) {
            typeText(text)
            clickEquals()
            checkState(error, listOfValue(10, false), listOfValue(4, false), false, false)
            clickClear()
        }

        // syntax error
        var expectedError = "Err: Syntax Error"
        checkErrorWithText("+", expectedError)
        checkErrorWithText("12+6//2", expectedError)
        checkErrorWithText("-5", expectedError)

        // divide by zero
        expectedError = "Err: Divide by 0"
        checkErrorWithText("6/0", expectedError)
        checkErrorWithText("6+3/0-2", expectedError)

        // overflow
        expectedError = "Err: Overflow value"
        checkErrorWithText(Int.MAX_VALUE.toString() + "000", expectedError)
    }

    @Test
    fun recreate() {
        // test 03 text
        // TODO
    }

    private fun mockRandomEnabler(
        numberValues: List<Boolean> = numbersLists.flatten(),
        operatorValues: List<Boolean> = operatorsLists.flatten(),
    ) {
        mockkConstructor(RandomEnabler::class)
        every { constructedWith<RandomEnabler>().isDigitEnabled(any()) } returnsMany numberValues
        every { constructedWith<RandomEnabler>().isOperatorEnabled(any()) } returnsMany operatorValues
        justRun { constructedWith<RandomEnabler>().update() }
    }

    // cannot launch scenario in before block due to mocking requirements
    private fun launchFragment(): ActivityScenario<BaseActivity> {
        val scenario = ActivityScenario.launchActivityForResult(BaseActivity::class.java)
        navigateToSelector("Calculator", "Random Enabled")
        return scenario
    }

    private fun checkState(
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
}
