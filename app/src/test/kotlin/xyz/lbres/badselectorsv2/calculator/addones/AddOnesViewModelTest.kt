package xyz.lbres.badselectorsv2.calculator.addones

import io.mockk.unmockkAll
import xyz.lbres.badselectorsv2.calculator.splitText
import xyz.lbres.badselectorsv2.testutils.mockLog
import xyz.lbres.kotlinutils.list.listOfNulls
import xyz.lbres.kotlinutils.list.mutablelist.mutableListOfNulls
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class AddOnesViewModelTest {
    private val maxSavedValues = 2
    private val empty: List<Int?> = listOfNulls(maxSavedValues)

    @BeforeTest
    fun setupTest() {
        mockLog()
    }

    @AfterTest
    fun cleanupTest() {
        unmockkAll()
    }

    @Test
    fun testInit() {
        val vm = AddOnesViewModel()
        assertEquals(maxSavedValues, vm.maxSavedValues)
        assertEquals(empty, vm.savedValues)
        assertTrue(vm.calcData.isEmpty())
    }

    @Test
    fun testSaveComputedValue() {
        val expectedValues: MutableList<Int?> = mutableListOfNulls(maxSavedValues)
        val vm = AddOnesViewModel()

        // save values
        saveResultToIndex(vm, 1, 0, expectedValues)
        assertEquals(expectedValues, vm.savedValues)

        saveResultToIndex(vm, -12, 1, expectedValues)
        assertEquals(expectedValues, vm.savedValues)

        // no available spots
        saveResult(vm, 5)
        assertEquals(expectedValues, vm.savedValues)

        // cleared spots
        vm.clearSavedValueAtIndex(1)
        vm.saveComputedValue()
        expectedValues[1] = 5
        assertEquals(expectedValues, vm.savedValues)

        // with error
        vm.clearSavedValueAtIndex(0)
        expectedValues[0] = null
        vm.setResult(null, "Error")
        vm.saveComputedValue()
        assertEquals(expectedValues, vm.savedValues)
    }

    @Test
    fun testAppendSavedValueAtIndex() {
        var vm = AddOnesViewModel()

        val append = { content: List<Any> ->
            content.forEach {
                when (it) {
                    is String -> appendText(vm, it)
                    is Int -> vm.appendSavedValueAtIndex(it)
                }
            }
        }

        // empty text
        append(listOf(0, 1))
        checkBlank(vm)

        saveResult(vm, 6)
        append(listOf(1, 0))
        assertEquals(splitText("6"), vm.calcData.computeText)

        // with text
        vm = AddOnesViewModel()
        saveResult(vm, 2)
        append(listOf("1", 0))
        assertEquals(splitText("12"), vm.calcData.computeText)

        vm = AddOnesViewModel()
        saveResult(vm, 2)
        append(listOf("1+1-1-", 0))
        assertEquals(splitText("1+1-1-2"), vm.calcData.computeText)
        vm.appendSavedValueAtIndex(1)

        // multiple values
        vm = AddOnesViewModel()
        saveResult(vm, 4)
        saveResult(vm, 5)
        append(listOf(1, 0))
        assertEquals(listOf("5", "4"), vm.calcData.computeText)

        vm = AddOnesViewModel()
        saveResult(vm, 4)
        saveResult(vm, 5)
        append(listOf("1+", 0, "-1+1+", 1, "-1"))
        assertEquals(splitText("1+4-1+1+5-1"), vm.calcData.computeText)

        // already appended
        vm = AddOnesViewModel()
        saveResult(vm, 4)
        vm.appendSavedValueAtIndex(0)
        assertEquals(listOf("4"), vm.calcData.computeText)
        vm.appendSavedValueAtIndex(0)
        assertEquals(listOf("4"), vm.calcData.computeText)
    }

    @Test
    fun testClearSavedValueAtIndex() {
        val vm = AddOnesViewModel()

        // clear with none
        vm.clearSavedValueAtIndex(0)
        assertEquals(empty, vm.savedValues)
        vm.clearSavedValueAtIndex(1)
        assertEquals(empty, vm.savedValues)

        // clear with only one
        saveResult(vm, 5)
        vm.clearSavedValueAtIndex(1)
        assertEquals(listOf(5, null), vm.savedValues)
        vm.clearSavedValueAtIndex(0)
        assertEquals(empty, vm.savedValues)

        // clear out of order
        saveResult(vm, -1)
        saveResult(vm, 0)
        vm.clearSavedValueAtIndex(0)
        assertEquals(listOf(null, 0), vm.savedValues)
        vm.clearSavedValueAtIndex(1)
        assertEquals(empty, vm.savedValues)
    }

    @Test
    fun testGetSavedValueMetadata() {
        val vm = AddOnesViewModel()
        val expectedValues: MutableList<Int?> = mutableListOfNulls(maxSavedValues)
        val inUse: MutableSet<Int> = mutableSetOf()

        // initial
        checkMetadata(vm, empty)

        // saved
        saveResultToIndex(vm, 5, 0, expectedValues)
        checkMetadata(vm, expectedValues)

        saveResultToIndex(vm, 14, 1, expectedValues)
        checkMetadata(vm, expectedValues, inUse)

        // used
        vm.appendSavedValueAtIndex(1)
        inUse.add(1)
        checkMetadata(vm, expectedValues, inUse)

        appendText(vm, "+2-")
        vm.appendSavedValueAtIndex(0)
        inUse.add(0)
        checkMetadata(vm, expectedValues, inUse)

        // changed
        vm.setResult(2, null)
        vm.clearSavedValueAtIndex(0)
        vm.saveComputedValue()
        expectedValues[0] = 2
        inUse.clear()
        checkMetadata(vm, expectedValues, inUse)

        // cleared
        vm.clearSavedValueAtIndex(1)
        expectedValues[1] = null
        checkMetadata(vm, expectedValues, inUse)
        vm.clearSavedValueAtIndex(0)
        expectedValues[0] = null
    }

    @Test
    fun testBackspaceComputeText() {
        val vm = AddOnesViewModel()
        val expectedValues = listOf(7, 4)
        val inUse: MutableSet<Int> = mutableSetOf()

        // empty
        vm.backspaceComputeText()
        checkBlank(vm)

        // no saved values
        appendText(vm, "1")
        vm.backspaceComputeText()
        checkBlank(vm)

        appendText(vm, "1+2--")
        vm.backspaceComputeText()
        assertEquals(splitText("1+2-"), vm.calcData.computeText)
        vm.backspaceComputeText()
        assertEquals(splitText("1+2"), vm.calcData.computeText)
        repeatBackspace(vm, 3)
        checkBlank(vm)

        // saved but not used
        expectedValues.forEach { saveResult(vm, it) }
        appendText(vm, "1+1+1")
        checkMetadata(vm, expectedValues, inUse)
        repeatBackspace(vm, 5)
        assertEquals(emptyList(), vm.calcData.computeText)
        checkMetadata(vm, expectedValues, inUse)

        // saved value
        expectedValues.forEach { saveResult(vm, it) }
        appendText(vm, "1+")
        vm.appendSavedValueAtIndex(1)
        inUse.add(1)
        appendText(vm, "-1-1-")
        vm.appendSavedValueAtIndex(0)
        inUse.add(0)
        appendText(vm, "+")
        var expectedText = splitText("1+4-1-1-7+")
        assertEquals(expectedText, vm.calcData.computeText)
        checkMetadata(vm, expectedValues, inUse)

        vm.backspaceComputeText()
        expectedText = splitText("1+4-1-1-7")
        assertEquals(expectedText, vm.calcData.computeText)
        checkMetadata(vm, expectedValues, inUse)

        vm.backspaceComputeText()
        expectedText = splitText("1+4-1-1-")
        inUse.remove(0)
        assertEquals(expectedText, vm.calcData.computeText)
        checkMetadata(vm, expectedValues, inUse)

        repeatBackspace(vm, 5)
        expectedText = splitText("1+4")
        assertEquals(expectedText, vm.calcData.computeText)
        checkMetadata(vm, expectedValues, inUse)

        vm.backspaceComputeText()
        expectedText = splitText("1+")
        inUse.remove(1)
        assertEquals(expectedText, vm.calcData.computeText)
        checkMetadata(vm, expectedValues, inUse)

        repeatBackspace(vm, 2)
        expectedText = emptyList()
        assertEquals(expectedText, vm.calcData.computeText)
        checkMetadata(vm, expectedValues, inUse)
    }

    @Test
    fun testResetComputeData() {
        val vm = AddOnesViewModel()

        // no saved values
        vm.resetComputeData()
        checkBlank(vm)

        appendText(vm, "1+1")
        vm.resetComputeData()
        checkBlank(vm)

        vm.setResult(15, null)
        vm.resetComputeData()
        checkBlank(vm)

        // saved but not used
        saveResult(vm, 16)
        val expectedValues = mutableListOf(16, null)
        vm.resetComputeData()
        assertTrue(vm.calcData.isEmpty())
        checkMetadata(vm, expectedValues)

        saveResultToIndex(vm, 11, 1, expectedValues)
        vm.resetComputeData()
        assertTrue(vm.calcData.isEmpty())
        checkMetadata(vm, expectedValues)

        // saved and in use
        vm.appendSavedValueAtIndex(0)
        vm.appendSavedValueAtIndex(1)
        vm.resetComputeData()
        assertTrue(vm.calcData.isEmpty())
        checkMetadata(vm, expectedValues)
    }

    // save result to viewmodel
    private fun saveResult(vm: AddOnesViewModel, result: Int) {
        vm.setResult(result, null)
        vm.saveComputedValue()
    }

    // save result and update saved values list
    private fun saveResultToIndex(vm: AddOnesViewModel, result: Int, index: Int, savedValues: MutableList<Int?>) {
        saveResult(vm, result)
        savedValues[index] = result
    }

    // append text to compute text
    private fun appendText(vm: AddOnesViewModel, text: String) {
        splitText(text).forEach { vm.appendComputeText(it) }
    }

    // check that calc data and saved value metadata is blank
    private fun checkBlank(vm: AddOnesViewModel) {
        assertTrue(vm.calcData.isEmpty())
        checkMetadata(vm, empty)
    }

    // check saved value metadata at every index
    private fun checkMetadata(vm: AddOnesViewModel, expectedValues: List<Int?>, inUse: Set<Int> = emptySet()) {
        assertEquals(expectedValues, vm.savedValues)
        expectedValues.forEachIndexed { index, value ->
            val expected = Pair(value, index in inUse)
            assertEquals(expected, vm.getSavedValueMetadata(index))
        }
    }

    // backspace multiple times
    private fun repeatBackspace(vm: AddOnesViewModel, count: Int) = repeat(count) { vm.backspaceComputeText() }
}
