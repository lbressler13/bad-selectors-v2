package xyz.lbres.badselectorsv2.phone.shufflecircle

import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.isCompletelyDisplayed
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withText
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import xyz.lbres.badselectorsv2.BaseActivity
import xyz.lbres.badselectorsv2.R
import xyz.lbres.badselectorsv2.testutils.matchers.withTab
import xyz.lbres.badselectorsv2.testutils.rules.RetryRule

@RunWith(AndroidJUnit4::class)
class ShuffleCircleFragmentTestOld {
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

    // TODO put this in tab fragment tests
    @Test
    fun screenTitle() {
        onView(withText("Bad Phone Selectors")).check(matches(isDisplayed()))
        onView(withTab("Shuffle Circle")).check(matches(isCompletelyDisplayed()))
    }
}
