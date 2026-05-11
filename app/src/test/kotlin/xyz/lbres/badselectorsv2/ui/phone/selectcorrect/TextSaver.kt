package xyz.lbres.badselectorsv2.ui.phone.selectcorrect

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

class TextSaver(private val viewInteraction: ViewInteraction) {
    private var savedText: String? = null

    private inner class PreviousTextMatcher(private val matching: Boolean): TypeSafeMatcher<View>() {
        override fun describeTo(description: Description?) {
            description?.appendText("match view with text: \"$savedText\"")
        }

        override fun matchesSafely(item: View?): Boolean {
            return item is TextView && savedText != null &&
                    ((matching && item.text == savedText) || (!matching && item.text != savedText))
        }
    }

    private inner class TextSaver: ViewAction {
        override fun getConstraints(): Matcher<View> = isAssignableFrom(TextView::class.java)
        override fun getDescription(): String = "saving text"

        override fun perform(uiController: UiController, view: View) {
            view as TextView
            savedText = view.text?.toString()
        }
    }

    fun saveText() = viewInteraction.perform(TextSaver())
    fun matchPreviousText() = viewInteraction.check(matches(PreviousTextMatcher(true)))
    fun notMatchPreviousText() = viewInteraction.check(matches(PreviousTextMatcher(false)))
}
