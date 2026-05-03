package xyz.lbres.badselectorsv2.ui.date.nestedcircles

import androidx.test.core.app.ActivityScenario
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.assertion.ViewAssertions.doesNotExist
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
import xyz.lbres.badselectorsv2.testutils.runWithFailMessage
import xyz.lbres.badselectorsv2.ui.date.checkDate
import xyz.lbres.badselectorsv2.ui.date.formatDate
import xyz.lbres.badselectorsv2.ui.testutils.enabledMatcher
import xyz.lbres.badselectorsv2.ui.testutils.matchers.atIndex
import xyz.lbres.badselectorsv2.ui.testutils.navigateToSelector
import xyz.lbres.badselectorsv2.ui.testutils.viewactions.forceClick
import java.time.LocalDate

@Category(Robolectric::class)
@RunWith(AndroidJUnit4::class)
class NestedCirclesFragmentTest {
    private val mockDate = LocalDate.of(2025, 1, 1)
    private val numYears = 60
    private val startYear = mockDate.year - numYears + 1
    private val maxChanges = mockDate.year / numYears

    private val minusButton = onView(withId(R.id.previousYearsButton))
    private val plusButton = onView(withId(R.id.nextYearsButton))
    private val monthsCircleId = R.id.monthsLayout
    private val daysCircleId = R.id.daysLayout
    private val yearsCircleId = R.id.yearsLayout

    private var scenario: ActivityScenario<BaseActivity>? = null

    @Before
    fun setupTest() {
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
        checkState()
        onView(withText("Change Available Years")).check(matches(isDisplayed()))
        // check exact number of children
        onView(atIndex(withId(monthsCircleId), 12)).check(doesNotExist())
        onView(atIndex(withId(daysCircleId), 31)).check(doesNotExist())
        onView(atIndex(withId(yearsCircleId), 60)).check(doesNotExist())
    }

    @Test
    fun useButtons() {
        repeat(12) {
            clickCircleButton(monthsCircleId, it)
            checkDate(it + 1)
        }

        repeat(31) {
            clickCircleButton(daysCircleId, it)
            checkDate(12, it + 1)
        }

        repeat(numYears) {
            clickCircleButton(yearsCircleId, it)
            checkDate(12, 31, startYear + it)
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
                checkAllCircles(disabledDays = disabledDays)
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
                checkAllCircles(disabledMonths = disabledMonths)
            }
        }
    }

    @Test
    fun changeYearsRange() {
        // decrement
        minusButton.perform(forceClick())
        checkState(plusEnabled = true)

        repeat(numYears) {
            clickCircleButton(yearsCircleId, it)
            checkDate(year = startYear - numYears + it)
        }

        // decrement to zero
        repeat(maxChanges - 1) { minusButton.perform(forceClick()) }
        checkYearsRangeButtons(false, true)
        repeat(numYears) {
            clickCircleButton(yearsCircleId, it)
            checkDate(year = it)
        }

        // increment
        plusButton.perform(forceClick())
        checkYearsRangeButtons(true, true)
        repeat(numYears) {
            clickCircleButton(yearsCircleId, it)
            checkDate(year = numYears + it)
        }

        // increment to start
        repeat(maxChanges - 1) { plusButton.perform(forceClick()) }
        checkYearsRangeButtons(true, false)
        repeat(numYears) {
            clickCircleButton(yearsCircleId, it)
            checkDate(year = startYear + it)
        }

        // doesn't affect day or month selectors
        clickCircleButton(monthsCircleId, 3)
        clickCircleButton(daysCircleId, 29)
        clickCircleButton(yearsCircleId, 0)
        var date = formatDate(4, 30, startYear)
        checkState(date = date, disabledMonths = listOf(1), disabledDays = listOf(30))

        minusButton.perform(forceClick())
        minusButton.perform(forceClick())
        clickCircleButton(yearsCircleId, 0)
        date = formatDate(4, 30, startYear - numYears * 2)
        checkState(date = date, disabledMonths = listOf(1), disabledDays = listOf(30), plusEnabled = true)

        plusButton.perform(forceClick())
        clickCircleButton(yearsCircleId, 0)
        date = formatDate(4, 30, startYear - numYears)
        checkState(date = date, disabledMonths = listOf(1), disabledDays = listOf(30), plusEnabled = true)
    }

    @Test
    fun recreate() {
        fun checkAndRecreate(check: () -> Unit) {
            check()
            scenario!!.recreate()
            check()
        }

        // blank
        scenario!!.recreate()
        checkState()

        // with partial date
        clickCircleButton(daysCircleId, 12)
        var date = formatDate(day = 13)
        checkAndRecreate { checkState(date = date) }

        clickCircleButton(yearsCircleId, 10)
        date = formatDate(day = 13, year = startYear + 10)
        checkAndRecreate { checkState(date = date) }

        // with full date
        clickCircleButton(monthsCircleId, 4)
        date = formatDate(5, 13, startYear + 10)
        checkAndRecreate { checkState(date = date) }

        // with shifted year range
        minusButton.perform(forceClick())
        clickCircleButton(yearsCircleId, 15)
        date = formatDate(5, 13, startYear - numYears + 15)
        checkAndRecreate { checkState(date = date, plusEnabled = true) }

        repeat(maxChanges - 1) { minusButton.perform(forceClick()) }
        date = formatDate(5, 13, startYear - numYears + 15)
        checkAndRecreate { checkState(date = date, minusEnabled = false, plusEnabled = true) }

        plusButton.perform(forceClick())
        checkAndRecreate { checkState(date = date, plusEnabled = true) }

        // with disabled buttons
        clickCircleButton(monthsCircleId, 1)
        date = formatDate(2, 13, startYear - numYears + 15)
        checkAndRecreate {
            checkState(date = date, plusEnabled = true, disabledDays = listOf(29, 30))
        }

        clickCircleButton(monthsCircleId, 5)
        clickCircleButton(daysCircleId, 29)
        date = formatDate(6, 30, startYear - numYears + 15)
        checkAndRecreate {
            checkState(date = date, plusEnabled = true, disabledMonths = listOf(1), disabledDays = listOf(30))
        }

        // with disabled minus button
        clickCircleButton(monthsCircleId, 0)
        clickCircleButton(daysCircleId, 0)
        date = formatDate(1, 1, startYear - numYears + 15)
        repeat(maxChanges - 1) { minusButton.perform(forceClick()) }
        checkAndRecreate { checkState(date = date, minusEnabled = false, plusEnabled = true) }
    }

    // click on the button at the given index
    private fun clickCircleButton(parentId: Int, index: Int) {
        onView(atIndex(withId(parentId), index)).perform(forceClick())
    }

    // check current state of ui
    private fun checkState(
        date: String = "________",
        disabledMonths: List<Int> = emptyList(),
        disabledDays: List<Int> = emptyList(),
        disabledYears: List<Int> = emptyList(),
        minusEnabled: Boolean = true,
        plusEnabled: Boolean = false,
    ) {
        checkDate(date)
        checkAllCircles(disabledMonths, disabledDays, disabledYears)

        minusButton.check(matches(allOf(isDisplayed(), enabledMatcher(minusEnabled))))
        plusButton.check(matches(allOf(isDisplayed(), enabledMatcher(plusEnabled))))
    }

    // check that the correct buttons are enabled or disabled in all circles
    private fun checkAllCircles(
        disabledMonths: List<Int> = emptyList(),
        disabledDays: List<Int> = emptyList(),
        disabledYears: List<Int> = emptyList(),
    ) {
        checkCircle(monthsCircleId, disabledMonths)
        checkCircle(daysCircleId, disabledDays)
        checkCircle(yearsCircleId, disabledYears)
    }

    // check that the correct buttons are enabled or disabled in a circle
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

    // check that the plus/minus buttons are enabled or disabled
    private fun checkYearsRangeButtons(minusEnabled: Boolean, plusEnabled: Boolean) {
        minusButton.check(matches(allOf(isDisplayed(), enabledMatcher(minusEnabled))))
        plusButton.check(matches(allOf(isDisplayed(), enabledMatcher(plusEnabled))))
    }
}
