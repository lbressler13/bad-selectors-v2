package xyz.lbres.badselectorsv2.ui.home

import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withText
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import xyz.lbres.badselectorsv2.BaseActivity
import xyz.lbres.badselectorsv2.R
import xyz.lbres.badselectorsv2.testutils.matchers.withTitle
import xyz.lbres.badselectorsv2.testutils.rules.RetryRule

@RunWith(AndroidJUnit4::class)
class HomeFragmentTest {

    @Rule
    @JvmField
    val rule = ActivityScenarioRule(BaseActivity::class.java)

    @Rule
    @JvmField
    val retryRule = RetryRule()

    @Test
    fun actionBarTitle() {
        val expectedTitle = "Bad Selectors"
        onView(withId(R.id.actionBar)).check(matches(withTitle(expectedTitle)))
    }

    @Test
    fun initialUi() {
        onView(withText("Under construction!")).check(matches(isDisplayed()))
        onView(withId(R.id.navigation_home)).check(matches(isDisplayed()))
    }

    @Test
    fun attributionsFragment() {
        onView(withId(R.id.infoButton)).perform(click())
        onView(withText("Give People Credit")).check(matches(isDisplayed()))
    }
}
