package xyz.lbres.badselectorsv2.ui.phone.choosedigits

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
class ChooseDigitsFragmentTest {
    private val digitsOrder = listOf(6, 3, 0, 1, 9, 4, 2, 8, 7, 5)

    @After
    fun cleanupTest() {
        unmockkAll()
    }

    @Test
    fun initialUi() {
        launchChooseDigitsFragment()
        checkPhoneNumber(listOfNulls(numDigits))
        onView(withText("Tap digits to set values")).check(matches(isDisplayed()))
        digitViews.forEach { it.check(matches(isClickable())) }
    }

    @Test
    fun openDialog() {
        launchChooseDigitsFragment()
        digitViews.forEach {
            it.perform(click())
            onViewInDialog(withText("Select Digit Value")).check(matches(isDisplayed()))
            closeDialog()
        }
    }

    @Test
    fun setNumber() {
        val selections = listOf(1 to 7, 6 to 4, 0 to 1, 8 to 9, 9 to 0, 7 to 2, 2 to 3, 4 to 6, 5 to 5, 3 to 8)
        val expectedNumber: MutableList<Int?> = mutableListOfNulls(10)

        withMockedDigitsOrder(digitsOrder) {
            launchChooseDigitsFragment()
            // set full number
            selections.forEach { (digitIndex, selectIndex) ->
                selectValue(digitIndex, selectIndex, expectedNumber)
                checkPhoneNumber(expectedNumber)
            }

            // change
            selectValue(4, 0, expectedNumber)
            checkPhoneNumber(expectedNumber)

            selectValue(9, 9, expectedNumber)
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
            val scenario = launchChooseDigitsFragment()
            val expectedNumber: MutableList<Int?> = mutableListOfNulls(10)
            val selections = listOf(4 to 5, 7 to 3, 2 to 3, 0 to 0, 1 to 5, 3 to 2, 5 to 4, 6 to 4, 8 to 2, 9 to 1)

            // blank
            scenario.recreate()
            checkPhoneNumber(expectedNumber)

            // partial number
            repeat(3) {
                val selection = selections[it]
                selectValue(selection.first, selection.second, expectedNumber)
            }

            scenario.recreate()
            checkPhoneNumber(expectedNumber)

            // full number
            repeat(7) {
                val selection = selections[it + 3]
                selectValue(selection.first, selection.second, expectedNumber)
            }

            scenario.recreate()
            checkPhoneNumber(expectedNumber)

            // changed
            selectValue(1, 0, expectedNumber)
            selectValue(3, 2, expectedNumber)

            scenario.recreate()
            checkPhoneNumber(expectedNumber)
        }
    }

    // click digit, select radio button, and update expected values
    private fun selectValue(digit: Int, radioButton: Int, expectedNumber: MutableList<Int?>) {
        clickDigit(digit)
        clickRadioButton(radioButton)
        closeDialog()
        expectedNumber[digit] = digitsOrder[radioButton]
    }
}
