package xyz.lbres.badselectorsv2.calculator.randomenabled

import io.mockk.unmockkAll
import io.mockk.verify
import junit.framework.TestCase.assertFalse
import xyz.lbres.badselectorsv2.testutils.withMockedIntRange
import xyz.lbres.badselectorsv2.utils.seededRandom
import xyz.lbres.badselectorsv2.utils.seededShuffled
import xyz.lbres.testutils.printErr
import kotlin.test.AfterTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class RandomEnabledViewModelTest {
    private val shuffledNumbers = listOf(1, 4, 7, 3, 2, 6, 0, 8, 9, 5)
    private val shuffledOpsIndices = listOf(0, 2, 3, 1)
    private val operators = listOf("+", "-", "x", "/")

    @AfterTest
    fun cleanupTest() {
        unmockkAll()
    }

    @Test
    fun testIsEnabled() {
        val randomMocks = getRandomMocks(listOf(5), listOf(2))
        val shuffledMocks = getShuffledMocks(listOf(shuffledNumbers), listOf(shuffledOpsIndices))
        withMockedIntRange(randomMocks, shuffledMocks) {
            val vm = RandomEnabledViewModel()
            val enabledNumbers = shuffledNumbers.subList(0, 5)
            val enabledOps = listOf("+", "x")
            checkEnabled(vm, enabledNumbers, enabledOps)

            // duplicate values
            assertTrue(vm.isDigitEnabled(4))
            assertFalse(vm.isDigitEnabled(6))
            assertTrue(vm.isOperatorEnabled("+"))
            assertFalse(vm.isOperatorEnabled("-"))

            verifyMockCalls(callCount = 1)
        }
    }

    @Test
    fun testIsEnabledInvalidValues() {
        val randomMocks = getRandomMocks(listOf(5), listOf(2))
        val shuffledMocks = getShuffledMocks(listOf(shuffledNumbers), listOf(shuffledOpsIndices))
        withMockedIntRange(randomMocks, shuffledMocks) {
            val vm = RandomEnabledViewModel()
            assertFalse(vm.isDigitEnabled(10))
            assertFalse(vm.isDigitEnabled(-1))
            assertFalse(vm.isOperatorEnabled(""))
            assertFalse(vm.isOperatorEnabled("1"))
            assertFalse(vm.isOperatorEnabled("^"))

            verifyMockCalls(callCount = 1)
        }
    }

    @Test
    fun testUpdateEnabled() {
        val numbersValues = listOf(
            shuffledNumbers,
            (0..9).toList(),
            (0..9).toList(), // repeat with different count
        )
        val opIndexValues = listOf(
            shuffledOpsIndices,
            (0..3).toList(),
            (0..3).toList(), // repeat with different count
        )
        val numberCountValues = listOf(4, 7, 3)
        val opCountValues = listOf(3, 3, 2)

        val randomMocks = getRandomMocks(numberCountValues, opCountValues)
        val shuffledMocks = getShuffledMocks(numbersValues, opIndexValues)

        withMockedIntRange(randomMocks, shuffledMocks) {
            val vm = RandomEnabledViewModel()
            var enabledNumbers = shuffledNumbers.subList(0, 4)
            var enabledOps = listOf("+", "x", "/")
            checkEnabled(vm, enabledNumbers, enabledOps)

            vm.updateEnabled()
            enabledNumbers = (0..6).toList() // first 7 numbers
            enabledOps = listOf("+", "-", "x")
            checkEnabled(vm, enabledNumbers, enabledOps)

            vm.updateEnabled()
            enabledNumbers = (0..2).toList() // first 3 numbers
            enabledOps = listOf("+", "-")
            checkEnabled(vm, enabledNumbers, enabledOps)

            verifyMockCalls(callCount = 3)
        }
    }

    @Test
    fun testUpdateRepeatedValues() {
        val numbersValues = listOf(
            shuffledNumbers, // initial
            shuffledNumbers, // repeat numbers
            (0..9).toList(), // repeat ops
            (0..9).toList(), // changed values
        )
        val opIndexValues = listOf(
            shuffledOpsIndices, // initial
            (0..3).toList(), // repeat numbers
            shuffledOpsIndices, // repeat ops
            (0..3).toList(), // changed values
        )

        val randomMocks = getRandomMocks(listOf(5), listOf(2))
        val shuffledMocks = getShuffledMocks(numbersValues, opIndexValues)

        withMockedIntRange(randomMocks, shuffledMocks) {
            val vm = RandomEnabledViewModel()
            var enabledNumbers = shuffledNumbers.subList(0, 5)
            var enabledOps = listOf("+", "x")
            checkEnabled(vm, enabledNumbers, enabledOps)

            vm.updateEnabled()
            enabledNumbers = (0..4).toList()
            enabledOps = listOf("+", "-")
            checkEnabled(vm, enabledNumbers, enabledOps)

            verifyMockCalls(callCount = 4)
        }
    }

    // check isDigitEnabled and isOperatorEnabled for given lists of numbers and operators
    private fun checkEnabled(vm: RandomEnabledViewModel, enabledNumbers: List<Int>, enabledOperators: List<String>) {
        repeat(10) {
            val result = vm.isDigitEnabled(it)
            try {
                assertEquals(it in enabledNumbers, result)
            } catch (e: AssertionError) {
                printErr("Failure checking index $it")
                throw e
            }
        }

        for (op in operators) {
            val result = vm.isOperatorEnabled(op)
            try {
                assertEquals(op in enabledOperators, result)
            } catch (e: AssertionError) {
                printErr("Failure checking operator $op")
                throw e
            }
        }
    }

    // verify that mocked IntRange functions were called as expected
    private fun verifyMockCalls(callCount: Int) {
        verify(exactly = callCount) { IntRange(3, 7).seededRandom() }
        verify(exactly = callCount) { IntRange(2, 3).seededRandom() }
        verify(exactly = callCount) { IntRange(0, 9).seededShuffled() }
        verify(exactly = callCount) { IntRange(0, 3).seededShuffled() }
    }

    // mock results of IntRange.seededRandom for the range of enabled numbers and enabled operators
    private fun getRandomMocks(numbers: List<Int>, ops: List<Int>) = mapOf(3..7 to numbers, 2..3 to ops)
    // mock results of IntRange.seededShuffled for the range of numbers and operator indices
    private fun getShuffledMocks(numbers: List<List<Int>>, ops: List<List<Int>>) = mapOf(0..9 to numbers, 0..3 to ops)
}
