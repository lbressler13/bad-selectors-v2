package xyz.lbres.badselectorsv2.ui.otp

import androidx.test.core.app.ActivityScenario
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withText
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import xyz.lbres.badselectorsv2.BaseActivity
import xyz.lbres.badselectorsv2.R
import xyz.lbres.badselectorsv2.ui.testutils.actionBar
import xyz.lbres.badselectorsv2.ui.testutils.matchers.withTitle
import xyz.lbres.badselectorsv2.ui.testutils.testNavbarUi
import xyz.lbres.badselectorsv2.ui.testutils.testNavigateToCalc
import xyz.lbres.badselectorsv2.ui.testutils.testNavigateToDate
import xyz.lbres.badselectorsv2.ui.testutils.testNavigateToHome
import xyz.lbres.badselectorsv2.ui.testutils.testNavigateToOtp
import xyz.lbres.badselectorsv2.ui.testutils.testNavigateToPhone

@RunWith(AndroidJUnit4::class)
class OTPTabFragmentTest {
    var scenario: ActivityScenario<BaseActivity>? = null

    @Before
    fun setupTest() {
        scenario = ActivityScenario.launchActivityForResult(BaseActivity::class.java)
        onView(withId(R.id.navigationOtp)).perform(click())
    }

    @After
    fun cleanupTest() {
        scenario = null
    }

    @Test
    fun initialUi() {
        actionBar.check(matches(withTitle("Bad OTP Receivers")))
        onView(withText("Under construction!")).check(matches(isDisplayed()))
        testNavbarUi(R.id.navigationOtp, "OTP")
    }

    @Test fun navigateToHome() = testNavigateToHome()
    @Test fun navigateToPhone() = testNavigateToPhone()
    @Test fun navigateToCalc() = testNavigateToCalc()
    @Test fun navigateToDate() = testNavigateToDate()
    @Test fun navigateToSelf() = testNavigateToOtp()
}
