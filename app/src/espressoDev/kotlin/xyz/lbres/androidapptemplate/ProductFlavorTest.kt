package xyz.lbres.androidapptemplate

import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.isClickable
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withText
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.hamcrest.Matchers.allOf
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import xyz.lbres.androidapptemplate.testutils.rules.RetryRule

@RunWith(AndroidJUnit4::class)
class ProductFlavorTest {

    @Rule
    @JvmField
    val rule = ActivityScenarioRule(BaseActivity::class.java)

    @Rule
    @JvmField
    val retryRule = RetryRule()

    @Test
    fun devToolsButton() {
        onView(withId(R.id.devToolsButton)).check(matches(allOf(isDisplayed(), isClickable())))
    }

    @Test
    fun openDevTools() {
        onView(withId(R.id.devToolsButton)).perform(click())
        onView(withText("Developer Tools")).check(matches(isDisplayed()))
    }
}
