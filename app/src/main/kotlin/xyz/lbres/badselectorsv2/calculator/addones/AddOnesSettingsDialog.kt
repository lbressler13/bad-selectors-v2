package xyz.lbres.badselectorsv2.calculator.addones

import android.widget.SeekBar
import androidx.lifecycle.ViewModelProvider
import xyz.lbres.badselectorsv2.abstracts.SettingsDialog
import xyz.lbres.badselectorsv2.databinding.DialogCalcAddOnesSettingsBinding
import xyz.lbres.badselectorsv2.ext.view.gone
import xyz.lbres.badselectorsv2.ext.view.visible
import xyz.lbres.kotlinutils.collection.ext.countNotNull

/**
 * Dialog to update settings for the [AddOnesFragment]
 */
class AddOnesSettingsDialog : SettingsDialog<DialogCalcAddOnesSettingsBinding>() {
    private lateinit var viewModel: AddOnesViewModel

    override val dialogClosedRequestKey: String? = CLOSED_REQUEST_KEY

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

        binding.numSavedValuesSeekbar.setOnSeekBarChangeListener(seekbarChangeListener)
        binding.cannotDisableText.text = ""
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

    /**
     * Listener for value change in seekbar. Shows and hides warning about invalid selection.
     */
    private val seekbarChangeListener: SeekBar.OnSeekBarChangeListener =
        object : SeekBar.OnSeekBarChangeListener {
            val savedCount = viewModel.savedValues.countNotNull()

            override fun onProgressChanged(seekbar: SeekBar?, progress: Int, fromUser: Boolean) {
                if (progress < savedCount) {
                    val savedValuesText = if (savedCount == 1) "value is saved" else "values are saved"
                    val warningText = "Cannot set number of values to $progress when $savedCount $savedValuesText"

                    binding.cannotDisableText.text = warningText
                    binding.cannotDisableText.visible()
                } else {
                    binding.cannotDisableText.text = ""
                    binding.cannotDisableText.gone()
                }
            }

            override fun onStartTrackingTouch(seekbar: SeekBar?) {}
            override fun onStopTrackingTouch(seekbar: SeekBar?) {}
        }

    companion object {
        const val TAG = "AddOnesSettingsDialog"
        const val CLOSED_REQUEST_KEY = "${TAG}_Closed"
    }
}
