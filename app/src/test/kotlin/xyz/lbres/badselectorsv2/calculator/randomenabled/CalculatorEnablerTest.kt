package xyz.lbres.badselectorsv2.calculator.randomenabled

import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkStatic
import io.mockk.unmockkAll
import io.mockk.verify
import junit.framework.TestCase.assertFalse
import xyz.lbres.badselectorsv2.utils.seededRandom
import xyz.lbres.badselectorsv2.utils.seededShuffled
import kotlin.test.AfterTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class CalculatorEnablerTest {
    private val shuffledNumbers = listOf(1, 4, 7, 3, 2, 6, 0, 8, 9, 5)
    private val shuffledOpsIndices = listOf(0, 2, 3, 1)
    private val operators = listOf("+", "-", "x", "/")

    @AfterTest
    fun cleanupTest() {
        unmockkAll()
    }

    @Test
    fun testIsEnabled() {
        mockkStatic(IntRange::seededRandom, IntRange::seededShuffled)
        with(mockk<IntRange>()) {
            every { IntRange(3, 7).seededRandom() } returns 5 // enabled numbers
            every { IntRange(2, 3).seededRandom() } returns 2 // enabled ops
            every { IntRange(0, 9).seededShuffled() } returns shuffledNumbers
            every { IntRange(0, 3).seededShuffled() } returns shuffledOpsIndices

            val enabler = CalculatorEnabler()
            val enabledNumbers = shuffledNumbers.subList(0, 5)
            val enabledOps = listOf("+", "x")
            checkNumbersEnabled(enabler, enabledNumbers)
            checkOperatorsEnabled(enabler, enabledOps)

            // duplicate values
            assertTrue(enabler.isEnabled(4))
            assertFalse(enabler.isEnabled(6))
            assertTrue(enabler.isEnabled("+"))
            assertFalse(enabler.isEnabled("-"))

            verifyCallCount(1)
        }
    }

    @Test
    fun testIsEnabledInvalidValues() {
        mockkStatic(IntRange::seededRandom, IntRange::seededShuffled)
        with(mockk<IntRange>()) {
            every { IntRange(3, 7).seededRandom() } returns 5
            every { IntRange(2, 3).seededRandom() } returns 2
            every { IntRange(0, 9).seededShuffled() } returns shuffledNumbers
            every { IntRange(0, 3).seededShuffled() } returns shuffledOpsIndices

            val enabler = CalculatorEnabler()
            assertFalse(enabler.isEnabled(10))
            assertFalse(enabler.isEnabled(-1))
            assertFalse(enabler.isEnabled(""))
            assertFalse(enabler.isEnabled("1"))
            assertFalse(enabler.isEnabled("^"))
        }
    }

    @Test
    fun testUpdate() {
        mockkStatic(IntRange::seededRandom, IntRange::seededShuffled)
        with(mockk<IntRange>()) {
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

            every { IntRange(3, 7).seededRandom() } returnsMany numberCountValues
            every { IntRange(2, 3).seededRandom() } returnsMany opCountValues
            every { IntRange(0, 9).seededShuffled() } returnsMany numbersValues
            every { IntRange(0, 3).seededShuffled() } returnsMany opIndexValues

            val enabler = CalculatorEnabler()
            var enabledNumbers = shuffledNumbers.subList(0, 4)
            var enabledOps = listOf("+", "x", "/")
            checkNumbersEnabled(enabler, enabledNumbers)
            checkOperatorsEnabled(enabler, enabledOps)

            enabler.update()
            enabledNumbers = (0..6).toList() // first 7 numbers
            enabledOps = listOf("+", "-", "x")
            checkNumbersEnabled(enabler, enabledNumbers)
            checkOperatorsEnabled(enabler, enabledOps)

            enabler.update()
            enabledNumbers = (0..2).toList() // first 3 numbers
            enabledOps = listOf("+", "-")
            checkNumbersEnabled(enabler, enabledNumbers)
            checkOperatorsEnabled(enabler, enabledOps)

            verifyCallCount(3)
        }
    }

    @Test
    fun testUpdateRepeatedValues() {
        mockkStatic(IntRange::seededRandom, IntRange::seededShuffled)
        with(mockk<IntRange>()) {
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
                (0..3).toList(), // changed ops
            )

            every { IntRange(3, 7).seededRandom() } returns 5 // enabled numbers
            every { IntRange(2, 3).seededRandom() } returns 2 // enabled ops
            every { IntRange(0, 9).seededShuffled() } returnsMany numbersValues
            every { IntRange(0, 3).seededShuffled() } returnsMany opIndexValues

            val enabler = CalculatorEnabler()
            var enabledNumbers = shuffledNumbers.subList(0, 5)
            var enabledOps = listOf("+", "x")
            checkNumbersEnabled(enabler, enabledNumbers)
            checkOperatorsEnabled(enabler, enabledOps)

            enabler.update()
            enabledNumbers = (0..4).toList()
            enabledOps = listOf("+", "-")
            checkNumbersEnabled(enabler, enabledNumbers)
            checkOperatorsEnabled(enabler, enabledOps)

            verifyCallCount(4)
        }
    }

    private fun checkOperatorsEnabled(enabler: CalculatorEnabler, enabledOperators: List<String>) {
        for (op in operators) {
            val result = enabler.isEnabled(op)
            try {
                assertEquals(op in enabledOperators, result)
            } catch (e: AssertionError) {
                println("Failure checking operator $op")
                throw e
            }
        }
    }

    private fun checkNumbersEnabled(enabler: CalculatorEnabler, enabledNumbers: List<Int>) {
        repeat(10) {
            val result = enabler.isEnabled(it)
            try {
                assertEquals(it in enabledNumbers, result)
            } catch (e: AssertionError) {
                println("Failure checking index $it")
                throw e
            }
        }
    }

    private fun verifyCallCount(callCount: Int) {
        verify(exactly = callCount) { IntRange(3, 7).seededRandom() }
        verify(exactly = callCount) { IntRange(2, 3).seededRandom() }
        verify(exactly = callCount) { IntRange(0, 9).seededShuffled() }
        verify(exactly = callCount) { IntRange(0, 3).seededShuffled() }
    }
}
