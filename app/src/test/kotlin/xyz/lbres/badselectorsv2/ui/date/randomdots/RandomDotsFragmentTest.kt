package xyz.lbres.badselectorsv2.ui.date.randomdots

import androidx.test.core.app.ActivityScenario
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withText
import androidx.test.ext.junit.runners.AndroidJUnit4
import io.mockk.every
import io.mockk.mockkStatic
import io.mockk.unmockkAll
import org.hamcrest.Matchers.allOf
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.experimental.categories.Category
import org.junit.runner.RunWith
import org.robolectric.Robolectric
import xyz.lbres.badselectorsv2.BaseActivity
import xyz.lbres.badselectorsv2.R
import xyz.lbres.badselectorsv2.ui.date.checkDate
import xyz.lbres.badselectorsv2.ui.testutils.matchers.atIndex
import xyz.lbres.badselectorsv2.ui.testutils.navigateToSelector
import xyz.lbres.badselectorsv2.ui.testutils.viewassertions.isNotPresented
import xyz.lbres.kotlinutils.utils.simpleIf
import java.time.LocalDate

@Category(Robolectric::class)
@RunWith(AndroidJUnit4::class)
class RandomDotsFragmentTest {
    private val mockDate = LocalDate.of(2025, 1, 1)

    private val generatedText = onView(withId(R.id.generatedNumber))
    private val clearButton = onView(withId(R.id.clearButton))
    private val layoutId = R.id.dotsLayout
    private val maxButtons = 100

    private var scenario: ActivityScenario<BaseActivity>? = null

    @Before
    fun setupTest() {
        mockkStatic(LocalDate::class)
        every { LocalDate.now() } returns mockDate
        scenario = ActivityScenario.launchActivityForResult(BaseActivity::class.java)
        navigateToSelector("Date", "Random Dots")
    }

    @After
    fun cleanUpTest() {
        scenario = null
        unmockkAll()
    }

    @Test
    fun initialUi() {
        generatedText.check(matches(allOf(isDisplayed(), withText(""))))
        clearButton.check(isNotPresented())
        onView(withText("Tap to select the month")).check(matches(isDisplayed()))
        checkButtonsDisplayed(setTo(12))
        checkDate()
    }

    @Test
    fun selectDate() {
        // TODO
    }

    @Test
    fun clearGenerated() {
        // TODO
    }

    @Test
    fun restart() {
        // TODO
    }

    @Test
    fun recreate() {
        // TODO
    }

    private fun clickButton(index: Int) {
        onView(atIndex(withId(layoutId), index)).perform(click())
    }

    private fun checkButtonsDisplayed(indices: Set<Int>) {
        repeat(maxButtons) {
            val matcher = simpleIf(it in indices, matches(isDisplayed()), isNotPresented())
            onView(atIndex(withId(layoutId), it)).check(matcher)
        }
    }

    // create a set containing values between 0 and the provided max
    private fun setTo(max: Int) = (0 until max).toSet()
}
