package xyz.lbres.badselectorsv2.phone

import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.isCompletelyDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import xyz.lbres.badselectorsv2.BaseActivity
import xyz.lbres.badselectorsv2.R
import xyz.lbres.badselectorsv2.testutils.actionBar
import xyz.lbres.badselectorsv2.testutils.matchers.withTab
import xyz.lbres.badselectorsv2.testutils.matchers.withTitle
import xyz.lbres.badselectorsv2.testutils.rules.RetryRule
import xyz.lbres.badselectorsv2.testutils.testNavigateToCalc
import xyz.lbres.badselectorsv2.testutils.testNavigateToDate
import xyz.lbres.badselectorsv2.testutils.testNavigateToHome
import xyz.lbres.badselectorsv2.testutils.testNavigateToOtp
import xyz.lbres.badselectorsv2.testutils.testNavigateToPhone

// TODO check selected navbar icon

@RunWith(AndroidJUnit4::class)
class PhoneTabFragmentTest {
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
    fun initialUi() {
        actionBar.check(matches(withTitle("Bad Phone Selectors")))
        onView(withTab("Shuffle Circle")).check(matches(isCompletelyDisplayed()))
    }

    @Test fun navigateToHome() = testNavigateToHome()
    @Test fun navigateToSelf() = testNavigateToPhone()
    @Test fun navigateToCalc() = testNavigateToCalc()
    @Test fun navigateToDate() = testNavigateToDate()
    @Test fun navigateToOtp() = testNavigateToOtp()
}
