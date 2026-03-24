package xyz.lbres.badselectorsv2.ui.date.nestedcircles

import androidx.test.core.app.ActivityScenario
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.doesNotExist
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.isClickable
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.isEnabled
import androidx.test.espresso.matcher.ViewMatchers.isNotClickable
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withText
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.hamcrest.Matchers.allOf
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.experimental.categories.Category
import org.junit.runner.RunWith
import org.robolectric.Robolectric
import xyz.lbres.badselectorsv2.BaseActivity
import xyz.lbres.badselectorsv2.ui.date.checkDate
import xyz.lbres.badselectorsv2.ui.testutils.navigateToSelector
import xyz.lbres.badselectorsv2.R
import xyz.lbres.badselectorsv2.ui.testutils.enabledMatcher
import xyz.lbres.badselectorsv2.ui.testutils.isDisabled
import xyz.lbres.badselectorsv2.ui.testutils.matchers.atIndex
import xyz.lbres.badselectorsv2.ui.testutils.runWithFailMessage
import xyz.lbres.kotlinutils.general.simpleIf

@Category(Robolectric::class)
@RunWith(AndroidJUnit4::class)
class NestedCirclesFragmentTest {
    private val minusButton = onView(withId(R.id.previousYearsButton))
    private val plusButton = onView(withId(R.id.nextYearsButton))
    private val monthsCircleId = R.id.monthsLayout
    private val daysCircleId = R.id.daysLayout
    private val yearsCircleId = R.id.yearsLayout

    private var scenario: ActivityScenario<BaseActivity>? = null


    @Before
    fun setupTest() {
        scenario = ActivityScenario.launchActivityForResult(BaseActivity::class.java)
        navigateToSelector("Date", "Nested Circles")
    }

    @After
    fun cleanUpTest() {
        scenario = null
    }

    @Test
    fun initialUi() {
        checkDate("________")
        minusButton.check(matches(allOf(isDisplayed(), isEnabled())))
        plusButton.check(matches(allOf(isDisplayed(), isDisabled())))

        checkCircle(monthsCircleId)
        checkCircle(daysCircleId)
        checkCircle(yearsCircleId)
        // TODO check exact # children
    }

    @Test
    fun useButtons() {

    }

    @Test
    fun disableButtons() {
        // sets all buttons enabled
        fun reset() {
            clickCircleButton(monthsCircleId, 0)
            clickCircleButton(daysCircleId, 0)
            clickCircleButton(yearsCircleId, 0)
        }

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

        reset()

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

    }

    @Test
    fun recreate() {

    }

    private fun clickCircleButton(parentId: Int, index: Int) {
        onView(atIndex(withId(parentId), index)).perform(click())
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
            println("$it, ${it !in disabledButtons}")
            val buttonMatcher = enabledMatcher(it !in disabledButtons)
            onView(atIndex(parentMatcher, it)).check(matches(buttonMatcher))
        }
    }
}
