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
import kotlin.collections.removeLast as removeLastKt

fun testExpandCollapseSelectors() {
    val allPositions = getExpandablePositions()
    val expandedPositions: MutableList<Int> = mutableListOf()
    val collapsedPositions = allPositions.toMutableList()

    // start collapsed
    checkGroupsExpandedCollapsed(expandedPositions)

    // expand
    while (collapsedPositions.isNotEmpty()) {
        val position = collapsedPositions.removeLastKt()
        expandCollapseGroup(position)
        expandedPositions.add(position)
        checkGroupsExpandedCollapsed(expandedPositions)
    }

    // collapse
    while (expandedPositions.isNotEmpty()) {
        val position = expandedPositions.removeLastKt()
        expandCollapseGroup(position)
        collapsedPositions.add(position)
        checkGroupsExpandedCollapsed(expandedPositions)
    }
}

fun testExpansionsPersistedOnLeave() {
    // using back button
    expandCollapseGroup(0)
    onView(withId(R.id.navigationPhone)).perform(click())

    pressBack()
    checkGroupsExpandedCollapsed(listOf(0))

    // using buttons
    expandCollapseGroup(2)
    onView(withId(R.id.navigationPhone)).perform(click())

    onView(withId(R.id.navigationHome)).perform(click())
    checkGroupsExpandedCollapsed(listOf(0, 2))
}

fun testNavigateWithPhoneSelectors() {
    expandCollapseGroup("Phone")
    runSingleSelectorNavigationTest("Random Circle")
}

fun testNavigateWithDateSelectors() {
    expandCollapseGroup("Date")
    runSingleSelectorNavigationTest("Nested Circles")
}

fun testNavigateWithCalcSelectors() {
    expandCollapseGroup("Calculator")
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
