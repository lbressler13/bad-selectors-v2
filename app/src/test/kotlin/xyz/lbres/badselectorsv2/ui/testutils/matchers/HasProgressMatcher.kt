package xyz.lbres.badselectorsv2.ui.testutils.matchers

import android.view.View
import androidx.appcompat.widget.AppCompatSeekBar
import androidx.test.espresso.matcher.BoundedMatcher
import org.hamcrest.Description
import org.hamcrest.Matcher

private class HasProgressMatcher(private val progress: Int): BoundedMatcher<View, AppCompatSeekBar>(AppCompatSeekBar::class.java) {
    override fun describeTo(description: Description?) {
        description?.appendText("match seekbar with progress: $progress")
    }

    override fun matchesSafely(item: AppCompatSeekBar?): Boolean {
        return progress == item?.progress
    }
}

fun hasProgress(progress: Int): Matcher<View> = HasProgressMatcher(progress)
