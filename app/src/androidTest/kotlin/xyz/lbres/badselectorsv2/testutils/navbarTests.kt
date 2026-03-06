package xyz.lbres.badselectorsv2.testutils

import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withText
import xyz.lbres.badselectorsv2.R

/**
 * Test for navigating to fragments via the navbar
 */
fun testNavigateToHome() = testNavbarAction(R.id.navigationHome, "Bad Selectors")
fun testNavigateToPhone() = testNavbarAction(R.id.navigationPhone, "Bad Phone Selectors")
fun testNavigateToCalc() = testNavbarAction(R.id.navigationCalc, "Bad Calculators")
fun testNavigateToDate() = testNavbarAction(R.id.navigationDate, "Bad Date Selectors")

private fun testNavbarAction(navbarButtonId: Int, expectedTitle: String) {
    onView(withId(navbarButtonId)).perform(click())
    onView(withText(expectedTitle)).check(matches(isDisplayed()))
}
