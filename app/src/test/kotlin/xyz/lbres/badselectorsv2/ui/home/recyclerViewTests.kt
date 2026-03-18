package xyz.lbres.badselectorsv2.ui.home

import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.Espresso.pressBack
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.action.ViewActions.scrollTo
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.isCompletelyDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withText
import xyz.lbres.badselectorsv2.R
import xyz.lbres.badselectorsv2.ui.testutils.matchers.withTab

fun testExpandCollapseSelectors() {
    // start collapsed
    checkGroupsCollapsed(listOf(0))

    // expand
    expandCollapseGroup(0)
    checkGroupsExpanded(listOf(0))

    // collapse
    expandCollapseGroup(0)
    checkGroupsCollapsed(listOf(0))
}

fun testExpansionsPersistedOnLeave() {
    // using back button
    expandCollapseGroup(0)
    onView(withId(R.id.navigationPhone)).perform(click())

    pressBack()
    checkGroupsExpanded(listOf(0))

    // using buttons
    onView(withId(R.id.navigationPhone)).perform(click())

    onView(withId(R.id.navigationHome)).perform(click())
    checkGroupsExpanded(listOf(0))
}

fun testNavigateWithPhoneSelectors() {
    expandCollapseGroup(0)
    runSingleSelectorNavigationTest("Shuffle Circle")
}

fun testNavigateWithDateSelectors() {
    expandCollapseGroup(1) // TODO change these to be by text
    runSingleSelectorNavigationTest("Nested Circles")
}

fun testNavigateWithCalcSelectors() {
    expandCollapseGroup(2)
    runSingleSelectorNavigationTest("Single Operation")
}

/**
 * Test navigation to a fragment using a specific selector.
 * Must be called from home fragment, with appropriate selector group expanded.
 * Returns to home fragment after check is complete
 *
 * @param selectorName [String]: name of selector to test
 */
private fun runSingleSelectorNavigationTest(selectorName: String) {
    onView(withText(selectorName)).perform(scrollTo(), click())
    onView(withTab(selectorName)).check(matches(isCompletelyDisplayed()))
    pressBack()
}
