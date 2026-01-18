package xyz.lbres.androidapptemplate.testutils.matchers

import android.view.View
import org.hamcrest.Description
import org.hamcrest.Matcher
import org.hamcrest.TypeSafeMatcher

/**
 * [Matcher] for if a view has an ancestor that matches a given matcher.
 *
 * @param ancestorMatcher [Matcher]<View?>: matcher for ancestor
 */
private class WithAncestorViewMatcher(private val ancestorMatcher: Matcher<View?>) : TypeSafeMatcher<View>() {
    override fun describeTo(description: Description?) {
        description?.appendText("matching ancestor with matcher")
        description?.appendDescriptionOf(ancestorMatcher)
    }

    /**
     * Walk from [view] to root view and determine if any ancestor matches the [ancestorMatcher]
     *
     * @param view [View]?: initial view to check
     * @return [Boolean] `true` if there is an ancestor that matches the matcher, `false` otherwise
     */
    override fun matchesSafely(view: View): Boolean {
        val rootView = view.rootView

        if (view == rootView) {
            return false
        }

        var currentView = view.parent

        while (currentView != rootView && !ancestorMatcher.matches(currentView)) {
            currentView = currentView.parent
        }

        return ancestorMatcher.matches(currentView)
    }
}

/**
 * [Matcher] to determine if a view has an ancestor that matches a given matcher
 *
 * @param ancestorMatcher [Matcher]<View?>: matcher for ancestor
 */
fun withAncestor(ancestorMatcher: Matcher<View?>): Matcher<View> = WithAncestorViewMatcher(ancestorMatcher)
