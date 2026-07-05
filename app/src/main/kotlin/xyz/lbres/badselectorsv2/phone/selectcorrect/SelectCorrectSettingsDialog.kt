package xyz.lbres.badselectorsv2.phone.selectcorrect

import androidx.lifecycle.ViewModelProvider
import xyz.lbres.badselectorsv2.abstracts.BaseDialog
import xyz.lbres.badselectorsv2.databinding.DialogPhoneSelectCorrectSettingsBinding

/**
 * Dialog to update settings for the [SelectCorrectFragment]
 */
class SelectCorrectSettingsDialog : BaseDialog<DialogPhoneSelectCorrectSettingsBinding>() {
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
    override fun saveChanges() {
        viewModel.singleSelect = binding.singleSelectSwitch.isChecked
    }

    companion object {
        val TAG = "SelectCorrectSettings"
    }
}
