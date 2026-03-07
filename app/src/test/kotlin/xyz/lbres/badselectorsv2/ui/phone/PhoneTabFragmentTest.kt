package xyz.lbres.badselectorsv2.ui.phone

import androidx.test.core.app.ActivityScenario
import androidx.test.espresso.Espresso
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.assertion.ViewAssertions
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
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

@RunWith(AndroidJUnit4::class)
class PhoneTabFragmentTest {
    var scenario: ActivityScenario<BaseActivity>? = null

    @Before
    fun setupTest() {
        scenario = ActivityScenario.launchActivityForResult(BaseActivity::class.java)
        Espresso.onView(ViewMatchers.withId(R.id.navigationPhone)).perform(ViewActions.click())
    }

    @After
    fun cleanupTest() {
        scenario = null
    }

    @Test
    fun initialUi() {
        actionBar.check(ViewAssertions.matches(withTitle("Bad Phone Selectors")))
        Espresso.onView(withTab("Shuffle Circle"))
            .check(ViewAssertions.matches(ViewMatchers.isCompletelyDisplayed()))
        testNavbarUi(R.id.navigationPhone, "Phone")
    }

    @Test
    fun navigateToHome() = testNavigateToHome()
    @Test
    fun navigateToSelf() = testNavigateToPhone()
    @Test
    fun navigateToCalc() = testNavigateToCalc()
    @Test
    fun navigateToDate() = testNavigateToDate()
    @Test
    fun navigateToOtp() = testNavigateToOtp()
}
