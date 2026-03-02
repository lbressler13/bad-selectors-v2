package xyz.lbres.badselectorsv2.phone.shufflecircle

import androidx.fragment.app.testing.launchFragmentInContainer
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.ext.junit.runners.AndroidJUnit4
import io.mockk.every
import io.mockk.justRun
import io.mockk.mockkConstructor
import io.mockk.unmockkAll
import org.junit.After
import org.junit.Test
import org.junit.runner.RunWith
import xyz.lbres.badselectorsv2.R
import xyz.lbres.badselectorsv2.testutils.matchers.atIndex

@RunWith(AndroidJUnit4::class)
class ShuffleCircleFragmentTest {
    @After
    fun cleanupTest() {
        unmockkAll()
    }

    @Test
    fun meaningOfLifeShouldBeDisplayed() {
        mockkConstructor(DigitShuffler::class)
        every { constructedWith<DigitShuffler>().getAtIndex(any(), any()) } returns 5
        justRun { constructedWith<DigitShuffler>().update() }

        launchFragmentInContainer<ShuffleCircleFragment>()
        onView(atIndex(withId(R.id.circleLayout), 0)).perform(click())
        onView(atIndex(withId(R.id.circleLayout), 3)).perform(click())
    }
}
