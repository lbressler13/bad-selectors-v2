package xyz.lbres.badselectorsv2.home

import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.ViewAction
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.contrib.RecyclerViewActions
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

private val selectorGroupRecycler = onView(withId(R.id.selectorGroupRecycler))
private val nestedRecyclerId = R.id.selectorRecycler

private val selectorNames: List<StringList> = listOf(
    listOf("Shuffle Circle"),
)

/**
 * Wrapper function to scroll a RecyclerView to [position], with ViewHolder type [SelectorGroupViewHolder]
 *
 * @param position [Int]: position to scroll to
 * @return [ViewAction]: action to scroll to the given position
 */
fun scrollToSelectorGroupAtPosition(position: Int): ViewAction {
    return RecyclerViewActions.scrollToPosition<SelectorGroupViewHolder>(position)
}

/**
 * Wrapper function to scroll a RecyclerView to [position], with ViewHolder type [SelectorViewHolder]
 *
 * @param position [Int]: position to scroll to
 * @return [ViewAction]: action to scroll to the given position
 */
fun scrollToSelectorPosition(position: Int): ViewAction {
    return RecyclerViewActions.scrollToPosition<SelectorViewHolder>(position)
}

/**
 * Wrapper function to perform an action at a given index in a RecyclerView, with ViewHolder type [SelectorGroupViewHolder]
 *
 * @param position [Int]: position to perform action
 * @param action [ViewAction]: action to perform
 * @return [ViewAction]: action to perform given [action] on the ViewHolder at position [position]
 */
fun actionOnSelectorGroupAtPosition(position: Int, action: ViewAction): ViewAction {
    return RecyclerViewActions.actionOnItemAtPosition<SelectorGroupViewHolder>(position, action)
}

/**
 * Click the expand/collapse button for an element in the main RecyclerView
 *
 * @param position [Int]: position of item to click
 */
fun expandCollapseGroup(position: Int) {
    val clickExpandCollapse = actionOnChildWithId(R.id.expandCollapseButton, click())
    selectorGroupRecycler.perform(actionOnSelectorGroupAtPosition(position, clickExpandCollapse))
}

/**
 * Check that the specified groups are collapsed
 *
 * @param positions [IntList]: list of groups which should be collapsed
 */
fun checkGroupsCollapsed(positions: IntList) {
    for (position in positions) {
        selectorGroupRecycler.perform(scrollToSelectorGroupAtPosition(0))
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
        selectorGroupRecycler.perform(scrollToSelectorGroupAtPosition(position))

        for (pair in selectorNames[position].withIndex()) {
            val nestedPosition = pair.index
            val name = pair.value

            val scrollSelectorRecycler = actionOnChildWithId(nestedRecyclerId, scrollToSelectorPosition(nestedPosition))
            selectorGroupRecycler.perform(actionOnSelectorGroupAtPosition(position, scrollSelectorRecycler))

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
