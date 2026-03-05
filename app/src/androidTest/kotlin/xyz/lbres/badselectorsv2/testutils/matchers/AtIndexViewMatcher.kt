package xyz.lbres.badselectorsv2.testutils.matchers

import android.view.View
import android.view.ViewGroup
import org.hamcrest.Description
import org.hamcrest.Matcher
import org.hamcrest.TypeSafeMatcher

/**
 * [Matcher] to match a child view at a given position
 *
 * @param parentMatcher [Matcher]: matcher for parent view
 * @param index [Int]
 */
private class MatchesAtIndexViewMatcher(private val parentMatcher: Matcher<View>, private val index: Int) :
    TypeSafeMatcher<View>() {
    override fun describeTo(description: Description?) {
        description?.appendText("match child at index $index")
    }

    override fun matchesSafely(item: View?): Boolean {
        val parent = item?.parent
        return parent is ViewGroup && parentMatcher.matches(parent) && parent.getChildAt(index) == item
    }
}

/**
 * [Matcher] to match a child view at a given position
 *
 * @param parentMatcher [Matcher]: matcher for parent view
 * @param index [Int]
 */
fun atIndex(parentMatcher: Matcher<View>, index: Int): Matcher<View> = MatchesAtIndexViewMatcher(parentMatcher, index)
