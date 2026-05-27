package xyz.lbres.badselectorsv2.ui.calculator.addones

import androidx.test.core.app.ActivityScenario
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.doesNotExist
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.experimental.categories.Category
import org.junit.runner.RunWith
import org.robolectric.Robolectric
import xyz.lbres.badselectorsv2.BaseActivity
import xyz.lbres.badselectorsv2.R
import xyz.lbres.badselectorsv2.ui.calculator.clickClear
import xyz.lbres.badselectorsv2.ui.calculator.clickEquals
import xyz.lbres.badselectorsv2.ui.calculator.numberButtons
import xyz.lbres.badselectorsv2.ui.calculator.typeText
import xyz.lbres.badselectorsv2.ui.testutils.navigateToSelector
import xyz.lbres.kotlinutils.list.listOfNulls
import xyz.lbres.kotlinutils.list.mutablelist.mutableListOfNulls

@Category(Robolectric::class)
@RunWith(AndroidJUnit4::class)
class AddOnesFragmentTest {

    private var scenario: ActivityScenario<BaseActivity>? = null

    @Before
    fun setupTest() {
        scenario = ActivityScenario.launchActivityForResult(BaseActivity::class.java)
        navigateToSelector("Calculator", "Add Ones")
    }

    @After
    fun cleanupTest() {
        scenario = null
    }

    @Test
    fun initialUi() {
        checkEnabledState("") // main text, enabled/disabled buttons, save button, saved values

        // check extra buttons not displayed
        onView(withId(R.id.timesButton)).check(doesNotExist())
        onView(withId(R.id.divideButton)).check(doesNotExist())
        numberButtons.forEachIndexed { index, view ->
            if (index != 1) {
                view.check(doesNotExist())
            }
        }
    }

    @Test
    fun equalsButton() {
        val savedValues: MutableList<Int?> = mutableListOfNulls(2)
        // no saved values
        typeText("1")
        clickEquals()
        checkSaveState("1", savedValues)
        clickClear()

        typeText("1+1+1+1")
        clickEquals()
        checkSaveState("4", savedValues)
        clickClear()

        // saved values
        typeAndSaveToIndex("1+1+1", 3, 0, savedValues)
        typeContent(listOf("1+1+", 0, "-1"))
        clickEquals()
        checkSaveState("4", savedValues)
    }

    @Test
    fun backspace() {
        // TODO
    }

    @Test
    fun clear() {
        val savedValues: MutableList<Int?> = mutableListOf(2, -1)
        fun typeAndCheckClear(content: List<Any>, withEquals: Boolean = false) {
            typeContent(content)
            if (withEquals) {
                clickEquals()
            }
            clickClear()
            checkEnabledState("", savedValues)
        }

        // text
        typeText("1+1+") // cannot use helper w/out saved values
        clickClear()
        checkEnabledState("")

        // saved values
        typeAndSaveToIndex("1+1", savedValues[0]!!, 0, savedValues)
        typeAndSaveToIndex("1-1-1", savedValues[1]!!, 1, savedValues)
        typeAndCheckClear(listOf(0, "+1-", 1))

        // computed value
        typeAndCheckClear(listOf("1+1"), true)
        typeAndCheckClear(listOf(0, "+1"), true)

        // error
        typeAndCheckClear(listOf("11"), true)
    }

    @Test
    fun compute() {
        // TODO

        // cannot delete values in use
    }

    @Test
    fun saveAndDeleteValues() {
        val savedValues: MutableList<Int?> = mutableListOfNulls(2)

        // save
        typeText("1+1+1+1+1+1")
        clickEquals()
        saveButton.perform(click())
        savedValues[0] = 6
        checkEnabledState("", savedValues)

        typeText("1-1-1-1")
        clickEquals()
        saveButton.perform(click())
        savedValues[1] = -2
        checkEnabledState("", savedValues)

        typeText("1+1")
        clickEquals()
        checkSaveState("2", savedValues) // check that save is disabled
        clickClear()

        // delete unused
        deleteAtIndex(1, savedValues)

        typeText("1+1+1")
        clickEquals()
        checkSaveState("3", savedValues)
        saveButton.perform(click())
        savedValues[1] = 3
        checkEnabledState("", savedValues)

        // delete in save view
        typeText("1-1")
        clickEquals()

        deleteAtIndex(0, savedValues)
        deleteAtIndex(1, savedValues)
        checkSaveState("0")

        saveButton.perform(click())
        savedValues[0] = 0
        checkEnabledState("", savedValues)

        // using saved value
        typeContent(listOf(0, "-1"))
        clickEquals()
        checkSaveState("-1", savedValues)
        saveButton.perform(click())
        savedValues[1] = -1
        checkEnabledState("", savedValues)

        // delete in error view
        typeContent(listOf(0, 1))
        clickEquals()
        checkErrorState(savedValues)
        deleteAtIndex(0, savedValues)
        deleteAtIndex(1, savedValues)
        checkErrorState()
    }

    @Test
    fun computeError() {
        val savedValues: MutableList<Int?> = mutableListOfNulls(2)
        fun typeAndCheckError(content: List<Any>, savedValues: List<Int?> = listOfNulls(2)) {
            typeContent(content)
            clickEquals()
            checkErrorState(savedValues)
            clickClear()
        }

        // no saved values
        typeAndCheckError(listOf("+"))
        typeAndCheckError(listOf("1+11"))

        // saved values
        typeAndSaveToIndex("1+1+1", 3, 0, savedValues)
        typeAndCheckError(listOf("1", 0), savedValues)

        typeAndSaveToIndex("1-1", 0, 1, savedValues)
        typeAndCheckError(listOf(0, 1), savedValues)

        typeAndCheckError(listOf(1, "+1--"), savedValues)

        // delete values in error state
        typeContent(listOf(0, "-"))
        clickEquals()
        deleteAtIndex(0, savedValues)
        checkErrorState(savedValues)
        deleteAtIndex(1, savedValues)
        checkErrorState(savedValues)
    }

    @Test
    fun recreate() {
        // blank

        // text

        // complete text view

        // error

        // saved values

        // TODO
    }
}
