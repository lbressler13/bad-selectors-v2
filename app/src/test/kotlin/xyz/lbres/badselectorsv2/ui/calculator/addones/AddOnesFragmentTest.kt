package xyz.lbres.badselectorsv2.ui.calculator.addones

import androidx.test.core.app.ActivityScenario
import androidx.test.espresso.Espresso.onView
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
import xyz.lbres.badselectorsv2.ui.calculator.numberButtons
import xyz.lbres.badselectorsv2.ui.testutils.navigateToSelector

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
