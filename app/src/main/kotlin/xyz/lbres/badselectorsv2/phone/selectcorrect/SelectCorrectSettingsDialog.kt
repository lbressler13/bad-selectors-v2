package xyz.lbres.badselectorsv2.phone.selectcorrect

import androidx.lifecycle.ViewModelProvider
import xyz.lbres.badselectorsv2.abstracts.SettingsDialog
import xyz.lbres.badselectorsv2.databinding.DialogSelectCorrectSettingsBinding

class SelectCorrectSettingsDialog : SettingsDialog<DialogSelectCorrectSettingsBinding>() {
    private lateinit var viewModel: SelectCorrectViewModel

    override fun inflateLayout() = DialogSelectCorrectSettingsBinding.inflate(layoutInflater)

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
        val TAG = "SelectCorrectSettings"
    }
}
