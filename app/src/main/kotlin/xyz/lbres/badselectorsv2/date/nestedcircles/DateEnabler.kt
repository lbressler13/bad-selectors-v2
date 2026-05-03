package xyz.lbres.badselectorsv2.date.nestedcircles

import xyz.lbres.badselectorsv2.date.utils.daysPerMonth
import xyz.lbres.badselectorsv2.date.utils.maxDay
import xyz.lbres.badselectorsv2.date.utils.maxMonth
import xyz.lbres.kotlinutils.booleanarray.booleanArrayOfValue
import xyz.lbres.kotlinutils.closedrange.intrange.ext.get
import xyz.lbres.kotlinutils.general.simpleIf
import xyz.lbres.kotlinutils.list.IntList
import java.time.LocalDate

// TODO leap year
class DateEnabler {
    val numYears: Int = 60
    val minYear: Int = 0
    val maxYear: Int = LocalDate.now().year // current year

    private var _day: Int? = null
    private var _month: Int? = null
    var day: Int?
        get() = _day
        set(value) {
            _day = value
            updateMonthsForDay()
        }
    var month: Int?
        get() = _month
        set(value) {
            _month = value
            updateDaysForMonth()
        }
    var year: Int? = null
        private set

    /**
     * Range of years that can be selected, has size [numYears]
     */
    var availableYears: IntRange
        private set

    private var _enabledDays = booleanArrayOfValue(maxDay, true)
    private var _enabledMonths = booleanArrayOfValue(maxMonth, true)
    private var _enabledYears = booleanArrayOfValue(numYears, true)

    /**
     * Days that are valid for the current month
     */
    var enabledDays: IntList = enabledToList(_enabledDays)
        private set

    /**
     * Months that are valid for the current day
     */
    var enabledMonths: IntList = enabledToList(_enabledMonths)
        private set

    // to be used for leap year
    val enabledYears: IntList = enabledToList(_enabledYears)

    /**
     * Initialize available years
     */
    init {
        val startYear = maxYear - numYears + 1
        availableYears = startYear..maxYear
    }

    /**
     * Update which days are valid based on the month.
     * i.e. Days 30 and 31 are not valid for February
     */
    private fun updateDaysForMonth() {
        val numDays = if (month == null) {
            maxDay
        } else {
            daysPerMonth[month!!]
        }

        _enabledDays.indices.forEach {
            _enabledDays[it] = it < numDays
        }
        enabledDays = enabledToList(_enabledDays)
    }

    /**
     * Update which months are valid based on the day.
     * i.e. February is not valid for day 30
     */
    private fun updateMonthsForDay() {
        daysPerMonth.forEachIndexed { monthIndex, days ->
            _enabledMonths[monthIndex] = day == null || day!! < days
        }
        enabledMonths = enabledToList(_enabledMonths)
    }

    fun setYearAt(index: Int?) {
        year = simpleIf(index == null, { null }, { availableYears.get(index!!) })
    }

    /**
     * Increment years by the number of years to display, or to max year
     */
    fun incrementAvailableYears() {
        var startYear: Int = availableYears.last + 1
        var endYear: Int = startYear + numYears - 1

        if (endYear >= maxYear) {
            endYear = maxYear
            startYear = maxYear - numYears + 1
        }

        availableYears = startYear..endYear
    }

    /**
     * Decrement years by the number of years to display, or to min year
     */
    fun decrementAvailableYears() {
        var endYear: Int = availableYears.first - 1
        var startYear: Int = endYear - numYears + 1

        if (startYear <= minYear) {
            startYear = minYear
            endYear = minYear + numYears - 1
        }

        availableYears = startYear..endYear
    }

    /**
     * Convert boolean array to list of indices where enabled = true
     *
     * @param enabled [BooleanArray]: array indicating which numbers should be included in the list
     * @return [IntList]: list consisting of only indices where the value in [enabled] is true
     */
    private fun enabledToList(enabled: BooleanArray): IntList {
        return enabled
            .mapIndexed { index, value -> simpleIf(value, index, null) }
            .filterNotNull()
    }
}
