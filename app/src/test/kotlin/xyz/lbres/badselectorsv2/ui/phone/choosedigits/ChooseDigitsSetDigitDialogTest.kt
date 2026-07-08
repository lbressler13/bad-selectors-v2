package xyz.lbres.badselectorsv2.ui.phone.choosedigits

import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.isChecked
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.isEnabled
import androidx.test.espresso.matcher.ViewMatchers.isNotChecked
import androidx.test.espresso.matcher.ViewMatchers.withText
import androidx.test.ext.junit.runners.AndroidJUnit4
import io.mockk.every
import io.mockk.mockkConstructor
import io.mockk.unmockkAll
import io.mockk.verify
import org.hamcrest.Matchers.allOf
import org.junit.After
import org.junit.Assert.assertFalse
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.shadows.ShadowDialog
import xyz.lbres.badselectorsv2.phone.choosedigits.ChooseDigitsViewModel
import xyz.lbres.badselectorsv2.phone.utils.digitsRange
import xyz.lbres.badselectorsv2.phone.utils.numDigits
import xyz.lbres.badselectorsv2.phone.withMockedPhoneRange
import xyz.lbres.badselectorsv2.ui.phone.checkPhoneNumber
import xyz.lbres.badselectorsv2.ui.phone.clickDigit
import xyz.lbres.badselectorsv2.ui.phone.digitViews
import xyz.lbres.badselectorsv2.ui.testutils.closeDialog
import xyz.lbres.badselectorsv2.ui.testutils.onViewInDialog
import xyz.lbres.kotlinutils.collection.list.listOfNulls
import xyz.lbres.kotlinutils.utils.simpleIf

@RunWith(AndroidJUnit4::class)
class ChooseDigitsSetDigitDialogTest {
    private val doneButton = onViewInDialog(withText("Done"))

    @After
    fun cleanupTest() {
        unmockkAll()
    }

    @Test
    fun testInitialUi() {
        launchChooseDigitsFragment()
        clickDigit(0)
        radioButtons.forEach { it.check(matches(allOf(isEnabled(), isNotChecked()))) }
        onViewInDialog(withText("Select Digit Value")).check(matches(isDisplayed()))
        doneButton.check(matches(isDisplayed()))
        closeDialog()
    }

    @Test
    fun testCloseDialog() {
        launchChooseDigitsFragment()
        clickDigit(0)
        doneButton.perform(click())
        val dialog = ShadowDialog.getLatestDialog()
        assertFalse(dialog.isShowing)
    }

    @Test
    fun testInteractWithUi() {
        launchChooseDigitsFragment()
        clickDigit(0)
        digitsRange.forEach {
            clickRadioButton(it)
            checkSelectedButton(it)
        }
        closeDialog()
    }

    @Test
    fun testValuesPersisted() {
        withMockedPhoneRange(shuffledMocks = listOf(digitsRange.toList())) {
            launchChooseDigitsFragment()
            val digit = digitViews[3]

            digit.perform(click())
            clickRadioButton(0)
            closeDialog()
            digit.check(matches(withText("0")))

            digit.perform(click())
            checkSelectedButton(0)

            clickRadioButton(6)
            closeDialog()
            digit.check(matches(withText("6")))

            digit.perform(click())
            checkSelectedButton(6)
            closeDialog()
        }
    }

    @Test
    fun testInvalidIndex() {
        mockkConstructor(ChooseDigitsViewModel::class)
        every { constructedWith<ChooseDigitsViewModel>().currentIndex } returns -1

        launchChooseDigitsFragment()
        clickDigit(0)

        // dialog should close immediately
        val dialog = ShadowDialog.getLatestDialog()
        assertFalse(dialog.isShowing)
        checkPhoneNumber(listOfNulls(numDigits))
        verify(exactly = 0) { anyConstructed<ChooseDigitsViewModel>().setCurrentDigit(any()) }
    }

    // check which radio button is selected
    private fun checkSelectedButton(selectedIndex: Int) {
        radioButtons.forEachIndexed { index, view ->
            val matcher = simpleIf(index == selectedIndex, isChecked(), isNotChecked())
            view.check(matches(matcher))
        }
    }
}
