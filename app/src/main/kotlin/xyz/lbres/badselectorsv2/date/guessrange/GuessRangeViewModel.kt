package xyz.lbres.badselectorsv2.date.guessrange

import xyz.lbres.badselectorsv2.date.BaseDateViewModel
import xyz.lbres.badselectorsv2.date.guessrange.rangesearcher.BinaryRangeSearcher
import xyz.lbres.badselectorsv2.date.guessrange.rangesearcher.RangeSearcher
import xyz.lbres.badselectorsv2.date.utils.DAY
import xyz.lbres.badselectorsv2.date.utils.MONTH
import xyz.lbres.badselectorsv2.date.utils.YEAR
import xyz.lbres.badselectorsv2.date.utils.daysPerMonth
import xyz.lbres.badselectorsv2.date.utils.monthRange
import java.time.LocalDate

/**
 * ViewModel containing values that are specific to the date range-based selector
 */
class GuessRangeViewModel : BaseDateViewModel() {
    public override var day: Int? = super.day
    public override var month: Int? = super.month
    public override var year: Int? = super.year

    var dateComponent: String? = MONTH
        private set

    lateinit var rangeSearcher: RangeSearcher
        private set

    /**
     * Settings
     */
    var isEfficient: Boolean = false
    var isEpoch: Boolean = false

    /**
     * Values related to the available years
     */
    private val minYear = 1000
    private val maxYear = LocalDate.now().year
    private var yearsRange: IntRange = 1900..maxYear
    val startYear: Int
        get() = yearsRange.first

    /**
     * Initialize available years
     */
    init {
        createNewRangeSearcher()
    }

    /**
     * Move to next section of date
     */
    fun advanceDateComponent() {
        val newComponent = when (dateComponent) {
            MONTH -> DAY
            DAY -> YEAR
            YEAR -> null
            else -> null
        }

        dateComponent = newComponent
        createNewRangeSearcher()
    }

    /**
     * Create range searcher based on values of efficient and epoch settings
     */
    private fun createNewRangeSearcher() {
        val range = when (dateComponent) {
            MONTH -> monthRange
            DAY -> 1..daysPerMonth[month!! - 1]
            YEAR -> yearsRange
            else -> 0..0
        }

        rangeSearcher = BinaryRangeSearcher(range)
    }

    /**
     * Set month, day, and year from a date
     *
     * @param date [LocalDate]
     */
    fun setDate(date: LocalDate) {
        month = date.monthValue
        day = date.dayOfMonth
        year = date.year
    }

    /**
     * Clear all data except settings values, which should persist until manually changed
     */
    override fun resetData() {
        super.resetData()
        dateComponent = MONTH
        createNewRangeSearcher()
    }
}
