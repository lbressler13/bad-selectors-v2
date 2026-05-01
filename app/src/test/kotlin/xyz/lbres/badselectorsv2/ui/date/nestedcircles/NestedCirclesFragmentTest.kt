package xyz.lbres.badselectorsv2.ui.date.nestedcircles

import androidx.test.core.app.ActivityScenario
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.doesNotExist
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.isEnabled
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
import xyz.lbres.badselectorsv2.ui.testutils.enabledMatcher
import xyz.lbres.badselectorsv2.ui.testutils.isDisabled
import xyz.lbres.badselectorsv2.ui.testutils.matchers.atIndex
import xyz.lbres.badselectorsv2.ui.testutils.navigateToSelector
import xyz.lbres.badselectorsv2.testutils.runWithFailMessage
import xyz.lbres.badselectorsv2.ui.date.padToFour
import xyz.lbres.badselectorsv2.ui.date.padToTwo
import xyz.lbres.badselectorsv2.ui.testutils.viewactions.forceClick
import xyz.lbres.kotlinutils.general.simpleIf
import java.time.LocalDate
import kotlin.test.assertFails

@Category(Robolectric::class)
@RunWith(AndroidJUnit4::class)
class NestedCirclesFragmentTest {
    private val mockDate = LocalDate.of(2025, 1, 1)

    private val minusButton = onView(withId(R.id.previousYearsButton))
    private val plusButton = onView(withId(R.id.nextYearsButton))
    private val monthsCircleId = R.id.monthsLayout
    private val daysCircleId = R.id.daysLayout
    private val yearsCircleId = R.id.yearsLayout

    private var scenario: ActivityScenario<BaseActivity>? = null

    @Before
    fun setupTest() {
        // TODO check if this is needed
        mockkStatic(LocalDate::class)
        every { LocalDate.now() } returns mockDate
        scenario = ActivityScenario.launchActivityForResult(BaseActivity::class.java)
        navigateToSelector("Date", "Nested Circles")
    }

    @After
    fun cleanUpTest() {
        scenario = null
        unmockkAll()
    }

    @Test
    fun initialUi() {
        checkDate("________")
        checkYearsRangeButtons(true, false)

        checkAllCirclesEnabled()
        // check exact # children
        onView(atIndex(withId(monthsCircleId), 12)).check(doesNotExist())
        onView(atIndex(withId(daysCircleId), 31)).check(doesNotExist())
        onView(atIndex(withId(yearsCircleId), 60)).check(doesNotExist())
    }

    @Test
    fun useButtons() {
        repeat(12) {
            clickCircleButton(monthsCircleId, it)
            checkDate("${padToTwo(it + 1)}______")
        }

        repeat(31) {
            clickCircleButton(daysCircleId, it)
            checkDate("12${padToTwo(it + 1)}____")
        }

        val startYear = mockDate.year - 60 + 1
        repeat(60) {
            clickCircleButton(yearsCircleId, it)
            checkDate("1231${startYear + it}")
        }
    }

    @Test
    fun disableButtons() {
        repeat(12) {
            val disabledDays = when (it) {
                1 -> listOf(29, 30)
                3, 5, 8, 10 -> listOf(30)
                else -> emptyList()
            }
            clickCircleButton(monthsCircleId, it)
            runWithFailMessage("Error checking month $it") {
                checkCircle(monthsCircleId)
                checkCircle(daysCircleId, disabledDays)
                checkCircle(yearsCircleId)
            }
        }

        // set all buttons enabled
        clickCircleButton(monthsCircleId, 0)
        clickCircleButton(daysCircleId, 0)
        clickCircleButton(yearsCircleId, 0)

        repeat(31) {
            val disabledMonths = when (it) {
                30 -> listOf(1, 3, 5, 8, 10)
                29 -> listOf(1)
                else -> emptyList()
            }
            clickCircleButton(daysCircleId, it)
            runWithFailMessage("Error checking day $it") {
                checkCircle(monthsCircleId, disabledMonths)
                checkCircle(daysCircleId)
                checkCircle(yearsCircleId)
            }
        }
    }

    @Test
    fun changeYearsRange() {
        val startYear = mockDate.year - 60 + 1
        val maxChanges = mockDate.year / 60

        // decrement
        minusButton.perform(forceClick())
        checkDate("________")
        checkYearsRangeButtons(true, true)
        checkAllCirclesEnabled()

        var changedStart = mockDate.year - 60 * 2 + 1
        repeat(60) {
            clickCircleButton(yearsCircleId, it)
            checkDate("____${changedStart + it}")
        }

        // decrement to zero
        repeat(maxChanges - 1) { minusButton.perform(forceClick()) }
        checkYearsRangeButtons(false, true)
        repeat(60) {
            clickCircleButton(yearsCircleId, it)
            checkDate("____${padToFour(it)}")
        }

        // increment
        plusButton.perform(forceClick())
        checkYearsRangeButtons(true, true)
        repeat(60) {
            clickCircleButton(yearsCircleId, it)
            checkDate("____${padToFour(it + 60)}")
        }

        // increment to start
        repeat(maxChanges - 1) { plusButton.perform(forceClick()) }
        checkYearsRangeButtons(true, false)
        repeat(60) {
            clickCircleButton(yearsCircleId, it)
            checkDate("____${startYear + it}")
        }

        // doesn't affect day or month selectors
        clickCircleButton(monthsCircleId, 3)
        clickCircleButton(daysCircleId, 29)
        clickCircleButton(yearsCircleId, 0)
        checkCircle(monthsCircleId, listOf(1))
        checkCircle(daysCircleId, listOf(30))
        checkCircle(yearsCircleId)
        checkDate("0430$startYear")

        minusButton.perform(forceClick())
        minusButton.perform(forceClick())
        clickCircleButton(yearsCircleId, 0)
        checkCircle(monthsCircleId, listOf(1))
        checkCircle(daysCircleId, listOf(30))
        checkCircle(yearsCircleId)
        checkYearsRangeButtons(true, true)
        checkDate("0430${startYear - 60 * 2}")

        plusButton.perform(forceClick())
        clickCircleButton(yearsCircleId, 0)
        checkCircle(monthsCircleId, listOf(1))
        checkCircle(daysCircleId, listOf(30))
        checkCircle(yearsCircleId)
        checkYearsRangeButtons(true, true)
        checkDate("0430${startYear - 60}")
    }

    @Test
    fun recreate() {
        // blank

        // with partial date

        // with full date

        // with shifted year range

        // with disabled buttons

        // with new changed date
    }

    private fun clickCircleButton(parentId: Int, index: Int) {
        onView(atIndex(withId(parentId), index)).perform(forceClick())
    }

    private fun checkCircle(parentId: Int, disabledButtons: List<Int> = emptyList()) {
        val childCount = when (parentId) {
            R.id.monthsLayout -> 12
            R.id.daysLayout -> 31
            R.id.yearsLayout -> 60
            else -> 0 // never reaches this case
        }

        val parentMatcher = withId(parentId)
        repeat(childCount) {
            runWithFailMessage("Failed checking button at $it with disabled buttons $disabledButtons") {
                val buttonMatcher = enabledMatcher(it !in disabledButtons)
                onView(atIndex(parentMatcher, it)).check(matches(buttonMatcher))
            }
        }
    }

    private fun checkAllCircles(disabledMonths: List<Int>, disabledDays: List<Int>, disabledYears: List<Int>) {
        checkCircle(monthsCircleId, disabledMonths)
        checkCircle(daysCircleId, disabledDays)
        checkCircle(yearsCircleId, disabledYears)
    }

    private fun checkAllCirclesEnabled() {
        checkCircle(monthsCircleId)
        checkCircle(daysCircleId)
        checkCircle(yearsCircleId)
    }

    private fun checkYearsRangeButtons(minusEnabled: Boolean, plusEnabled: Boolean) {
        val enabledMatcher = { enabled: Boolean ->
            simpleIf(enabled, isEnabled(), isDisabled())
        }
        minusButton.check(matches(allOf(isDisplayed(), enabledMatcher(minusEnabled))))
        plusButton.check(matches(allOf(isDisplayed(), enabledMatcher(plusEnabled))))
    }
}
