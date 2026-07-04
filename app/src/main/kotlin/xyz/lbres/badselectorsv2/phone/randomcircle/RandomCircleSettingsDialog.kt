package xyz.lbres.badselectorsv2.phone.randomcircle

import androidx.lifecycle.ViewModelProvider
import xyz.lbres.badselectorsv2.abstracts.SelectorDialog
import xyz.lbres.badselectorsv2.databinding.DialogPhoneRandomCircleSettingsBinding

/**
 * Dialog to update settings for the [RandomCircleFragment]
 */
class RandomCircleSettingsDialog : SelectorDialog<DialogPhoneRandomCircleSettingsBinding>() {
    private lateinit var viewModel: RandomCircleViewModel

    override fun inflateLayout() = DialogPhoneRandomCircleSettingsBinding.inflate(layoutInflater)

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
    override fun saveChanges() {
        viewModel.russianRoulette = binding.russianRouletteSwitch.isChecked
    }

    companion object {
        val TAG = "RandomCircleSettings"
    }
}
