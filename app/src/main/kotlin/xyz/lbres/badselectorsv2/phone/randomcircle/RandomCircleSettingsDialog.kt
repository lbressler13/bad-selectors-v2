package xyz.lbres.badselectorsv2.phone.randomcircle

import androidx.lifecycle.ViewModelProvider
import xyz.lbres.badselectorsv2.abstracts.SettingsDialog
import xyz.lbres.badselectorsv2.databinding.DialogRandomCircleSettingsBinding

/**
 * Dialog to update settings for the [RandomCircleFragment]
 */
class RandomCircleSettingsDialog : SettingsDialog<DialogRandomCircleSettingsBinding>() {
    private lateinit var viewModel: RandomCircleViewModel

    override fun inflateLayout() = DialogRandomCircleSettingsBinding.inflate(layoutInflater)

    /**
     * Update UI to show initial settings
     */
    override fun setInitialUi() {
        viewModel = ViewModelProvider(requireActivity())[RandomCircleViewModel::class.java]
        binding.russianRouletteSwitch.isChecked = viewModel.russianRoulette
    }

    /**
     * Save changes to settings
     */
    override fun saveUpdatedSettings() {
        viewModel.russianRoulette = binding.russianRouletteSwitch.isChecked
    }

    companion object {
        val TAG = "RandomCircleSettings"
    }
}
