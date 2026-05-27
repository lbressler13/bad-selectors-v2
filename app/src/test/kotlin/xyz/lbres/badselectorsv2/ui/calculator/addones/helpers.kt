package xyz.lbres.badselectorsv2.ui.calculator.addones

import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.isDescendantOfA
import androidx.test.espresso.matcher.ViewMatchers.isEnabled
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withText
import org.hamcrest.Matchers.allOf
import xyz.lbres.badselectorsv2.R
import xyz.lbres.badselectorsv2.ui.calculator.backspaceButton
import xyz.lbres.badselectorsv2.ui.calculator.clearButton
import xyz.lbres.badselectorsv2.ui.calculator.clickClear
import xyz.lbres.badselectorsv2.ui.calculator.clickEquals
import xyz.lbres.badselectorsv2.ui.calculator.equalsButton
import xyz.lbres.badselectorsv2.ui.calculator.mainText
import xyz.lbres.badselectorsv2.ui.calculator.numberButtons
import xyz.lbres.badselectorsv2.ui.calculator.typeText
import xyz.lbres.badselectorsv2.ui.testutils.enabledMatcher
import xyz.lbres.badselectorsv2.ui.testutils.matchers.isShown
import xyz.lbres.badselectorsv2.ui.testutils.viewactions.forceClick
import xyz.lbres.badselectorsv2.ui.testutils.viewassertions.isNotPresented
import xyz.lbres.kotlinutils.list.listOfNulls

val saveButton = onView(withId(R.id.saveButton))
val plusButton = onView(withId(R.id.plusButton))
val oneButton = numberButtons[1]
val minusButton = onView(withId(R.id.minusButton))
val savedTexts = listOf(R.id.savedValueText1, R.id.savedValueText2).map {
    onView(allOf(withId(R.id.valueText), isDescendantOfA(withId(it))))
}
val deleteButtons = listOf(R.id.savedValueText1, R.id.savedValueText2).map {
    onView(allOf(withId(R.id.deleteButton), isDescendantOfA(withId(it))))
}

fun checkEnabledState(text: String, savedValues: List<Int?> = listOfNulls(2), inUse: Set<Int> = emptySet()) {
    checkState(text, savedValues, inUse, saveView = false, errorView = false)
}

fun checkSaveState(text: String, savedValues: List<Int?> = listOfNulls(2)) {
    checkState(text, savedValues, emptySet(), saveView = true, errorView = false)
}

fun checkErrorState(savedValues: List<Int?> = listOfNulls(2)) {
    val text = "Err: Syntax Error"
    checkState(text, savedValues, emptySet(), saveView = false, errorView = true)
}

private fun checkState(text: String, savedValues: List<Int?>, inUse: Set<Int>, saveView: Boolean, errorView: Boolean) {
    val enabledView = !saveView && !errorView
    mainText.check(matches(withText(text)))
    val saveEnabled = savedValues.any { it == null }
    checkButtons(
        primaryEnabled = enabledView,
        saveDisplayed = saveView,
        equalsEnabled = enabledView,
        saveEnabled = saveEnabled,
    )
    checkSavedValues(savedValues, inUse, enabledView)
}

private fun checkSavedValues(
    savedValues: List<Int?> = listOfNulls(2),
    inUse: Set<Int> = emptySet(),
    enabledUnused: Boolean = true,
) {
    savedValues.forEachIndexed { index, value ->
        val text = savedValueToText(value)
        val enabled = value != null && index !in inUse
        val textEnabledMatcher = enabledMatcher(enabled && enabledUnused)
        savedTexts[index].check(matches(allOf(textEnabledMatcher, withText(text))))
        deleteButtons[index].check(matches(enabledMatcher(enabled)))
    }
}

private fun checkButtons(
    primaryEnabled: Boolean = true,
    saveDisplayed: Boolean = false,
    equalsEnabled: Boolean = true,
    saveEnabled: Boolean = true,
) {
    clearButton.check(matches(isEnabled()))

    oneButton.check(matches(enabledMatcher(primaryEnabled)))
    plusButton.check(matches(enabledMatcher(primaryEnabled)))
    minusButton.check(matches(enabledMatcher(primaryEnabled)))
    backspaceButton.check(matches(enabledMatcher(primaryEnabled)))

    if (saveDisplayed) {
        saveButton.check(matches(allOf(isShown(), enabledMatcher(saveEnabled))))
        equalsButton.check(isNotPresented())
    } else {
        equalsButton.check(matches(allOf(isShown(), enabledMatcher(equalsEnabled))))
        saveButton.check(isNotPresented())
    }
}

fun typeAndSaveToIndex(text: String, value: Int, index: Int, savedValues: MutableList<Int?>) {
    typeAndSaveToIndex(listOf(text), value, index, savedValues)
}

fun typeAndSaveToIndex(content: List<Any>, value: Int, index: Int, savedValues: MutableList<Int?>) {
    typeContent(content)
    clickEquals()
    saveButton.perform(click())
    clickClear()
    savedValues[index] = value
}

fun deleteAtIndex(index: Int, savedValues: MutableList<Int?>) {
    deleteButtons[index].perform(click())
    savedValues[index] = null
}

fun typeContent(content: List<Any>) {
    content.forEach {
        when (it) {
            is String -> typeText(it)
            is Int -> savedTexts[it].perform(forceClick())
            else -> throw IllegalArgumentException("Cannot add value of type ${it::class.java}")
        }
    }
}

private fun savedValueToText(value: Int?) = value?.toString() ?: ""
