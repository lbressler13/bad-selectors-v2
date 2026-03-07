package xyz.lbres.badselectorsv2.testutils

import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.matcher.ViewMatchers.withId
import xyz.lbres.badselectorsv2.R

/**
 * Open dialog using dev tools button
 */
fun openDevTools() {
    onView(withId(R.id.devToolsButton)).perform(click())
}
