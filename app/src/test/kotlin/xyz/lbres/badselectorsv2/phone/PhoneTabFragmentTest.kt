package xyz.lbres.badselectorsv2.phone

import androidx.test.core.app.ActivityScenario
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withText
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.runner.RunWith
import xyz.lbres.badselectorsv2.BaseActivity
import xyz.lbres.badselectorsv2.R
import kotlin.test.Test

@RunWith(AndroidJUnit4::class)
class PhoneTabFragmentTest {
    @Test
    fun initialUi() {
        val scenario = ActivityScenario.launchActivityForResult(BaseActivity::class.java)
        onView(withText("Bad Selectors")).check(matches(isDisplayed()))
        onView(withId(R.id.navigationPhone)).perform(click())
        onView(withText("Bad Phone Selectors")).check(matches(isDisplayed()))
    }
}
