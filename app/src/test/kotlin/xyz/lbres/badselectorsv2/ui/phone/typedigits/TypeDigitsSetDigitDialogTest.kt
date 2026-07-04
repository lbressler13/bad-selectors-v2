package xyz.lbres.badselectorsv2.ui.phone.typedigits

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
import org.hamcrest.Matchers.allOf
import org.junit.After
import org.junit.Assert.assertFalse
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.shadows.ShadowDialog
import xyz.lbres.badselectorsv2.phone.typedigits.TypeDigitsSetDigitDialog
import xyz.lbres.badselectorsv2.phone.typedigits.TypeDigitsViewModel
import xyz.lbres.badselectorsv2.phone.utils.digitsRange
import xyz.lbres.badselectorsv2.testutils.mockLog
import xyz.lbres.badselectorsv2.ui.phone.clickDigit
import xyz.lbres.badselectorsv2.ui.phone.digitViews
import xyz.lbres.badselectorsv2.ui.testutils.closeDialog
import xyz.lbres.badselectorsv2.ui.testutils.onViewInDialog

@RunWith(AndroidJUnit4::class)
class TypeDigitsSetDigitDialogTest {
    private val digitsOrder = listOf(6, 3, 0, 1, 9, 4, 2, 8, 7, 5)

    private val doneButton = onViewInDialog(withText("Done"))

    @Before
    fun setupTest() {
        mockLog()
    }

    @After
    fun cleanupTest() {
        unmockkAll()
    }

    @Test
    fun testInitialUi() {
        launchTypeDigitsFragment()
        clickDigit(0)
        radioButtons.forEach { it.check(matches(allOf(isEnabled(), isNotChecked()))) }
        onViewInDialog(withText("Select Digit Value")).check(matches(isDisplayed()))
    }

    @Test
    fun testCloseDialog() {
        launchTypeDigitsFragment()
        clickDigit(0)
        doneButton.perform(click())
        val dialog = ShadowDialog.getLatestDialog()
        assertFalse(dialog.isShowing)
    }

    @Test
    fun testInteractWithUi() {
        launchTypeDigitsFragment()
        clickDigit(0)
        radioButtons.forEachIndexed { index, view ->
            clickRadioButton(index)
            checkSelectedButton(index)
        }
    }

    @Test
    fun testValuesPersisted() {
        withMockedDigitsOrder(digitsRange.toList()) {
            launchTypeDigitsFragment()
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
        }
    }

    @Test
    fun testInvalidIndex() {
        mockkConstructor(TypeDigitsViewModel::class)
        every { constructedWith<TypeDigitsViewModel>().selectValue(any(), any()) } returns 0
        val scenario = launchTypeDigitsFragment()

        val dialog = TypeDigitsSetDigitDialog(-1)

        // TODO
    }

    private fun checkSelectedButton(selectedIndex: Int) {
        radioButtons.forEachIndexed { index, view ->
            if (index == selectedIndex) {
                view.check(matches(isChecked()))
            } else {
                view.check(matches(isNotChecked()))
            }
        }
    }
}
