package xyz.lbres.badselectorsv2.phone.utils

import xyz.lbres.badselectorsv2.testutils.withMockedIntRange
import xyz.lbres.kotlinutils.collection.list.IntList
import kotlin.collections.forEach
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNotEquals
import kotlin.test.assertTrue

/**
 * Test that a generator is generating unique numbers and perform extra validation on the generated numbers
 */
fun testBehaviour(generator: PhoneNumberGenerator, validateNumbers: (Set<IntList>) -> Unit) {
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
fun testDefaultBehaviour(generator: PhoneNumberGenerator) {
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
 * Validate number generation given a repeats range
 */
fun testMockedRangeRepeats(range: IntRange, mockReturns: IntList, initialRange: IntRange? = null) {
    withMockedIntRange(randomMocks = mapOf(range to mockReturns)) {
        val previousGenerated: MutableSet<IntList> = mutableSetOf()
        val generator = PhoneNumberGenerator(fullNumberRepeats = initialRange ?: range)
        mockReturns.forEach { testRepeatedNumber(generator, it, previousGenerated) }
        assertEquals(mockReturns.size, previousGenerated.size)
    }
}

/**
 * Validate that a number doesn't change and doesn't match previous numbers
 */
fun testRepeatedNumber(
    generator: PhoneNumberGenerator,
    repetitions: Int,
    previousGenerated: MutableSet<IntList> = mutableSetOf(),
    add: Boolean = true,
): IntList {
    val generated = generator.generateNumber()
    repeat(repetitions - 1) { assertEquals(generated, generator.generateNumber()) }
    assertFalse(generated in previousGenerated)
    if (add) {
        previousGenerated.add(generated)
    }
    return generated
}

/**
 * Validate that all 10 digits were generated at every index
 */
fun assertAllDigitsGenerated(generatedNumbers: Set<IntList>) {
    digitsRange.forEach {
        assertEquals(digitsRange.toSet(), digitsAtIndex(it, generatedNumbers))
    }
}

/**
 * Get all values generated at the given index
 */
fun digitsAtIndex(index: Int, numbers: Set<IntList>) = numbers.map { it[index] }.toSet()
