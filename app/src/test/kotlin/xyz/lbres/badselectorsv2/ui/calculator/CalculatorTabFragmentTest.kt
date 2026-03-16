package xyz.lbres.badselectorsv2.ui.calculator

import androidx.test.core.app.ActivityScenario
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.action.ViewActions.swipeRight
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.isCompletelyDisplayed
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
import xyz.lbres.badselectorsv2.ui.testutils.actionBar
import xyz.lbres.badselectorsv2.ui.testutils.matchers.withTab
import xyz.lbres.badselectorsv2.ui.testutils.matchers.withTitle
import xyz.lbres.badselectorsv2.ui.testutils.testNavbarUi
import xyz.lbres.badselectorsv2.ui.testutils.testNavigateToCalc
import xyz.lbres.badselectorsv2.ui.testutils.testNavigateToDate
import xyz.lbres.badselectorsv2.ui.testutils.testNavigateToHome
import xyz.lbres.badselectorsv2.ui.testutils.testNavigateToOtp
import xyz.lbres.badselectorsv2.ui.testutils.testNavigateToPhone

@Category(Robolectric::class)
@RunWith(AndroidJUnit4::class)
class CalculatorTabFragmentTest {
    var scenario: ActivityScenario<BaseActivity>? = null

    @Before
    fun setupTest() {
        scenario = ActivityScenario.launchActivityForResult(BaseActivity::class.java)
        onView(withId(R.id.navigationCalc)).perform(click())
    }

    @After
    fun cleanupTest() {
        scenario = null
    }

    @Test
    fun initialUi() {
        actionBar.check(matches(withTitle("Bad Calculators")))
        onView(withTab("Single Operation")).check(matches(isCompletelyDisplayed()))
        testNavbarUi(R.id.navigationCalc, "Calculator")
    }

    @Test
    fun tabs() {
        onView(withId(R.id.viewPager)).perform(swipeRight())
        onView(withTab("Random Enabled")).check(matches(isCompletelyDisplayed()))
    }

    @Test fun navigateToHome() = testNavigateToHome()
    @Test fun navigateToPhone() = testNavigateToPhone()
    @Test fun navigateToSelf() = testNavigateToCalc()
    @Test fun navigateToDate() = testNavigateToDate()
    @Test fun navigateToOtp() = testNavigateToOtp()
}
