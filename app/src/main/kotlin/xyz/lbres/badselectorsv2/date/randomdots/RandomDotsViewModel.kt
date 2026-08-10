package xyz.lbres.badselectorsv2.date.randomdots

import android.util.Log
import xyz.lbres.badselectorsv2.date.BaseDateViewModel
import xyz.lbres.badselectorsv2.date.utils.DateComponent
import xyz.lbres.badselectorsv2.date.utils.daysPerMonth
import xyz.lbres.badselectorsv2.date.utils.maxMonth
import java.time.LocalDate

class RandomDotsViewModel : BaseDateViewModel() {
    private val maxDots = 100
    private val initialNumDots = maxMonth

    var selectedNumber: Int? = null

    var dateComponent: DateComponent? = DateComponent.MONTH
        private set

    /**
     * First two digits of the year
     */
    var firstHalfYear: Int? = null
        private set

    /**
     * Second two digits of the year
     */
    var secondHalfYear: Int? = null
        private set

    private var numDots: Int = initialNumDots

    /**
     * Information about all dots
     */
    private val dotPositions: Array<Pair<Int, Int>?> = Array(maxDots) { null }
    private val _visibleIndices: MutableSet<Int> = (0 until numDots).toMutableSet()
    val visibleIndices: Set<Int>
        get() = _visibleIndices

    init {
        updateNumDots(initialNumDots)
    }

    private fun updateNumDots(newValue: Int) {
        numDots = newValue
    }

    fun getDotPosition(index: Int): Pair<Int, Int>? {
        return if (validIndex(index)) {
            dotPositions[index]
        } else {
            null
        }
    }

    fun showDot(index: Int) {
        _visibleIndices.add(index)
    }

    fun hideDot(index: Int) {
        _visibleIndices.remove(index)
    }

    fun updateDotPosition(index: Int, x: Int, y: Int) {
        if (validIndex(index)) {
            dotPositions[index] = Pair(x, y)
        } else {
            Log.w(null, "Dot index $index is out of bounds, not updating dot position")
        }
    }

    /**
     * Assign the [selectedNumber] to the current date component
     */
    fun useSelectedNumber() {
        val number = selectedNumber!!

        when {
            month == null -> month = number + 1
            day == null -> day = number + 1
            firstHalfYear == null -> firstHalfYear = number
            secondHalfYear == null -> {
                secondHalfYear = number
                year = firstHalfYear!! * 100 + secondHalfYear!!
            }
        }

        incrementDateComponent()
        selectedNumber = null
    }

    /**
     * Increment the date component, update the number of dots, and reset the selected number
     */
    private fun incrementDateComponent() {
        when (dateComponent) {
            DateComponent.MONTH -> {
                dateComponent = DateComponent.DAY
                updateNumDots(daysPerMonth[month!! - 1])
            }
            DateComponent.DAY -> {
                dateComponent = DateComponent.FIRST_HALF_YEAR
                updateNumDots(LocalDate.now().year / 100 + 1)
            }
            DateComponent.FIRST_HALF_YEAR -> {
                dateComponent = DateComponent.SECOND_HALF_YEAR
                updateNumDots(getSecondHalfYears())
            }
            else -> {
                dateComponent = null
                updateNumDots(initialNumDots)
            }
        }
    }

    /**
     * Get the number of years available for the second half of the year, based on selection for the first half.
     * Will be 100 for anything before the current century.
     */
    private fun getSecondHalfYears(): Int {
        val currentYear = LocalDate.now().year

        return if (currentYear / 100 == firstHalfYear) {
            currentYear % 100 + 1
        } else {
            100
        }
    }

    private fun validIndex(index: Int) = 0 <= index && index <= dotPositions.lastIndex

    fun resetDots() {
        _visibleIndices.addAll(0..numDots)
    }

    /**
     * Reset all data and move dots
     */
    override fun resetData() {
        super.resetData()
        // TODO reset positions/hidden
        selectedNumber = null
        firstHalfYear = null
        secondHalfYear = null

        dateComponent = DateComponent.MONTH
        numDots = initialNumDots
    }
}
