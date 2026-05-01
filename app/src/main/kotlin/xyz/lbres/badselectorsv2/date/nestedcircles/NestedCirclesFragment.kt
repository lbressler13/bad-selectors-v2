package xyz.lbres.badselectorsv2.date.nestedcircles

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.core.view.children
import androidx.core.view.forEachIndexed
import androidx.lifecycle.ViewModelProvider
import xyz.lbres.badselectorsv2.databinding.ComponentDateNumbersBinding
import xyz.lbres.badselectorsv2.databinding.FragmentNestedCirclesBinding
import xyz.lbres.badselectorsv2.date.BaseDateFragment
import xyz.lbres.badselectorsv2.date.BaseDateViewModel
import xyz.lbres.badselectorsv2.ext.view.disable
import xyz.lbres.badselectorsv2.ext.view.enable
import xyz.lbres.badselectorsv2.ext.view.fullOpacity
import xyz.lbres.badselectorsv2.ext.view.halfOpacity
import xyz.lbres.badselectorsv2.ext.viewgroup.setChildOnClickListener
import xyz.lbres.kotlinutils.list.IntList

/**
 * Fragment that displays 3 concentric, unlabelled circles of buttons, corresponding to year, day, and month.
 * The range of available years to select can be changed via buttons on screen.
 * A setting can also be enabled to shuffle the order of numbers in each circle.
 */
class NestedCirclesFragment : BaseDateFragment() {
    private lateinit var viewModel: NestedCirclesViewModel
    override val dateViewModel: BaseDateViewModel
        get() = viewModel

    private lateinit var binding: FragmentNestedCirclesBinding
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
        viewModel = ViewModelProvider(requireActivity())[NestedCirclesViewModel::class.java]
        binding = FragmentNestedCirclesBinding.inflate(inflater, container, false)

        // initialize UI
        populateCircleLayouts()
        initPlusMinusButtons()
        displayDate()

        return binding.root
    }

    /**
     * Initialize onClicks and UI for plus/minus buttons
     */
    private fun initPlusMinusButtons() {
        // buttons that are used to adjust which years are available
        binding.previousYearsButton.setOnClickListener {
            viewModel.enabler.decrementAvailableYears()
            updatePlusMinusButtons()
        }
        binding.nextYearsButton.setOnClickListener {
            viewModel.enabler.incrementAvailableYears()
            updatePlusMinusButtons()
        }

        updatePlusMinusButtons()
    }

    /**
     * Update enabled/disabled state of buttons to increment and decrement years, depending on the available years
     */
    private fun updatePlusMinusButtons() {
        if (viewModel.enabler.availableYears.first <= viewModel.enabler.minYear) {
            disableYearControlButton(binding.previousYearsButton)
        } else {
            enableYearControlButton(binding.previousYearsButton)
        }

        if (viewModel.enabler.maxYear <= viewModel.enabler.availableYears.last) {
            disableYearControlButton(binding.nextYearsButton)
        } else {
            enableYearControlButton(binding.nextYearsButton)
        }
    }

    /**
     * Populate circle layouts for month, day, and year
     */
    private fun populateCircleLayouts() {
        binding.monthsLayout.setChildOnClickListener { _, index ->
            viewModel.enabler.month = index
            displayMonth()
        }

        binding.daysLayout.setChildOnClickListener { _, index ->
            viewModel.enabler.day = index
            displayDay()
        }

        binding.yearsLayout.setChildOnClickListener { _, index ->
            viewModel.enabler.setYearAt(index)
            displayYear()
        }
    }

    /**
     * Display month in UI and disable buttons for invalid days
     */
    override fun displayMonth() {
        super.displayMonth()
        enableSubset(binding.daysLayout, viewModel.enabler.enabledDays)
    }

    /**
     * Display day in UI and disable buttons for invalid months
     */
    override fun displayDay() {
        super.displayDay()
        enableSubset(binding.monthsLayout, viewModel.enabler.enabledMonths)
    }

    /**
     * Enable and update UI For a button related to incrementing/decrementing years
     *
     * @param button [View]: view to update
     */
    private fun enableYearControlButton(button: View) {
        button.enable()
        button.fullOpacity()
    }

    /**
     * Disable and update UI For a button related to incrementing/decrementing years
     *
     * @param button [View]: view to update
     */
    private fun disableYearControlButton(button: View) {
        button.disable()
        button.halfOpacity()
    }

    /**
     * Enable a subset of children in a layout, and disable all others
     *
     * @param layout [ViewGroup]
     * @param enabledIndices [IntList]: list of indices of children that should be enabled
     */
    private fun enableSubset(layout: ViewGroup, enabledIndices: IntList) {
        layout.children.forEachIndexed { index, view ->
            view.isEnabled = index in enabledIndices
        }
    }
}
