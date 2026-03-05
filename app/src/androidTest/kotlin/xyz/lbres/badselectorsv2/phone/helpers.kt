package xyz.lbres.badselectorsv2.phone

import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withText
import org.hamcrest.Matchers.allOf
import xyz.lbres.badselectorsv2.R

val digitViews = listOf(
    onView(withId(R.id.digit0)),
    onView(withId(R.id.digit1)),
    onView(withId(R.id.digit2)),
    onView(withId(R.id.digit3)),
    onView(withId(R.id.digit4)),
    onView(withId(R.id.digit5)),
    onView(withId(R.id.digit6)),
    onView(withId(R.id.digit7)),
    onView(withId(R.id.digit8)),
    onView(withId(R.id.digit9)),
)

/**
 * Check that correct phone number is displayed in digit views
 *
 * @param expectedNumber [List]<Int?>: list with expected digits, where blanks are represented by `null`
 * @param digitsToCheck [List]<Int>: list of digits to check, defaults to full range of digits
 */
fun checkPhoneNumber(expectedNumber: List<Int?>, digitsToCheck: List<Int> = (0..9).toList()) {
    repeat(10) {
        val expectedText = expectedNumber[it]?.toString() ?: "_"
        if (it in digitsToCheck) {
            digitViews[it].check(matches(allOf(isDisplayed(), withText(expectedText))))
        }
    }
}

fun checkPhoneNumber(expectedNumber: List<Int?>, digitsToCheck: IntRange) {
    checkPhoneNumber(expectedNumber, digitsToCheck.toList())
}
