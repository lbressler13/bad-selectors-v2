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
    fun testGenerateWithoutRepeats() {
        val generator = FullNumberGenerator(allowRepeatDigits = false)
        var previousGenerated: Set<IntList> = emptySet()
        repeat(3) {
            // generate numbers
            val generatedNumbers: MutableSet<IntList> = mutableSetOf()
            repeat(10) { generatedNumbers.add(generator.generateNumber()) }

            // ensure that numbers were not all duplicates
            generatedNumbers.forEach { number ->
                assertFalse { number.all { it == number[0] } }
            }
            assertEquals(10, generatedNumbers.size)

            // ensure all 10 digits are included
            repeat(10) { index ->
                val generatedAtDigit = generatedNumbers.map { it[index] }.toSet()
                assertEquals(digitsRange.toSet(), generatedAtDigit)
            }

            // ensure the generator isn't cycling through the same combinations
            if (previousGenerated.isNotEmpty()) {
                assertNotEquals(previousGenerated, generatedNumbers)
            }
            previousGenerated = generatedNumbers
        }
    }

    @Test
    fun testGenerateExtendedTimeToUpdate() {
        val previousGenerated: MutableSet<IntList> = mutableSetOf()

        var generator = FullNumberGenerator(fullNumberRepeats = 2..2)
        repeat(5) {
            val generated = generator.generateNumber()
            // always repeats once after generation
            assertEquals(generated, generator.generateNumber())
            assertFalse(generated in previousGenerated)
            previousGenerated.add(generated)
        }

        generator = FullNumberGenerator(fullNumberRepeats = 2 until 3)
        repeat(3) {
            val generated = generator.generateNumber()
            // always repeats once after generation
            assertEquals(generated, generator.generateNumber())
            assertFalse(generated in previousGenerated)
            previousGenerated.add(generated)
        }

        previousGenerated.clear()
        generator = FullNumberGenerator(fullNumberRepeats = 4..4)
        repeat(5) {
            val generated = generator.generateNumber()
            // always repeats three times after generation
            assertEquals(generated, generator.generateNumber())
            assertEquals(generated, generator.generateNumber())
            assertEquals(generated, generator.generateNumber())
            assertFalse(generated in previousGenerated)
            previousGenerated.add(generated)
        }

        mockkStatic(IntRange::seededRandom) {
            testMockedRange(1..3, listOf(1, 3, 2, 1))
            testMockedRange(4..8, listOf(5, 5, 5, 5))
            testMockedRange(1 until 8, listOf(5, 5, 5, 5))
        }
    }

    @Test
    fun testInvalidRepeatRanges() {
        val errorMessage = { initialRange: IntRange, newRange: IntRange ->
            "Invalid full number repeats range provided: $initialRange. Using alternate range $newRange instead."
        }

        var range = 0..0
        testDefaultBehaviour(FullNumberGenerator(fullNumberRepeats = range))
        verify { Log.w(null, errorMessage(range, 1..1)) }

        range = -2..-1
        testDefaultBehaviour(FullNumberGenerator(fullNumberRepeats = range))
        verify { Log.w(null, errorMessage(range, 1..1)) }

        range = -2 until 2
        testDefaultBehaviour(FullNumberGenerator(fullNumberRepeats = range))
        verify { Log.w(null, errorMessage(range, 1..1)) }

        range = 5..2
        testDefaultBehaviour(FullNumberGenerator(fullNumberRepeats = range))
        verify { Log.w(null, errorMessage(range, 1..1)) }

        // range start modified
        mockkStatic(IntRange::seededRandom) {
            testMockedRange(1..3, listOf(1, 3, 2, 1), initRange = -3..3)
            verify { Log.w(null, errorMessage(-3..3, 1..3)) }
            testMockedRange(1..7, listOf(5, 5, 5, 5), initRange = 0 until 8)
            verify { Log.w(null, errorMessage(0..7, 1..7)) }
        }
    }

    @Test
    fun testGenerateNoRepeatsAndExtendedTime() {
//        repeat(3) {
//            // generate numbers
//            val generatedNumbers: MutableSet<IntList> = mutableSetOf()
//            repeat(10) { generatedNumbers.add(generator.generateNumber()) }
//
//            // ensure that numbers were not all duplicates
//            generatedNumbers.forEach { number ->
//                assertFalse { number.all { it == number[0] } }
//            }
//            assertEquals(10, generatedNumbers.size)
//
//            // ensure all 10 digits are included
//            repeat(10) { index ->
//                val generatedAtDigit = generatedNumbers.map { it[index] }.toSet()
//                assertEquals(digitsRange.toSet(), generatedAtDigit)
//            }
//
//            // ensure the generator isn't cycling through the same combinations
//            if (previousGenerated.isNotEmpty()) {
//                assertNotEquals(previousGenerated, generatedNumbers)
//            }
//            previousGenerated = generatedNumbers
//        }

        // TODO run test several times
        val allGenerated: MutableList<IntList> = mutableListOf()
        val generator = FullNumberGenerator(false, 2..6)
        val mockRandomValues = listOf(2, 5, 4, 3, 3, 6, 2, 5, 4, 2)
        mockkStatic(IntRange::seededRandom) {
            with(mockk<IntRange>()) {
                every { IntRange(2, 6).seededRandom() } returnsMany mockRandomValues

                mockRandomValues.forEach {
                    // add numbers
                    val generated = generator.generateNumber()
                    repeat(it - 1) {
                        assertEquals(generated, generator.generateNumber())
                    }
                    allGenerated.add(generated)
                }
            }
        }

        assertEquals(10, allGenerated.size)
        assertEquals(10, allGenerated.toSet().size)
        // ensure all 10 digits are included
        repeat(10) { index ->
              val generatedAtDigit = allGenerated.map { it[index] }.toSet()
              assertEquals(digitsRange.toSet(), generatedAtDigit)
        }
    }

    @Test
    fun testReset() {
        // with repeats
        var generator = FullNumberGenerator()
        testDefaultBehaviour(generator, 2)
        generator.reset()
        testDefaultBehaviour(generator, 2)

        // without repeats
        generator = FullNumberGenerator(false)
        val generatedDigits = List(10) { mutableSetOf<Int>() }
        repeat(3) {
            val generated = generator.generateNumber()
            generated.forEachIndexed { index, value -> generatedDigits[index].add(value) }
        }
        generator.reset()
        val newGeneratedDigits = List(10) { mutableSetOf<Int>() }
        repeat(3) {
            val generated = generator.generateNumber()
            generated.forEachIndexed { index, value -> newGeneratedDigits[index].add(value) }
        }
        // some overlap, which means remaining counts were reset
        assertTrue { digitsRange.any { generatedDigits[it].intersect(newGeneratedDigits[it]).isNotEmpty() } }
        repeat(7) {
            val generated = generator.generateNumber()
            generated.forEachIndexed { index, value -> newGeneratedDigits[index].add(value) }
        }
        repeat(10) { assertEquals(digitsRange.toSet(), newGeneratedDigits[it])}

        // extended range
        generator = FullNumberGenerator(fullNumberRepeats = 2..4)
        val mockRandomValues = listOf(2, 4, 3, 2)
        mockkStatic(IntRange::seededRandom) {
            with(mockk<IntRange>()) {
                every { IntRange(2, 4).seededRandom() } returnsMany mockRandomValues

                // 2
                var generated = generator.generateNumber()
                assertEquals(generated, generator.generateNumber())

                // 4
                generated = generator.generateNumber()
                assertEquals(generated, generator.generateNumber())

                // interrupt
                generator.reset()

                // generate again
                var prevGenerated = generated
                generated = generator.generateNumber()
                assertNotEquals(prevGenerated, generated)
                assertEquals(generated, generator.generateNumber())
                assertEquals(generated, generator.generateNumber())

                // move to new number
                prevGenerated = generated
                generated = generator.generateNumber()
                assertNotEquals(prevGenerated, generated)
                assertEquals(generated, generator.generateNumber())
            }
        }
    }

    private fun testDefaultBehaviour(generator: FullNumberGenerator, repetitions: Int = 3) {
        var previousGenerated: Set<IntList> = emptySet()
        repeat(repetitions) {
            // generate numbers
            val generatedNumbers: MutableSet<IntList> = mutableSetOf()
            repeat(10) { generatedNumbers.add(generator.generateNumber()) }

            // ensure that numbers were not all duplicates
            generatedNumbers.forEach { number ->
                assertFalse { number.all { it == number[0] } }
            }
            assertEquals(10, generatedNumbers.size)

            // validate selected digits
            var containsAllCount = 0
            repeat(10) { index ->
                val generatedAtDigit = generatedNumbers.map { it[index] }.toSet()
                assertTrue(digitsRange.toSet().containsAll(generatedAtDigit))
                if (generatedAtDigit == digitsRange.toSet()) {
                    containsAllCount++
                }
            }
            assertNotEquals(10, containsAllCount)

            // ensure the generator isn't cycling through the same combinations
            if (previousGenerated.isNotEmpty()) {
                assertNotEquals(previousGenerated, generatedNumbers)
            }
            previousGenerated = generatedNumbers
        }
    }

    // these params should probably be renamed
    private fun testMockedRange(range: IntRange, mockRandomValues: IntList, initRange: IntRange? = null) {
        val previousGenerated: MutableSet<IntList> = mutableSetOf()
        val generator = FullNumberGenerator(fullNumberRepeats = initRange ?: range)

        with(mockk<IntRange>()) {
            every { IntRange(range.first, range.last).seededRandom() } returnsMany mockRandomValues

            mockRandomValues.forEach {
                val generated = generator.generateNumber()
                repeat(it - 1) {
                    assertEquals(generated, generator.generateNumber())
                }
                assertFalse(generated in previousGenerated)
                previousGenerated.add(generated)
            }
        }
    }
}
