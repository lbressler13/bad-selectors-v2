package xyz.lbres.badselectorsv2.ui.calculator.addones

import androidx.test.core.app.ActivityScenario
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.assertion.ViewAssertions.doesNotExist
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.isDescendantOfA
import androidx.test.espresso.matcher.ViewMatchers.isEnabled
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
import xyz.lbres.badselectorsv2.ui.calculator.clickBackspace
import xyz.lbres.badselectorsv2.ui.calculator.clickClear
import xyz.lbres.badselectorsv2.ui.calculator.clickEquals
import xyz.lbres.badselectorsv2.ui.calculator.typeText
import xyz.lbres.badselectorsv2.ui.testutils.navigateToSelector
import xyz.lbres.badselectorsv2.R
import xyz.lbres.badselectorsv2.ui.calculator.backspaceButton
import xyz.lbres.badselectorsv2.ui.calculator.clearButton
import xyz.lbres.badselectorsv2.ui.calculator.equalsButton
import xyz.lbres.badselectorsv2.ui.calculator.mainText
import xyz.lbres.badselectorsv2.ui.calculator.numberButtons
import xyz.lbres.badselectorsv2.ui.testutils.isDisabled
import xyz.lbres.badselectorsv2.ui.testutils.matchers.isShown
import xyz.lbres.badselectorsv2.ui.testutils.viewassertions.isNotPresented

@Category(Robolectric::class)
@RunWith(AndroidJUnit4::class)
class AddOnesFragmentTest {
    private val saveButton = onView(withId(R.id.saveButton))

    private var scenario: ActivityScenario<BaseActivity>? = null
    private var savedText1 = onView(allOf(withId(R.id.valueText), isDescendantOfA(withId(R.id.savedValueText1))))
    private var savedText2 = onView(allOf(withId(R.id.valueText), isDescendantOfA(withId(R.id.savedValueText2))))
    private var deleteButton1 = onView(allOf(withId(R.id.deleteButton), isDescendantOfA(withId(R.id.savedValueText1))))
    private var deleteButton2 = onView(allOf(withId(R.id.deleteButton), isDescendantOfA(withId(R.id.savedValueText2))))

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
        val enabledButtonMatcher = allOf(isShown(), isEnabled())
        onView(withId(R.id.plusButton)).check(matches(enabledButtonMatcher))
        onView(withId(R.id.minusButton)).check(matches(enabledButtonMatcher))
        onView(withId(R.id.oneButton)).check(matches(enabledButtonMatcher))
        equalsButton.check(matches(enabledButtonMatcher))
        clearButton.check(matches(enabledButtonMatcher))
        backspaceButton.check(matches(enabledButtonMatcher))

        mainText.check(matches(withText("")))

        saveButton.check(isNotPresented())
        savedText1.check(matches(allOf(isShown(), withText(""))))
        savedText2.check(matches(allOf(isShown(), withText(""))))
        deleteButton1.check(matches(allOf(isShown(), isDisabled())))
        deleteButton2.check(matches(allOf(isShown(), isDisabled())))

        onView(withId(R.id.timesButton)).check(doesNotExist())
        onView(withId(R.id.divideButton)).check(doesNotExist())
        numberButtons.forEachIndexed { index, view ->
            if (index != 1) {
                view.check(doesNotExist())
            }
        }
    }

    @Test
    fun eqButton() {
        // TODO
    }

    @Test
    fun backspace() {
        // TODO
    }

    @Test
    fun clear() {
        // TODO
    }

    @Test
    fun compute() {
        // TODO
    }

    @Test
    fun saveValues() {
        // TODO
    }

    @Test
    fun computeError() {
        // TODO
    }

    @Test
    fun recreate() {
        // TODO
    }
}
