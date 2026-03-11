package xyz.lbres.badselectorsv2.ui.testutils

import android.content.Intent
import android.view.View
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.contrib.RecyclerViewActions.actionOnItem
import androidx.test.espresso.contrib.RecyclerViewActions.scrollTo
import androidx.test.espresso.intent.Intents.getIntents
import androidx.test.espresso.matcher.RootMatchers.isDialog
import androidx.test.espresso.matcher.ViewMatchers.hasDescendant
import androidx.test.espresso.matcher.ViewMatchers.isEnabled
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withText
import org.hamcrest.Matcher
import org.hamcrest.Matchers.not
import org.junit.Assert.assertEquals
import xyz.lbres.badselectorsv2.R
import xyz.lbres.badselectorsv2.home.selectorgroup.SelectorGroupViewHolder
import xyz.lbres.badselectorsv2.ui.testutils.viewactions.actionOnChildWithId
import xyz.lbres.badselectorsv2.ui.testutils.viewactions.forceClick
import java.lang.AssertionError

private typealias VH = SelectorGroupViewHolder

/**
 * Assert that the correct number of link clicks have occurred, and that the most recent has the correct url.
 *
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
 * Check that a view is disabled
 */
fun isDisabled() = not(isEnabled())

/**
 * Match a view within a dialog
 */
fun onViewInDialog(matcher: Matcher<View>) = onView(matcher).inRoot(isDialog())

/**
 * Navigate to a given selector from the home screen
 *
 * @param groupName [String]: name of group
 * @param selectorName [String]: name of selector
 */
fun navigateToSelector(groupName: String, selectorName: String) {
    val clickExpandCollapse = actionOnChildWithId(R.id.expandCollapseButton, click())
    val selectorGroupRecycler = onView(withId(R.id.selectorGroupRecycler))
    val vhMatcher = hasDescendant(withText(groupName))

    selectorGroupRecycler
        .perform(scrollTo<VH>(vhMatcher))
        .perform(actionOnItem<VH>(vhMatcher, clickExpandCollapse))
    onView(withText(selectorName)).perform(forceClick())
}
