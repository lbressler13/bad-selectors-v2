package xyz.lbres.badselectorsv2.ui.testutils.matchers

import android.util.TypedValue
import android.view.View
import android.widget.TextView
import org.hamcrest.Description
import org.hamcrest.Matcher
import org.hamcrest.TypeSafeMatcher

class HasThemeTextColorMatcher(private val themeAttrId: Int) : TypeSafeMatcher<View>() {
    override fun describeTo(description: Description?) {
        description?.appendText("match view with text color from attribute: $themeAttrId")
    }

    override fun matchesSafely(item: View?): Boolean {
        if (item !is TextView) {
            return false
        }
        val color = TypedValue()
        item.context.theme.resolveAttribute(themeAttrId, color, true)
        return item.currentTextColor == color.data
    }
}

/**
 * [Matcher] to check that the current text color of a [TextView] matches a theme attribute.
 * Alternate to hasTextColor, which requires a color resource ide.
 *
 * @param themeAttrId [Int]: id of theme attribute to check
 */
fun hasThemeTextColor(themeAttrId: Int): Matcher<View> = HasThemeTextColorMatcher(themeAttrId)
