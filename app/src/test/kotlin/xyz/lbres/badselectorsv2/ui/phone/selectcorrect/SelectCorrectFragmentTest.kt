package xyz.lbres.badselectorsv2.ui.phone.selectcorrect

import androidx.test.core.app.ActivityScenario
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.ViewInteraction
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.isEnabled
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withText
import androidx.test.ext.junit.runners.AndroidJUnit4
import io.mockk.every
import io.mockk.mockkConstructor
import io.mockk.unmockkAll
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
import xyz.lbres.badselectorsv2.ui.phone.checkPhoneNumber
import xyz.lbres.badselectorsv2.ui.phone.digitViews
import xyz.lbres.badselectorsv2.ui.testutils.matchers.hasThemeTextColor
import xyz.lbres.badselectorsv2.ui.testutils.navigateToSelector
import xyz.lbres.badselectorsv2.ui.testutils.viewactions.forceClick
import xyz.lbres.badselectorsv2.ui.testutils.viewassertions.isNotPresented
import xyz.lbres.kotlinutils.list.IntList

@Category(Robolectric::class)
@RunWith(AndroidJUnit4::class)
class SelectCorrectFragmentTest {
    private val mockGeneratedValues = listOf(
        listOf(7, 4, 0, 2, 5, 6, 8, 1, 3, 9),
        listOf(0, 1, 2, 3, 4, 5, 6, 7, 8, 9),
        listOf(3, 6, 2, 7, 9, 8, 5, 4, 1, 0),
        listOf(9, 8, 7, 4, 5, 6, 2, 1, 0, 3),
        listOf(1, 3, 5, 7, 9, 0, 2, 4, 6, 8),
        listOf(5, 6, 2, 9, 1, 0, 3, 4, 8, 7),
    )

    private val generateButton = onView(withId(R.id.generateNumberButton))
    private val markCorrectMessage = onView(withId(R.id.markCorrectText))
    private val restartButton = onView(withId(R.id.restartButton))

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
    fun generateNumber() {
        // TODO without mocking in order to keep frozen
        // mockGenerateNumber()
        launchFragment()


        val textSavers = digitViews.map { TextSaver(it) }
        val savedDigits: MutableSet<Int> = mutableSetOf()
        val checkSaved = {
            textSavers.forEachIndexed { index, saver ->
                if (index in savedDigits) {
                    saver.matchPreviousText()
                } else {
                    saver.notMatchPreviousText()
                }
            }
            checkDigitColors(savedDigits)
        }

        textSavers.forEach { it.saveText() }
        textSavers.forEach { it.matchPreviousText() }

        generateButton.checkDisplayedAndClick()
        textSavers.forEach { it.notMatchPreviousText() }
        textSavers.forEach { it.saveText() }

        digitViews[6].checkDisplayedAndClick()
        digitViews[9].checkDisplayedAndClick()
        savedDigits.add(6)
        savedDigits.add(9)

        generateButton.checkDisplayedAndClick()
        checkSaved()
    }

    @Test
    fun completeNumber() {
        mockGenerateNumber()
        launchFragment()
        val selectOrder = listOf(
            listOf(3, 5),
            emptyList(),
            listOf(4, 9, 0, 2),
            listOf(6),
            listOf(1, 8, 7),
        )

        val selectedDigits: MutableSet<Int> = mutableSetOf()
        selectOrder.forEachIndexed { index, digits ->
            generateButton.checkDisplayedAndClick()
            checkPhoneNumber(mockGeneratedValues[index + 1])
            digits.forEach { digitViews[it].checkDisplayedAndClick() }
            selectedDigits.addAll(digits)
            checkDigitColors(selectedDigits)
        }
        checkRestartUi(mockGeneratedValues[5])
    }

    @Test
    fun restart() {
        mockGenerateNumber()
        launchFragment()
        digitViews.forEach { it.checkDisplayedAndClick() }
        restartButton.checkDisplayedAndClick()
        checkInitialUi(mockGeneratedValues[1])
    }

    @Test
    fun recreate() {
        // TODO check colors persisted
        mockGenerateNumber()
        val scenario = launchFragment()

        // initial ui
        checkInitialUi(mockGeneratedValues[0])
        scenario.recreate()
        checkInitialUi(mockGeneratedValues[0])

        // generated
        generateButton.checkDisplayedAndClick()
        checkDigitColors(emptySet())
        checkPhoneNumber(mockGeneratedValues[1])
        scenario.recreate()
        checkDigitColors(emptySet())
        checkPhoneNumber(mockGeneratedValues[1])

        digitViews[3].checkDisplayedAndClick()
        digitViews[7].checkDisplayedAndClick()
        checkDigitColors(setOf(3, 7))
        scenario.recreate()
        checkDigitColors(setOf(3, 7))
        checkPhoneNumber(mockGeneratedValues[1])

        digitViews[2].checkDisplayedAndClick()
        checkDigitColors(setOf(2, 3, 7))
        scenario.recreate()
        checkDigitColors(setOf(2, 3, 7))
        checkPhoneNumber(mockGeneratedValues[1])

        // completed number
        digitViews.forEach { it.checkDisplayedAndClick() }
        checkRestartUi(mockGeneratedValues[1])
        scenario.recreate()
        checkRestartUi(mockGeneratedValues[1])

        restartButton.checkDisplayedAndClick()
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
        // dividerViews.forEach { it.check(matches(hasStandardColor)) }
    }

    private fun checkRestartUi(phoneNumber: IntList) {
        generateButton.check(isNotPresented())
        markCorrectMessage.check(isNotPresented())
        restartButton.check(matches(allOf(isDisplayed(), isEnabled())))
        checkPhoneNumber(phoneNumber)
        checkDigitColors(digitsRange.toSet())
        // dividerViews.forEach { it.check(matches(hasSelectedColor)) }
    }

    private fun checkDigitColors(selectedDigits: Set<Int>) {
        digitViews.forEachIndexed { index, view ->
            if (index in selectedDigits) {
                view.check(matches(hasSelectedColor))
            } else {
                view.check(matches(hasStandardColor))
            }
        }
    }

    private fun ViewInteraction.checkDisplayedAndClick() {
        check(matches(isDisplayed()))
        perform(forceClick())
    }

    private val hasStandardColor = hasThemeTextColor(com.google.android.material.R.attr.colorOnBackground)
    private val hasSelectedColor = hasThemeTextColor(com.google.android.material.R.attr.colorPrimary)
}
