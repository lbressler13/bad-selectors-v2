package xyz.lbres.badselectorsv2.ui.testutils

import android.view.View
import android.widget.TextView
import androidx.test.espresso.UiController
import androidx.test.espresso.ViewAction
import androidx.test.espresso.ViewInteraction
import androidx.test.espresso.assertion.ViewAssertions
import androidx.test.espresso.matcher.ViewMatchers
import org.hamcrest.Description
import org.hamcrest.Matcher
import org.hamcrest.TypeSafeMatcher
import xyz.lbres.badselectorsv2.testutils.printWarn

/**
 * Util to save text from a textview and validate the current text against previous text
 */
class TextSaver(private val viewInteraction: ViewInteraction) {
    private var savedText: String? = null

    init {
        printWarn("Use TestSaver with caution! Use mocking when possible.")
    }

    private inner class PreviousTextMatcher(private val matching: Boolean) : TypeSafeMatcher<View>() {
        override fun describeTo(description: Description?) {
            description?.appendText("match view with text: \"$savedText\"")
        }

        override fun matchesSafely(item: View?): Boolean {
            return when {
                item !is TextView -> false
                savedText != null && matching && item.text == savedText -> true
                !matching && item.text != savedText -> true
                else -> false
            }
        }
    }

    private inner class TextSaver : ViewAction {
        override fun getConstraints(): Matcher<View> =
            ViewMatchers.isAssignableFrom(TextView::class.java)
        override fun getDescription(): String = "saving text"

        override fun perform(uiController: UiController, view: View) {
            view as TextView
            savedText = view.text?.toString()
        }
    }

    /**
     * Save current text
     */
    fun saveText() = viewInteraction.perform(TextSaver())

    /**
     * Check that the current text matches the saved text
     */
    fun matchPreviousText() = viewInteraction.check(ViewAssertions.matches(PreviousTextMatcher(true)))

    /**
     * Check that the current text does not match the saved text
     */
    fun notMatchPreviousText() = viewInteraction.check(
        ViewAssertions.matches(
            PreviousTextMatcher(
                false,
            ),
        ),
    )
}
