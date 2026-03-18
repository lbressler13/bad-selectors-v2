package xyz.lbres.badselectorsv2.date.nestedcircles

import xyz.lbres.badselectorsv2.date.BaseDateViewModel
import xyz.lbres.badselectorsv2.date.utils.daysPerMonth
import xyz.lbres.badselectorsv2.date.utils.maxDay
import xyz.lbres.badselectorsv2.date.utils.maxMonth
import xyz.lbres.badselectorsv2.date.utils.dayRange
import xyz.lbres.badselectorsv2.date.utils.monthRange
import xyz.lbres.kotlinutils.closedrange.intrange.ext.get
import xyz.lbres.kotlinutils.list.IntList
import java.time.LocalDate

/**
 * ViewModel containing values that are specific to the date circle selector
 */
class NestedCirclesViewModel : BaseDateViewModel() {
    val numYearsToDisplay: Int = 60
    val minYear: Int = 0
    val maxYear: Int = LocalDate.now().year // current year

    // all indices are initially valid
    private val defaultValidDayIndices: IntList = (0 until maxDay).toList()
    private val defaultValidMonthIndices: IntList = (0 until maxMonth).toList()

    /**
     * Range of years that can be selected, has size [numYearsToDisplay]
     */
    var availableYears: IntRange
        private set

    /**
     * Days that are valid for the current month
     */
    var validDayIndices: IntList = defaultValidDayIndices
        private set

    /**
     * Months that are valid for the current day
     */
    var validMonthIndices: IntList = defaultValidMonthIndices
        private set

    /**
     * Initialize available years
     */
    init {
        val startYear = maxYear - numYearsToDisplay + 1
        availableYears = startYear..maxYear

        updateDaysForMonth()
        updateMonthsForDay()
    }

    /**
     * Update which days are valid based on the month.
     * i.e. Days 30 and 31 are not valid for February
     */
    private fun updateDaysForMonth() {
        validDayIndices = if (month == null) {
            defaultValidDayIndices
        } else {
            val numDays = daysPerMonth[month!! - 1]
            val indices = (numDays + 1..maxDay).map {
                indexOfDay(it)
            }
            defaultValidDayIndices - indices.toSet()
        }
    }

    /**
     * Update which months are valid based on the day.
     * i.e. February is not valid for day 30
     */
    private fun updateMonthsForDay() {
        validMonthIndices = if (day == null) {
            defaultValidMonthIndices
        } else {
            daysPerMonth.mapIndexed { monthIndex, days ->
                if (day!! <= days) {
                    indexOfMonth(monthIndex + 1)
                } else {
                    null
                }
            }.filterNotNull()
        }
    }

    /**
     * Update the current month, using shuffled value if enabled
     */
    fun setMonthFromIndex(index: Int) {
        month = monthRange.get(index)
        updateDaysForMonth()
    }

    /**
     * Update the current day, using shuffled value if enabled
     */
    fun setDayFromIndex(index: Int) {
        day = dayRange.get(index)
        updateMonthsForDay()
    }

    /**
     * Update the current year by indexing into the currently available years, using shuffled value if enabled
     */
    fun setYearFromIndex(index: Int) {
        val offset = index
        year = availableYears.first + offset
    }

    /**
     * Increment years by the number of years to display, or to max year
     */
    fun incrementAvailableYears() {
        var startYear: Int = availableYears.last + 1
        var endYear: Int = startYear + numYearsToDisplay - 1

        if (endYear >= maxYear) {
            endYear = maxYear
            startYear = maxYear - numYearsToDisplay + 1
        }

        availableYears = startYear..endYear
    }

    /**
     * Decrement years by the number of years to display, or to min year
     */
    fun decrementAvailableYears() {
        var endYear: Int = availableYears.first - 1
        var startYear: Int = endYear - numYearsToDisplay + 1

        if (startYear <= minYear) {
            startYear = minYear
            endYear = minYear + numYearsToDisplay - 1
        }

        availableYears = startYear..endYear
    }

    /**
     * Get the current index of a month in the current range or shuffled list
     *
     * @param month [Int]
     * @return [Int]: index of month
     */
    // TODO probably don't need these wrappers
    private fun indexOfMonth(month: Int): Int {
        return monthRange.indexOf(month)
    }

    /**
     * Get the current index of a day in the current range or shuffled list
     *
     * @param day [Int]
     * @return [Int]: index of day
     */
    private fun indexOfDay(day: Int): Int {
        return dayRange.indexOf(day)
    }
}
