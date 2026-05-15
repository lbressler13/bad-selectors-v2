package xyz.lbres.badselectorsv2.ui.phone.selectcorrect

import androidx.test.core.app.ActivityScenario
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.isEnabled
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withText
import androidx.test.ext.junit.runners.AndroidJUnit4
import io.mockk.every
import io.mockk.mockkConstructor
import io.mockk.unmockkAll
import org.hamcrest.CoreMatchers.not
import org.hamcrest.core.AllOf.allOf
import org.junit.After
import org.junit.Test
import org.junit.experimental.categories.Category
import org.junit.runner.RunWith
import org.robolectric.Robolectric
import xyz.lbres.badselectorsv2.BaseActivity
import xyz.lbres.badselectorsv2.R
import xyz.lbres.badselectorsv2.phone.utils.PhoneNumberGenerator
import xyz.lbres.badselectorsv2.phone.utils.digitsRange
import xyz.lbres.badselectorsv2.phone.utils.numDigits
import xyz.lbres.badselectorsv2.ui.phone.checkPhoneNumber
import xyz.lbres.badselectorsv2.ui.phone.digitViews
import xyz.lbres.badselectorsv2.ui.phone.dividerViews
import xyz.lbres.badselectorsv2.ui.testutils.TextSaver
import xyz.lbres.badselectorsv2.ui.testutils.closeDialog
import xyz.lbres.badselectorsv2.ui.testutils.matchers.hasThemeTextColor
import xyz.lbres.badselectorsv2.ui.testutils.navigateToSelector
import xyz.lbres.badselectorsv2.ui.testutils.onViewInDialog
import xyz.lbres.badselectorsv2.ui.testutils.openSettingsDialog
import xyz.lbres.badselectorsv2.ui.testutils.viewactions.forceClick
import xyz.lbres.badselectorsv2.ui.testutils.viewassertions.isNotPresented
import xyz.lbres.kotlinutils.list.IntList

@Category(Robolectric::class)
@RunWith(AndroidJUnit4::class)
class SelectCorrectFragmentTest {
    private val mockGeneratedValues = listOf(
        listOf(7, 7, 6, 5, 1, 3, 5, 2, 6, 8),
        listOf(4, 2, 1, 9, 8, 8, 6, 4, 6, 5),
        listOf(3, 6, 2, 7, 9, 8, 5, 4, 1, 0),
        listOf(3, 0, 1, 2, 3, 5, 8, 8, 3, 5),
        listOf(6, 9, 8, 7, 3, 1, 6, 5, 0, 0),
    )

    private val generateButton = onView(withId(R.id.generateNumberButton))
    private val markCorrectMessage = onView(withId(R.id.markCorrectText))
    private val restartButton = onView(withId(R.id.restartButton))
    private val singleSelectSwitch = onViewInDialog(withId(R.id.singleSelectSwitch))

    @After
    fun cleanupTest() {
        unmockkAll()
    }

    @Test
    fun initialUi() {
        mockGenerateNumber()
        launchFragment()
        checkInitialUi(mockGeneratedValues[0])
    }

    @Test
    fun generateAndCompleteNumber() {
        mockGenerateNumber()
        launchFragment()

        for (i in 1 until mockGeneratedValues.size) {
            generateButton.perform(forceClick())
            checkPhoneNumber(mockGeneratedValues[i])
        }
        digitViews.forEach { it.perform(forceClick()) }
        checkRestartUi(mockGeneratedValues.last())
    }

    @Test
    fun completeNumberUnmocked() {
        launchFragment()
        val selectOrder = listOf(
            listOf(3, 5),
            emptyList(),
            listOf(4, 9, 0, 2),
            listOf(6),
            listOf(1, 8, 7),
        )
        val textSaver = TextSaver()
        val saveText = { textSaver.saveText() }
        val withPreviousText = { textSaver.withPreviousText() }

        digitViews.forEach { it.perform(saveText()) }
        val selectedDigits: MutableSet<Int> = mutableSetOf()

        selectOrder.forEachIndexed { index, digits ->
            generateButton.perform(forceClick())

            // check that the correct digits are frozen from previous update
            digitViews.forEachIndexed { index, view ->
                when (index) {
                    in selectedDigits -> view.check(matches(withPreviousText()))
                    else -> view.check(matches(not(withPreviousText())))
                }
            }

            // add each digit
            digits.forEach {
                digitViews[it].perform(forceClick())
                selectedDigits.add(it)
                checkDigitColors(selectedDigits, selectedDigits.size == numDigits)
            }
            digitViews.forEach { it.perform(saveText()) }
        }
        digitViews.forEach { it.check(matches(withPreviousText())) }
        checkRestartUi(null)
    }

    @Test
    fun singleSelect() {
        mockGenerateNumber()
        launchFragment()

        // enable single select
        openSettingsDialog()
        singleSelectSwitch.perform(click())
        closeDialog()

        // click single digit
        digitViews[4].perform(forceClick())
        checkPhoneNumber(mockGeneratedValues[1])
        digitViews[6].perform(forceClick())
        checkPhoneNumber(mockGeneratedValues[2])

        // generate normally
        generateButton.perform(forceClick())
        checkPhoneNumber(mockGeneratedValues[3])

        digitViews[9].perform(forceClick())
        checkPhoneNumber(mockGeneratedValues[4])

        // change back
        openSettingsDialog()
        singleSelectSwitch.perform(click())
        closeDialog()
        checkPhoneNumber(mockGeneratedValues[4])

        digitViews[0].perform(click())
        digitViews[7].perform(click())
        checkPhoneNumber(mockGeneratedValues[4])
    }

    @Test
    fun restart() {
        mockGenerateNumber()
        launchFragment()
        digitViews.forEach { it.perform(forceClick()) }
        restartButton.perform(forceClick())
        checkInitialUi(mockGeneratedValues[1])
    }

    @Test
    fun settingsDialog() {
        mockGenerateNumber()
        launchFragment()
        digitViews[3].perform(forceClick())

        // open dialog
        openSettingsDialog()
        onViewInDialog(withText("Settings")).check(matches(isDisplayed()))
        singleSelectSwitch.check(matches(isDisplayed()))

        // close and validate that ui didn't change
        closeDialog()
        checkPhoneNumber(mockGeneratedValues[0])
        checkDigitColors(setOf(3))
    }

    @Test
    fun recreate() {
        mockGenerateNumber()
        val scenario = launchFragment()

        // initial ui
        checkInitialUi(mockGeneratedValues[0])
        scenario.recreate()
        checkInitialUi(mockGeneratedValues[0])

        // generated
        generateButton.perform(forceClick())
        checkDigitColors(emptySet())
        checkPhoneNumber(mockGeneratedValues[1])
        scenario.recreate()
        checkDigitColors(emptySet())
        checkPhoneNumber(mockGeneratedValues[1])

        digitViews[3].perform(forceClick())
        digitViews[7].perform(forceClick())
        checkDigitColors(setOf(3, 7))
        scenario.recreate()
        checkDigitColors(setOf(3, 7))
        checkPhoneNumber(mockGeneratedValues[1])

        digitViews[2].perform(forceClick())
        checkDigitColors(setOf(2, 3, 7))
        scenario.recreate()
        checkDigitColors(setOf(2, 3, 7))
        checkPhoneNumber(mockGeneratedValues[1])

        // completed number
        digitViews.forEach { it.perform(forceClick()) }
        checkRestartUi(mockGeneratedValues[1])
        scenario.recreate()
        checkRestartUi(mockGeneratedValues[1])

        restartButton.perform(forceClick())
        checkInitialUi(mockGeneratedValues[2])
        scenario.recreate()
        checkInitialUi(mockGeneratedValues[2])
    }

    // cannot launch scenario in before block due to mocking requirements
    private fun launchFragment(): ActivityScenario<BaseActivity> {
        val scenario = ActivityScenario.launchActivityForResult(BaseActivity::class.java)
        navigateToSelector("Phone", "Select Correct")
        return scenario
    }

    private fun mockGenerateNumber(mockValues: List<IntList> = mockGeneratedValues) {
        mockkConstructor(PhoneNumberGenerator::class)
        every { constructedWith<PhoneNumberGenerator>().generateNumber(false) } returnsMany mockValues
        every { constructedWith<PhoneNumberGenerator>().reset() } answers { callOriginal() }
    }

    private fun checkInitialUi(phoneNumber: IntList) {
        val generateText = "Generate New Number"
        val markCorrectText = "Tap digits to mark as correct"
        val generateMatcher = allOf(isDisplayed(), isEnabled(), withText(generateText))

        generateButton.check(matches(generateMatcher))
        markCorrectMessage.check(matches(allOf(isDisplayed(), withText(markCorrectText))))
        restartButton.check(isNotPresented())
        checkPhoneNumber(phoneNumber)
        checkDigitColors(emptySet())
    }

    private fun checkRestartUi(phoneNumber: IntList?) {
        generateButton.check(isNotPresented())
        markCorrectMessage.check(isNotPresented())
        restartButton.check(matches(allOf(isDisplayed(), isEnabled())))
        if (phoneNumber != null) {
            checkPhoneNumber(phoneNumber)
        }
        checkDigitColors(digitsRange.toSet(), true)
    }

    private fun checkDigitColors(selectedDigits: Set<Int>, dividersSelected: Boolean = false) {
        val hasStandardColor = hasThemeTextColor(com.google.android.material.R.attr.colorOnBackground)
        val hasSelectedColor = hasThemeTextColor(com.google.android.material.R.attr.colorPrimary)

        digitViews.forEachIndexed { index, view ->
            if (index in selectedDigits) {
                view.check(matches(hasSelectedColor))
            } else {
                view.check(matches(hasStandardColor))
            }
        }
        val dividerMatcher = if (dividersSelected) hasSelectedColor else hasStandardColor
        dividerViews.forEach { it.check(matches(dividerMatcher)) }
    }
}
