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
import xyz.lbres.badselectorsv2.ui.calculator.clickBackspace
import xyz.lbres.badselectorsv2.ui.calculator.clickClear
import xyz.lbres.badselectorsv2.ui.calculator.clickEquals
import xyz.lbres.badselectorsv2.ui.calculator.numberButtons
import xyz.lbres.badselectorsv2.ui.calculator.splitText
import xyz.lbres.badselectorsv2.ui.calculator.typeText
import xyz.lbres.badselectorsv2.ui.testutils.navigateToSelector
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
        val savedValues: MutableList<Int?> = mutableListOfNulls(maxSavedValues)

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
        val savedValues: MutableList<Int?> = mutableListOfNulls(maxSavedValues)

        // can't use with saved values, need to check when they get deleted
        fun typeAndBackspace(text: String) {
            typeText(text)
            val split = splitText(text)
            repeat(text.length) {
                clickBackspace()
                val joinedText = split.subList(0, split.lastIndex - it).joinToString(" ")
                checkEnabledState(joinedText, savedValues)
            }
            checkEnabledState("", savedValues)
        }

        // no saved values
        clickBackspace()
        checkEnabledState("")

        typeAndBackspace("1+1+1")

        // saved values not used
        typeAndSaveToIndex("1+1+1", 3, 0, savedValues)
        typeAndBackspace("1-11+1")

        // saved values
        typeContent(listOf(0))
        clickBackspace()
        checkEnabledState("", savedValues)

        typeAndSaveToIndex(listOf("1-1-1-1-1-1-1-1-1-", 0), -10, 1, savedValues) // 2 digit number

        typeContent(listOf(0, "+1+-", 1, "+1"))
        val inUse = mutableSetOf(0, 1)
        repeatBackspace(2)
        checkEnabledState("3 + 1 + - -10", savedValues, inUse)
        clickBackspace()
        inUse.remove(1)
        checkEnabledState("3 + 1 + -", savedValues, inUse)

        // add back and delete again
        typeContent(listOf(1, "+1"))
        inUse.add(1)
        checkEnabledState("3 + 1 + - -10 + 1", savedValues, inUse)
        repeatBackspace(3)
        inUse.remove(1)
        checkEnabledState("3 + 1 + -", savedValues, inUse)

        repeatBackspace(4)
        checkEnabledState("3", savedValues, inUse)
        clickBackspace()
        inUse.remove(0)
        checkEnabledState("", savedValues, inUse)
    }

    @Test
    fun clear() {
        val savedValues: MutableList<Int?> = mutableListOf(2, -1)

        fun typeAndClear(content: List<Any>, withEquals: Boolean = false) {
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
        typeAndClear(listOf(0, "+1-", 1))

        // computed value
        typeAndClear(listOf("1+1"), true)
        typeAndClear(listOf(0, "+1"), true)

        // error
        typeAndClear(listOf("11"), true)
    }

    @Test
    fun compute() {
        val savedValues: MutableList<Int?> = mutableListOfNulls(maxSavedValues)

        fun typeAndSave(content: List<Any>, result: Int, indexToSave: Int, delete: Boolean = false) {
            typeContent(content)
            clickEquals()
            checkSaveState(result.toString(), savedValues)

            if (delete) {
                deleteAtIndex(indexToSave, savedValues)
            }
            saveButton.perform(click())
            savedValues[indexToSave] = result
        }

        // eq without computed values
        typeText("1")
        clickEquals()
        checkSaveState("1", savedValues)
        clickClear()

        // with computed values
        typeAndSave(listOf("1+1+1+1+1+1+1-1+1+1+1+1"), 10, 0)
        typeAndSave(listOf(0, "+1+1+1+1+1+1"), 16, 1)
        typeAndSave(listOf(0, "+", 1), 26, 0, true)
        typeAndSave(listOf(0, "+", 1, "+1+1+1"), 45, 1, true)
        typeAndSave(listOf(0, "+", 1), 71, 0, true)
        typeAndSave(listOf(0, "+", 1), 116, 0, true)
        typeAndSave(listOf("1-", 0), -115, 1, true)
    }

    @Test
    fun saveAndDeleteValues() {
        val savedValues: MutableList<Int?> = mutableListOfNulls(maxSavedValues)

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

        // after backspace
        typeContent(listOf("1+", 0, "-1"))
        repeatBackspace(3)
        deleteAtIndex(0, savedValues)
        checkEnabledState("1 +", savedValues)

        // delete in error view
        typeContent(listOf(1, "1"))
        clickEquals()
        checkErrorState(savedValues)
        deleteAtIndex(1, savedValues)
        checkErrorState()
    }

    @Test
    fun computeError() {
        val savedValues: MutableList<Int?> = mutableListOfNulls(maxSavedValues)

        fun typeAndCheckError(content: List<Any>, savedValues: List<Int?> = defaultSaved) {
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
        scenario!!.recreate()
        checkEnabledState("")

        // text
        typeText("1-1")
        scenario!!.recreate()
        checkEnabledState("1 - 1")

        // computed text
        clickEquals()
        scenario!!.recreate()
        checkSaveState("0")
        clickClear()

        // error
        typeText("+")
        clickEquals()
        scenario!!.recreate()
        checkErrorState()

        // saved values
        val savedValues: MutableList<Int?> = mutableListOfNulls(maxSavedValues)
        val inUse: MutableSet<Int> = mutableSetOf()

        typeAndSaveToIndex("1+1+1", 3, 0, savedValues)
        scenario!!.recreate()
        checkEnabledState("", savedValues)

        typeContent(listOf("1-", 0, "-1"))
        inUse.add(0)
        scenario!!.recreate()
        checkEnabledState("1 - 3 - 1", savedValues, inUse)

        clickEquals()
        scenario!!.recreate()
        checkSaveState("-3", savedValues)

        saveButton.perform(click())
        savedValues[1] = -3
        deleteAtIndex(0, savedValues)
        checkEnabledState("", savedValues, inUse)
    }
}
