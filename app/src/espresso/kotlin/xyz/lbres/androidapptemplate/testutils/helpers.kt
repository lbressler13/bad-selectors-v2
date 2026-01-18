package xyz.lbres.androidapptemplate.testutils

import android.content.Context
import android.content.Intent
import androidx.test.espresso.intent.Intents.getIntents
import androidx.test.ext.junit.rules.ActivityScenarioRule
import org.junit.Assert.assertEquals
import xyz.lbres.androidapptemplate.BaseActivity
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
