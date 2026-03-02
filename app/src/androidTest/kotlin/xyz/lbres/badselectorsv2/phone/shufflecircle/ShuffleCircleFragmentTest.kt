package xyz.lbres.badselectorsv2.phone.shufflecircle

import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.isClickable
import androidx.test.espresso.matcher.ViewMatchers.isCompletelyDisplayed
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.isEnabled
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withText
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.hamcrest.Matchers.allOf
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import xyz.lbres.badselectorsv2.BaseActivity
import xyz.lbres.badselectorsv2.R
import xyz.lbres.badselectorsv2.testutils.createGetLocalView
import xyz.lbres.badselectorsv2.testutils.matchers.atIndex
import xyz.lbres.badselectorsv2.testutils.matchers.withTab
import xyz.lbres.badselectorsv2.testutils.rules.RetryRule
import xyz.lbres.badselectorsv2.testutils.viewassertions.isNotPresented

@RunWith(AndroidJUnit4::class)
class ShuffleCircleFragmentTest {
    private val getLocalView = createGetLocalView(R.id.phoneShuffleCircleRoot)

    @Rule
    @JvmField
    val rule = ActivityScenarioRule(BaseActivity::class.java)

    @Rule
    @JvmField
    val retryRule = RetryRule()

    @Before
    fun setupTest() {
        onView(withId(R.id.navigationPhone)).perform(click())
    }

    @Test
    fun screenTitle() {
        onView(withText("Bad Phone Selectors")).check(matches(isDisplayed()))
        onView(withTab("Shuffle Circle")).check(matches(isCompletelyDisplayed()))
    }

    @Test
    fun initialUi() {
        val buttonEnabled = matches(allOf(isDisplayed(), isEnabled(), isClickable()))
        getLocalView(R.id.selectButton).check(buttonEnabled)
        getLocalView(R.id.restartButton).check(isNotPresented())
        getLocalView(R.id.currentDigit).check(matches(withText("")))

        val digitBlank = matches(allOf(isDisplayed(), withText("_")))
        getLocalView(R.id.digit0).check(digitBlank)
        getLocalView(R.id.digit1).check(digitBlank)
        getLocalView(R.id.digit2).check(digitBlank)
        getLocalView(R.id.digit3).check(digitBlank)
        getLocalView(R.id.digit4).check(digitBlank)
        getLocalView(R.id.digit5).check(digitBlank)
        getLocalView(R.id.digit6).check(digitBlank)
        getLocalView(R.id.digit7).check(digitBlank)
        getLocalView(R.id.digit8).check(digitBlank)
        getLocalView(R.id.digit9).check(digitBlank)

        // check circle buttons
        repeat(10) {
            onView(atIndex(withId(R.id.circleLayout), it)).check(buttonEnabled)
        }
        var threwException = false
        try {
            onView(atIndex(withId(R.id.circleLayout), 10)).check(matches(isDisplayed()))
        } catch (_: Exception) {
            threwException = true
        }
        assertTrue(threwException)
    }
}
