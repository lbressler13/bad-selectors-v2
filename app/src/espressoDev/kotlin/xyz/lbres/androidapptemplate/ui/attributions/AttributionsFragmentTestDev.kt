package xyz.lbres.androidapptemplate.ui.attributions

import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.hasDescendant
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withText
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.hamcrest.Matchers.allOf
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import xyz.lbres.androidapptemplate.BaseActivity
import xyz.lbres.androidapptemplate.R
import xyz.lbres.androidapptemplate.testutils.doRefreshUI
import xyz.lbres.androidapptemplate.testutils.hideDevToolsButton
import xyz.lbres.androidapptemplate.testutils.matchers.matchesAtPosition
import xyz.lbres.androidapptemplate.testutils.rules.RetryRule
import xyz.lbres.androidapptemplate.ui.attributions.constants.authorAttributions

@RunWith(AndroidJUnit4::class)
class AttributionsFragmentTestDev {

    private val recyclerId = R.id.attributionsRecycler

    @Rule
    @JvmField
    val activityRule = ActivityScenarioRule(BaseActivity::class.java)

    @Rule
    @JvmField
    val retryRule = RetryRule()

    @Test
    fun refreshUI() {
        val authorTitles = authorAttributions.map { "Icon made by ${it.name} from www.flaticon.com" }

        // refresh with initial view (all collapsed)
        onView(withId(R.id.infoButton)).perform(click())
        doRefreshUI()

        onView(withId(R.id.expandCollapseMessage)).check(matches(withText("Expand")))
        authorTitles.indices.forEach {
            val withAuthorTitle = hasDescendant(withText(authorTitles[it]))
            onView(withId(recyclerId)).check(matches(matchesAtPosition(it, allOf(isDisplayed(), withAuthorTitle))))
        }
        checkImagesNotPresented(listOf(0, 1))

        // expand some
        hideDevToolsButton(0)
        onView(withId(R.id.expandCollapseMessage)).perform(click())
        expandCollapseAttribution(0)

        Thread.sleep(5000) // wait for dev tools to reappear after hide
        doRefreshUI()

        checkImagesDisplayed(listOf(0))
        checkImagesNotPresented(listOf(1))
        onView(withId(R.id.expandCollapseMessage)).check(matches(withText("Collapse")))
    }
}
