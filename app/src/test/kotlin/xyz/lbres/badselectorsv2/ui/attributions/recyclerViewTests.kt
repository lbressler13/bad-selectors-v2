package xyz.lbres.badselectorsv2.ui.attributions

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
import xyz.lbres.badselectorsv2.R
import xyz.lbres.badselectorsv2.attributions.constants.authorAttributions
import xyz.lbres.badselectorsv2.ui.testutils.matchers.matchesAtPosition
import xyz.lbres.badselectorsv2.ui.testutils.viewactions.actionOnChildWithId
import xyz.lbres.badselectorsv2.ui.testutils.viewactions.clickLinkInText

private val imageUrls = authorAttributions.map { it.images.map { it.url } }
private val attributionsRecycler = onView(withId(R.id.attributionsRecycler))

fun testExpandCollapseAttributions() {
    val authorTitles = authorAttributions.map { "Icon made by ${it.name} from www.flaticon.com" }
    val titleAssertion: (Int) -> Matcher<View> = {
        matchesAtPosition(it, allOf(isDisplayed(), hasDescendant(withText(authorTitles[it]))))
    }

    repeat(authorTitles.size) {
        attributionsRecycler
            .perform(scrollToAuthorPosition(it))
            .check(matches(titleAssertion(it)))
    }

    val indices = authorTitles.indices.toList()
    checkImagesNotPresented(indices)

    val expanded = mutableListOf<Int>()

    val expandGroup = { index: Int ->
        expandCollapseAttribution(index)
        expanded.add(index)
        checkImagesDisplayed(expanded)
        checkImagesNotPresented(indices - expanded)
    }

    val collapseGroup = { index: Int ->
        expandCollapseAttribution(index)
        expanded.remove(index)
        checkImagesDisplayed(expanded)
        checkImagesNotPresented(indices - expanded)
    }

    // expand all
    expandGroup(1)
    expandGroup(0)
    expandGroup(4)
    expandGroup(2)
    expandGroup(3)

    // collapse
    collapseGroup(4)
    collapseGroup(1)
    collapseGroup(0)
    collapseGroup(2)
    collapseGroup(3)
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
    repeat(authorAttributions.size) {
        expandCollapseAttribution(it)
    }
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
