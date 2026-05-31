package xyz.lbres.badselectorsv2.ui.testutils.viewactions

import android.view.View
import android.widget.SeekBar
import androidx.test.espresso.UiController
import androidx.test.espresso.ViewAction
import androidx.test.espresso.matcher.ViewMatchers.isAssignableFrom
import org.hamcrest.Matcher

/**
 * Set the progress of a seekbar
 *
 * @param progress [Int]: the updated progress value
 */
private class SetProgressViewAction(private val progress: Int) : ViewAction {
    override fun getConstraints(): Matcher<View> = isAssignableFrom(SeekBar::class.java)
    override fun getDescription(): String = "setting seekbar progress to $progress"

    override fun perform(uiController: UiController?, view: View?) {
        view as SeekBar
        view.progress = progress
    }
}

/**
 * [ViewAction] to set the progress of a seekbar
 *
 * @param progress [Int]: the updated progress value
 */
fun setProgress(progress: Int): ViewAction = SetProgressViewAction(progress)
