package xyz.lbres.badselectorsv2.date.nestedcircles

import io.mockk.every
import io.mockk.mockkStatic
import io.mockk.unmockkAll
import xyz.lbres.badselectorsv2.testutils.runWithFailMessage
import xyz.lbres.kotlinutils.closedrange.intrange.ext.get
import xyz.lbres.kotlinutils.list.IntList
import java.time.LocalDate
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class DateEnablerTest {
    private val mockDate = LocalDate.of(2025, 1, 1)

    @BeforeTest
    fun setupTest() {
        mockkStatic(LocalDate::class)
        every { LocalDate.now() } returns mockDate
    }

    @AfterTest
    fun cleanupTest() {
        unmockkAll()
    }

    @Test
    fun testInit() {
        val enabler = DateEnabler()
        assertNull(enabler.day)
        assertNull(enabler.month)
        assertNull(enabler.year)

        assertEquals(0, enabler.minYear)
        assertEquals(60, enabler.numYears)
        assertEquals(2025, enabler.maxYear)
        assertEquals(1966..2025, enabler.availableYears)

        assertEquals((0 until 31).toList(), enabler.enabledDays)
        assertEquals((0 until 12).toList(), enabler.enabledMonths)
        assertEquals((0 until 60).toList(), enabler.enabledYears)
    }

    @Test
    fun testSetMonth() {
        var enabler = DateEnabler()

        fun disabledDays(month: Int): IntList {
            return when (month) {
                1 -> listOf(29, 30)
                3, 5, 8, 10 -> listOf(30)
                else -> emptyList()
            }
        }

        repeat(12) {
            runWithFailMessage("Error testing month $it") {
                enabler.month = it
                checkEnabledDate(enabler, disabledDays = disabledDays(it))
            }
        }

        // set back to 0
        enabler.month = 0 // jan
        checkEnabledDate(enabler, disabledDays = disabledDays(0))

        // doesn't change other components
        enabler = DateEnabler()
        assertNull(enabler.day)
        assertNull(enabler.year)
        enabler.month = 6
        assertNull(enabler.day)
        assertNull(enabler.year)

        enabler.day = 12
        enabler.setYearAt(0)
        enabler.month = 8
        assertEquals(12, enabler.day)
        assertEquals(1966, enabler.year)
    }

    @Test
    fun testSetDay() {
        var enabler = DateEnabler()

        fun disabledMonths(day: Int): IntList {
            return when (day) {
                29 -> listOf(1) // feb
                30 -> listOf(1, 3, 5, 8, 10) // feb, apr, june, sept, nov
                else -> emptyList()
            }
        }

        repeat(31) {
            runWithFailMessage("Failure testing day $it") {
                enabler.day = it
                checkEnabledDate(enabler, disabledMonths = disabledMonths(it))
            }
        }

        // set back to 0
        enabler.day = 0
        checkEnabledDate(enabler, disabledMonths = disabledMonths(0))

        // doesn't change other components
        enabler = DateEnabler()
        assertNull(enabler.month)
        assertNull(enabler.year)
        enabler.day = 6
        assertNull(enabler.month)
        assertNull(enabler.year)

        enabler.month = 2
        enabler.setYearAt(0)
        enabler.day = 8
        assertEquals(2, enabler.month)
        assertEquals(1966, enabler.year)
    }

    @Test
    fun testSetYear() {
        var enabler = DateEnabler()
        val year = mockDate.year

        fun validateYears(startYear: Int) {
            repeat(60) {
                enabler.setYearAt(it)
                assertEquals(startYear + it, enabler.year)
            }
        }

        fun decrementAndValidate(startYear: Int, decrementCount: Int): Int {
            var updatedStart = startYear
            repeat(decrementCount) {
                enabler.decrementAvailableYears()
                updatedStart -= 60
            }
            validateYears(updatedStart)
            return updatedStart
        }

        fun incrementAndValidate(startYear: Int, incrementCount: Int): Int {
            var updatedStart = startYear
            repeat(incrementCount) {
                enabler.incrementAvailableYears()
                updatedStart += 60
            }
            validateYears(updatedStart)
            return updatedStart
        }

        // initial values
        var startYear = year - 60 + 1
        validateYears(startYear)

        // decremented
        var decrementsToZero = year / 60

        startYear = decrementAndValidate(startYear, 1)
        decrementsToZero--

        startYear = decrementAndValidate(startYear, 3)
        decrementsToZero -= 3

        // decremented to zero
        repeat(decrementsToZero) { enabler.decrementAvailableYears() }
        repeat(60) {
            enabler.setYearAt(it)
            assertEquals(it, enabler.year)
        }

        // incremented
        var incrementsToStart = year / 60

        startYear = incrementAndValidate(0, 1)
        incrementsToStart--

        startYear = incrementAndValidate(startYear, 2)
        incrementsToStart -= 2

        // incremented to start
        repeat(incrementsToStart) { enabler.incrementAvailableYears() }
        startYear = year - 60 + 1
        validateYears(startYear)

        // duplicate
        enabler.setYearAt(3)
        assertEquals(startYear + 3, enabler.year)
        enabler.setYearAt(3)
        assertEquals(startYear + 3, enabler.year)

        // null
        enabler.setYearAt(null)
        assertNull(enabler.year)

        // doesn't change other components
        enabler = DateEnabler()
        assertNull(enabler.month)
        assertNull(enabler.day)
        enabler.setYearAt(6)
        assertNull(enabler.month)
        assertNull(enabler.day)

        enabler.month = 2
        enabler.day = 8
        enabler.setYearAt(58)
        assertEquals(2, enabler.month)
        assertEquals(8, enabler.day)
    }

    @Test
    fun testIncrementYear() {
        var enabler = DateEnabler()
        val year = mockDate.year
        var startYear = year - 60 + 1

        // unabled to increment at start
        enabler.incrementAvailableYears()
        repeat(60) {
            val number = enabler.availableYears.get(it)
            assertEquals(startYear + it, number)
        }

        val iterations = year / 60 - 1
        repeat(iterations + 1) { enabler.decrementAvailableYears() } // go down to 0

        startYear = 0
        repeat(iterations) {
            enabler.incrementAvailableYears()
            startYear += 60
            repeat(60) { yearIdx ->
                val number = enabler.availableYears.get(yearIdx)
                assertEquals(startYear + yearIdx, number)
            }
        }

        // back to start
        println(enabler.availableYears)
        enabler.incrementAvailableYears()
        println(enabler.availableYears)
        repeat(60) {
            val number = enabler.availableYears.get(it)
            assertEquals(year - 60 + 1 + it, number)
        }

        // check month/day enabled
        enabler = DateEnabler()
        enabler.day = 29 // 30
        enabler.month = 3 // april
        checkEnabledDate(enabler, disabledMonths = listOf(1), disabledDays = listOf(30))

        enabler.decrementAvailableYears() // decrement because year already is at maximum
        enabler.decrementAvailableYears()
        enabler.incrementAvailableYears()
        checkEnabledDate(enabler, disabledMonths = listOf(1), disabledDays = listOf(30))
    }

    @Test
    fun testDecrementYear() {
        var enabler = DateEnabler()
        val year = mockDate.year

        val iterations = year / 60 - 1
        var startYear = year - 60 + 1
        repeat(iterations) {
            enabler.decrementAvailableYears()
            startYear -= 60
            repeat(60) { yearIdx ->
                val number = enabler.availableYears.get(yearIdx)
                assertEquals(startYear + yearIdx, number)
            }
        }

        // to zero
        enabler.decrementAvailableYears()
        repeat(60) {
            val number = enabler.availableYears.get(it)
            assertEquals(it, number)
        }

        // past zero
        enabler.decrementAvailableYears()
        repeat(60) {
            val number = enabler.availableYears.get(it)
            assertEquals(it, number)
        }

        // check month/day enabled
        enabler = DateEnabler()
        enabler.day = 29 // 30
        enabler.month = 3 // april
        checkEnabledDate(enabler, disabledMonths = listOf(1), disabledDays = listOf(30))

        enabler.incrementAvailableYears() // increment because year already is at minimum
        enabler.incrementAvailableYears()
        enabler.decrementAvailableYears()
        checkEnabledDate(enabler, disabledMonths = listOf(1), disabledDays = listOf(30))
    }

    private fun checkEnabledDate(
        enabler: DateEnabler,
        disabledMonths: IntList = emptyList(),
        disabledDays: IntList = emptyList(),
        disabledYears: IntList = emptyList(),
    ) {
        checkEnabledMonths(enabler, disabledMonths)
        checkEnabledDays(enabler, disabledDays)
        checkEnabledYears(enabler, disabledYears)
    }

    private fun checkEnabledMonths(enabler: DateEnabler, disabledList: IntList = emptyList()) {
        repeat(12) {
            runWithFailMessage("Failed checking month $it with list $disabledList") {
                assertEquals(it !in disabledList, it in enabler.enabledMonths)
            }
        }
    }

    private fun checkEnabledDays(enabler: DateEnabler, disabledList: IntList = emptyList()) {
        repeat(31) {
            runWithFailMessage("Failed checking day $it with list $disabledList") {
                assertEquals(it !in disabledList, it in enabler.enabledDays)
            }
        }
    }

    private fun checkEnabledYears(enabler: DateEnabler, disabledList: IntList = emptyList()) {
        repeat(60) {
            runWithFailMessage("Failed checking year $it with list $disabledList") {
                assertEquals(it !in disabledList, it in enabler.enabledYears)
            }
        }
    }
}
