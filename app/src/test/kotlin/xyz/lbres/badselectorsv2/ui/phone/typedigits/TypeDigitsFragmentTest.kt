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
import xyz.lbres.badselectorsv2.ui.phone.clickDigit
import xyz.lbres.badselectorsv2.ui.phone.digitViews
import xyz.lbres.badselectorsv2.ui.testutils.closeDialog
import xyz.lbres.badselectorsv2.ui.testutils.onViewInDialog
import xyz.lbres.kotlinutils.collection.list.listOfNulls
import xyz.lbres.kotlinutils.collection.list.mutableListOfNulls

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
        val setDigitsOrder = listOf(1, 6, 0, 8, 9, 7, 2, 4, 5, 3)
        val selectOrder = listOf(7, 4, 1, 9, 0, 2, 3, 6, 5, 8)
        val expectedNumber: MutableList<Int?> = mutableListOfNulls(10)

        withMockedDigitsOrder(digitsOrder) {
            launchTypeDigitsFragment()
            // set full number
            repeat(10) {
                val digitIndex = setDigitsOrder[it]
                val selectValue = selectOrder[it]

                clickDigit(digitIndex)
                clickRadioButton(selectValue)
                closeDialog()

                expectedNumber[digitIndex] = digitsOrder[selectValue]
                checkPhoneNumber(expectedNumber)
            }

            // change
            clickDigit(4)
            clickRadioButton(0)
            closeDialog()
            expectedNumber[4] = digitsOrder[0]
            checkPhoneNumber(expectedNumber)

            clickDigit(9)
            clickRadioButton(9)
            closeDialog()
            expectedNumber[9] = digitsOrder[9]
            checkPhoneNumber(expectedNumber)

            // open without changing
            val currentValue = expectedNumber[9]!!
            clickDigit(9)
            closeDialog()
            checkPhoneNumber(expectedNumber)

            clickDigit(9)
            clickRadioButton(currentValue - 1) // select something else
            clickRadioButton(currentValue) // change back
            closeDialog()
            checkPhoneNumber(expectedNumber)
        }
    }

    @Test
    fun recreate() {
        withMockedDigitsOrder(digitsOrder) {
            val scenario = launchTypeDigitsFragment()

            // blank

            // partial number

            // full number

            // changed
        }
        // TODO
    }
}
