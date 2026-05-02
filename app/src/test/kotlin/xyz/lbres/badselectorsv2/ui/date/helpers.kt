package xyz.lbres.badselectorsv2.ui.date

import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withText
import xyz.lbres.badselectorsv2.R

/**
 * Check that a date is displayed in the date numbers component
 *
 * @param date [String]: 8-character string consisting of digits and underscores
 */
fun checkDate(date: String) {
    val viewIds = listOf(R.id.month0, R.id.month1, R.id.day0, R.id.day1, R.id.year0, R.id.year1, R.id.year2, R.id.year3)
    viewIds.forEachIndexed { index, viewId ->
        val text = date[index].toString()
        onView(withId(viewId)).check(matches(withText(text)))
    }
}

/** Pad a number with 0s to create a string of length 2 */
fun padToTwo(number: Int): String = number.toString().padStart(2, '0')
/** Pad a number with 0s to create a string of length 4 */
fun padToFour(number: Int): String = number.toString().padStart(4, '0')
