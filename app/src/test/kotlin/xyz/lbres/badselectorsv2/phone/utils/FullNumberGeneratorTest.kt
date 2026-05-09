package xyz.lbres.badselectorsv2.phone.utils

import android.util.Log
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkStatic
import io.mockk.unmockkAll
import io.mockk.verify
import xyz.lbres.badselectorsv2.testutils.mockkLog
import xyz.lbres.badselectorsv2.utils.seededRandom
import xyz.lbres.kotlinutils.array.ext.setAllValues
import xyz.lbres.kotlinutils.list.IntList
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNotEquals
import kotlin.test.assertTrue

class FullNumberGeneratorTest {
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
        testDefaultBehaviour(FullNumberGenerator())
    }

    @Test
    fun testGenerateNoDigitRepeats() {
        val generator = FullNumberGenerator(allowRepeatDigits = false)
        testBehaviour(generator) { generatedNumbers ->
            // ensure all 10 digits are included
            repeat(numDigits) { index ->
                val generatedAtDigit = generatedNumbers.map { it[index] }.toSet()
                assertEquals(digitsRange.toSet(), generatedAtDigit)
            }
        }
    }

    @Test
    fun testGenerateFullRepeats() {
        val previousGenerated: MutableSet<IntList> = mutableSetOf()

        var generator = FullNumberGenerator(fullNumberRepeats = 2..2)
        repeat(5) {
            val generated = testRepeatedNumber(generator, 2, previousGenerated)
            previousGenerated.add(generated)
        }

        generator = FullNumberGenerator(fullNumberRepeats = 2 until 3)
        repeat(2) {
            val generated = testRepeatedNumber(generator, 2, previousGenerated)
            previousGenerated.add(generated)
        }

        previousGenerated.clear()
        generator = FullNumberGenerator(fullNumberRepeats = 4..4)
        repeat(5) {
            val generated = testRepeatedNumber(generator, 4, previousGenerated)
            previousGenerated.add(generated)
        }

        mockkStatic(IntRange::seededRandom) {
            testMockedRange(1..3, listOf(1, 3, 2, 1))
            testMockedRange(4..8, listOf(5, 5, 5, 5))
            testMockedRange(1 until 8, listOf(5, 5, 5, 5))
        }
    }

    @Test
    fun testInvalidFullRepeats() {
        val errorMessage = { initialRange: IntRange, newRange: IntRange ->
            "Invalid full number repeats range provided: $initialRange. Using alternate range $newRange instead."
        }

        val testFullRangeInvalid = { range: IntRange ->
            testDefaultBehaviour(FullNumberGenerator(fullNumberRepeats = range))
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
        val generator = FullNumberGenerator(false, 2..6)
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
    }

    @Test
    fun testFreezeAtIndex() {
        val generatedNumbers: MutableSet<IntList> = mutableSetOf()
        var generator = FullNumberGenerator()
        var generated = generator.generateNumber()
        val frozen: Array<Int?> = arrayOfNulls(numDigits)

        frozen.setAllValues(null)
        generatedNumbers.clear()

        // no digit repeats
        generator = FullNumberGenerator(false)
        generated = generator.generateNumber()
        generatedNumbers.add(generated)
        var frozenDigits = listOf(0, 1, 3, 5, 9)
        frozenDigits.forEach {
            generator.freezeAtIndex(it)
            frozen[it] = generated[it]
        }
        repeat(6) { generatedNumbers.add(generator.generateNumber()) }
        generated = generator.generateNumber()
        generatedNumbers.add(generated)
        generator.freezeAtIndex(7)
        frozen[7] = generated[7]
        repeat(2) { generatedNumbers.add(generator.generateNumber()) }
        digitsRange.forEach {
            val digits = digitsAtIndex(it, generatedNumbers)
            when (it) {
                in frozenDigits -> assertEquals(setOf(frozen[it]), digits)
                7 -> assertEquals(8, digits.size)
                else -> assertEquals(digitsRange.toSet(), digits)
            }
        }

        // full number repeats
    }

    @Test
    fun testReset() {
        // with digit repeats
        var generator = FullNumberGenerator()
        testDefaultBehaviour(generator)
        generator.reset()
        testDefaultBehaviour(generator)

        // frozen digits
        val generated = generator.generateNumber()
        val frozen = generated[2]
        generator.freezeAtIndex(2)
        generator.reset()
        // retries because there's a 10% chance of a match each time
        var error: AssertionError? = null
        for (i in 0..2) {
            try {
                assertNotEquals(frozen, generator.generateNumber()[2])
                error = null
                break
            } catch (e: AssertionError) {
                error = e
            }
        }
        if (error != null) {
            throw error
        }

        // without digit repeats
        generator = FullNumberGenerator(false)
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
        val generator = FullNumberGenerator(fullNumberRepeats = 2..4)
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
    private fun testBehaviour(generator: FullNumberGenerator, validateNumbers: (Set<IntList>) -> Unit) {
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
    private fun testDefaultBehaviour(generator: FullNumberGenerator) {
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
        val generator = FullNumberGenerator(fullNumberRepeats = initialRange ?: repeatsRange)

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
        generator: FullNumberGenerator,
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
