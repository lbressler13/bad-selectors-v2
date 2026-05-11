package xyz.lbres.badselectorsv2.ui.testutils

import android.view.View
import android.widget.TextView
import androidx.test.espresso.UiController
import androidx.test.espresso.ViewAction
import androidx.test.espresso.matcher.ViewMatchers.isAssignableFrom
import org.hamcrest.Description
import org.hamcrest.Matcher
import org.hamcrest.TypeSafeMatcher

/**
 * Util to save text from a textview and validate the current text against the previously saved text
 */
class TextSaver {
    private var savedText: MutableMap<Int, String> = mutableMapOf()

    private inner class PreviousTextMatcher() : TypeSafeMatcher<View>() {
        override fun describeTo(description: Description?) {
            description?.appendText("match text in view")
        }

        override fun matchesSafely(item: View?): Boolean {
            return item is TextView && item.text != null && item.text == savedText[item.id]
        }
    }

    private inner class TextSaveAction : ViewAction {
        override fun getConstraints(): Matcher<View> = isAssignableFrom(TextView::class.java)
        override fun getDescription(): String = "saving text"

        override fun perform(uiController: UiController, view: View) {
            view as TextView
            if (view.text != null) {
                savedText[view.id] = view.text.toString()
            }
        }
    }

    /**
     * Save current text
     */
    fun saveText(): ViewAction = TextSaveAction()

    /**
     * Check that the current text matches the saved text
     */
    fun withPreviousText(): Matcher<View> = PreviousTextMatcher()

    /**
     * Clear all existing saved text
     */
    fun clear() = savedText.clear()
}
