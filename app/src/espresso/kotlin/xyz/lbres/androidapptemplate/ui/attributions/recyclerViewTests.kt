package xyz.lbres.androidapptemplate.ui.attributions

import android.view.View
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.hasDescendant
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withText
import org.hamcrest.Matcher
import org.hamcrest.Matchers.allOf
import org.hamcrest.Matchers.containsString
import xyz.lbres.androidapptemplate.R
import xyz.lbres.androidapptemplate.testutils.assertLinkOpened
import xyz.lbres.androidapptemplate.testutils.matchers.matchesAtPosition
import xyz.lbres.androidapptemplate.testutils.viewactions.actionOnChildWithId
import xyz.lbres.androidapptemplate.testutils.viewactions.clickLinkInText
import xyz.lbres.androidapptemplate.ui.attributions.constants.authorAttributions

private val imageUrls = authorAttributions.map { it.images.map { it.url } }
private val attributionsRecycler = onView(withId(R.id.attributionsRecycler))

fun testExpandCollapseAttributions() {
    val authorTitles = authorAttributions.map { "Icon made by ${it.name} from www.flaticon.com" }
    val titleAssertion: (Int) -> Matcher<View> = {
        matchesAtPosition(it, allOf(isDisplayed(), hasDescendant(withText(authorTitles[it]))))
    }

    attributionsRecycler.check(matches(titleAssertion(0)))
    attributionsRecycler.check(matches(titleAssertion(1)))

    checkImagesNotPresented(listOf(0, 1))

    // expand all
    expandCollapseAttribution(1)
    checkImagesDisplayed(listOf(1))
    checkImagesNotPresented(listOf(0))

    expandCollapseAttribution(0)
    checkImagesDisplayed(listOf(0, 1))

    // collapse
    expandCollapseAttribution(1)
    checkImagesDisplayed(listOf(0))
    checkImagesNotPresented(listOf(1))

    expandCollapseAttribution(0)
    checkImagesNotPresented(listOf(0, 1))
}

fun testAttributionLinks() {
    var expectedLinkClicks = 0
    val clickFlaticon = actionOnChildWithId(R.id.attribution, clickLinkInText("www.flaticon.com"))

    // authors
    for (pair in authorAttributions.withIndex()) {
        val position = pair.index
        val author = pair.value

        // flaticon link
        attributionsRecycler.perform(actionOnAuthorItemAtPosition(position, clickFlaticon))
        expectedLinkClicks++
        assertLinkOpened("https://www.flaticon.com", expectedLinkClicks)

        // author link
        val clickAuthor = actionOnChildWithId(R.id.attribution, clickLinkInText(author.name))
        attributionsRecycler.perform(actionOnAuthorItemAtPosition(position, clickAuthor))
        expectedLinkClicks++
        assertLinkOpened(author.url, expectedLinkClicks)
    }

    // images
    expandCollapseAttribution(0)
    expandCollapseAttribution(1)
    for (position in imageUrls.indices) {
        attributionsRecycler.perform(scrollToAuthorPosition(position))
        val urls = imageUrls[position]

        for (url in urls) {
            onView(withText(containsString(url))).perform(clickLinkInText(url))
            expectedLinkClicks++
            assertLinkOpened(url, expectedLinkClicks)
        }
    }

    // check link with attributions open
    attributionsRecycler.perform(actionOnAuthorItemAtPosition(0, clickFlaticon))
    expectedLinkClicks++
    assertLinkOpened("https://www.flaticon.com", expectedLinkClicks)

    // author link
    val author = authorAttributions[0]
    val clickAuthor = actionOnChildWithId(R.id.attribution, clickLinkInText(author.name))
    attributionsRecycler.perform(actionOnAuthorItemAtPosition(0, clickAuthor))
    expectedLinkClicks++
    assertLinkOpened(author.url, expectedLinkClicks)
}
