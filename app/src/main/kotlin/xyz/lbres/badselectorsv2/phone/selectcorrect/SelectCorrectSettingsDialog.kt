package xyz.lbres.badselectorsv2.phone.selectcorrect

import androidx.lifecycle.ViewModelProvider
import xyz.lbres.badselectorsv2.abstracts.SettingsDialog
import xyz.lbres.badselectorsv2.databinding.DialogPhoneSelectCorrectSettingsBinding

/**
 * Dialog to update settings for the [SelectCorrectFragment]
 */
class SelectCorrectSettingsDialog : SettingsDialog<DialogPhoneSelectCorrectSettingsBinding>() {
    private lateinit var viewModel: SelectCorrectViewModel

    override fun inflateLayout() = DialogPhoneSelectCorrectSettingsBinding.inflate(layoutInflater)

    /**
     * Update UI to show initial settings
     */
    override fun setInitialUi() {
        viewModel = ViewModelProvider(requireActivity())[SelectCorrectViewModel::class]
        binding.singleSelectSwitch.isChecked = viewModel.singleSelect
    }

    /**
     * Save changes to settings
     */
    override fun saveUpdatedSettings() {
        viewModel.singleSelect = binding.singleSelectSwitch.isChecked
    }

    companion object {
        const val TAG = "SelectCorrectSettings"
    }
}
