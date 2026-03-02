package xyz.lbres.badselectorsv2.phone.shufflecircle

import androidx.fragment.app.testing.launchFragmentInContainer
import androidx.lifecycle.Lifecycle
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.ext.junit.runners.AndroidJUnit4
import io.mockk.every
import io.mockk.justRun
import io.mockk.mockkConstructor
import org.junit.Test
import org.junit.runner.RunWith
import xyz.lbres.badselectorsv2.R
import xyz.lbres.badselectorsv2.testutils.matchers.atIndex

@RunWith(AndroidJUnit4::class)
class ShuffleCircleFragmentTest {
    @Test
    fun meaningOfLifeShouldBeDisplayed() {
        mockkConstructor(DigitShuffler::class)
        every { constructedWith<DigitShuffler>().getAtIndex(any(), any()) } returns 5
        justRun { constructedWith<DigitShuffler>().update() }

        // launchFragmentInContainer { ShuffleCircleFragment() }
        val scenario = launchFragmentInContainer<ShuffleCircleFragment>(initialState = Lifecycle.State.INITIALIZED)
        scenario.moveToState(Lifecycle.State.RESUMED)
        onView(atIndex(withId(R.id.circleLayout), 0)).perform(click())
        onView(atIndex(withId(R.id.circleLayout), 3)).perform(click())
    }
}
