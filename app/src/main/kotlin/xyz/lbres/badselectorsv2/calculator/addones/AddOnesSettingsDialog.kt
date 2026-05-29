package xyz.lbres.badselectorsv2.calculator.addones

import androidx.lifecycle.ViewModelProvider
import xyz.lbres.badselectorsv2.abstracts.SettingsDialog
import xyz.lbres.badselectorsv2.databinding.DialogCalcAddOnesSettingsBinding
import xyz.lbres.badselectorsv2.ext.view.gone
import xyz.lbres.kotlinutils.collection.ext.countNotNull

/**
 * Dialog to update settings for the [AddOnesFragment]
 */
class AddOnesSettingsDialog : SettingsDialog<DialogCalcAddOnesSettingsBinding>() {
    private lateinit var viewModel: AddOnesViewModel

    /**
     * Inflate layout and return view binding
     */
    override fun inflateLayout() = DialogCalcAddOnesSettingsBinding.inflate(layoutInflater)

    /**
     * Update UI to show initial settings
     */
    override fun setInitialUi() {
        viewModel = ViewModelProvider(requireActivity())[AddOnesViewModel::class.java]

        val sliderRange: IntRange = binding.numSavedValuesSeekbar.min..binding.numSavedValuesSeekbar.max
        if (viewModel.maxSavedValues in sliderRange) {
            binding.numSavedValuesSeekbar.progress = viewModel.maxSavedValues
        }

        // binding.numSavedValuesSeekbar.setOnSeekBarChangeListener(seekbarChangeListener)
        binding.cannotDisableText.gone()
    }

    /**
     * Save changes to settings
     */
    override fun saveUpdatedSettings() {
        val seekbarPosition = binding.numSavedValuesSeekbar.progress
        val savedCount = viewModel.savedValues.countNotNull()
        if (savedCount <= seekbarPosition) {
            // viewModel.maxSavedValues = savedCount
        }
    }

    companion object {
        const val TAG = "AddOnesSettingsDialog"
        const val CLOSED_REQUEST_KEY = "${TAG}_ClosedEvent"
    }
}
