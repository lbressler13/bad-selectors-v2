package xyz.lbres.badselectorsv2.ui.home

import androidx.test.core.app.ActivityScenario
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.contrib.RecyclerViewActions.actionOnItemAtPosition
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.experimental.categories.Category
import org.junit.runner.RunWith
import org.robolectric.Robolectric
import xyz.lbres.badselectorsv2.BaseActivity
import xyz.lbres.badselectorsv2.R
import xyz.lbres.badselectorsv2.home.selectorgroup.SelectorGroupViewHolder
import xyz.lbres.badselectorsv2.ui.testutils.actionBar
import xyz.lbres.badselectorsv2.ui.testutils.matchers.withTitle
import xyz.lbres.badselectorsv2.ui.testutils.testNavbarUi
import xyz.lbres.badselectorsv2.ui.testutils.testNavigateToCalc
import xyz.lbres.badselectorsv2.ui.testutils.testNavigateToDate
import xyz.lbres.badselectorsv2.ui.testutils.testNavigateToHome
import xyz.lbres.badselectorsv2.ui.testutils.testNavigateToOtp
import xyz.lbres.badselectorsv2.ui.testutils.testNavigateToPhone
import xyz.lbres.badselectorsv2.ui.testutils.viewactions.forceClick

@Category(Robolectric::class)
@RunWith(AndroidJUnit4::class)
class HomeFragmentTest {
    var scenario: ActivityScenario<BaseActivity>? = null

    @Before
    fun setupTest() {
        scenario = ActivityScenario.launchActivityForResult(BaseActivity::class.java)
    }

    @After
    fun cleanupTest() {
        scenario = null
    }

    @Test
    fun actionBarTitle() {
        actionBar.check(matches(withTitle("Bad Selectors")))
    }

    @Test
    fun initialUi() {
        onView(withId(R.id.navigationHome)).check(matches(isDisplayed()))
        onView(withId(R.id.navigationPhone)).check(matches(isDisplayed()))
        testNavbarUi(R.id.navigationHome, "Home")
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
    @Test fun navigateWithDateSelectors() = testNavigateWithDateSelectors()
    @Test fun navigateWithCalcSelectors() = testNavigateWithCalcSelectors()
    @Test
    fun navigateWithOtpLabel() {
        val selectorGroupRecycler = onView(withId(R.id.selectorGroupRecycler))
        selectorGroupRecycler.perform(actionOnItemAtPosition<SelectorGroupViewHolder>(3, forceClick()))
        actionBar.check(matches(withTitle("Bad OTP Receivers")))
    }

    @Test fun expandCollapseSelectors() = testExpandCollapseSelectors()
    @Test fun expansionsPersistedOnLeave() = testExpansionsPersistedOnLeave()

    @Test
    fun recreate() {
        expandCollapseGroup(0)
        scenario!!.recreate()
        checkGroupsExpanded(listOf(0))
    }
}
