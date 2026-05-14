package xyz.lbres.badselectorsv2.ui.phone.selectcorrect

import androidx.test.core.app.ActivityScenario
import androidx.test.espresso.Espresso.onView
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

@Category(Robolectric::class)
@RunWith(AndroidJUnit4::class)
class SelectCorrectSettingsDialogTest {
    private var scenario: ActivityScenario<BaseActivity>? = null

    private val settingsButton = onView(withId(R.id.settingsButton))
    private val singleSelectSwitch = onViewInDialog(withId(R.id.singleSelectSwitch))
    private val doneButton = onViewInDialog(withText("Done"))

    @Before
    fun setupTest() {
        scenario = ActivityScenario.launchActivityForResult(BaseActivity::class.java)
        navigateToSelector("Phone", "Select Correct")
    }

    @After
    fun cleanupTest() {
        scenario = null
    }

    @Test
    fun loadInitialUi() {
        settingsButton.perform(click())
        onViewInDialog(withText("Settings")).check(matches(isDisplayed()))
        doneButton.check(matches(isDisplayed()))

        val switchMatcher = allOf(isDisplayed(), isNotChecked(), withText("Single select"))
        singleSelectSwitch.check(matches(switchMatcher))
    }

    @Test
    fun closeDialog() {
        settingsButton.perform(click())
        doneButton.perform(click())
        val dialog = ShadowDialog.getLatestDialog()
        assertFalse(dialog.isShowing)
    }

    @Test
    fun interactWithUi() {
        settingsButton.perform(click())
        singleSelectSwitch.perform(click()).check(matches(isChecked()))
        singleSelectSwitch.perform(click()).check(matches(isNotChecked()))
        singleSelectSwitch.perform(click()).check(matches(isChecked()))

        doneButton.perform(click())
        settingsButton.perform(click())
        singleSelectSwitch.check(matches(isChecked()))
        singleSelectSwitch.perform(click())

        doneButton.perform(click())
        settingsButton.perform(click())
        singleSelectSwitch.check(matches(isNotChecked()))
    }
}
