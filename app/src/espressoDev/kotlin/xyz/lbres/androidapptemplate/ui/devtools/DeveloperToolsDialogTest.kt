package xyz.lbres.androidapptemplate.ui.devtools

import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withSpinnerText
import androidx.test.espresso.matcher.ViewMatchers.withText
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.hamcrest.Matchers.allOf
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import xyz.lbres.androidapptemplate.BaseActivity
import xyz.lbres.androidapptemplate.R
import xyz.lbres.androidapptemplate.testutils.openDevTools
import xyz.lbres.androidapptemplate.testutils.rules.RetryRule

@RunWith(AndroidJUnit4::class)
class DeveloperToolsDialogTest {
    @Rule
    @JvmField
    val activityRule = ActivityScenarioRule(BaseActivity::class.java)

    @Rule
    @JvmField
    val retryRule = RetryRule()

    @Test
    fun loadInitialUi() {
        openDevTools()

        onView(withText("Developer Tools")).check(matches(isDisplayed()))

        onView(withId(R.id.refreshUIButton))
            .check(matches(allOf(isDisplayed(), withText("Refresh UI"))))
        onView(withId(R.id.hideDevToolsButton))
            .check(matches(allOf(isDisplayed(), withText("Hide dev tools"))))

        onView(withId(R.id.devToolsTimeSpinner))
            .check(matches(allOf(isDisplayed(), withSpinnerText("5000ms"))))
    }

    @Test
    fun refreshUI() {
        openDevTools()
        onView(withId(R.id.refreshUIButton)).perform(click())
        onView(withText("Developer Tools")).check(matches(isDisplayed()))
    }

    @Test fun hideDevToolsOptionsDisplayed() = testHideDevToolsOptionsDisplayed()

    @Test fun interactWithHideDevToolsSpinner() = testInteractWithHideDevToolsSpinner()

    @Test fun hideDevTools() = testHideDevTools()

    @Test
    fun attributionsFragment() {
        onView(withId(R.id.infoButton)).perform(click())

        openDevTools()
        onView(withText("Developer Tools")).check(matches(isDisplayed()))
    }
}
