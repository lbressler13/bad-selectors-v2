package xyz.lbres.badselectorsv2.devtools

import androidx.test.espresso.DataInteraction
import androidx.test.espresso.Espresso.onData
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.PerformException
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.RootMatchers.isPlatformPopup
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withSpinnerText
import androidx.test.espresso.matcher.ViewMatchers.withText
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.hamcrest.core.AllOf.allOf
import org.hamcrest.core.Is.`is`
import org.hamcrest.core.IsInstanceOf.instanceOf
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import xyz.lbres.badselectorsv2.BaseActivity
import xyz.lbres.badselectorsv2.R
import xyz.lbres.badselectorsv2.testutils.rules.RetryRule

@RunWith(AndroidJUnit4::class)
class DeveloperToolsDialogHideTest {
    @Rule
    @JvmField
    val activityRule = ActivityScenarioRule(BaseActivity::class.java)

    @Rule
    @JvmField
    val retryRule = RetryRule()

    @Test
    fun hideDevTools() {
        val spinner = onView(withId(R.id.devToolsTimeSpinner))
        onView(withId(R.id.devToolsButton)).perform(click())

        spinner.check(matches(withSpinnerText("5000ms"))).perform(click())

        spinnerItemAt(0).check(matches(allOf(isDisplayed(), withText("5000ms"))))
        spinnerItemAt(1).check(matches(allOf(isDisplayed(), withText("10000ms"))))
        spinnerItemAt(2).check(matches(allOf(isDisplayed(), withText("30000ms"))))
        spinnerItemAt(3).check(matches(allOf(isDisplayed(), withText("60000ms"))))

        var performException = false
        try {
            spinnerItemAt(4).check(matches(isDisplayed()))
        } catch (_: PerformException) {
            performException = true
        }

        if (!performException) {
            throw PerformException.Builder()
                .withCause(IllegalStateException("Dev tools spinner has too many options"))
                .build()
        }
    }

    private fun spinnerItemAt(position: Int): DataInteraction {
        return onData(`is`(instanceOf(String::class.java)))
            .inRoot(isPlatformPopup())
            .atPosition(position)
    }
}
