package xyz.lbres.badselectorsv2.calculator.addones

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.view.isVisible
import androidx.lifecycle.ViewModelProvider
import xyz.lbres.badselectorsv2.calculator.BaseCalculatorFragment
import xyz.lbres.badselectorsv2.calculator.BaseCalculatorViewModel
import xyz.lbres.badselectorsv2.calculator.utils.runComputation
import xyz.lbres.badselectorsv2.databinding.ComponentCalcSavedValueBinding
import xyz.lbres.badselectorsv2.databinding.FragmentCalcAddOnesBinding
import xyz.lbres.badselectorsv2.ext.view.disable
import xyz.lbres.badselectorsv2.ext.view.enable
import xyz.lbres.badselectorsv2.ext.view.fullOpacity
import xyz.lbres.badselectorsv2.ext.view.gone
import xyz.lbres.badselectorsv2.ext.view.halfOpacity
import xyz.lbres.badselectorsv2.ext.view.visible
import xyz.lbres.badselectorsv2.utils.getColorOnPrimary
import xyz.lbres.badselectorsv2.utils.getDisabledForeground
import xyz.lbres.badselectorsv2.utils.setImageButtonTint
import xyz.lbres.kotlinutils.collection.ext.countNotNull

/**
 * Fragment with calculator that contains buttons for adding, subtracting, and one, as well as several saved values.
 * At most 4 computed values can be stored at a time, and each value can be present at most once in the compute text.
 */
class AddOnesFragment : BaseCalculatorFragment() {
    private lateinit var viewModel: AddOnesViewModel
    override val calculatorViewModel: BaseCalculatorViewModel
        get() = viewModel

    private lateinit var binding: FragmentCalcAddOnesBinding
    override val rootView: View
        get() = binding.root

    private lateinit var savedValueViews: List<ComponentCalcSavedValueBinding>

    override var computeSeparator: String = " "

    /**
     * Initialize fragment
     */
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        viewModel = ViewModelProvider(requireActivity())[AddOnesViewModel::class.java]
        binding = FragmentCalcAddOnesBinding.inflate(layoutInflater, container, false)
        savedValueViews = listOf(
            binding.savedValueText1,
            binding.savedValueText2,
            binding.savedValueText3,
            binding.savedValueText4,
        )

        // init UI
        initKeypad()
        initMainText()
        initSavedViews()
        initSettingsDialog()

        if (viewModel.calcData.computedValue != null) {
            showComputedUi()
        } else if (viewModel.calcData.error != null) {
            showErrorUi()
        }

        return binding.root
    }

    /**
     * Handle backspace being pressed and update saved values
     */
    override fun handleBackspace() {
        super.handleBackspace()
        updateSavedViews()
    }

    /**
     * Set UI to a state for adding values to compute text
     */
    override fun resetUi() {
        super.resetUi()

        updateSavedViews()
        binding.saveButton.gone()
        binding.equalsButton.visible()
    }

    /**
     * Update UI to display the computed value and show save button
     */
    private fun showComputedUi() {
        disableAllButClear() // doesn't disable save
        updateSavedViews()

        binding.equalsButton.gone()
        binding.saveButton.visible()
    }

    /**
     * Enable/disable save button and update UI based on ability to save
     */
    private fun updateSaveButton() {
        val saveButton = binding.saveButton

        val buttonColor = if (canSaveValue()) {
            getColorOnPrimary(requireContext())
        } else {
            getDisabledForeground(requireContext())
        }
        setImageButtonTint(saveButton, buttonColor)
        saveButton.isEnabled = canSaveValue()
    }

    /**
     * Show/hide saved values, and enable or disable various components based on max saved values, usage, and current result state
     */
    private fun updateSavedViews() {
        val maxSavedValues = viewModel.maxSavedValues

        savedValueViews.forEachIndexed { position, view ->
            view.root.visibility = when {
                maxSavedValues == 0 && position == 0 -> View.INVISIBLE
                maxSavedValues <= position -> View.GONE
                else -> View.VISIBLE
            }
            if (view.root.isVisible) {
                updateSingleSavedView(position)
            }
        }

        updateSaveButton()
    }

    /**
     * Enable/disable the textview and closed button of a single saved value view depending on usage and current result state.
     *
     * @param position [Int]: position of view to update
     */
    private fun updateSingleSavedView(position: Int) {
        val (value, inUse) = viewModel.getSavedValueMetadata(position)
        val computationComplete = viewModel.calcData.computedValue != null || viewModel.calcData.error != null

        val textview: TextView = savedValueViews[position].valueText
        val deleteButton: View = savedValueViews[position].deleteButton

        textview.text = value?.toString() ?: ""
        if (value != null && !inUse && !computationComplete) {
            textview.enable()
            textview.fullOpacity()
        } else {
            textview.disable()
            textview.halfOpacity()
        }

        deleteButton.isEnabled = value != null && (!inUse || computationComplete)
    }

    /**
     * Update UI when error is displayed
     */
    override fun showErrorUi() {
        super.showErrorUi()
        updateSavedViews()
    }

    /**
     * Handle equals button being pressed
     */
    override fun handleEquals() {
        try {
            val computedValue = runComputation(viewModel.calcData.computeText, buildMultiDigit = false)
            viewModel.setResult(computedValue, null)
            showComputedUi()
        } catch (e: Exception) {
            val errorMessage = e.message ?: "Error"
            viewModel.setResult(null, errorMessage)
        }
    }

    /**
     * If there is currently space to save a new value
     *
     * @return [Boolean]: true if the number of saved values is less than the max allowed number, false otherwise
     */
    private fun canSaveValue(): Boolean {
        val currentUsed = viewModel.savedValues.countNotNull()
        return viewModel.calcData.computedValue != null && currentUsed < viewModel.maxSavedValues
    }

    /**
     * Init onClick functions related to saved values and save button, and set initial UI
     */
    private fun initSavedViews() {
        savedValueViews.forEachIndexed { index, view ->
            // add saved number to main text
            view.valueText.setOnClickListener {
                viewModel.appendSavedValueAtIndex(index)
                updateSavedViews()
                updateMainText()
            }

            // delete saved number
            view.deleteButton.setOnClickListener {
                viewModel.clearSavedValueAtIndex(index)
                updateSavedViews()
            }
        }

        // onClick for save button
        binding.saveButton.setOnClickListener {
            if (canSaveValue()) {
                viewModel.saveComputedValue()
                viewModel.resetComputeData()
                updateMainText()
                updateSavedViews()
                resetUi()
            }
        }

        // display initial values
        updateSavedViews()
    }

    /**
     * Initialize dialog to update settings
     */
    private fun initSettingsDialog() {
        binding.settingsButton.root.setOnClickListener {
            val settingsDialog = AddOnesSettingsDialog()
            settingsDialog.show(childFragmentManager, AddOnesSettingsDialog.TAG)
        }

        val requestKey = AddOnesSettingsDialog.CLOSED_REQUEST_KEY
        childFragmentManager.setFragmentResultListener(requestKey, this) { _, _ ->
            updateSavedViews()
        }
    }
}
