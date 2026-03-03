package xyz.lbres.badselectorsv2.testutils.matchers

import android.view.View
import androidx.test.espresso.matcher.BoundedMatcher
import com.google.android.material.tabs.TabLayout.TabView
import org.hamcrest.Description
import org.hamcrest.Matcher

/**
 * [Matcher] to match a tab with a given title.
 *
 * Adapted from responses to this StackOverflow post:
 * https://stackoverflow.com/questions/70303861/how-do-i-assert-that-a-scrollable-tablayout-is-currently-showing-a-certain-tab
 */
class WithTabMatcher(private val title: String) : BoundedMatcher<View, TabView>(TabView::class.java) {
    override fun describeTo(description: Description?) {
        description?.appendText("match tab with title: \"$title\"")
    }

    override fun matchesSafely(item: TabView?): Boolean {
        return title == item?.tab?.text
    }
}

/**
 * [Matcher] to match a tab with a given title.
 */
fun withTab(title: String): Matcher<View> = WithTabMatcher(title)
