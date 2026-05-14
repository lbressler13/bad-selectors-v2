package xyz.lbres.badselectorsv2.phone.shufflecircle

import androidx.lifecycle.ViewModelProvider
import xyz.lbres.badselectorsv2.abstracts.SettingsDialog
import xyz.lbres.badselectorsv2.databinding.DialogShuffleCircleSettingsBinding

/**
 * Dialog to update settings for the [ShuffleCircleFragment]
 */
class ShuffleCircleSettingsDialog : SettingsDialog<DialogShuffleCircleSettingsBinding>() {
    private lateinit var viewModel: ShuffleCircleViewModel

    override fun inflateLayout() = DialogShuffleCircleSettingsBinding.inflate(layoutInflater)

    /**
     * Update UI to show initial settings
     */
    override fun setInitialUi() {
        viewModel = ViewModelProvider(requireActivity())[ShuffleCircleViewModel::class.java]
        binding.russianRouletteSwitch.isChecked = viewModel.russianRoulette
    }

    /**
     * Save changes to settings
     */
    override fun saveUpdatedSettings() {
        viewModel.russianRoulette = binding.russianRouletteSwitch.isChecked
    }

    companion object {
        val TAG = "ShuffleCircleSettings"
    }
}
