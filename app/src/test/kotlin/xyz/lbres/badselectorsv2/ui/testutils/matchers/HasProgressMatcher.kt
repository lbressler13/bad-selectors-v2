package xyz.lbres.badselectorsv2.ui.testutils.matchers

import android.view.View
import android.widget.SeekBar
import androidx.test.espresso.matcher.BoundedMatcher
import org.hamcrest.Description
import org.hamcrest.Matcher

/**
 * [Matcher] to match a seekbar with the given progress.
 */
private class HasProgressMatcher(private val progress: Int) :
    BoundedMatcher<View, SeekBar>(SeekBar::class.java) {
    override fun describeTo(description: Description?) {
        description?.appendText("match seekbar with progress: $progress")
    }

    override fun matchesSafely(item: SeekBar?): Boolean {
        return progress == item?.progress
    }
}

/**
 * [Matcher] to match a seekbar with the given progress.
 */
fun hasProgress(progress: Int): Matcher<View> = HasProgressMatcher(progress)
