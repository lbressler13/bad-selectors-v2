package xyz.lbres.badselectorsv2.ui.testutils

import android.view.View
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.isDescendantOfA
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withText
import com.google.android.material.bottomnavigation.BottomNavigationView
import org.hamcrest.Description
import org.hamcrest.Matchers.allOf
import org.hamcrest.TypeSafeMatcher
import xyz.lbres.badselectorsv2.R

/**
 * Check that all navbar buttons are displayed and correct button is selected
 *
 * @param title [String]: title of expected button
 */
fun testNavbarUi(navbarButtonId: Int, title: String) {
    onView(withId(R.id.navigationHome)).check(matches(isDisplayed()))
    onView(withId(R.id.navigationPhone)).check(matches(isDisplayed()))
    onView(withId(R.id.navigationCalc)).check(matches(isDisplayed()))
    onView(withId(R.id.navigationDate)).check(matches(isDisplayed()))
    onView(withId(R.id.navigationOtp)).check(matches(isDisplayed()))
    onView(withId(R.id.navbar)).check(matches(HasSelectedItem(navbarButtonId)))
    // navbar button includes large and small labels with the same text
    onView(allOf(withText(title), isDescendantOfA(withId(navbarButtonId)), isDisplayed()))
        .check(matches(isDisplayed()))
}

/**
 * Test for navigating to fragments via the navbar
 */
fun testNavigateToHome() = testNavbarAction(R.id.navigationHome, "Bad Selectors")
fun testNavigateToPhone() = testNavbarAction(R.id.navigationPhone, "Bad Phone Selectors")
fun testNavigateToCalc() = testNavbarAction(R.id.navigationCalc, "Bad Calculators")
fun testNavigateToDate() = testNavbarAction(R.id.navigationDate, "Bad Date Selectors")
fun testNavigateToOtp() = testNavbarAction(R.id.navigationOtp, "Bad OTP Receivers")

private fun testNavbarAction(navbarButtonId: Int, expectedTitle: String) {
    onView(withId(navbarButtonId)).perform(click())
    onView(withText(expectedTitle)).check(matches(isDisplayed()))
}

// matcher for checking the selected view of a navbar
private class HasSelectedItem(private val selectedViewId: Int) :
    TypeSafeMatcher<View>() {
    override fun describeTo(description: Description?) {
        description?.appendText("match selected item in navbar")
    }

    override fun matchesSafely(item: View?): Boolean {
        return item is BottomNavigationView && item.selectedItemId == selectedViewId
    }
}
