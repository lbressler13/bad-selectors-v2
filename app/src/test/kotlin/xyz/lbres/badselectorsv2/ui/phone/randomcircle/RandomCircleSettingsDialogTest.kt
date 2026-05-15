package xyz.lbres.badselectorsv2.ui.phone.randomcircle

import androidx.test.core.app.ActivityScenario
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.isChecked
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.isNotChecked
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
import xyz.lbres.badselectorsv2.ui.testutils.navigateToSelector
import xyz.lbres.badselectorsv2.ui.testutils.onViewInDialog
import xyz.lbres.badselectorsv2.ui.testutils.openSettingsDialog

@Category(Robolectric::class)
@RunWith(AndroidJUnit4::class)
class RandomCircleSettingsDialogTest {
    private var scenario: ActivityScenario<BaseActivity>? = null

    private val russianRouletteSwitch = onViewInDialog(withId(R.id.russianRouletteSwitch))
    private val doneButton = onViewInDialog(withText("Done"))

    @Before
    fun setupTest() {
        scenario = ActivityScenario.launchActivityForResult(BaseActivity::class.java)
        navigateToSelector("Phone", "Random Circle")
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

        val switchMatcher = allOf(isDisplayed(), isNotChecked(), withText("Russian roulette"))
        russianRouletteSwitch.check(matches(switchMatcher))
    }

    @Test
    fun closeDialog() {
        doneButton.perform(click())
        val dialog = ShadowDialog.getLatestDialog()
        assertFalse(dialog.isShowing)
    }

    @Test
    fun interactWithUi() {
        russianRouletteSwitch.perform(click()).check(matches(isChecked()))
        russianRouletteSwitch.perform(click()).check(matches(isNotChecked()))
        russianRouletteSwitch.perform(click()).check(matches(isChecked()))

        doneButton.perform(click())
        openSettingsDialog()
        russianRouletteSwitch.check(matches(isChecked()))
        russianRouletteSwitch.perform(click())

        doneButton.perform(click())
        openSettingsDialog()
        russianRouletteSwitch.check(matches(isNotChecked()))
    }
}
