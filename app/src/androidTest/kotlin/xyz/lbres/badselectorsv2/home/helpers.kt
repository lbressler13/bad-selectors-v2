package xyz.lbres.badselectorsv2.home

import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.contrib.RecyclerViewActions.actionOnItemAtPosition
import androidx.test.espresso.contrib.RecyclerViewActions.scrollToPosition
import androidx.test.espresso.matcher.ViewMatchers.hasDescendant
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withText
import org.hamcrest.Matchers.allOf
import xyz.lbres.badselectorsv2.R
import xyz.lbres.badselectorsv2.home.selector.SelectorViewHolder
import xyz.lbres.badselectorsv2.home.selectorgroup.SelectorGroupViewHolder
import xyz.lbres.badselectorsv2.testutils.matchers.isShown
import xyz.lbres.badselectorsv2.testutils.matchers.matchesAtPosition
import xyz.lbres.badselectorsv2.testutils.viewactions.actionOnChildWithId
import xyz.lbres.badselectorsv2.testutils.viewassertions.isNotPresented
import xyz.lbres.kotlinutils.list.IntList
import xyz.lbres.kotlinutils.list.StringList

// private aliases for ViewActions that require the vh class
private typealias SVH = SelectorViewHolder
private typealias SGVH = SelectorGroupViewHolder

private val selectorGroupRecycler = onView(withId(R.id.selectorGroupRecycler))
private val nestedRecyclerId = R.id.selectorRecycler

private val selectorNames: List<StringList> = listOf(
    listOf("Shuffle Circle"),
)

/**
 * Click the expand/collapse button for an element in the main RecyclerView
 *
 * @param position [Int]: position of item to click
 */
fun expandCollapseGroup(position: Int) {
    val clickExpandCollapse = actionOnChildWithId(R.id.expandCollapseButton, click())
    selectorGroupRecycler.perform(actionOnItemAtPosition<SGVH>(position, clickExpandCollapse))
}

/**
 * Check that the specified groups are collapsed
 *
 * @param positions [IntList]: list of groups which should be collapsed
 */
fun checkGroupsCollapsed(positions: IntList) {
    for (position in positions) {
        selectorGroupRecycler.perform(scrollToPosition<SGVH>(0))
        for (name in selectorNames[position]) {
            onView(withText(name)).check(isNotPresented())
        }
    }
}

/**
 * Check that the specified groups are expanded
 *
 * @param positions [IntList]: list of groups which should be expanded
 */
fun checkGroupsExpanded(positions: IntList) {
    for (position in positions) {
        selectorGroupRecycler.perform(scrollToPosition<SGVH>(position))

        for (idxPair in selectorNames[position].withIndex()) {
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

            selectorGroupRecycler.check(matches(matchesAtPosition(position, hasDescendant(nestedMatcher))))
        }
    }
}
