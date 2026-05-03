package xyz.lbres.badselectorsv2.ui.home

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.action.ViewActions.scrollTo
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.contrib.RecyclerViewActions.actionOnItem
import androidx.test.espresso.contrib.RecyclerViewActions.actionOnItemAtPosition
import androidx.test.espresso.contrib.RecyclerViewActions.scrollToPosition
import androidx.test.espresso.matcher.ViewMatchers.hasDescendant
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withParent
import androidx.test.espresso.matcher.ViewMatchers.withText
import org.hamcrest.Matchers.allOf
import xyz.lbres.badselectorsv2.R
import xyz.lbres.badselectorsv2.abstracts.TabFragment
import xyz.lbres.badselectorsv2.home.selector.SelectorViewHolder
import xyz.lbres.badselectorsv2.home.selectorgroup.SelectorGroupViewHolder
import xyz.lbres.badselectorsv2.ui.testutils.matchers.isShown
import xyz.lbres.badselectorsv2.ui.testutils.matchers.matchesAtPosition
import xyz.lbres.badselectorsv2.ui.testutils.viewactions.actionOnChildWithId
import xyz.lbres.badselectorsv2.ui.testutils.viewactions.forceClick
import xyz.lbres.badselectorsv2.ui.testutils.viewassertions.isNotPresented
import xyz.lbres.kotlinutils.general.simpleIf
import xyz.lbres.kotlinutils.list.IntList

// private aliases for ViewActions that require the vh class
private typealias SVH = SelectorViewHolder
private typealias SGVH = SelectorGroupViewHolder

private val selectorGroupRecycler = onView(withId(R.id.selectorGroupRecycler))
private val nestedRecyclerId = R.id.selectorRecycler

private val getSelectorNames = { position: Int ->
    val context: Context = ApplicationProvider.getApplicationContext()
    TabFragment.allMetadata[position].tabTitleResIds
        .map { context.getString(it) }
}

fun getExpandablePositions(): IntList {
    return TabFragment.allMetadata
        .mapIndexed { index, metadata -> simpleIf(metadata.tabTitleResIds.isEmpty(), null, index) }
        .filterNotNull()
}

/**
 * Click the expand/collapse button for an element in the main RecyclerView
 *
 * @param position [Int]: position of item to click
 */
fun expandCollapseGroup(position: Int) {
    val clickExpandCollapse = actionOnChildWithId(R.id.expandCollapseButton, forceClick())
    selectorGroupRecycler.perform(actionOnItemAtPosition<SGVH>(position, clickExpandCollapse))
}

/**
 * Click the expand/collapse button for an element in the main RecyclerView
 *
 * @param title [String]: position of item to click
 */
fun expandCollapseGroup(title: String) {
    onView(allOf(withId(R.id.selectorTitle), withText(title)))
        .perform(scrollTo(), click())
}

fun checkGroupsExpandedCollapsed(expandedPositions: IntList) {
    val collapsedPositions = getExpandablePositions() - expandedPositions

    for (position in expandedPositions) {
        checkGroupExpanded(position)
    }
    for (position in collapsedPositions) {
        checkGroupCollapsed(position)
    }
}

private fun checkGroupCollapsed(position: Int) {
    selectorGroupRecycler.perform(scrollToPosition<SGVH>(position))
    for (name in getSelectorNames(position)) {
        onView(withText(name)).check(isNotPresented())
    }
}

private fun checkGroupExpanded(position: Int) {
    selectorGroupRecycler.perform(scrollToPosition<SGVH>(position))

    for (idxPair in getSelectorNames(position).withIndex()) {
        val nestedPosition = idxPair.index
        val name = idxPair.value

        val scrollSelectorRecycler = actionOnChildWithId(nestedRecyclerId, scrollToPosition<SVH>(nestedPosition))
        selectorGroupRecycler.perform(actionOnItemAtPosition<SGVH>(position, scrollSelectorRecycler))

        val nameMatcher = allOf(isShown(), withText(name))
        val nestedMatcher = allOf(
            withId(nestedRecyclerId),
            isDisplayed(),
            matchesAtPosition(nestedPosition, nameMatcher),
        )

        onView(withText(name)).perform(scrollTo())
        selectorGroupRecycler.check(matches(matchesAtPosition(position, hasDescendant(nestedMatcher))))
    }
}

/**
 * Check that the specified groups are collapsed
 *
 * @param positions [IntList]: list of groups which should be collapsed
 */
fun checkGroupsCollapsed(positions: IntList) {
    for (position in positions) {
        checkGroupCollapsed(position)
    }
}

/**
 * Check that the specified groups are expanded
 *
 * @param positions [IntList]: list of groups which should be expanded
 */
fun checkGroupsExpanded(positions: IntList) {
    for (position in positions) {
        checkGroupExpanded(position)
    }
}
