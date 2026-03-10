package xyz.lbres.badselectorsv2.ui.attributions

import android.app.Activity
import android.app.Instrumentation.ActivityResult
import android.content.Intent
import androidx.test.core.app.ActivityScenario
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.doesNotExist
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.intent.Intents
import androidx.test.espresso.intent.Intents.intending
import androidx.test.espresso.intent.matcher.IntentMatchers.isInternal
import androidx.test.espresso.matcher.ViewMatchers.hasDescendant
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withText
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.hamcrest.Matchers.not
import org.hamcrest.core.AllOf.allOf
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.experimental.categories.Category
import org.junit.runner.RunWith
import org.robolectric.Robolectric
import xyz.lbres.badselectorsv2.BaseActivity
import xyz.lbres.badselectorsv2.ProductFlavor
import xyz.lbres.badselectorsv2.R
import xyz.lbres.badselectorsv2.attributions.constants.authorAttributions
import xyz.lbres.badselectorsv2.ui.testutils.assertLinkOpened
import xyz.lbres.badselectorsv2.ui.testutils.hideDevToolsButton
import xyz.lbres.badselectorsv2.ui.testutils.matchers.matchesAtPosition
import xyz.lbres.badselectorsv2.ui.testutils.matchers.withTitle
import xyz.lbres.badselectorsv2.ui.testutils.testNavbarUi
import xyz.lbres.badselectorsv2.ui.testutils.testNavigateToCalc
import xyz.lbres.badselectorsv2.ui.testutils.testNavigateToDate
import xyz.lbres.badselectorsv2.ui.testutils.testNavigateToHome
import xyz.lbres.badselectorsv2.ui.testutils.testNavigateToOtp
import xyz.lbres.badselectorsv2.ui.testutils.testNavigateToPhone
import xyz.lbres.badselectorsv2.ui.testutils.viewactions.clickLinkInText
import xyz.lbres.badselectorsv2.ui.testutils.viewactions.forceClick
import xyz.lbres.badselectorsv2.ui.testutils.viewassertions.isNotPresented

@Category(Robolectric::class)
@RunWith(AndroidJUnit4::class)
class AttributionsFragmentTest {
    var scenario: ActivityScenario<BaseActivity>? = null
    val intent = Intent(ApplicationProvider.getApplicationContext(), BaseActivity::class.java)

    @Before
    fun setupTest() {
        scenario = ActivityScenario.launchActivityForResult(intent)

        // setup intents for testing links
        Intents.init()
        intending(not(isInternal())).respondWith(ActivityResult(Activity.RESULT_OK, null))

        onView(withId(R.id.infoButton)).perform(click())

        // hide dev tools to avoid interference with expanding/collapsing attributions
        if (ProductFlavor.devMode) {
            hideDevToolsButton()
        }
    }

    @After
    fun cleanupTest() {
        Intents.release()
        scenario = null
    }

    @Test
    fun loadActionBarWithTitle() {
        val expectedTitle = "Give People Credit"
        onView(withId(R.id.actionBar)).check(matches(withTitle(expectedTitle)))
        testNavbarUi(R.id.navigationHome, "Home")
    }

    @Test
    fun flaticonMessage() {
        val flaticonShort = "All icons are taken from Flaticon. Expand for details of their attribution policies."
        val flaticonLong = "All icons are taken from Flaticon, which allows free use of icons for personal and " +
            "commercial purposes with attribution. In accordance with their policies, attributions are provided " +
            "below.\n\nSee here for more information about their policies."

        // initial view
        onView(withId(R.id.flaticonPolicyMessage)).check(matches(withText(flaticonShort)))
        onView(withId(R.id.expandCollapseMessage)).check(matches(withText("Expand")))
        onView(withText(flaticonLong)).check(doesNotExist())
        onView(withText("Collapse")).check(doesNotExist())

        // expand
        onView(withId(R.id.expandCollapseMessage)).perform(click())

        onView(withId(R.id.flaticonPolicyMessage)).check(matches(withText(flaticonLong)))
        onView(withId(R.id.expandCollapseMessage)).check(matches(withText("Collapse")))
        onView(withText(flaticonShort)).check(doesNotExist())
        onView(withText("Expand")).check(doesNotExist())

        // collapse again
        onView(withId(R.id.expandCollapseMessage)).perform(click())

        onView(withId(R.id.flaticonPolicyMessage)).check(matches(withText(flaticonShort)))
        onView(withId(R.id.expandCollapseMessage)).check(matches(withText("Expand")))
        onView(withText(flaticonLong)).check(doesNotExist())
        onView(withText("Collapse")).check(doesNotExist())
    }

    @Test
    fun closeButton() {
        onView(withId(R.id.closeButton)).perform(forceClick())
        onView(withText("Bad Selectors")).check(matches(isDisplayed()))
    }

    @Test fun navigateToHome() = testNavigateToHome()
    @Test fun navigateToPhone() = testNavigateToPhone()
    @Test fun navigateToCalc() = testNavigateToCalc()
    @Test fun navigateToDate() = testNavigateToDate()
    @Test fun navigateToOtp() = testNavigateToOtp()

    @Test fun expandCollapseAttributions() = testExpandCollapseAttributions()

    @Test
    fun expandFlaticonMessageAndAttributions() {
        onView(withId(R.id.expandCollapseMessage)).perform(click())
        onView(withId(R.id.expandCollapseMessage)).check(matches(withText("Collapse")))

        expandCollapseAttribution(0)
        checkImagesDisplayed(listOf(0))
        checkImagesNotPresented(listOf(1))

        expandCollapseAttribution(1)
        checkImagesDisplayed(listOf(0, 1))

        expandCollapseAttribution(2)
        checkImagesDisplayed(listOf(0, 1, 2))
    }

    @Test
    fun flaticonPolicyLinks() {
        var expectedLinkClicks = 0

        // click link while collapsed
        onView(withId(R.id.flaticonPolicyMessage)).perform(clickLinkInText("Flaticon"))
        expectedLinkClicks++
        assertLinkOpened("https://www.flaticon.com", expectedLinkClicks)

        onView(withId(R.id.expandCollapseMessage)).perform(click())

        // click both links after expanding
        onView(withId(R.id.flaticonPolicyMessage)).perform(clickLinkInText("Flaticon"))
        expectedLinkClicks++
        assertLinkOpened("https://www.flaticon.com", expectedLinkClicks)

        onView(withId(R.id.flaticonPolicyMessage)).perform(clickLinkInText("here"))
        expectedLinkClicks++
        /* ktlint-disable max-line-length */
        assertLinkOpened(
            "https://support.flaticon.com/s/article/Attribution-How-when-and-where-FI?language=en_US&Id=ka03V0000004Q5lQAE",
            expectedLinkClicks,
        )
        /* ktlint-enable max-line-length */
    }

    @Test
    fun dataNotPersistedOnClose() {
        val recycler = onView(withId(R.id.attributionsRecycler))

        // expand attributions
        for (pair in authorAttributions.withIndex()) {
            val index = pair.index
            val author = pair.value

            expandCollapseAttribution(index)
            recycler.perform(scrollToAuthorPosition(index))
            val image = author.images[0]
            onView(withText(image.url)).check(matches(isDisplayed()))
        }

        // expand flaticon message
        onView(withText("Expand")).check(matches(isDisplayed()))
        onView(withText("Collapse")).check(doesNotExist())
        onView(withText("Expand")).perform(click())
        onView(withText("Expand")).check(doesNotExist())
        onView(withText("Collapse")).check(matches(isDisplayed()))

        // close and reopen fragment
        onView(withId(R.id.closeButton)).perform(forceClick())
        onView(withId(R.id.infoButton)).perform(click())

        // check image attributions no longer displayed
        for (pair in authorAttributions.withIndex()) {
            val index = pair.index
            val author = pair.value

            recycler.perform(scrollToAuthorPosition(index))
            val image = author.images[0]
            onView(withText(image.url)).check(isNotPresented())
        }

        // check flaticon message collapsed
        onView(withText("Expand")).check(matches(isDisplayed()))
        onView(withText("Collapse")).check(doesNotExist())
    }

    @Test fun attributionsLinks() = testAttributionLinks()

    @Test
    fun recreate() {
        val authorTitles = authorAttributions.map { "Icon made by ${it.name} from www.flaticon.com" }

        // refresh with initial view (all collapsed)
        scenario!!.recreate()

        onView(withId(R.id.expandCollapseMessage)).check(matches(withText("Expand")))
        authorTitles.indices.forEach {
            val withAuthorTitle = hasDescendant(withText(authorTitles[it]))
            onView(withId(R.id.attributionsRecycler))
                .check(matches(matchesAtPosition(it, allOf(isDisplayed(), withAuthorTitle))))
        }
        checkImagesNotPresented(listOf(0, 1))

        // expand some
        if (ProductFlavor.devMode) {
            hideDevToolsButton(0)
        }
        onView(withId(R.id.expandCollapseMessage)).perform(click())
        expandCollapseAttribution(0)

        scenario!!.recreate()

        checkImagesDisplayed(listOf(0))
        checkImagesNotPresented(listOf(1))
        onView(withId(R.id.expandCollapseMessage)).check(matches(withText("Collapse")))
    }
}
