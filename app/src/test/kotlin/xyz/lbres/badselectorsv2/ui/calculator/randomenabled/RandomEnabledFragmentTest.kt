package xyz.lbres.badselectorsv2.ui.calculator.randomenabled

import androidx.test.core.app.ActivityScenario
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
import xyz.lbres.badselectorsv2.ui.calculator.clickBackspace
import xyz.lbres.badselectorsv2.ui.calculator.clickClear
import xyz.lbres.badselectorsv2.ui.calculator.clickEquals
import xyz.lbres.badselectorsv2.ui.calculator.typeText
import xyz.lbres.badselectorsv2.ui.testutils.navigateToSelector

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
        clickEquals() // don't update index on blank
        checkState("", numbersLists[currentIndex], operatorsLists[currentIndex])

        typeTextWithIndex("4x7")
        checkState("4x7", numbersLists[currentIndex], operatorsLists[currentIndex])
        clickEqualsWithIndex()
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
        clickBackspace() // don't update index on blank
        checkState("", fullNumbersLists[currentIndex], fullOperatorsLists[currentIndex])

        // text
        typeTextWithIndex("4x7")
        checkState("4x7", fullNumbersLists[currentIndex], fullOperatorsLists[currentIndex])
        clickBackspaceWithIndex()
        checkState("4x", fullNumbersLists[currentIndex], fullOperatorsLists[currentIndex])

        // backspace to blank
        clickBackspaceWithIndex()
        checkState("4", fullNumbersLists[currentIndex], fullOperatorsLists[currentIndex])
        clickBackspaceWithIndex()
        checkState("", fullNumbersLists[currentIndex], fullOperatorsLists[currentIndex])
        clickBackspace()
        checkState("", fullNumbersLists[currentIndex], fullOperatorsLists[currentIndex])

        // backspace multidigit number
        typeTextWithIndex("14")
        clickEqualsWithIndex()
        checkState("14", fullNumbersLists[currentIndex], fullOperatorsLists[currentIndex])
        clickBackspaceWithIndex()
        checkState("", fullNumbersLists[currentIndex], fullOperatorsLists[currentIndex])

        typeTextWithIndex("10")
        clickEqualsWithIndex()
        typeTextWithIndex("+6")
        checkState("10+6", fullNumbersLists[currentIndex], fullOperatorsLists[currentIndex])
        clickBackspaceWithIndex()
        checkState("10+", fullNumbersLists[currentIndex], fullOperatorsLists[currentIndex])
        clickBackspaceWithIndex()
        checkState("10", fullNumbersLists[currentIndex], fullOperatorsLists[currentIndex])
        clickBackspaceWithIndex()
        checkState("", fullNumbersLists[currentIndex], fullOperatorsLists[currentIndex])
    }

    @Test
    fun clear() {
        mockRandomEnabler()
        launchFragment()

        // on blank
        checkState("", numbersLists[currentIndex], operatorsLists[currentIndex])
        clickClear() // don't update index on blank
        checkState("", numbersLists[currentIndex], operatorsLists[currentIndex])

        typeTextWithIndex("4x7")
        checkState("4x7", numbersLists[currentIndex], operatorsLists[currentIndex])
        clickClearWithIndex()
        checkState("", numbersLists[currentIndex], operatorsLists[currentIndex])
    }

    @Test
    fun computeSingleNumber() {
        val fullNumbersLists = numbersLists + numbersLists
        val fullOperatorsLists = operatorsLists + operatorsLists
        mockRandomEnabler(fullNumbersLists.flatten(), fullOperatorsLists.flatten())
        launchFragment()

        // single number
        typeTextWithIndex("4")
        checkState("4", fullNumbersLists[currentIndex], fullOperatorsLists[currentIndex])
        clickEqualsWithIndex()
        checkState("4", fullNumbersLists[currentIndex], fullOperatorsLists[currentIndex])

        typeTextWithIndex("6")
        checkState("46", fullNumbersLists[currentIndex], fullOperatorsLists[currentIndex])
        clickEqualsWithIndex()
        checkState("46", fullNumbersLists[currentIndex], fullOperatorsLists[currentIndex])

        clickClearWithIndex()
        typeTextWithIndex("51")
        checkState("51", fullNumbersLists[currentIndex], fullOperatorsLists[currentIndex])
        clickEqualsWithIndex()
        checkState("51", fullNumbersLists[currentIndex], fullOperatorsLists[currentIndex])

        clickClearWithIndex()
        typeTextWithIndex("03")
        checkState("03", fullNumbersLists[currentIndex], fullOperatorsLists[currentIndex])
        clickEqualsWithIndex()
        checkState("3", fullNumbersLists[currentIndex], fullOperatorsLists[currentIndex])
    }

    @Test
    fun compute() {
        val fullNumbersLists = List(5) { numbersLists }.flatten()
        val fullOperatorsLists = List(5) { operatorsLists }.flatten()
        mockRandomEnabler(fullNumbersLists.flatten(), fullOperatorsLists.flatten())
        launchFragment()

        typeTextWithIndex("4+6") // four, six, plus
        checkState("4+6", fullNumbersLists[currentIndex], fullOperatorsLists[currentIndex])
        clickEqualsWithIndex()
        checkState("10", fullNumbersLists[currentIndex], fullOperatorsLists[currentIndex])

        typeTextWithIndex("/6") // div
        checkState("10/6", fullNumbersLists[currentIndex], fullOperatorsLists[currentIndex])
        clickEqualsWithIndex()
        checkState("1", fullNumbersLists[currentIndex], fullOperatorsLists[currentIndex])

        typeTextWithIndex("-12") // one, two, minus
        checkState("1-12", fullNumbersLists[currentIndex], fullOperatorsLists[currentIndex])
        clickEqualsWithIndex()
        checkState("-11", fullNumbersLists[currentIndex], fullOperatorsLists[currentIndex])

        typeTextWithIndex("x78") // seven, eight, times
        checkState("-11x78", fullNumbersLists[currentIndex], fullOperatorsLists[currentIndex])
        clickEqualsWithIndex()
        checkState("-858", fullNumbersLists[currentIndex], fullOperatorsLists[currentIndex])

        // multi operator
        clickClearWithIndex()
        typeTextWithIndex("15+6/2") // five
        checkState("15+6/2", fullNumbersLists[currentIndex], fullOperatorsLists[currentIndex])
        clickEqualsWithIndex()
        checkState("18", fullNumbersLists[currentIndex], fullOperatorsLists[currentIndex])

        clickClearWithIndex()
        typeTextWithIndex("2+4x6-93/10") // zero, three, nine
        checkState("2+4x6-93/10", fullNumbersLists[currentIndex], fullOperatorsLists[currentIndex])
        clickEqualsWithIndex()
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
            checkErrorState(error)
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
        val scenario = launchFragment()

        checkState("", fullNumbersLists[currentIndex], fullOperatorsLists[currentIndex])
        scenario.recreate()
        checkState("", fullNumbersLists[currentIndex], fullOperatorsLists[currentIndex])

        // text
        typeTextWithIndex("03")
        checkState("03", fullNumbersLists[currentIndex], fullOperatorsLists[currentIndex])
        scenario.recreate()
        checkState("03", fullNumbersLists[currentIndex], fullOperatorsLists[currentIndex])

        typeTextWithIndex("x5/")
        checkState("03x5/", fullNumbersLists[currentIndex], fullOperatorsLists[currentIndex])
        scenario.recreate()
        checkState("03x5/", fullNumbersLists[currentIndex], fullOperatorsLists[currentIndex])

        clickBackspaceWithIndex()
        checkState("03x5", fullNumbersLists[currentIndex], fullOperatorsLists[currentIndex])
        scenario.recreate()
        checkState("03x5", fullNumbersLists[currentIndex], fullOperatorsLists[currentIndex])

        // eq value
        clickEqualsWithIndex()
        checkState("15", fullNumbersLists[currentIndex], fullOperatorsLists[currentIndex])
        scenario.recreate()
        checkState("15", fullNumbersLists[currentIndex], fullOperatorsLists[currentIndex])

        // cleared
        clickClearWithIndex()
        checkState("", fullNumbersLists[currentIndex], fullOperatorsLists[currentIndex])
        scenario.recreate()
        checkState("", fullNumbersLists[currentIndex], fullOperatorsLists[currentIndex])

        // error
        typeTextWithIndex("+")
        clickEqualsWithIndex()
        checkErrorState("Err: Syntax Error")
        scenario.recreate()
        checkErrorState("Err: Syntax Error")
    }

    // mock RandomEnabler class
    private fun mockRandomEnabler(
        numbersReturnValues: List<Boolean> = numbersLists.flatten(),
        operatorsReturnValues: List<Boolean> = operatorsLists.flatten(),
    ) {
        mockkConstructor(RandomEnabler::class)
        every { constructedWith<RandomEnabler>().isDigitEnabled(any()) } returnsMany numbersReturnValues
        every { constructedWith<RandomEnabler>().isOperatorEnabled(any()) } returnsMany operatorsReturnValues
        justRun { constructedWith<RandomEnabler>().update() }
    }

    // cannot launch scenario in before block due to mocking requirements
    private fun launchFragment(): ActivityScenario<BaseActivity> {
        val scenario = ActivityScenario.launchActivityForResult(BaseActivity::class.java)
        navigateToSelector("Calculator", "Random Enabled")
        return scenario
    }

    // wrapper to update current index when text is typed
    private fun typeTextWithIndex(text: String) {
        typeText(text)
        currentIndex += text.length
    }

    // wrapper to update current index when equals is pressed
    private fun clickEqualsWithIndex() {
        clickEquals()
        currentIndex++
    }

    // wrapper to update current index when clear is pressed
    private fun clickClearWithIndex() {
        clickClear()
        currentIndex++
    }

    // wrapper to update current index when backspace is pressed
    private fun clickBackspaceWithIndex() {
        clickBackspace()
        currentIndex++
    }
}
