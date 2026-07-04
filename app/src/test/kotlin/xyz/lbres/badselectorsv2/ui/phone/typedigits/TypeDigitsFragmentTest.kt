package xyz.lbres.badselectorsv2.ui.phone.typedigits

import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.isClickable
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withText
import androidx.test.ext.junit.runners.AndroidJUnit4
import io.mockk.unmockkAll
import org.junit.After
import org.junit.Test
import org.junit.experimental.categories.Category
import org.junit.runner.RunWith
import org.robolectric.Robolectric
import xyz.lbres.badselectorsv2.phone.utils.numDigits
import xyz.lbres.badselectorsv2.ui.phone.checkPhoneNumber
import xyz.lbres.badselectorsv2.ui.phone.digitViews
import xyz.lbres.badselectorsv2.ui.testutils.onViewInDialog
import xyz.lbres.kotlinutils.collection.list.listOfNulls

@Category(Robolectric::class)
@RunWith(AndroidJUnit4::class)
class TypeDigitsFragmentTest {
    private val digitsOrder = listOf(6, 3, 0, 1, 9, 4, 2, 8, 7, 5)

    private val doneButton = onViewInDialog(withText("Done"))

    @After
    fun cleanupTest() {
        unmockkAll()
    }

    @Test
    fun initialUi() {
        launchTypeDigitsFragment()
        checkPhoneNumber(listOfNulls(numDigits))
        onView(withText("Tap digits to set values")).check(matches(isDisplayed()))
        digitViews.forEach { it.check(matches(isClickable())) }
    }

    @Test
    fun openDialog() {
        launchTypeDigitsFragment()
        digitViews.forEach {
            it.perform(click())
            onViewInDialog(withText("Select Digit Value")).check(matches(isDisplayed()))
            doneButton.perform(click())
        }
    }

    @Test
    fun setNumber() {
        withMockedDigitsOrder(digitsOrder) {
            launchTypeDigitsFragment()
        }
        // TODO
    }

    @Test
    fun refreshUi() {
        withMockedDigitsOrder(digitsOrder) {
            val scenario = launchTypeDigitsFragment()
        }
        // TODO
    }
}
