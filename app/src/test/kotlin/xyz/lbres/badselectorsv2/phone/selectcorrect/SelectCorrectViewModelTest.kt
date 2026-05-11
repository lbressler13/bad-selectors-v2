package xyz.lbres.badselectorsv2.phone.selectcorrect

import io.mockk.EqMatcher
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkConstructor
import io.mockk.mockkStatic
import io.mockk.unmockkAll
import xyz.lbres.badselectorsv2.phone.utils.PhoneNumberGenerator
import xyz.lbres.badselectorsv2.phone.utils.digitsRange
import xyz.lbres.badselectorsv2.phone.utils.numDigits
import xyz.lbres.badselectorsv2.utils.createRandom
import xyz.lbres.badselectorsv2.utils.seededRandom
import xyz.lbres.badselectorsv2.utils.seededShuffled
import xyz.lbres.kotlinutils.list.IntList
import xyz.lbres.kotlinutils.random.ext.nextBoolean
import kotlin.collections.listOf
import kotlin.ranges.contains
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertNull

class SelectCorrectViewModelTest {
    private val shuffledDigits = listOf(7, 4, 0, 2, 5, 6, 8, 1, 3, 9)

    @BeforeTest
    fun setupTest() {
        mockkConstructor(PhoneNumberGenerator::class)
    }

    @AfterTest
    fun cleanupTest() {
        unmockkAll()
    }

    @Test
    fun testInit() {
        val vm = SelectCorrectViewModel()
        digitsRange.forEach { vm.generatedNumber[it] in digitsRange }
    }

    @Test
    fun testGetAndSetDigit() {
    }

    @Test
    fun testUpdateNumber() {

    }

    @Test
    fun testSetDigitAt() {

    }

    // mock generation in number generator
    private fun setupGeneratorMocks(mockValues: List<IntList>) {
        every { constructedWith<PhoneNumberGenerator>().generateNumber(false) } returnsMany mockValues
        every { constructedWith<PhoneNumberGenerator>().reset() } answers { callOriginal() }
    }
}
