package xyz.lbres.badselectorsv2.ui.calculator

import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.ViewInteraction
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withText
import xyz.lbres.badselectorsv2.R

// all numbers
val numberButtons: List<ViewInteraction> = listOf(
    onView(withId((R.id.zeroButton))),
    onView(withId((R.id.oneButton))),
    onView(withId((R.id.twoButton))),
    onView(withId((R.id.threeButton))),
    onView(withId((R.id.fourButton))),
    onView(withId((R.id.fiveButton))),
    onView(withId((R.id.sixButton))),
    onView(withId((R.id.sevenButton))),
    onView(withId((R.id.eightButton))),
    onView(withId((R.id.nineButton))),
)

// all operators
val operatorButtons: Map<String, ViewInteraction> = mapOf(
    "+" to onView(withId((R.id.plusButton))),
    "-" to onView(withId((R.id.minusButton))),
    "x" to onView(withId((R.id.timesButton))),
    "/" to onView(withId((R.id.divideButton))),
)

val clearButton = onView(withId(R.id.clearButton))
val mainText = onView(withId(R.id.mainText))

fun mainTextMatches(text: String) {
    mainText.check(matches(withText(text)))
}
