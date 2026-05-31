package xyz.lbres.badselectorsv2.ui.calculator.addones

import androidx.test.core.app.ActivityScenario
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withText
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.hamcrest.Matchers.allOf
import org.junit.After
import org.junit.Assert.assertFalse
import org.junit.Before
import org.junit.Test
import org.junit.experimental.categories.Category
import org.junit.runner.RunWith
import org.robolectric.Robolectric
import org.robolectric.shadows.ShadowDialog
import xyz.lbres.badselectorsv2.BaseActivity
import xyz.lbres.badselectorsv2.R
import xyz.lbres.badselectorsv2.ui.testutils.matchers.hasProgress
import xyz.lbres.badselectorsv2.ui.testutils.navigateToSelector
import xyz.lbres.badselectorsv2.ui.testutils.onViewInDialog
import xyz.lbres.badselectorsv2.ui.testutils.openSettingsDialog
import xyz.lbres.badselectorsv2.ui.testutils.viewactions.setProgress
import xyz.lbres.badselectorsv2.ui.testutils.viewassertions.isNotPresented

@Category(Robolectric::class)
@RunWith(AndroidJUnit4::class)
class AddOneSettingsDialogTest {
    private var scenario: ActivityScenario<BaseActivity>? = null

    private val seekbar = onView(withId(R.id.numSavedValuesSeekbar))
    private val warningLabel = onView(withId(R.id.cannotDisableText))
    private val doneButton = onViewInDialog(withText("Done"))

    @Before
    fun setupTest() {
        scenario = ActivityScenario.launchActivityForResult(BaseActivity::class.java)
        navigateToSelector("Calculator", "Add Ones")
        openSettingsDialog()
    }

    @After
    fun cleanupTest() {
        scenario = null
    }

    @Test
    fun loadInitialUi() {
        onViewInDialog(withText("Settings")).check(matches(isDisplayed()))
        doneButton.check(matches(isDisplayed()))

        val seekbarMatcher = allOf(isDisplayed(), hasProgress(4))
        seekbar.check(matches(seekbarMatcher))
        warningLabel.check(isNotPresented())
        onViewInDialog(withId(R.id.seekbarLabel)).check(matches(withText("Number of saved values")))
    }

    @Test
    fun closeDialog() {
        doneButton.perform(click())
        val dialog = ShadowDialog.getLatestDialog()
        assertFalse(dialog.isShowing)
    }

    @Test
    fun interactWithUi() {
        setAndCheckProgress(0)
        setAndCheckProgress(1)
        setAndCheckProgress(2)
        setAndCheckProgress(3)
    }

    @Test
    fun interactWithUiWithSavedValues() {
        val warning = { savedCount: Int, setCount: Int ->
            if (savedCount == 1) {
                "Cannot set number of values to 0 when 1 value is saved"
            } else {
                "Cannot set number of values to $setCount when $savedCount values are saved"
            }
        }

        doneButton.perform(click())
        typeAndSave("1+1")
        setAndCheckProgress(0, warning(1, 0))
    }

    private fun setAndCheckProgress(progress: Int, warning: String? = null) {
        seekbar.perform(setProgress(progress))
            .check(matches(hasProgress(progress)))

        if (warning == null) {
            warningLabel.check(isNotPresented())
        } else {
            warningLabel.check(matches(allOf(isDisplayed(), withText(warning))))
        }
    }
}
