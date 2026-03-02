package xyz.lbres.badselectorsv2.testutils

import android.content.Context
import android.content.Intent
import android.view.View
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.ViewInteraction
import androidx.test.espresso.intent.Intents.getIntents
import androidx.test.espresso.matcher.ViewMatchers.isDescendantOfA
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.ext.junit.rules.ActivityScenarioRule
import org.hamcrest.Matcher
import org.hamcrest.Matchers.allOf
import org.junit.Assert.assertEquals
import xyz.lbres.badselectorsv2.BaseActivity
import java.lang.AssertionError

/**
 * Assert that the correct number of link clicks have occurred, and that the most recent has the correct url.
 *
 * @param url [String]: the expected url of last link
 * @param expectedLinkClicks [Int]: expected number of times that links have been clicked
 */
fun assertLinkOpened(url: String, expectedLinkClicks: Int) {
    val intents = getIntents().filter { it.action == Intent.ACTION_VIEW }

    if (intents.size != expectedLinkClicks) {
        throw AssertionError("Expected $expectedLinkClicks link clicks, found ${intents.size}")
    }

    val intent = intents.last()
    assertEquals(url, intent.dataString)
}

/**
 * Get the current activity context
 *
 * @param activityRule [ActivityScenarioRule]: test rule with the activity
 * @return [Context] context object
 */
fun getContextEspresso(activityRule: ActivityScenarioRule<BaseActivity>): Context {
    var context: Context? = null
    activityRule.scenario.onActivity {
        context = it.supportFragmentManager.fragments[0].requireContext()
    }

    return context!!
}

/**
 * Create a function which generates a ViewInteraction to find a view with a given ID,
 * within a root with a specified ID.
 *
 * @param rootId [Int]: the resource ID for the root
 * @return (Int) -> [ViewInteraction]
 */
fun createGetLocalView(rootId: Int): (Int) -> ViewInteraction {
    return { viewId ->
        val parentMatcher = isDescendantOfA(withId(rootId))
        val matchers: MutableList<Matcher<View>> = mutableListOf(parentMatcher, withId(viewId))
        onView(allOf(matchers))
    }
}
