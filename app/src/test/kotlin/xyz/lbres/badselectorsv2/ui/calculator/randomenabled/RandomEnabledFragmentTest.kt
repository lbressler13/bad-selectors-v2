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
import org.junit.Before
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
    private var currentIndex: Int = 0

    @Before
    fun setupTest() {
        currentIndex = 0
    }

    @After
    fun cleanupTest() {
        unmockkAll()
    }

    @Test
    fun initialUi() {
        mockRandomEnabler()

        launchFragment()
        checkState("", numbersLists[currentIndex], operatorsLists[currentIndex])
    }

    @Test
    fun equalsButton() {
        mockRandomEnabler()
        launchFragment()

        // doesn't change on blank
        checkState("", numbersLists[currentIndex], operatorsLists[currentIndex])
        clickEquals()
        checkState("", numbersLists[currentIndex], operatorsLists[currentIndex])

        typeTextWithIndex("4x7")
        checkState("4x7", numbersLists[currentIndex], operatorsLists[currentIndex])
        clickEqualsWithIndex()
        // updated on equals click
        checkState("28", numbersLists[currentIndex], operatorsLists[currentIndex])
    }

    @Test
    fun backspace() {
        val fullNumbersLists = numbersLists + numbersLists + numbersLists
        val fullOperatorsLists = operatorsLists + operatorsLists + operatorsLists
        mockRandomEnabler(fullNumbersLists.flatten(), fullOperatorsLists.flatten())
        launchFragment()

        // backspace on blank
        checkState("", fullNumbersLists[currentIndex], fullOperatorsLists[currentIndex])
        clickBackspace()
        checkState("", fullNumbersLists[currentIndex], fullOperatorsLists[currentIndex])

        // text
        typeTextWithIndex("4x7")
        checkState("4x7", fullNumbersLists[currentIndex], fullOperatorsLists[currentIndex])
        clickBackspaceWithIndex()
        // updated on backspace click
        checkState("4x", fullNumbersLists[currentIndex], fullOperatorsLists[currentIndex])

        // backspace to blank
        clickBackspaceWithIndex()
        checkState("4", fullNumbersLists[currentIndex], fullOperatorsLists[currentIndex])
        clickBackspaceWithIndex()
        checkState("", fullNumbersLists[currentIndex], fullOperatorsLists[currentIndex])
        clickBackspace()
        checkState("", fullNumbersLists[currentIndex], fullOperatorsLists[currentIndex])

        // backspace multidigit number
        typeTextWithIndex("14")  // 8
        clickEqualsWithIndex()  // 9
        checkState("14", fullNumbersLists[currentIndex], fullOperatorsLists[currentIndex])
        clickBackspaceWithIndex()  // 10
        checkState("", fullNumbersLists[currentIndex], fullOperatorsLists[currentIndex])

        typeTextWithIndex("10")  // 12
        clickEqualsWithIndex()  // 13
        typeTextWithIndex("+6") // 15
        checkState("10+6", fullNumbersLists[currentIndex], fullOperatorsLists[currentIndex])
        clickBackspaceWithIndex()  // 16
        checkState("10+", fullNumbersLists[currentIndex], fullOperatorsLists[currentIndex])
        clickBackspaceWithIndex()  // 17
        checkState("10", fullNumbersLists[currentIndex], fullOperatorsLists[currentIndex])
        clickBackspaceWithIndex()  // 18
        checkState("", fullNumbersLists[currentIndex], fullOperatorsLists[currentIndex])
    }

    @Test
    fun clear() {
        mockRandomEnabler()
        launchFragment()

        // on blank
        checkState("", numbersLists[currentIndex], operatorsLists[currentIndex])
        clickClear()
        checkState("", numbersLists[currentIndex], operatorsLists[currentIndex])

        typeTextWithIndex("4x7")
        checkState("4x7", numbersLists[currentIndex], operatorsLists[currentIndex])
        clickClearWithIndex()
        // updated on clear click
        checkState("", numbersLists[currentIndex], operatorsLists[currentIndex])
    }

    @Test
    fun computeSingleNumber() {
        val fullNumbersLists = numbersLists + numbersLists
        val fullOperatorsLists = operatorsLists + operatorsLists
        mockRandomEnabler(fullNumbersLists.flatten(), fullOperatorsLists.flatten())

        // call count on each line
        launchFragment() // 0

        // single number
        typeTextWithIndex("4")  // 1  four
        checkState("4", fullNumbersLists[currentIndex], fullOperatorsLists[currentIndex])
        clickEqualsWithIndex() // 2
        checkState("4", fullNumbersLists[currentIndex], fullOperatorsLists[currentIndex])

        typeTextWithIndex("6")  // 3  two
        checkState("46", fullNumbersLists[currentIndex], fullOperatorsLists[currentIndex])
        clickEqualsWithIndex() // 4
        checkState("46", fullNumbersLists[currentIndex], fullOperatorsLists[currentIndex])

        clickClearWithIndex()  // 5
        typeTextWithIndex("51")  // 7  one, five
        checkState("51", fullNumbersLists[currentIndex], fullOperatorsLists[currentIndex])
        clickEqualsWithIndex() // 8
        checkState("51", fullNumbersLists[currentIndex], fullOperatorsLists[currentIndex])

        clickClearWithIndex()  // 9
        typeTextWithIndex("03")  // 11  zero, three
        checkState("03", fullNumbersLists[currentIndex], fullOperatorsLists[currentIndex])
        clickEqualsWithIndex() // 12
        checkState("3", fullNumbersLists[currentIndex], fullOperatorsLists[currentIndex])
    }

    @Test
    fun compute() {
        // total: 36
        val fullNumbersLists = List(5) { numbersLists }.flatten()
        val fullOperatorsLists = List(5) { operatorsLists }.flatten()
        mockRandomEnabler(fullNumbersLists.flatten(), fullOperatorsLists.flatten())

        // call count on each line
        launchFragment()  // 0

        typeTextWithIndex("4+6")  // 3  plus
        checkState("4+6", fullNumbersLists[currentIndex], fullOperatorsLists[currentIndex])
        clickEqualsWithIndex() // 4
        checkState("10", fullNumbersLists[currentIndex], fullOperatorsLists[currentIndex])

        typeTextWithIndex("/6")  // 6  divide
        checkState("10/6", fullNumbersLists[currentIndex], fullOperatorsLists[currentIndex])
        clickEqualsWithIndex()  // 7
        checkState("1", fullNumbersLists[currentIndex], fullOperatorsLists[currentIndex])

        typeTextWithIndex("-12")  // 10  minus
        checkState("1-12", fullNumbersLists[currentIndex], fullOperatorsLists[currentIndex])
        clickEqualsWithIndex()  // 11
        checkState("-11", fullNumbersLists[currentIndex], fullOperatorsLists[currentIndex])

        typeTextWithIndex("x78")  // 14  times
        checkState("-11x78", fullNumbersLists[currentIndex], fullOperatorsLists[currentIndex])
        clickEqualsWithIndex()  // 15
        checkState("-858", fullNumbersLists[currentIndex], fullOperatorsLists[currentIndex])

        // multi operator
        clickClearWithIndex()  // 16
        typeTextWithIndex("15+6/2")  // 22
        checkState("15+6/2", fullNumbersLists[currentIndex], fullOperatorsLists[currentIndex])
        clickEqualsWithIndex()  // 23
        checkState("18", fullNumbersLists[currentIndex], fullOperatorsLists[currentIndex])

        clickClearWithIndex()  // 24
        typeTextWithIndex("2+4x6-93/10")  // 35
        checkState("2+4x6-93/10", fullNumbersLists[currentIndex], fullOperatorsLists[currentIndex])
        clickEqualsWithIndex()  // 36
        checkState("17", fullNumbersLists[currentIndex], fullOperatorsLists[currentIndex])
    }

    @Test
    fun computeError() {
        val fullNumbersLists = List(6) { numbersLists }.flatten()
        val fullOperatorsLists = List(6) { operatorsLists }.flatten()
        mockRandomEnabler(fullNumbersLists.flatten(), fullOperatorsLists.flatten())

        fun checkErrorWithText(text: String, error: String) {
            typeTextWithIndex(text)
            clickEqualsWithIndex()
            checkState(
                error,
                listOfValue(10, false),
                listOfValue(4, false),
                equalsEnabled = false,
                backspaceEnabled = false,
            )
            clickClearWithIndex()
            checkState("", fullNumbersLists[currentIndex], fullOperatorsLists[currentIndex])
        }

        launchFragment()

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
        val fullNumbersLists = numbersLists + numbersLists
        val fullOperatorsLists = operatorsLists + operatorsLists

        val mockNumbersLists = fullNumbersLists.toMutableList()
        val mockOperatorsLists = fullOperatorsLists.toMutableList()
        val recreateIndices = listOf(0, 2, 5, 6, 7, 8, 10).sortedDescending()
        recreateIndices.forEach {
            mockNumbersLists.add(it, fullNumbersLists[it])
            mockOperatorsLists.add(it, fullOperatorsLists[it])
        }

        mockRandomEnabler(mockNumbersLists.flatten(), mockOperatorsLists.flatten())

        // call count on each line
        val scenario = launchFragment() // 0

        checkState("", fullNumbersLists[currentIndex], fullOperatorsLists[currentIndex])
        scenario.recreate()
        checkState("", fullNumbersLists[currentIndex], fullOperatorsLists[currentIndex])

        // text
        typeTextWithIndex("03")  // 2
        checkState("03", fullNumbersLists[currentIndex], fullOperatorsLists[currentIndex])
        scenario.recreate()
        checkState("03", fullNumbersLists[currentIndex], fullOperatorsLists[currentIndex])

        typeTextWithIndex("x5/")  // 5
        checkState("03x5/", fullNumbersLists[currentIndex], fullOperatorsLists[currentIndex])
        scenario.recreate()
        checkState("03x5/", fullNumbersLists[currentIndex], fullOperatorsLists[currentIndex])

        clickBackspaceWithIndex()  // 6
        checkState("03x5", fullNumbersLists[currentIndex], fullOperatorsLists[currentIndex])
        scenario.recreate()
        checkState("03x5", fullNumbersLists[currentIndex], fullOperatorsLists[currentIndex])

        // eq value
        clickEqualsWithIndex()  // 7
        checkState("15", fullNumbersLists[currentIndex], fullOperatorsLists[currentIndex])
        scenario.recreate()
        checkState("15", fullNumbersLists[currentIndex], fullOperatorsLists[currentIndex])

        // cleared
        clickClearWithIndex()  // 8
        checkState("", fullNumbersLists[currentIndex], fullOperatorsLists[currentIndex])
        scenario.recreate()
        checkState("", fullNumbersLists[currentIndex], fullOperatorsLists[currentIndex])

        // error
        typeTextWithIndex("+")  // 9
        clickEqualsWithIndex()  // 10
        checkState("Err: Syntax Error", listOfValue(10, false), listOfValue(4, false), false, false)
        scenario.recreate()
        checkState("Err: Syntax Error", listOfValue(10, false), listOfValue(4, false), false, false)
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

    private fun typeTextWithIndex(text: String) {
        typeText(text)
        currentIndex += text.length
    }

    private fun clickEqualsWithIndex() {
        clickEquals()
        currentIndex++
    }

    private fun clickClearWithIndex() {
        clickClear()
        currentIndex++
    }

    private fun clickBackspaceWithIndex() {
        clickBackspace()
        currentIndex++
    }
}
