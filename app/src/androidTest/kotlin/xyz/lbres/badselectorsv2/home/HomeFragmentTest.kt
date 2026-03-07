package xyz.lbres.badselectorsv2.home

import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.contrib.RecyclerViewActions.actionOnItemAtPosition
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import xyz.lbres.badselectorsv2.BaseActivity
import xyz.lbres.badselectorsv2.R
import xyz.lbres.badselectorsv2.home.selectorgroup.SelectorGroupViewHolder
import xyz.lbres.badselectorsv2.testutils.actionBar
import xyz.lbres.badselectorsv2.testutils.matchers.withTitle
import xyz.lbres.badselectorsv2.testutils.rules.RetryRule
import xyz.lbres.badselectorsv2.testutils.testNavigateToCalc
import xyz.lbres.badselectorsv2.testutils.testNavigateToDate
import xyz.lbres.badselectorsv2.testutils.testNavigateToHome
import xyz.lbres.badselectorsv2.testutils.testNavigateToOtp
import xyz.lbres.badselectorsv2.testutils.testNavigateToPhone
import xyz.lbres.badselectorsv2.testutils.viewactions.forceClick

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
        actionBar.check(matches(withTitle("Bad Selectors")))
    }

    @Test
    fun initialUi() {
        // TODO check selector groups
        onView(withId(R.id.navigationHome)).check(matches(isDisplayed()))
        onView(withId(R.id.navigationPhone)).check(matches(isDisplayed()))

        // onView(withId(R.id.navbar)).check(matches)
    }

    @Test
    fun attributionsFragment() {
        onView(withId(R.id.infoButton)).perform(click())
        actionBar.check(matches(withTitle("Give People Credit")))
    }

    @Test fun navigateToSelf() = testNavigateToHome()
    @Test fun navigateToPhone() = testNavigateToPhone()
    @Test fun navigateToCalc() = testNavigateToCalc()
    @Test fun navigateToDate() = testNavigateToDate()
    @Test fun navigateToOtp() = testNavigateToOtp()

    @Test fun navigateWithPhoneSelectors() = testNavigateWithPhoneSelectors()

    @Test
    fun navigateWithCalcLabel() {
        val selectorGroupRecycler = onView(withId(R.id.selectorGroupRecycler))
        selectorGroupRecycler.perform(actionOnItemAtPosition<SelectorGroupViewHolder>(2, forceClick()))
        actionBar.check(matches(withTitle("Bad Calculators")))
    }

    @Test
    fun navigateWithDateLabel() {
        val selectorGroupRecycler = onView(withId(R.id.selectorGroupRecycler))
        selectorGroupRecycler.perform(actionOnItemAtPosition<SelectorGroupViewHolder>(1, forceClick()))
        actionBar.check(matches(withTitle("Bad Date Selectors")))
    }

    @Test
    fun navigateWithOtpLabel() {
        val selectorGroupRecycler = onView(withId(R.id.selectorGroupRecycler))
        selectorGroupRecycler.perform(actionOnItemAtPosition<SelectorGroupViewHolder>(3, forceClick()))
        actionBar.check(matches(withTitle("Bad OTP Receivers")))
    }

    @Test fun expandCollapseSelectors() = testExpandCollapseSelectors()
    @Test fun expansionsPersistedOnLeave() = testExpansionsPersistedOnLeave()
}
