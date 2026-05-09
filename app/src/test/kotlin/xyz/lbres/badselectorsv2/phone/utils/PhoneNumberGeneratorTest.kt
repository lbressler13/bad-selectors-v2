package xyz.lbres.badselectorsv2.phone.utils

import android.util.Log
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkStatic
import io.mockk.unmockkAll
import io.mockk.verify
import xyz.lbres.badselectorsv2.testutils.mockkLog
import xyz.lbres.badselectorsv2.testutils.runWithRetries
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
    fun testAlternateConstructor() {
        val mockReturns = listOf(2, 3, 2, 4)
        withMockedRange(2..4, mockReturns) {
            val previousGenerated: MutableSet<IntList> = mutableSetOf()
            val generator = PhoneNumberGenerator(2..4)
            mockReturns.forEach { testRepeatedNumber(generator, it, previousGenerated) }
            assertEquals(mockReturns.size, previousGenerated.size)
        }
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
        repeat(5) { testRepeatedNumber(generator, 2, previousGenerated) }

        generator = PhoneNumberGenerator(fullNumberRepeats = 2 until 3)
        repeat(2) { testRepeatedNumber(generator, 2, previousGenerated) }

        previousGenerated.clear()
        generator = PhoneNumberGenerator(fullNumberRepeats = 4..4)
        repeat(5) { testRepeatedNumber(generator, 4, previousGenerated) }

        testMockedRangeRepeats(1..3, listOf(1, 3, 2, 1))
        testMockedRangeRepeats(4..8, listOf(5, 5, 5, 5))
        testMockedRangeRepeats(1 until 8, listOf(5, 5, 5, 5))
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
        testMockedRangeRepeats(1..3, listOf(1, 3, 2, 1), initialRange = -3..3)
        verify { Log.w(null, errorMessage(-3..3, 1..3)) }
        testMockedRangeRepeats(1..7, listOf(5, 5, 5, 5), initialRange = 0 until 8)
        verify { Log.w(null, errorMessage(0..7, 1..7)) }
    }

    @Test
    fun testGenerateNoDigitRepeatsAndFullRepeats() {
        val generator = PhoneNumberGenerator(false, 2..6)
        var previousGenerated: Set<IntList> = emptySet()
        val mockRandomValues = listOf(2, 5, 4, 3, 3, 6, 2, 5, 4, 2)
        withMockedRange(2..6, mockRandomValues + mockRandomValues + mockRandomValues) {
            repeat(3) {
                // generate numbers
                val generatedNumbers: MutableSet<IntList> = mutableSetOf()
                mockRandomValues.forEach { testRepeatedNumber(generator, it, generatedNumbers) }

                // ensure total generated numbers is correct
                assertEquals(numDigits, generatedNumbers.size)
                // ensure all digits are included at every position
                assertAllDigitsGenerated(generatedNumbers)

                // ensure the generator isn't cycling through the same combinations
                assertNotEquals(previousGenerated, generatedNumbers)
                previousGenerated = generatedNumbers
            }
        }
        verify(exactly = 0) { Log.w(any(), any<String>()) }
    }

    @Test
    fun testForceGenerate() {
        // default
        var generator = PhoneNumberGenerator()
        var generated = generator.generateNumber()
        assertNotEquals(generated, generator.generateNumber(true))

        // no repeat digits
        generator = PhoneNumberGenerator(false)
        val generatedNumbers: MutableSet<IntList> = mutableSetOf()
        repeat(6) { generatedNumbers.add(generator.generateNumber()) }
        generated = generator.generateNumber(true)
        digitsRange.forEach {
            assertFalse { generated[it] in digitsAtIndex(it, generatedNumbers) }
        }

        // repeat full numbers
        withMockedRange(2..4, listOf(4, 2, 3, 2)) {
            generator = PhoneNumberGenerator(fullNumberRepeats = 2..4)
            generated = generator.generateNumber()
            assertEquals(generated, generator.generateNumber()) // 4

            var previousGenerated = generated
            generated = generator.generateNumber(true)
            assertNotEquals(previousGenerated, generated)
            assertEquals(generated, generator.generateNumber())

            previousGenerated = generated
            generated = generator.generateNumber()
            assertNotEquals(previousGenerated, generated)
            assertEquals(generated, generator.generateNumber())
        }

        // frozen
        generator = PhoneNumberGenerator()
        generated = generator.generateNumber()
        generator.freezeAtIndex(4)
        val frozen = generated[4]
        repeat(2) {
            val newGenerated = generator.generateNumber()
            assertNotEquals(generated, newGenerated)
            assertEquals(frozen, newGenerated[4])
        }
    }

    // test that the repeats remaining is being pulled from correct range
    @Test
    fun testRepeatCounts() {
        val range = 2..4
        val generator = PhoneNumberGenerator(range)

        val counts: MutableSet<Int> = mutableSetOf()
        var currentCount = 0
        var lastGenerated: IntList? = null

        repeat(200) {
            val generated = generator.generateNumber()
            if (generated == lastGenerated) {
                currentCount++
            } else {
                if (lastGenerated != null) {
                    counts.add(currentCount)
                }
                currentCount = 1
                lastGenerated = generated
            }
        }

        assertEquals(range.toSet(), counts)
    }

    @Test
    fun testFreezeAtIndex() {
        val generatedNumbers: MutableSet<IntList> = mutableSetOf()
        var generator = PhoneNumberGenerator()
        var generated = generator.generateNumber()
        val frozen: Array<Int?> = arrayOfNulls(numDigits)

        val freezeDigits = { digits: List<Int> ->
            digits.forEach {
                generator.freezeAtIndex(it)
                frozen[it] = generated[it]
            }
        }
        val freezeDigit = { digit: Int -> freezeDigits(listOf(digit)) }
        val generate = { repetitions: Int ->
            repeat(repetitions) {
                generated = generator.generateNumber()
                generatedNumbers.add(generated)
            }
        }

        // default generator
        generator = PhoneNumberGenerator()
        val freezeOrder = listOf(6, 2, 4, 9, 0, 1, 3, 7, 8, 5)
        repeat(10) {
            generate(1)
            freezeDigit(freezeOrder[it])
            for (i in 0 until it) {
                val digit = freezeOrder[it]
                assertEquals(frozen[digit], generated[digit])
            }
        }
        repeat(5) {
            generate(1)
            assertEquals(frozen.toList(), generated)
        }

        // reset values
        frozen.setAllValues(null)
        generatedNumbers.clear()

        // no digit repeats
        generator = PhoneNumberGenerator(false)
        generate(1)
        var frozenDigits = listOf(0, 1, 3, 5, 9)
        freezeDigits(frozenDigits)
        generate(7)
        freezeDigit(7)
        generate(2)
        digitsRange.forEach {
            val digits = digitsAtIndex(it, generatedNumbers)
            when (it) {
                in frozenDigits -> assertEquals(setOf(frozen[it]), digits)
                7 -> assertEquals(8, digits.size)
                else -> assertEquals(digitsRange.toSet(), digits)
            }
        }
        generate(2)
        freezeDigits(listOf(2, 4, 6, 8))
        repeat(5) {
            generate(1)
            assertEquals(frozen.toList(), generated)
        }

        // reset values
        frozen.setAllValues(null)
        generatedNumbers.clear()

        // full number repeats
        generator = PhoneNumberGenerator(false, 2..4)
        val mockRandomValues = listOf(2, 4, 3)
        withMockedRange(2..4, mockRandomValues) {
            every { IntRange(2, 4).seededRandom() } returnsMany mockRandomValues
            generate(1)
            freezeDigit(6) // freeze after 1 repeat
            generate(2)
            assertEquals(frozen[6], generated[6])
            freezeDigits(listOf(4, 9))
            val prevGenerated = generated
            generate(1)
            // nothing updates before repeat count it up
            assertEquals(prevGenerated, generated)
            generate(4)
            // everything else updates when repeat count it up
            repeat(numDigits) {
                if (it in listOf(4, 6, 9)) {
                    assertEquals(frozen[it], generated[it])
                } else {
                    assertNotEquals(prevGenerated[it], generated[it])
                }
            }
        }

        // repeat freezing
        generator = PhoneNumberGenerator()
        generate(1)
        freezeDigit(2)
        val frozenVal = generated[2]
        generate(2)
        freezeDigit(2)
        generate(2)
        assertEquals(frozenVal, generated[2])

        // reset values
        frozen.setAllValues(null)
        generatedNumbers.clear()

        // freeze after reset
        generator = PhoneNumberGenerator()
        freezeDigit(3)
        generator.reset()
        while (generated[3] == frozen[3]) {
            generate(1)
        }
        freezeDigit(3)
        generate(1)
        assertEquals(frozen[3], generated[3])
    }

    @Test
    fun testReset() {
        // with digit repeats
        var generator = PhoneNumberGenerator()
        testDefaultBehaviour(generator)
        generator.reset()
        testDefaultBehaviour(generator)

        // frozen digits
        val generated = generator.generateNumber()
        val frozen = generated[2]
        generator.freezeAtIndex(2)
        generator.reset()
        // retries because there's a 10% chance of a match each time
        runWithRetries(3) {
            assertNotEquals(frozen, generator.generateNumber()[2])
        }

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
                testRepeatedNumber(generator, 2, add = false) // reset after 2 generations

                // 4
                var generated = testRepeatedNumber(generator, 2, add = false)

                // interrupt
                generator.reset()

                // 3
                var prevGenerated = generated
                generated = testRepeatedNumber(generator, 3, add = false)
                assertNotEquals(prevGenerated, generated)

                // move to new number
                prevGenerated = generated
                generated = testRepeatedNumber(generator, 2, add = false)
                assertNotEquals(prevGenerated, generated)
            }
        }
    }
}
