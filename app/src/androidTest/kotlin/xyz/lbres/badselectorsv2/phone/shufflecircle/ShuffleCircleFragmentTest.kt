package xyz.lbres.badselectorsv2.phone.shufflecircle

import androidx.fragment.app.testing.launchFragmentInContainer
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.isClickable
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
import xyz.lbres.badselectorsv2.testutils.isDisabled
import xyz.lbres.badselectorsv2.testutils.matchers.atIndex
import xyz.lbres.badselectorsv2.testutils.viewassertions.isNotPresented
import xyz.lbres.kotlinutils.list.IntList

@RunWith(AndroidJUnit4::class)
class ShuffleCircleFragmentTest {
    private val selectButton = onView(withId(R.id.selectButton))
    private val restartButton = onView(withId(R.id.restartButton))
    private val currentDigit = onView(withId(R.id.currentDigit))
    private val circleButton = { idx: Int -> onView(atIndex(withId(R.id.circleLayout), idx))}
    private val digitViews = listOf(
        onView(withId(R.id.digit0)),
        onView(withId(R.id.digit1)),
        onView(withId(R.id.digit2)),
        onView(withId(R.id.digit3)),
        onView(withId(R.id.digit4)),
        onView(withId(R.id.digit5)),
        onView(withId(R.id.digit6)),
        onView(withId(R.id.digit7)),
        onView(withId(R.id.digit8)),
        onView(withId(R.id.digit9)),
    )

    @After
    fun cleanupTest() {
        unmockkAll()
    }

    @Test
    fun initialUi() {
        launchFragmentInContainer<ShuffleCircleFragment>()
        selectButton.check(matches(allOf(isDisplayed(), isEnabled())))
        restartButton.check(isNotPresented())
        currentDigit.check(matches(withText("")))

        digitViews.forEach { it.check(matches(allOf(isDisplayed(), withText("_")))) }

        // check circle buttons
        repeat(10) { circleButton(it).check(matches(allOf(isDisplayed(), isEnabled()))) }
        // validate only 10 buttons
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
        val returnValues = listOf(1, 4, 0, 2, 4, 5, 8, 9, 3, 2)
        mockDigitShuffler(returnValues = returnValues)

        launchFragmentInContainer<ShuffleCircleFragment>()
        repeat(10) {
            Thread.sleep(2000)
            circleButton(0).perform(click())
            selectButton.check(matches(isClickable()))
            // circleButton(0).perform(click())
            Thread.sleep(2000)

            onView(withId(R.id.selectButton)).perform(click())
        }

        restartButton.check(matches(allOf(isDisplayed(), isEnabled())))
        selectButton.check(matches(allOf(isDisplayed(), isDisabled())))
        repeat(10) {
            circleButton(it).check(matches(allOf(isDisplayed(), isDisabled())))
        }
        digitViews.forEachIndexed { index, digit ->
            val expectedText = returnValues[index].toString()
            digit.check(matches(withText(expectedText)))
        }
    }

    @Test
    fun restart() {
        mockDigitShuffler(returnValue = 5)
        launchFragmentInContainer<ShuffleCircleFragment>()
        repeat(10) {
            circleButton(0).perform(click())
            selectButton.perform(click())
        }
        restartButton.perform(click())

        selectButton.check(matches(allOf(isDisplayed(), isEnabled())))
        restartButton.check(isNotPresented())
        currentDigit.check(matches(withText("")))

        digitViews.forEach { it.check(matches(allOf(isDisplayed(), withText("_")))) }
        repeat(10) { circleButton(it).check(matches(allOf(isDisplayed(), isEnabled()))) }
    }

    private fun mockDigitShuffler(returnValue: Int? = null, returnValues: IntList? = null) {
        mockkConstructor(DigitShuffler::class)
        if (returnValue != null) {
            every { constructedWith<DigitShuffler>().getAtIndex(any(), any()) } returns returnValue
        } else if (returnValues != null) {
            every { constructedWith<DigitShuffler>().getAtIndex(any(), any()) } returnsMany returnValues
        }
        justRun { constructedWith<DigitShuffler>().update() }
    }
}
