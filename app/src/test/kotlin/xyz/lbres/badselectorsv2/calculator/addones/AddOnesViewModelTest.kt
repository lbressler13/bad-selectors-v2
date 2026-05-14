package xyz.lbres.badselectorsv2.calculator.addones

import android.util.Log
import io.mockk.unmockkAll
import io.mockk.verify
import xyz.lbres.badselectorsv2.calculator.splitText
import xyz.lbres.badselectorsv2.calculator.utils.CalcData
import xyz.lbres.badselectorsv2.testutils.mockLog
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals

class AddOnesViewModelTest {
    private val empty: List<Int?> = listOf(null, null)

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
        assertEquals(2, vm.maxSavedValues)
        assertEquals(empty, vm.savedValues)
        assertEquals(CalcData(), vm.calcData)
    }

    @Test
    fun testSaveComputedValue() {
        var vm = AddOnesViewModel()
        vm.setResult(1, null)

        // check with no available spots
        // check with error
    }

    @Test
    // TODO this test is way too long
    fun testAppendSavedValueAtIndex() {
        var vm = AddOnesViewModel()

        val appendNull = { index: Int, callCount: Int ->
            vm.appendSavedValueAtIndex(index)
            verify(exactly = callCount) { Log.e(any(), any()) }
        }

        // empty text
        appendNull(0, 1)
        appendNull(1, 2)

        saveResult(vm, 6)
        appendNull(1, 3)

        vm.appendSavedValueAtIndex(0)
        assertEquals(splitText("6"), vm.calcData.computeText)

        // with text
        vm = AddOnesViewModel()
        saveResult(vm, 2)
        appendText(vm, "+")
        vm.appendSavedValueAtIndex(0)
        var expectedText = splitText("+2")
        assertEquals(expectedText, vm.calcData.computeText)

        vm = AddOnesViewModel()
        saveResult(vm, 2)
        appendText(vm, "1")
        vm.appendSavedValueAtIndex(0)
        expectedText = splitText("12")
        assertEquals(expectedText, vm.calcData.computeText)

        vm = AddOnesViewModel()
        saveResult(vm, 2)
        appendText(vm, "1+1-1-")
        vm.appendSavedValueAtIndex(0)
        expectedText = splitText("1+1-1-2")
        assertEquals(expectedText, vm.calcData.computeText)
        appendNull(1, 4)

        // multiple values
        vm = AddOnesViewModel()
        saveResult(vm, 4)
        saveResult(vm, 5)
        vm.appendSavedValueAtIndex(1)
        vm.appendSavedValueAtIndex(0)
        assertEquals(listOf("5", "4"), vm.calcData.computeText)

        vm = AddOnesViewModel()
        saveResult(vm, 4)
        saveResult(vm, 5)
        appendText(vm, "1+")
        vm.appendSavedValueAtIndex(0)
        appendText(vm, "-1+1+")
        vm.appendSavedValueAtIndex(1)
        appendText(vm, "-1")
        expectedText = splitText("1+4-1+1+5-1")
        assertEquals(expectedText, vm.calcData.computeText)

        // already appended
        vm = AddOnesViewModel()
        saveResult(vm, 4)
        vm.appendSavedValueAtIndex(0)
        assertEquals(listOf("4"), vm.calcData.computeText)
        vm.appendSavedValueAtIndex(0)
        assertEquals(listOf("4"), vm.calcData.computeText)
        verify(exactly = 5) { Log.e(any(), any()) }
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
    fun testSavedValueMetadata() {

    }

    @Test
    fun testBackspaceComputeText() {
        // empty
        var vm = AddOnesViewModel()
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

        // saved value

        // computed value

        // error
    }

    @Test
    fun testResetComputeData() {
        // check that saved values aren't cleared, just saved indices
    }

    private fun saveResult(vm: AddOnesViewModel, result: Int) {
        vm.setResult(result, null)
        vm.saveComputedValue()
    }

    private fun appendText(vm: AddOnesViewModel, text: String) {
        splitText(text).forEach { vm.appendComputeText(it) }
    }

    private fun checkBlank(vm: AddOnesViewModel) {
        assertEquals(CalcData(), vm.calcData)
        assertEquals(empty, vm.savedValues)
    }

    private fun repeatBackspace(vm: AddOnesViewModel, count: Int) = repeat(count) { vm.backspaceComputeText() }
}
