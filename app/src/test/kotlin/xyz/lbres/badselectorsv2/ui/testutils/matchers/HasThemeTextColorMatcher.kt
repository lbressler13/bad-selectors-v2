package xyz.lbres.badselectorsv2.ui.testutils.matchers

import android.graphics.Color
import android.util.TypedValue
import android.view.View
import android.widget.TextView
import org.hamcrest.Description
import org.hamcrest.Matcher
import org.hamcrest.TypeSafeMatcher
import xyz.lbres.badselectorsv2.testutils.printErr

private class HasThemeTextColorMatcher(private val themeAttrId: Int) : TypeSafeMatcher<View>() {
    override fun describeTo(description: Description?) {
        description?.appendText("match view with text color from attribute: $themeAttrId")
    }

    override fun matchesSafely(item: View?): Boolean {
        if (item !is TextView) {
            return false
        }
        val colorData = TypedValue()
        item.context.theme.resolveAttribute(themeAttrId, colorData, true)
        val actualColor = item.currentTextColor
        val expectedColor = colorData.data

        val actualStr = colorToHex(actualColor)
        val expectedStr = colorToHex(expectedColor)

        if (actualColor != colorData.data) {
            val errorMessage = "Actual color $actualStr does not match expected color $expectedStr"
            printErr("[TextColorMatcher] $errorMessage")
        }

        return actualColor == expectedColor
    }

    private fun colorToHex(color: Int): String {
        return String.format("#%02X%06X", (0xFF and Color.alpha(color)), (0xFFFFFF and color))
    }
}

/**
 * [Matcher] to check that the current text color of a [TextView] matches a theme attribute.
 * Alternate to hasTextColor, which requires a color resource id.
 *
 * @param themeAttrId [Int]: id of theme attribute to check
 */
fun hasThemeTextColor(themeAttrId: Int): Matcher<View> = HasThemeTextColorMatcher(themeAttrId)
