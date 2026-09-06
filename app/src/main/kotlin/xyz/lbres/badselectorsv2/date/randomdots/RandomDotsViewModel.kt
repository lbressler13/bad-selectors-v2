package xyz.lbres.badselectorsv2.date.randomdots

import android.util.Log
import xyz.lbres.badselectorsv2.date.BaseDateViewModel
import xyz.lbres.badselectorsv2.date.utils.DateComponent
import xyz.lbres.badselectorsv2.date.utils.daysPerMonth
import xyz.lbres.badselectorsv2.date.utils.maxMonth
import java.time.LocalDate

/**
 * ViewModel containing values that are specific to the random dots date selector
 */
class RandomDotsViewModel : BaseDateViewModel() {
    private val maxDots = 100
    private val initialNumDots = maxMonth

    /**
     * Most recently clicked number
     */
    var selectedNumber: Int? = null

    /**
     * Current date component being selected, or null if full date is complete
     */
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
    private val _visibleIndices: MutableSet<Int> = (0 until maxMonth).toMutableSet()
    val visibleIndices: Set<Int>
        get() = _visibleIndices

    init {
        updateNumDots(initialNumDots)
    }

    /**
     * Update dot data when number of dots changes
     */
    private fun updateNumDots(newValue: Int) {
        numDots = newValue
        _visibleIndices.clear()
        _visibleIndices.addAll(0 until numDots)
    }

    /**
     * Get the position of a dot
     *
     * @param index [Int]: index of dot
     * @return [Pair]<Int, Int>?: position of dot, or null if index is invalid
     */
    fun getDotPosition(index: Int): Pair<Int, Int>? {
        return if (index in dotPositions.indices) {
            dotPositions[index]
        } else {
            Log.w(null, "Dot index $index is out of bounds, unable to get dot position")
            null
        }
    }

    /**
     * Hide a dot
     *
     * @param index [Int]: index of dot to hide
     */
    fun hideDot(index: Int) {
        _visibleIndices.remove(index)
    }

    /**
     * Update the position of a dot
     *
     * @param index [Int]: index of dot to update
     * @param x [Int]: new x position
     * @param y [Int]: new y position
     */
    fun updateDotPosition(index: Int, x: Int, y: Int) {
        if (index in dotPositions.indices) {
            dotPositions[index] = Pair(x, y)
        } else {
            Log.w(null, "Dot index $index is out of bounds, not updating dot position")
        }
    }

    /**
     * Assign the [selectedNumber] to the current date component
     */
    fun useSelectedNumber() {
        if (selectedNumber != null) {
            val number = selectedNumber!!

            when (dateComponent) {
                DateComponent.MONTH -> month = number + 1
                DateComponent.DAY -> day = number + 1
                DateComponent.FIRST_HALF_YEAR -> firstHalfYear = number
                DateComponent.SECOND_HALF_YEAR -> {
                    secondHalfYear = number
                    year = firstHalfYear!! * 100 + secondHalfYear!!
                }
                else -> {}
            }

            incrementDateComponent()
            selectedNumber = null
        } else {
            Log.w(null, "Unable to use selected number, selected number is null")
        }
    }

    /**
     * Increment the date component, update the number of dots, and reset the selected number
     */
    private fun incrementDateComponent() {
        dateComponent = when (dateComponent?.next()) {
            DateComponent.YEAR -> DateComponent.FIRST_HALF_YEAR
            else -> dateComponent?.next()
        }

        val newNumDots = when (dateComponent) {
            DateComponent.DAY -> daysPerMonth[month!! - 1]
            DateComponent.FIRST_HALF_YEAR -> LocalDate.now().year / 100 + 1 // 00-20
            DateComponent.SECOND_HALF_YEAR -> getSecondHalfYears()
            null -> 0
            else -> initialNumDots
        }
        updateNumDots(newNumDots)
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

    /**
     * Make all dots for the current component visible
     */
    fun resetDots() {
        _visibleIndices.addAll(0 until numDots)
    }

    /**
     * Reset all data
     */
    override fun resetData() {
        super.resetData()
        selectedNumber = null
        // month, day, and year reset in parent class
        firstHalfYear = null
        secondHalfYear = null

        dateComponent = DateComponent.MONTH
        updateNumDots(initialNumDots)
    }
}
