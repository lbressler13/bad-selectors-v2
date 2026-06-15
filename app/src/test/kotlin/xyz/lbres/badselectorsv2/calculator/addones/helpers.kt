package xyz.lbres.badselectorsv2.calculator.addones

import xyz.lbres.badselectorsv2.calculator.splitText
import xyz.lbres.kotlinutils.collection.list.listOfNulls
import kotlin.test.assertEquals
import kotlin.test.assertTrue

private val maxSavedValues = 2
private val empty: List<Int?> = listOfNulls(maxSavedValues)

// save result to viewmodel
fun saveResult(vm: AddOnesViewModel, result: Int) {
    vm.setResult(result, null)
    vm.saveComputedValue()
}

// save result and update saved values list
fun saveResultToIndex(vm: AddOnesViewModel, result: Int, index: Int, savedValues: MutableList<Int?>) {
    saveResult(vm, result)
    savedValues[index] = result
}

// save multiple results to viewmodel
fun saveResults(vm: AddOnesViewModel, results: List<Int>) {
    results.forEach { saveResult(vm, it) }
}

// append text to compute text
fun appendText(vm: AddOnesViewModel, text: String) {
    splitText(text).forEach { vm.appendComputeText(it) }
}

// check that calc data and saved value metadata is blank
fun checkBlank(vm: AddOnesViewModel) {
    assertTrue(vm.calcData.isEmpty())
    checkMetadata(vm, empty)
}

// check saved value metadata at every index
fun checkMetadata(vm: AddOnesViewModel, expectedValues: List<Int?>, inUse: Set<Int> = emptySet()) {
    assertEquals(expectedValues, vm.savedValues)
    expectedValues.forEachIndexed { index, value ->
        val expected = Pair(value, index in inUse)
        assertEquals(expected, vm.getSavedValueMetadata(index))
    }
}

// backspace multiple times
fun repeatBackspace(vm: AddOnesViewModel, count: Int) = repeat(count) { vm.backspaceComputeText() }
