package xyz.lbres.badselectorsv2.ui.testutils

import android.view.View
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.contrib.RecyclerViewActions.actionOnItem
import androidx.test.espresso.contrib.RecyclerViewActions.scrollTo
import androidx.test.espresso.matcher.RootMatchers.isDialog
import androidx.test.espresso.matcher.ViewMatchers.hasDescendant
import androidx.test.espresso.matcher.ViewMatchers.isEnabled
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withText
import org.hamcrest.Matcher
import org.hamcrest.Matchers.not
import xyz.lbres.badselectorsv2.R
import xyz.lbres.badselectorsv2.home.selectorgroup.SelectorGroupViewHolder
import xyz.lbres.badselectorsv2.ui.testutils.viewactions.actionOnChildWithId
import xyz.lbres.badselectorsv2.ui.testutils.viewactions.forceClick
import xyz.lbres.kotlinutils.general.simpleIf

private typealias VH = SelectorGroupViewHolder

/**
 * Check that a view is disabled
 */
fun isDisabled() = not(isEnabled())

/**
 * Matcher to check if view is either enabled or disabled
 *
 * @param enabled [Boolean]: true for enabled matcher, false for disabled matcher
 */
fun enabledMatcher(enabled: Boolean) = simpleIf(enabled, isEnabled(), isDisabled())

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

/**
 * Open dialog through settings button
 */
fun openSettingsDialog() {
    onView(withId(R.id.settingsButton)).perform(forceClick())
}

/**
 * Close dialog
 */
fun closeDialog() {
    onView(withText("Done")).perform(click())
}
