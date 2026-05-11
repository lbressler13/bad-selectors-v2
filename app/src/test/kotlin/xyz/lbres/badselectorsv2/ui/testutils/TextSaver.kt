package xyz.lbres.badselectorsv2.ui.testutils

import android.view.View
import android.widget.TextView
import androidx.test.espresso.UiController
import androidx.test.espresso.ViewAction
import androidx.test.espresso.ViewInteraction
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.isAssignableFrom
import org.hamcrest.Description
import org.hamcrest.Matcher
import org.hamcrest.TypeSafeMatcher
import xyz.lbres.badselectorsv2.testutils.printErr
import xyz.lbres.badselectorsv2.testutils.printWarn
import xyz.lbres.badselectorsv2.ui.testutils.TextSaver.PreviousTextMatcher
import xyz.lbres.badselectorsv2.ui.testutils.TextSaver.TextSaveAction

/**
 * Util to save text from a textview and validate the current text against the previously saved text
 */
object TextSaver {
    // private var savedText: String? = null
    private var savedText: MutableMap<Int, String> = mutableMapOf()

    private class PreviousTextMatcher() : TypeSafeMatcher<View>() {
        override fun describeTo(description: Description?) {
            description?.appendText("match text in view")
        }

        override fun matchesSafely(item: View?): Boolean {
            if (item !is TextView || item.id !in savedText) {
                return false
            }

            val previousText = savedText[item.id]
            if (previousText != item.text) {
                val errorMessage = "Current text '${item.text}' does not match previous text '$previousText'"
                printErr("[TextSaver] $errorMessage")
                return false
            }

            return true
        }
    }

    private class TextSaveAction : ViewAction {
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
    fun matchPreviousText(): Matcher<View> = PreviousTextMatcher()
    // withPreviousText

    /**
     * Clear all existing saved text
     */
    fun clear() = savedText.clear()

    /**
     * Check that the current text matches the saved text
     */
    // fun matchPreviousText() = viewInteraction.check(matches(PreviousTextMatcher(true)))

    /**
     * Check that the current text does not match the saved text
     */
    // fun notMatchPreviousText() = viewInteraction.check(matches(PreviousTextMatcher(false)))
}

/**
 * Save current text
 */
fun saveText(): ViewAction = TextSaver.saveText()

/**
 * Check that the current text matches the saved text
 */
fun matchPreviousText(): Matcher<View> = TextSaver.matchPreviousText()
