package xyz.lbres.badselectorsv2.calculator

import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
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
import xyz.lbres.badselectorsv2.testutils.actionBar
import xyz.lbres.badselectorsv2.testutils.matchers.withTitle
import xyz.lbres.badselectorsv2.testutils.rules.RetryRule
import xyz.lbres.badselectorsv2.testutils.testNavigateToCalc
import xyz.lbres.badselectorsv2.testutils.testNavigateToDate
import xyz.lbres.badselectorsv2.testutils.testNavigateToHome
import xyz.lbres.badselectorsv2.testutils.testNavigateToOtp
import xyz.lbres.badselectorsv2.testutils.testNavigateToPhone

@RunWith(AndroidJUnit4::class)
class CalculatorTabFragmentTest {
    @Rule
    @JvmField
    val rule = ActivityScenarioRule(BaseActivity::class.java)

    @Rule
    @JvmField
    val retryRule = RetryRule()

    @Before
    fun setupTest() {
        onView(withId(R.id.navigationCalc)).perform(click())
    }

    @Test
    fun initialUi() {
        actionBar.check(matches(withTitle("Bad Calculators")))
        onView(withText("Under construction!")).check(matches(isDisplayed()))
    }

    @Test fun navigateToHome() = testNavigateToHome()
    @Test fun navigateToPhone() = testNavigateToPhone()
    @Test fun navigateToSelf() = testNavigateToCalc()
    @Test fun navigateToDate() = testNavigateToDate()
    @Test fun navigateToOtp() = testNavigateToOtp()
}
