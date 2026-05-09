package xyz.lbres.badselectorsv2.phone.utils

import android.util.Log
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkStatic
import io.mockk.unmockkAll
import io.mockk.verify
import xyz.lbres.badselectorsv2.testutils.mockkLog
import xyz.lbres.badselectorsv2.utils.seededRandom
import xyz.lbres.kotlinutils.list.IntList
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNotEquals
import kotlin.test.assertTrue

class PhoneNumberGeneratorTest {
    @BeforeTest
    fun setupTests() {
        mockkLog()
    }

    @AfterTest
    fun cleanupTest() {
        unmockkAll()
    }

    @Test
    fun testGenerateNumber() {
        testDefaultBehaviour(PhoneNumberGenerator())
        verify(exactly = 0) { Log.w(any(), any<String>()) }
    }

    @Test
    fun testGenerateNoDigitRepeats() {
        val generator = PhoneNumberGenerator(allowRepeatDigits = false)
        testBehaviour(generator) { generatedNumbers ->
            // ensure all 10 digits are included
            repeat(numDigits) { index ->
                val generatedAtDigit = generatedNumbers.map { it[index] }.toSet()
                assertEquals(digitsRange.toSet(), generatedAtDigit)
            }
        }
        verify(exactly = 0) { Log.w(any(), any<String>()) }
    }

    @Test
    fun testGenerateFullRepeats() {
        val previousGenerated: MutableSet<IntList> = mutableSetOf()

        var generator = PhoneNumberGenerator(fullNumberRepeats = 2..2)
        repeat(5) {
            val generated = testRepeatedNumber(generator, 2, previousGenerated)
            previousGenerated.add(generated)
        }

        generator = PhoneNumberGenerator(fullNumberRepeats = 2 until 3)
        repeat(2) {
            val generated = testRepeatedNumber(generator, 2, previousGenerated)
            previousGenerated.add(generated)
        }

        previousGenerated.clear()
        generator = PhoneNumberGenerator(fullNumberRepeats = 4..4)
        repeat(5) {
            val generated = testRepeatedNumber(generator, 4, previousGenerated)
            previousGenerated.add(generated)
        }

        mockkStatic(IntRange::seededRandom) {
            testMockedRange(1..3, listOf(1, 3, 2, 1))
            testMockedRange(4..8, listOf(5, 5, 5, 5))
            testMockedRange(1 until 8, listOf(5, 5, 5, 5))
        }
        verify(exactly = 0) { Log.w(any(), any<String>()) }
    }

    @Test
    fun testInvalidFullRepeats() {
        val errorMessage = { initialRange: IntRange, newRange: IntRange ->
            "Invalid full number repeats range provided: $initialRange. Using alternate range $newRange instead."
        }

        val testFullRangeInvalid = { range: IntRange ->
            testDefaultBehaviour(PhoneNumberGenerator(fullNumberRepeats = range))
            verify { Log.w(null, errorMessage(range, 1..1)) }
        }

        testFullRangeInvalid(0..0)
        testFullRangeInvalid(-2..-1)
        testFullRangeInvalid(-2 until 2)
        testFullRangeInvalid(5..2)

        // invalid range start only
        mockkStatic(IntRange::seededRandom) {
            testMockedRange(1..3, listOf(1, 3, 2, 1), initialRange = -3..3)
            verify { Log.w(null, errorMessage(-3..3, 1..3)) }
            testMockedRange(1..7, listOf(5, 5, 5, 5), initialRange = 0 until 8)
            verify { Log.w(null, errorMessage(0..7, 1..7)) }
        }
    }

    @Test
    fun testGenerateNoDigitRepeatsAndFullRepeats() {
        val generator = PhoneNumberGenerator(false, 2..6)
        var previousGenerated: Set<IntList> = emptySet()
        val mockRandomValues = listOf(2, 5, 4, 3, 3, 6, 2, 5, 4, 2)
        mockkStatic(IntRange::seededRandom) {
            repeat(3) {
                with(mockk<IntRange>()) {
                    every { IntRange(2, 6).seededRandom() } returnsMany mockRandomValues

                    // generate numbers
                    val generatedNumbers: MutableSet<IntList> = mutableSetOf()
                    mockRandomValues.forEach {
                        val generated = testRepeatedNumber(generator, it, generatedNumbers)
                        generatedNumbers.add(generated)
                    }

                    // ensure total generated numbers is correct
                    assertEquals(numDigits, generatedNumbers.size)
                    // ensure all digits are included at every position
                    assertAllDigitsGenerated(generatedNumbers)

                    // ensure the generator isn't cycling through the same combinations
                    assertNotEquals(previousGenerated, generatedNumbers)
                    previousGenerated = generatedNumbers
                }
            }
        }
        verify(exactly = 0) { Log.w(any(), any<String>()) }
    }

    @Test
    fun testReset() {
        // with digit repeats
        var generator = PhoneNumberGenerator()
        testDefaultBehaviour(generator)
        generator.reset()
        testDefaultBehaviour(generator)

        // without digit repeats
        generator = PhoneNumberGenerator(false)
        val generatedNumbers = mutableSetOf<IntList>()
        repeat(3) { generatedNumbers.add(generator.generateNumber()) }
        generator.reset()
        val newGeneratedNumbers = mutableSetOf<IntList>()
        repeat(3) { newGeneratedNumbers.add(generator.generateNumber()) }
        // some overlap, which means remaining counts were reset
        assertTrue {
            digitsRange.any {
                val oldDigits = digitsAtIndex(it, generatedNumbers)
                val newDigits = digitsAtIndex(it, newGeneratedNumbers)
                newDigits.intersect(oldDigits).isNotEmpty() && newDigits != oldDigits
            }
        }
        repeat(7) { newGeneratedNumbers.add(generator.generateNumber()) }
        assertAllDigitsGenerated(newGeneratedNumbers)
    }

    @Test
    fun testResetFullRepeats() {
        val generator = PhoneNumberGenerator(fullNumberRepeats = 2..4)
        val mockRandomValues = listOf(2, 4, 3, 2)
        mockkStatic(IntRange::seededRandom) {
            with(mockk<IntRange>()) {
                every { IntRange(2, 4).seededRandom() } returnsMany mockRandomValues

                // 2
                testRepeatedNumber(generator, 2)

                // 4
                var generated = testRepeatedNumber(generator, 2) // reset after 2 generations

                // interrupt
                generator.reset()

                // 3
                var prevGenerated = generated
                generated = testRepeatedNumber(generator, 3)
                assertNotEquals(prevGenerated, generated)

                // move to new number
                prevGenerated = generated
                generated = testRepeatedNumber(generator, 2)
                assertNotEquals(prevGenerated, generated)
            }
        }
    }

    /**
     * Test that a generator is generating unique numbers and perform extra validation on the generated numbers
     */
    private fun testBehaviour(generator: PhoneNumberGenerator, validateNumbers: (Set<IntList>) -> Unit) {
        var previousGenerated: Set<IntList> = emptySet()
        repeat(3) {
            // generate numbers
            val generatedNumbers: MutableSet<IntList> = mutableSetOf()
            repeat(numDigits) { generatedNumbers.add(generator.generateNumber()) }

            // ensure that numbers were not all duplicates
            generatedNumbers.forEach { number ->
                assertFalse { number.all { it == number[0] } }
            }
            assertEquals(numDigits, generatedNumbers.size)

            // validate selected digits
            validateNumbers(generatedNumbers)

            // ensure the generator isn't cycling through the same combinations
            assertNotEquals(previousGenerated, generatedNumbers)
            previousGenerated = generatedNumbers
        }
    }

    /**
     * Test that generator behaves like it was initialized with default parameters
     */
    private fun testDefaultBehaviour(generator: PhoneNumberGenerator) {
        testBehaviour(generator) { generatedNumbers ->
            var containsAllCount = 0
            repeat(numDigits) { index ->
                val generatedAtDigit = generatedNumbers.map { it[index] }.toSet()
                assertTrue(digitsRange.toSet().containsAll(generatedAtDigit))
                if (generatedAtDigit == digitsRange.toSet()) {
                    containsAllCount++
                }
            }
            assertNotEquals(numDigits, containsAllCount)
        }
    }

    /**
     * Validate number generation with given a repeats range. Assumes seededRandom is already mocked
     */
    private fun testMockedRange(repeatsRange: IntRange, mockRandomValues: IntList, initialRange: IntRange? = null) {
        val previousGenerated: MutableSet<IntList> = mutableSetOf()
        val generator = PhoneNumberGenerator(fullNumberRepeats = initialRange ?: repeatsRange)

        with(mockk<IntRange>()) {
            every { IntRange(repeatsRange.first, repeatsRange.last).seededRandom() } returnsMany mockRandomValues

            mockRandomValues.forEach {
                val generated = testRepeatedNumber(generator, it, previousGenerated)
                previousGenerated.add(generated)
            }
        }
        assertEquals(mockRandomValues.size, previousGenerated.size)
    }

    /**
     * Validate that a number doesn't change and doesn't match previous numbers
     */
    private fun testRepeatedNumber(
        generator: PhoneNumberGenerator,
        repetitions: Int,
        previousGenerated: Set<IntList> = emptySet(),
    ): IntList {
        val generated = generator.generateNumber()
        repeat(repetitions - 1) {
            assertEquals(generated, generator.generateNumber())
        }
        assertFalse(generated in previousGenerated)
        return generated
    }

    /**
     * Validate that all 10 digits were generated at every index
     */
    private fun assertAllDigitsGenerated(generatedNumbers: Set<IntList>) {
        digitsRange.forEach {
            assertEquals(digitsRange.toSet(), digitsAtIndex(it, generatedNumbers))
        }
    }

    /**
     * Get all values generated at the given index
     */
    private fun digitsAtIndex(index: Int, numbers: Set<IntList>) = numbers.map { it[index] }.toSet()
}
