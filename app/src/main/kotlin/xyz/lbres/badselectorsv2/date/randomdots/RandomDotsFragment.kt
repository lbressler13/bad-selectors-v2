package xyz.lbres.badselectorsv2.date.randomdots

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.children
import androidx.lifecycle.ViewModelProvider
import xyz.lbres.badselectorsv2.R
import xyz.lbres.badselectorsv2.databinding.ComponentDateNumbersBinding
import xyz.lbres.badselectorsv2.databinding.FragmentDateRandomDotsBinding
import xyz.lbres.badselectorsv2.date.BaseDateFragment
import xyz.lbres.badselectorsv2.date.BaseDateViewModel
import xyz.lbres.badselectorsv2.date.utils.DateComponent
import xyz.lbres.badselectorsv2.ext.view.gone
import xyz.lbres.badselectorsv2.ext.view.visible
import xyz.lbres.badselectorsv2.ext.view.visibleIf
import xyz.lbres.badselectorsv2.ext.viewgroup.setChildOnClickListener
import xyz.lbres.customview.movingview.MovingView
import xyz.lbres.kotlinutils.generic.ifNotNull

class RandomDotsFragment : BaseDateFragment() {
    private lateinit var viewModel: RandomDotsViewModel
    override val dateViewModel: BaseDateViewModel
        get() = viewModel

    private lateinit var binding: FragmentDateRandomDotsBinding
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
        viewModel = ViewModelProvider(requireActivity())[RandomDotsViewModel::class.java]
        binding = FragmentDateRandomDotsBinding.inflate(layoutInflater)

        // initialize UI
        initializeDotsLayout()
        initializeSelectedNumber()
        binding.restartButton.root.setOnClickListener { reset() }

        updateUi()

        return binding.root
    }

    private fun updateUi(updateDotPositions: Boolean = true) {
        // restart
        if (viewModel.secondHalfYear != null) {
            binding.restartButton.root.visible()
            binding.mainBody.gone()
        } else {
            // selected number
            val displayNumber = when (viewModel.dateComponent) {
                DateComponent.DAY, DateComponent.MONTH -> viewModel.selectedNumber.ifNotNull { it + 1 }
                null -> null
                else -> viewModel.selectedNumber
            }
            binding.generatedNumber.text = displayNumber?.toString() ?: ""
            binding.clearButton.visibleIf(viewModel.visibleIndices.isEmpty())
            binding.selectNumberMessage.text = if (viewModel.dateComponent == null) {
                ""
            } else {
                getString(R.string.tap_dots_to_select, viewModel.dateComponent!!.label)
            }

            // dots
            if (updateDotPositions) {
                binding.dotsLayout.forceUpdate(forceChildUpdates = true)
            }
            binding.dotsLayout.children.forEachIndexed { index, view ->
                view.visibleIf(index in viewModel.visibleIndices)
            }
        }

        // display date
        displayDate()
        // override year components
        val dateLayout = binding.dateNumbersLayout
        if (viewModel.firstHalfYear != null) {
            addNumberToViews(viewModel.firstHalfYear!!, listOf(dateLayout.year0, dateLayout.year1))
        }
        if (viewModel.secondHalfYear != null) {
            addNumberToViews(viewModel.secondHalfYear!!, listOf(dateLayout.year2, dateLayout.year3))
        }
    }

    private fun reset() {
        viewModel.resetData()
        binding.restartButton.root.gone()
        binding.mainBody.visible()
        updateUi()
    }

    private fun initializeDotsLayout() {
        binding.dotsLayout.setChildOnClickListener { index, view ->
            view as MovingView
            val storedPosition = viewModel.getDotPosition(index)
            if (storedPosition != null) {
                // TODO add force position to motion layout
            }
            view.visibleIf(index in viewModel.visibleIndices)

            view.setOnClickListener {
                viewModel.hideDot(index)
                viewModel.selectedNumber = index
                updateUi()
            }

            view.setOnMoveListener { _, x, y ->
                viewModel.updateDotPosition(index, x, y)
            }
        }

        binding.dotsLayout.setInitialChildPositions()
    }

    private fun initializeSelectedNumber() {
        binding.generatedNumber.setOnClickListener {
            if (viewModel.selectedNumber != null) {
                viewModel.useSelectedNumber()
                updateUi()
            }
        }
        binding.clearButton.setOnClickListener {
            viewModel.resetDots()
            viewModel.selectedNumber = null
            updateUi()
        }
    }
}
