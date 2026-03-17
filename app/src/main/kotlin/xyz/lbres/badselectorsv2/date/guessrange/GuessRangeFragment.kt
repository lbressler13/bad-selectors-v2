package xyz.lbres.badselectors.date.guessrange

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import xyz.lbres.badselectorsv2.R
import xyz.lbres.badselectorsv2.databinding.ComponentDateNumbersBinding
import xyz.lbres.badselectorsv2.databinding.FragmentGuessRangeBinding
import xyz.lbres.badselectorsv2.date.BaseDateFragment
import xyz.lbres.badselectorsv2.date.BaseDateViewModel
import xyz.lbres.badselectorsv2.date.guessrange.GuessRangeViewModel
import xyz.lbres.badselectorsv2.date.utils.DAY
import xyz.lbres.badselectorsv2.date.utils.MONTH
import xyz.lbres.badselectorsv2.date.utils.YEAR
import xyz.lbres.badselectorsv2.ext.textview.textToInt
import xyz.lbres.badselectorsv2.ext.view.gone
import xyz.lbres.badselectorsv2.ext.view.visible
import xyz.lbres.kotlinutils.closedrange.ext.isSingleValue
import java.time.LocalDate

/**
 * Fragment that selects date by presenting a series of ranges for each section of the date.
 * Incorrect values are eliminated based on user input.
 * All range computation is handled by a [RangeSearcher].
 */
class GuessRangeFragment : BaseDateFragment() {
    private lateinit var viewModel: GuessRangeViewModel
    override val dateViewModel: BaseDateViewModel
        get() = viewModel

    private lateinit var binding: FragmentGuessRangeBinding
    override val dateNumbersLayout: ComponentDateNumbersBinding
        get() = binding.dateNumbersLayout

    /**
     * Initialize fragment
     */
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        viewModel = ViewModelProvider(requireActivity())[GuessRangeViewModel::class.java]
        binding = FragmentGuessRangeBinding.inflate(inflater, container, false)

        // initialize UI
        handleDateComponent()
        displayDate()
        binding.restartButton.root.setOnClickListener { reset() }

        return binding.root
    }

    /**
     * Run search and modify UI for the current date component
     */
    private fun handleDateComponent() {
        val component = viewModel.dateComponent

        val formattedRangeQuestion = getString(R.string.in_range_question, component)
        binding.rangeQuestionText.text = formattedRangeQuestion

        val formattedSingleValQuestion = getString(R.string.correct_value_question, component)
        binding.singleValueQuestionText.text = formattedSingleValQuestion

        // scale down text for full dates b/c they are big
        val valueTextSize: Float = 70f // TODO move to layout
        binding.rangeStartText.textSize = valueTextSize
        binding.rangeEndText.textSize = valueTextSize

        when (component) {
            MONTH -> runMonthSearch()
            DAY -> runDaySearch()
            YEAR -> runYearSearch()
            null -> showRestartUi()
        }
    }

    /**
     * Reset data and UI to initial state
     */
    private fun reset() {
        viewModel.resetData()

        binding.restartButton.root.gone()
        setBlankRangeUi()
        displayDate()
        handleDateComponent()
        binding.mainBody.visible()
    }

    /**
     * Update UI to display range
     *
     * @param startValue [Int]: start of range
     * @param endValue [Int]: end of range
     */
    private fun updateRangeUi(startValue: Int, endValue: Int) {
        binding.rangeStartText.text = valueToString(startValue)
        binding.rangeEndText.text = valueToString(endValue)
    }

    /**
     * Setup UI to ask if a single value is correct
     *
     * @param value [Int]: value to ask about
     * @param onSuccess () -> [Unit]: function to call if value is correct
     * @param onFailure () -> [Unit]: function to call if value is incorrect
     */
    private fun createSingleValueUi(value: Int, onSuccess: () -> Unit, onFailure: () -> Unit) {
        binding.dateRangeLayout.gone()
        binding.singleValueLayout.visible()

        binding.yesButton.setOnClickListener {
            setBlankRangeUi() // reset from single value to range UI
            onSuccess()
            viewModel.advanceDateComponent()
            handleDateComponent()
        }
        binding.noButton.setOnClickListener {
            setBlankRangeUi() // reset from single value to range UI
            onFailure()
        }

        binding.singleValueText.text = valueToString(value)
    }

    /**
     * Setup blank range UI
     */
    private fun setBlankRangeUi() {
        binding.singleValueLayout.gone()
        binding.dateRangeLayout.visible()

        binding.rangeStartText.text = ""
        binding.rangeEndText.text = ""
    }

    /**
     * UI changes when showing restart button
     */
    private fun showRestartUi() {
        binding.mainBody.gone()
        binding.restartButton.root.visible()
    }

    /**
     * Search for month
     */
    private fun runMonthSearch() {
        val onSuccess: () -> Unit = {
            viewModel.month = binding.singleValueText.textToInt()
            displayMonth()
        }

        runGenericSearch(onSuccess)
    }

    /**
     * Search for day
     */
    private fun runDaySearch() {
        val onSuccess: () -> Unit = {
            viewModel.day = binding.singleValueText.textToInt()
            displayDay()
        }

        runGenericSearch(onSuccess)
    }

    /**
     * Search for year
     */
    private fun runYearSearch() {
        val onSuccess: () -> Unit = {
            viewModel.year = binding.singleValueText.textToInt()
            displayYear()
        }

        runGenericSearch(onSuccess)
    }

    /**
     * Set actions of yes and no buttons, and make calls to update range
     *
     * @param onSuccess () -> [Unit]: function to call if single value is found successfully
     */
    private fun runGenericSearch(onSuccess: () -> Unit) {
        val onFailure: () -> Unit = {
            viewModel.rangeSearcher.restart()
            runGenericSearch(onSuccess)
        }

        binding.yesButton.setOnClickListener {
            viewModel.rangeSearcher.markLastRangeCorrect()
            updateRange(onSuccess, onFailure)
        }

        binding.noButton.setOnClickListener {
            viewModel.rangeSearcher.markLastRangeIncorrect()
            updateRange(onSuccess, onFailure)
        }

        // get first range
        updateRange(onSuccess, onFailure)
    }

    /**
     * Get next range and update UI
     *
     * @param onSuccess () -> [Unit]: function to call if single value is found successfully
     * @param onFailure () -> [Unit]: function to call if single value is not found successfully
     */
    private fun updateRange(onSuccess: () -> Unit, onFailure: () -> Unit) {
        val searcher = viewModel.rangeSearcher
        val range = searcher.getRange()

        // if searcher has been reduced to one value, set ui for single value
        if (range.isSingleValue() && searcher.getSingleValue() != null) {
            createSingleValueUi(range.first, onSuccess, onFailure)
        } else {
            updateRangeUi(range.first, range.last)
        }
    }

    /**
     * Shift date forward from base epoch date, defined by the first date of the range
     *
     * @param shift [Int]: size of shift
     * @return [LocalDate]: date corresponding to shifted value
     */
    private fun getDateFromEpochShift(shift: Int): LocalDate {
        val startDate: Long = LocalDate.of(viewModel.startYear, 1, 1).toEpochDay()
        val shiftedDate: Long = startDate + shift
        return LocalDate.ofEpochDay(shiftedDate)
    }

    /**
     * Format date as string in M/d/yyyy format.
     * Cannot use DateFormatter class because it doesn't allow year 0.
     *
     * @param date [LocalDate]: value to format
     * @return [String]
     */
    private fun getDateString(date: LocalDate): String {
        val month = date.monthValue.toString()
        val day = date.dayOfMonth.toString()
        val year = date.year.toString().padStart(4, '0')

        return "$month/$day/$year"
    }

    /**
     * Case int to string or date string, depending on epoch setting
     *
     * @param value [Int]: value to cast
     * @return [String]: int as string if not epoch date, or date based on epoch shift
     */
    private fun valueToString(value: Int): String {
        if (!viewModel.isEpoch) {
            return value.toString()
        }

        val date: LocalDate = getDateFromEpochShift(value)
        return getDateString(date)
    }

    /**
     * Parse date from string in M/d/yyyy format
     *
     * @param text [String]: value to parse
     * @return [LocalDate]
     */
    private fun parseDateFromString(text: String): LocalDate {
        val components: List<String> = text.split('/')
        val month = components[0].toInt()
        val day = components[1].toInt()
        val year = components[2].toInt()

        return LocalDate.of(year, month, day)
    }
}
