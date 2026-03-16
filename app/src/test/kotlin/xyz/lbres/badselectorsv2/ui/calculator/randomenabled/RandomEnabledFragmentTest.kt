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
    private val operatorsLists = listOf(
        listOf(false, false, true, true),
        listOf(true, false, true, true),
        listOf(false, true, false, true),
        listOf(true, true, true, false),
        listOf(false, true, true, false),
        listOf(false, true, true, true),
        listOf(false, true, false, true),
        listOf(true, true, true, false),
    )

    // number of times that the digit shuffler update function has been called during the test
    private var updateCounter: Int = 0

    @Before
    fun setupTest() {
        updateCounter = 0
    }

    @After
    fun cleanupTest() {
        unmockkAll()
    }

    @Test
    fun initialUi() {
        mockRandomEnabler()
        launchFragment()
        checkState("", numbersLists[updateCounter], operatorsLists[updateCounter])
    }

    @Test
    fun equalsButton() {
        mockRandomEnabler()
        launchFragment()

        // doesn't change on blank
        checkState("", numbersLists[updateCounter], operatorsLists[updateCounter])
        clickEquals() // don't update index on blank
        checkState("", numbersLists[updateCounter], operatorsLists[updateCounter])

        typeTextWithCounter("4x7")
        checkState("4x7", numbersLists[updateCounter], operatorsLists[updateCounter])
        clickEqualsWithCounter()
        checkState("28", numbersLists[updateCounter], operatorsLists[updateCounter])
    }

    @Test
    fun backspace() {
        val fullNumbersLists = numbersLists * 3
        val fullOperatorsLists = operatorsLists * 3
        mockRandomEnabler(fullNumbersLists.flatten(), fullOperatorsLists.flatten())
        launchFragment()

        // backspace on blank
        checkState("", fullNumbersLists[updateCounter], fullOperatorsLists[updateCounter])
        clickBackspace() // don't update index on blank
        checkState("", fullNumbersLists[updateCounter], fullOperatorsLists[updateCounter])

        // text
        typeTextWithCounter("4x7")
        checkState("4x7", fullNumbersLists[updateCounter], fullOperatorsLists[updateCounter])
        clickBackspaceWithCounter()
        checkState("4x", fullNumbersLists[updateCounter], fullOperatorsLists[updateCounter])

        // backspace to blank
        clickBackspaceWithCounter()
        checkState("4", fullNumbersLists[updateCounter], fullOperatorsLists[updateCounter])
        clickBackspaceWithCounter()
        checkState("", fullNumbersLists[updateCounter], fullOperatorsLists[updateCounter])
        clickBackspace()
        checkState("", fullNumbersLists[updateCounter], fullOperatorsLists[updateCounter])

        // backspace multidigit number
        typeTextWithCounter("14")
        clickEqualsWithCounter()
        checkState("14", fullNumbersLists[updateCounter], fullOperatorsLists[updateCounter])
        clickBackspaceWithCounter()
        checkState("", fullNumbersLists[updateCounter], fullOperatorsLists[updateCounter])

        typeTextWithCounter("10")
        clickEqualsWithCounter()
        typeTextWithCounter("+6")
        checkState("10+6", fullNumbersLists[updateCounter], fullOperatorsLists[updateCounter])
        clickBackspaceWithCounter()
        checkState("10+", fullNumbersLists[updateCounter], fullOperatorsLists[updateCounter])
        clickBackspaceWithCounter()
        checkState("10", fullNumbersLists[updateCounter], fullOperatorsLists[updateCounter])
        clickBackspaceWithCounter()
        checkState("", fullNumbersLists[updateCounter], fullOperatorsLists[updateCounter])
    }

    @Test
    fun clear() {
        val fullNumbersLists = numbersLists * 2
        val fullOperatorsLists = operatorsLists * 2
        mockRandomEnabler(fullNumbersLists.flatten(), fullOperatorsLists.flatten())
        launchFragment()

        // on blank
        checkState("", fullNumbersLists[updateCounter], fullOperatorsLists[updateCounter])
        clickClear() // don't update index on blank
        checkState("", fullNumbersLists[updateCounter], fullOperatorsLists[updateCounter])

        // with text
        typeTextWithCounter("4x7")
        checkState("4x7", fullNumbersLists[updateCounter], fullOperatorsLists[updateCounter])
        clickClearWithCounter()
        checkState("", fullNumbersLists[updateCounter], fullOperatorsLists[updateCounter])

        // with computed
        typeTextWithCounter("4x7")
        clickEqualsWithCounter()
        checkState("28", fullNumbersLists[updateCounter], fullOperatorsLists[updateCounter])
        clickClearWithCounter()
        checkState("", fullNumbersLists[updateCounter], fullOperatorsLists[updateCounter])
    }

    @Test
    fun computeSingleNumber() {
        val fullNumbersLists = numbersLists * 2
        val fullOperatorsLists = operatorsLists * 2
        mockRandomEnabler(fullNumbersLists.flatten(), fullOperatorsLists.flatten())
        launchFragment()

        typeTextWithCounter("4")
        checkState("4", fullNumbersLists[updateCounter], fullOperatorsLists[updateCounter])
        clickEqualsWithCounter()
        checkState("4", fullNumbersLists[updateCounter], fullOperatorsLists[updateCounter])

        typeTextWithCounter("6")
        checkState("46", fullNumbersLists[updateCounter], fullOperatorsLists[updateCounter])
        clickEqualsWithCounter()
        checkState("46", fullNumbersLists[updateCounter], fullOperatorsLists[updateCounter])

        clickClearWithCounter()
        typeTextWithCounter("51")
        checkState("51", fullNumbersLists[updateCounter], fullOperatorsLists[updateCounter])
        clickEqualsWithCounter()
        checkState("51", fullNumbersLists[updateCounter], fullOperatorsLists[updateCounter])

        clickClearWithCounter()
        typeTextWithCounter("03")
        checkState("03", fullNumbersLists[updateCounter], fullOperatorsLists[updateCounter])
        clickEqualsWithCounter()
        checkState("3", fullNumbersLists[updateCounter], fullOperatorsLists[updateCounter])
    }

    @Test
    fun compute() {
        val fullNumbersLists = numbersLists * 5
        val fullOperatorsLists = operatorsLists * 5
        mockRandomEnabler(fullNumbersLists.flatten(), fullOperatorsLists.flatten())
        launchFragment()

        typeTextWithCounter("4+6") // four, six, plus
        checkState("4+6", fullNumbersLists[updateCounter], fullOperatorsLists[updateCounter])
        clickEqualsWithCounter()
        checkState("10", fullNumbersLists[updateCounter], fullOperatorsLists[updateCounter])

        typeTextWithCounter("/6") // div
        checkState("10/6", fullNumbersLists[updateCounter], fullOperatorsLists[updateCounter])
        clickEqualsWithCounter()
        checkState("1", fullNumbersLists[updateCounter], fullOperatorsLists[updateCounter])

        typeTextWithCounter("-12") // one, two, minus
        checkState("1-12", fullNumbersLists[updateCounter], fullOperatorsLists[updateCounter])
        clickEqualsWithCounter()
        checkState("-11", fullNumbersLists[updateCounter], fullOperatorsLists[updateCounter])

        typeTextWithCounter("x78") // seven, eight, times
        checkState("-11x78", fullNumbersLists[updateCounter], fullOperatorsLists[updateCounter])
        clickEqualsWithCounter()
        checkState("-858", fullNumbersLists[updateCounter], fullOperatorsLists[updateCounter])

        // multi operator
        clickClearWithCounter()
        typeTextWithCounter("15+6/2") // five
        checkState("15+6/2", fullNumbersLists[updateCounter], fullOperatorsLists[updateCounter])
        clickEqualsWithCounter()
        checkState("18", fullNumbersLists[updateCounter], fullOperatorsLists[updateCounter])

        clickClearWithCounter()
        typeTextWithCounter("2+4x6-93/10") // zero, three, nine
        checkState("2+4x6-93/10", fullNumbersLists[updateCounter], fullOperatorsLists[updateCounter])
        clickEqualsWithCounter()
        checkState("17", fullNumbersLists[updateCounter], fullOperatorsLists[updateCounter])
    }

    @Test
    fun computeError() {
        val fullNumbersLists = numbersLists * 6
        val fullOperatorsLists = operatorsLists * 6
        mockRandomEnabler(fullNumbersLists.flatten(), fullOperatorsLists.flatten())

        fun typeAndCheckError(text: String, error: String) {
            typeTextWithCounter(text)
            clickEqualsWithCounter()
            checkErrorState(error)
            clickClearWithCounter()
            checkState("", fullNumbersLists[updateCounter], fullOperatorsLists[updateCounter])
        }

        launchFragment()

        // syntax error
        var expectedError = "Err: Syntax Error"
        typeAndCheckError("+", expectedError)
        typeAndCheckError("12+6//2", expectedError)
        typeAndCheckError("-5", expectedError)

        // divide by zero
        expectedError = "Err: Divide by 0"
        typeAndCheckError("6/0", expectedError)
        typeAndCheckError("6+3/0-2", expectedError)

        // overflow
        expectedError = "Err: Overflow value"
        typeAndCheckError(Int.MAX_VALUE.toString() + "000", expectedError)
    }

    @Test
    fun recreate() {
        val fullNumbersLists = numbersLists * 2
        val fullOperatorsLists = operatorsLists * 2

        // insert duplicate values to be used when the fragment is recreated
        val mockNumbersLists = fullNumbersLists.toMutableList()
        val mockOperatorsLists = fullOperatorsLists.toMutableList()
        val recreateIndices = listOf(0, 2, 5, 6, 7, 8, 10).sortedDescending()
        recreateIndices.forEach {
            mockNumbersLists.add(it, fullNumbersLists[it])
            mockOperatorsLists.add(it, fullOperatorsLists[it])
        }

        mockRandomEnabler(mockNumbersLists.flatten(), mockOperatorsLists.flatten())
        val scenario = launchFragment()

        checkState("", fullNumbersLists[updateCounter], fullOperatorsLists[updateCounter])
        scenario.recreate()
        checkState("", fullNumbersLists[updateCounter], fullOperatorsLists[updateCounter])

        // text
        typeTextWithCounter("03")
        checkState("03", fullNumbersLists[updateCounter], fullOperatorsLists[updateCounter])
        scenario.recreate()
        checkState("03", fullNumbersLists[updateCounter], fullOperatorsLists[updateCounter])

        typeTextWithCounter("x5/")
        checkState("03x5/", fullNumbersLists[updateCounter], fullOperatorsLists[updateCounter])
        scenario.recreate()
        checkState("03x5/", fullNumbersLists[updateCounter], fullOperatorsLists[updateCounter])

        clickBackspaceWithCounter()
        checkState("03x5", fullNumbersLists[updateCounter], fullOperatorsLists[updateCounter])
        scenario.recreate()
        checkState("03x5", fullNumbersLists[updateCounter], fullOperatorsLists[updateCounter])

        // eq value
        clickEqualsWithCounter()
        checkState("15", fullNumbersLists[updateCounter], fullOperatorsLists[updateCounter])
        scenario.recreate()
        checkState("15", fullNumbersLists[updateCounter], fullOperatorsLists[updateCounter])

        // cleared
        clickClearWithCounter()
        checkState("", fullNumbersLists[updateCounter], fullOperatorsLists[updateCounter])
        scenario.recreate()
        checkState("", fullNumbersLists[updateCounter], fullOperatorsLists[updateCounter])

        // error
        typeTextWithCounter("+")
        clickEqualsWithCounter()
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

    // wrapper to update current counter when text is typed
    private fun typeTextWithCounter(text: String) {
        typeText(text)
        updateCounter += text.length
    }

    // wrapper to update current counter when equals is pressed
    private fun clickEqualsWithCounter() {
        clickEquals()
        updateCounter++
    }

    // wrapper to update current counter when clear is pressed
    private fun clickClearWithCounter() {
        clickClear()
        updateCounter++
    }

    // wrapper to update current counter when backspace is pressed
    private fun clickBackspaceWithCounter() {
        clickBackspace()
        updateCounter++
    }

    // TODO move to kotlin-utils
    private operator fun <T> List<T>.times(other: Int) = List(other) { this }.flatten()
}
