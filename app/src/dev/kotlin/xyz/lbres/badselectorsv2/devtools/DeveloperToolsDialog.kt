package xyz.lbres.badselectorsv2.devtools

import android.os.Handler
import android.os.Looper
import android.widget.ArrayAdapter
import android.widget.Spinner
import xyz.lbres.badselectorsv2.R
import xyz.lbres.badselectorsv2.abstracts.BaseDialog
import xyz.lbres.badselectorsv2.databinding.DialogDeveloperToolsBinding
import xyz.lbres.badselectorsv2.ext.view.gone
import xyz.lbres.badselectorsv2.ext.view.visible

/**
 * Dialog with various developer tools, only available in dev build flavor
 */
class DeveloperToolsDialog : BaseDialog<DialogDeveloperToolsBinding>() {
    override val titleResId: Int = R.string.title_dev_tools

    override fun inflateLayout(): DialogDeveloperToolsBinding = DialogDeveloperToolsBinding.inflate(layoutInflater)

    /**
     * Set up dialog buttons
     */
    override fun setInitialUi() {
        binding.refreshUIButton.setOnClickListener { requireActivity().recreate() }
        initHideDevTools()
    }

    /**
     * Initialize the spinner used to set the time to hide the dev tools button.
     */
    private fun initHideDevTools() {
        val spinner: Spinner = binding.devToolsTimeSpinner
        ArrayAdapter.createFromResource(
            requireContext(),
            R.array.dev_tools_time_options,
            R.layout.component_spinner_item_selected,
        ).also { adapter ->
            // Specify the layout to use when the list of choices appears
            adapter.setDropDownViewResource(R.layout.component_spinner_item_dropdown)
            // Apply the adapter to the spinner
            spinner.adapter = adapter
        }

        binding.hideDevToolsButton.setOnClickListener { hideDevToolsOnClick() }
    }

    /**
     * On click for the hide dev tools button.
     * Hides the button for an amount of time based on the current value of the spinner.
     */
    private fun hideDevToolsOnClick() {
        val timerString = binding.devToolsTimeSpinner.selectedItem.toString()
        val numString = timerString.substring(0, timerString.length - 2) // remove ms from end
        val timer = Integer.parseInt(numString).toLong()

        val button = requireBaseActivity().binding.devToolsButton
        button.gone()

        // unhide dev tools button
        Handler(Looper.getMainLooper()).postDelayed({
            button.visible()
        }, timer)

        dismiss()
    }

    companion object {
        // tag is required when showing dialog
        const val TAG = "DeveloperToolsDialog"
    }
}
