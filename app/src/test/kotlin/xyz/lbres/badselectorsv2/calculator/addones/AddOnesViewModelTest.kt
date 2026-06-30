package xyz.lbres.badselectorsv2.calculator.addones

import android.util.Log
import io.mockk.unmockkAll
import io.mockk.verify
import xyz.lbres.badselectorsv2.calculator.splitText
import xyz.lbres.badselectorsv2.testutils.mockLog
import xyz.lbres.kotlinutils.collection.list.listOfNulls
import xyz.lbres.kotlinutils.collection.list.mutableListOfNulls
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class AddOnesViewModelTest {
    private val maxSavedValues = 4
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
        saveResultsToIndices(vm, listOf(1, -12, 0, 6), listOf(0, 1, 2, 3), expectedValues, true)

        // no available spots
        saveResult(vm, 5)
        assertEquals(expectedValues, vm.savedValues)

        // cleared spots
        clearAtIndex(vm, 1, expectedValues)
        vm.saveComputedValue()
        expectedValues[1] = 5
        assertEquals(expectedValues, vm.savedValues)

        // multiple blanks
        clearAtIndex(vm, 1, expectedValues)
        clearAtIndex(vm, 3, expectedValues)
        saveResult(vm, -1)
        expectedValues[1] = -1  // stored in earliest blank
        assertEquals(expectedValues, vm.savedValues)

        // with error
        clearAtIndex(vm, 0, expectedValues)
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
        append(listOf(0, 1, 2, 3))
        checkBlank(vm)

        saveResult(vm, 6)
        append(listOf(3, 0, 2, 1))
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
        saveResults(vm, listOf(4, 5, 0, -1))
        append(listOf(1, 0, 3, 2))
        assertEquals(listOf("5", "4", "-1", "0"), vm.calcData.computeText)

        vm = AddOnesViewModel()
        saveResults(vm, listOf(4, 5, 0, -1))
        append(listOf("1+", 0, "-1+1+", 1, "-1+", 2, "-", 3))
        val expected = splitText("1+4-1+1+5-1+0-") + listOf("-1")
        assertEquals(expected, vm.calcData.computeText)

        // already appended
        vm = AddOnesViewModel()
        saveResult(vm, 4)
        vm.appendSavedValueAtIndex(0)
        assertEquals(listOf("4"), vm.calcData.computeText)
        vm.appendSavedValueAtIndex(0)
        assertEquals(listOf("4"), vm.calcData.computeText)

        // index out of bounds
        vm.appendSavedValueAtIndex(5)
        assertEquals(listOf("4"), vm.calcData.computeText)
        verify(exactly = 1) { Log.e(any(), any()) }
    }

    @Test
    fun testClearSavedValueAtIndex() {
        val vm = AddOnesViewModel()

        // clear with none
        vm.clearSavedValueAtIndex(0)
        assertEquals(empty, vm.savedValues)
        vm.clearSavedValueAtIndex(1)
        assertEquals(empty, vm.savedValues)
        vm.clearSavedValueAtIndex(2)
        assertEquals(empty, vm.savedValues)
        vm.clearSavedValueAtIndex(3)
        assertEquals(empty, vm.savedValues)

        // clear with only one
        saveResult(vm, 5)
        vm.clearSavedValueAtIndex(1)
        assertEquals(listOf(5, null, null, null), vm.savedValues)
        vm.clearSavedValueAtIndex(0)
        assertEquals(empty, vm.savedValues)

        // clear out of order
        var savedValues: MutableList<Int?> = mutableListOf(-1, 0, 6, 23)
        @Suppress("UNCHECKED_CAST")
        saveResults(vm, savedValues as List<Int>)
        clearAtIndices(vm, listOf(0, 1, 3, 2), savedValues, true)

        // index out of bounds
        savedValues = mutableListOf(-1, 0, 7, 7)
        @Suppress("UNCHECKED_CAST")
        saveResults(vm, savedValues as List<Int>)
        vm.clearSavedValueAtIndex(5)
        verify(exactly = 1) { Log.e(any(), any()) }
        assertEquals(savedValues, vm.savedValues)
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
        checkMetadata(vm, expectedValues, inUse)

        saveResultToIndex(vm, 14, 1, expectedValues)
        checkMetadata(vm, expectedValues, inUse)

        saveResultToIndex(vm, 1, 2, expectedValues)
        checkMetadata(vm, expectedValues, inUse)

        // used
        vm.appendSavedValueAtIndex(1)
        inUse.add(1)
        checkMetadata(vm, expectedValues, inUse)

        appendText(vm, "+2-")
        vm.appendSavedValueAtIndex(0)
        inUse.add(0)
        checkMetadata(vm, expectedValues, inUse)

        saveResultToIndex(vm, 3, 3, expectedValues)

        // changed
        vm.setResult(2, null)
        vm.clearSavedValueAtIndex(0)
        vm.saveComputedValue()
        expectedValues[0] = 2
        inUse.clear()
        checkMetadata(vm, expectedValues, inUse)

        // cleared
        clearAtIndex(vm, 2, expectedValues)
        checkMetadata(vm, expectedValues, inUse)
        clearAtIndex(vm, 0, expectedValues)

        // index out of bounds
        val result = vm.getSavedValueMetadata(5)
        assertEquals(Pair(null, true), result)
        verify(exactly = 1) { Log.e(any(), any()) }
    }

    @Test
    fun testBackspaceComputeText() {
        val vm = AddOnesViewModel()
        val expectedValues = listOf(7, 4, 5, 2)
        val inUse: MutableSet<Int> = mutableSetOf()

        val append = { content: List<Any> ->
            content.forEach {
                when (it) {
                    is String -> appendText(vm, it)
                    is Int -> {
                        vm.appendSavedValueAtIndex(it)
                        inUse.add(it)
                    }
                }
            }
        }

        // empty
        vm.backspaceComputeText()
        checkBlank(vm)

        // no saved values
        appendText(vm, "1")
        vm.backspaceComputeText()
        checkBlank(vm)

        appendText(vm, "1+2--")
        var expectedText = splitText("1+2-")
        backspaceAndValidate(vm, 1, expectedText, empty, inUse)
        expectedText = splitText("1+2")
        backspaceAndValidate(vm, 1, expectedText, empty, inUse)
        backspaceAndValidate(vm, 3, emptyList(), empty, inUse)

        // saved but not used
        saveResults(vm, expectedValues)
        appendText(vm, "1+1+1")
        checkMetadata(vm, expectedValues, inUse)
        backspaceAndValidate(vm, 5, emptyList(), expectedValues, inUse)

        // saved value
        saveResults(vm, expectedValues)
        append(listOf("1+", 1, "-1-1-", 0, 3, "+", 2, "-"))
        expectedText = splitText("1+4-1-1-72+5-")
        assertEquals(expectedText, vm.calcData.computeText)
        checkMetadata(vm, expectedValues, inUse)

        expectedText = splitText("1+4-1-1-72+5")
        backspaceAndValidate(vm, 1, expectedText, expectedValues, inUse)

        expectedText = splitText("1+4-1-1-72+")
        inUse.remove(2)
        backspaceAndValidate(vm, 1, expectedText, expectedValues, inUse)

        expectedText = splitText("1+4-1-1-7")
        inUse.remove(3)
        backspaceAndValidate(vm, 2, expectedText, expectedValues, inUse)

        expectedText = splitText("1+4-1-1-")
        inUse.remove(0)
        backspaceAndValidate(vm, 1, expectedText, expectedValues, inUse)

        expectedText = splitText("1+4")
        backspaceAndValidate(vm, 5, expectedText, expectedValues, inUse)

        expectedText = splitText("1+")
        inUse.remove(1)
        backspaceAndValidate(vm, 1, expectedText, expectedValues, inUse)

        expectedText = emptyList()
        backspaceAndValidate(vm, 2, expectedText, expectedValues, inUse)
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
        val expectedValues: MutableList<Int?> = mutableListOfNulls(maxSavedValues)
        expectedValues[0] = 16
        vm.resetComputeData()
        assertTrue(vm.calcData.isEmpty())
        checkMetadata(vm, expectedValues)

        saveResultsToIndices(vm, listOf(11, 12, 13), listOf(1, 2, 3), expectedValues)
        vm.resetComputeData()
        assertTrue(vm.calcData.isEmpty())
        checkMetadata(vm, expectedValues)

        // saved and in use
        vm.appendSavedValueAtIndex(0)
        vm.appendSavedValueAtIndex(2)
        vm.resetComputeData()
        assertTrue(vm.calcData.isEmpty())
        checkMetadata(vm, expectedValues)
    }
}
