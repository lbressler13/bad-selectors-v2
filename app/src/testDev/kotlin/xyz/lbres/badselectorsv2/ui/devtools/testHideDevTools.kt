package xyz.lbres.badselectorsv2.ui.devtools

import androidx.test.espresso.DataInteraction
import androidx.test.espresso.Espresso.onData
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.PerformException
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.RootMatchers.isPlatformPopup
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withSpinnerText
import androidx.test.espresso.matcher.ViewMatchers.withText
import org.hamcrest.Matchers.allOf
import org.hamcrest.Matchers.instanceOf
import org.hamcrest.Matchers.`is`
import org.hamcrest.Matchers.not
import org.junit.Assert.assertFalse
import org.robolectric.shadows.ShadowDialog
import org.robolectric.shadows.ShadowLooper
import xyz.lbres.badselectorsv2.R
import xyz.lbres.badselectorsv2.testutils.runWithFailMessage
import xyz.lbres.badselectorsv2.ui.testutils.closeDialog
import xyz.lbres.badselectorsv2.ui.testutils.onViewInDialog
import xyz.lbres.badselectorsv2.ui.testutils.openDevTools
import java.util.concurrent.TimeUnit

private val spinner = onViewInDialog(withId(R.id.devToolsTimeSpinner))
private val hideDevToolsButton = onViewInDialog(withId(R.id.hideDevToolsButton))
private val devToolsButton = onView(withId(R.id.devToolsButton))

private val hideTimes = listOf(5000L, 10000L, 30000L, 60000L)

fun testHideDevToolsOptionsDisplayed() {
    openDevTools()

    spinner.check(matches(withSpinnerText("${hideTimes.first()}ms"))).perform(click())

    hideTimes.forEachIndexed { index, time ->
        runWithFailMessage("Checking text at index $index") {
            spinnerItemAt(index).check(matches(allOf(isDisplayed(), withText("${time}ms"))))
        }
    }

    var performException = false
    try {
        spinnerItemAt(hideTimes.size).check(matches(isDisplayed()))
    } catch (_: PerformException) {
        performException = true
    }

    if (!performException) {
        throw PerformException.Builder()
            .withCause(IllegalStateException("Dev tools spinner has too many options"))
            .build()
    }
}

fun testInteractWithHideDevToolsSpinner() {
    openDevTools()

    val checkOrder = listOf(1, 0, 2, 3)
    checkOrder.forEach {
        runWithFailMessage("Interacting with index $it") {
            spinner.perform(click())
            spinnerItemAt(it).perform(click())
            val time = hideTimes[it]
            spinner.check(matches(withSpinnerText("${time}ms")))
        }
    }

    // close and re-open dialog
    closeDialog()
    openDevTools()
    spinner.check(matches(withSpinnerText("${hideTimes.last()}ms")))
}

fun testHideDevTools() {
    val buffer = 500L

    hideTimes.forEachIndexed { index, time ->
        runWithFailMessage("Hiding for duration $time at index $index") {
            openDevTools()
            spinner.perform(click())
            spinnerItemAt(index).perform(click())
            spinner.check(matches(withSpinnerText("${time}ms")))
            hideDevToolsButton.perform(click())

            // check that dialog is not showing
            val dialog = ShadowDialog.getLatestDialog()
            assertFalse(dialog.isShowing)

            val shadowLooper = ShadowLooper.shadowMainLooper()
            devToolsButton.check(matches(not(isDisplayed())))
            shadowLooper.idleFor(time - buffer, TimeUnit.MILLISECONDS)
            devToolsButton.check(matches(not(isDisplayed())))
            shadowLooper.idleFor(buffer * 2, TimeUnit.MILLISECONDS)
            devToolsButton.check(matches(isDisplayed()))
        }
    }
}

/**
 * Get the item at a specific position in the spinner
 *
 * @param position [Int]
 * @return [DataInteraction]: the item located at [position]
 */
private fun spinnerItemAt(position: Int): DataInteraction {
    return onData(`is`(instanceOf(String::class.java)))
        .inRoot(isPlatformPopup())
        .atPosition(position)
}
