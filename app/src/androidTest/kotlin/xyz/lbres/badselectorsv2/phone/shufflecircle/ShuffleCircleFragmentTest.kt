package xyz.lbres.badselectorsv2.phone.shufflecircle

import androidx.fragment.app.testing.launchFragmentInContainer
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.isEnabled
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withText
import androidx.test.ext.junit.runners.AndroidJUnit4
import io.mockk.every
import io.mockk.justRun
import io.mockk.mockkConstructor
import io.mockk.unmockkAll
import org.hamcrest.Matchers.allOf
import org.junit.After
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import xyz.lbres.badselectorsv2.R
import xyz.lbres.badselectorsv2.phone.checkPhoneNumber
import xyz.lbres.badselectorsv2.testutils.isDisabled
import xyz.lbres.badselectorsv2.testutils.matchers.atIndex
import xyz.lbres.badselectorsv2.testutils.viewactions.forceClick
import xyz.lbres.badselectorsv2.testutils.viewassertions.isNotPresented
import xyz.lbres.kotlinutils.list.IntList
import xyz.lbres.kotlinutils.list.listOfNulls
import xyz.lbres.kotlinutils.list.listOfValue

@RunWith(AndroidJUnit4::class)
class ShuffleCircleFragmentTest {
    private val selectButton = onView(withId(R.id.selectButton))
    private val restartButton = onView(withId(R.id.restartButton))
    private val currentDigit = onView(withId(R.id.currentDigit))
    private val circleButton = { idx: Int -> onView(atIndex(withId(R.id.circleLayout), idx)) }

    @After
    fun cleanupTest() {
        unmockkAll()
    }

    @Test
    fun initialUi() {
        launchFragmentInContainer<ShuffleCircleFragment>()
        checkInitialUi()

        // validate circle button count
        var threwException = false
        try {
            circleButton(10).check(matches(isDisplayed()))
        } catch (_: Exception) {
            threwException = true
        }
        assertTrue(threwException)
    }

    @Test
    fun completeNumber() {
        val turns = listOf(
            listOf(5 to 1, 3 to 2, 3 to 3, 6 to 1),
            listOf(7 to 4),
            listOf(0 to 9, 3 to 9, 1 to 0),
            listOf(2 to 1, 4 to 2),
            listOf(0 to 1, 7 to 3, 8 to 4),
            listOf(5 to 6, 7 to 2, 4 to 3, 0 to 8, 0 to 5),
            listOf(7 to 8, 6 to 3, 5 to 7),
            listOf(8 to 9, 7 to 8, 4 to 1, 2 to 2, 6 to 3, 9 to 9),
            listOf(9 to 6, 7 to 6, 4 to 6, 1 to 3),
            listOf(6 to 3, 4 to 8, 3 to 9, 1 to 1, 4 to 2, 5 to 2, 9 to 2, 1 to 3),
        )
        val returnValues = turns.flatMap { turn ->
            turn.map { it.second }
        }
        val phoneNumber = turns.map { it[it.lastIndex].second }

        mockDigitShuffler(returnValues = returnValues)

        launchFragmentInContainer<ShuffleCircleFragment>()
        turns.forEachIndexed { index, turn ->
            turn.forEach {
                val buttonIndex = it.first
                val value = it.second
                circleButton(buttonIndex).perform(forceClick())
                currentDigit.check(matches(withText(value.toString())))
            }
            selectButton.perform(forceClick())
            currentDigit.check(matches(withText("")))
            checkPhoneNumber(phoneNumber, 0..index)
        }

        checkRestartUi(phoneNumber)
    }

    @Test
    fun restart() {
        mockDigitShuffler(returnValue = 5)
        launchFragmentInContainer<ShuffleCircleFragment>()
        repeat(10) {
            circleButton(0).perform(forceClick())
            selectButton.perform(forceClick())
        }
        checkPhoneNumber(listOfValue(10, 5))

        restartButton.perform(forceClick())
        checkInitialUi()
    }

    @Test
    fun recreate() {
        val phoneNumber = listOf(0, 1, 2, 3, 4, 5, 6, 7, 8, 9)
        val returnValues = phoneNumber.subList(0, 2) + listOf(0) + phoneNumber.subList(2, 10)
        val digitPropValues = listOf(-1, -1, 0, 3, -1) // initial value + one for each recreate
        mockDigitShuffler(returnValues = returnValues)
        every { constructedWith<DigitShuffler>().digit } returnsMany digitPropValues

        val scenario = launchFragmentInContainer<ShuffleCircleFragment>()

        // set first 2 digits
        repeat(2) {
            circleButton(0).perform(forceClick())
            selectButton.perform(forceClick())
        }
        currentDigit.check(matches(withText("")))
        restartButton.check(isNotPresented())
        checkPhoneNumber(phoneNumber, 0..1)

        // check that blank digit is persisted
        scenario.recreate()
        currentDigit.check(matches(withText("")))
        restartButton.check(isNotPresented())
        checkPhoneNumber(phoneNumber, 0..1)

        // set currentDigit
        circleButton(0).perform(forceClick())
        currentDigit.check(matches(withText("0")))
        checkPhoneNumber(phoneNumber, 0..1)

        // check that digit is persisted
        scenario.recreate()
        currentDigit.check(matches(withText("0")))
        checkPhoneNumber(phoneNumber, 0..1)

        // get next digit
        circleButton(0).perform(forceClick())
        currentDigit.check(matches(withText("2")))
        selectButton.perform(forceClick()) // digit 3
        checkPhoneNumber(phoneNumber, 0..2)

        // use saved digit
        circleButton(0).perform(forceClick())
        scenario.recreate()
        selectButton.perform(forceClick()) // digit 4
        checkPhoneNumber(phoneNumber, 0..3)

        // finish phone number
        repeat(6) {
            circleButton(0).perform(forceClick())
            selectButton.perform(forceClick())
        }
        checkRestartUi(phoneNumber)

        // check restart persisted
        scenario.recreate()
        checkRestartUi(phoneNumber)

        // clear restart
        restartButton.perform(forceClick())
        scenario.recreate()
        checkInitialUi()
    }

    // mock DigitShuffler class
    private fun mockDigitShuffler(returnValue: Int? = null, returnValues: IntList? = null) {
        mockkConstructor(DigitShuffler::class)
        if (returnValue != null) {
            every { constructedWith<DigitShuffler>().getAtIndex(any(), any()) } returns returnValue
        } else if (returnValues != null) {
            every { constructedWith<DigitShuffler>().getAtIndex(any(), any()) } returnsMany returnValues
        }
        justRun { constructedWith<DigitShuffler>().update() }
    }

    private fun checkRestartUi(phoneNumber: List<Int>) {
        restartButton.check(matches(allOf(isDisplayed(), isEnabled())))
        selectButton.check(matches(allOf(isDisplayed(), isDisabled())))
        repeat(10) {
            circleButton(it).check(matches(allOf(isDisplayed(), isDisabled())))
        }
        checkPhoneNumber(phoneNumber)
    }

    private fun checkInitialUi() {
        selectButton.check(matches(allOf(isDisplayed(), isEnabled())))
        restartButton.check(isNotPresented())
        currentDigit.check(matches(withText("")))

        checkPhoneNumber(listOfNulls(10))
        repeat(10) { circleButton(it).check(matches(allOf(isDisplayed(), isEnabled()))) }
    }
}
