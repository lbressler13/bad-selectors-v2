package xyz.lbres.badselectorsv2.testutils.matchers

import android.view.View
import androidx.appcompat.widget.Toolbar
import org.hamcrest.Description
import org.hamcrest.Matcher
import org.hamcrest.TypeSafeMatcher

/**
 * [Matcher] for checking if a Toolbar has a given title
 */
private class WithTitleMatcher(val title: String) : TypeSafeMatcher<View>() {
    override fun describeTo(description: Description?) {
        description?.appendText("match view with title: \"$title\"")
    }

    override fun matchesSafely(item: View?): Boolean {
        return item is Toolbar && item.title == title
    }
}

/**
 * [Matcher] for a Toolbar, to determine if the toolbar has a given title
 *
 * @param title [String]: expected title
 */
fun withTitle(title: String): Matcher<View> = WithTitleMatcher(title)
