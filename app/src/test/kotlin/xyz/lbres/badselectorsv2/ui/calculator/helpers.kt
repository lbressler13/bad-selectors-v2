package xyz.lbres.badselectorsv2.ui.calculator

import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.ViewInteraction
import androidx.test.espresso.matcher.ViewMatchers.withId
import xyz.lbres.badselectorsv2.R
import xyz.lbres.badselectorsv2.ui.testutils.viewactions.forceClick
import xyz.lbres.kotlinutils.string.ext.isInt

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

val operators = listOf("+", "-", "x", "/")

val mainText: ViewInteraction = onView(withId(R.id.mainText))
val equalsButton: ViewInteraction = onView(withId(R.id.equalsButton))
val backspaceButton: ViewInteraction = onView(withId(R.id.backspaceButton))
val clearButton: ViewInteraction = onView(withId(R.id.clearButton))

fun clickEquals() = equalsButton.perform(forceClick())
fun clickBackspace() = backspaceButton.perform(forceClick())
fun clickClear() = clearButton.perform(forceClick())

fun splitText(text: String): List<String> = text.toList().map(Char::toString)

fun typeText(text: String) {
    val textList = splitText(text)
    textList.forEach {
        if (it.isInt()) {
            numberButtons[it.toInt()].perform(forceClick())
        } else {
            operatorButtons[it]!!.perform(forceClick())
        }
    }
}
