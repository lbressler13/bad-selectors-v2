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

    @Test
    fun navigateHome() {
        onView(withId(R.id.navigationHome)).perform(click())
        actionBar.check(matches(withTitle("Bad Selectors")))
    }

    @Test
    fun navigateToPhone() {
        onView(withId(R.id.navigationPhone)).perform(click())
        actionBar.check(matches(withTitle("Bad Phone Selectors")))
    }

    @Test
    fun navigateToDate() {
        onView(withId(R.id.navigationDate)).perform(click())
        actionBar.check(matches(withTitle("Bad Date Selectors")))
    }

    @Test
    fun navigateToSelf() {
        onView(withId(R.id.navigationCalc)).perform(click())
        onView(withText("Bad Calculators")).check(matches(isDisplayed()))
    }
}
