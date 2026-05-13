package xyz.lbres.badselectorsv2.abstracts

import android.app.AlertDialog
import android.app.Dialog
import android.content.DialogInterface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.DialogFragment
import androidx.viewbinding.ViewBinding
import xyz.lbres.badselectorsv2.R

/**
 * Abstract dialog to handle common functionality between settings dialogs, including init, dismiss, and setting up ViewModel
 */
abstract class SettingsDialog<T : ViewBinding> : DialogFragment() {
    protected lateinit var binding: T

    /**
     * Request key to notify parent that dialog has closed. Defaults to null
     */
    protected open var dialogClosedRequestKey: String? = null

    /**
     * Initialize dialog
     */
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        binding = inflateLayout()

        val doneText = getString(R.string.done)
        val title = getString(R.string.title_settings)

        return AlertDialog.Builder(requireContext())
            .setView(binding.root)
            .setMessage(title)
            .setPositiveButton(doneText) { _, _ -> }
            .create()
    }

    /**
     * Initialize ViewModel when view is created, as lifecycle owner can't be accessed until view exists
     */
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        setInitialUi()
        return binding.root
    }

    /**
     * Inflate layout and return view binding
     */
    protected abstract fun inflateLayout(): T

    /**
     * Update UI to show initial settings
     */
    protected abstract fun setInitialUi()

    /**
     * Save changes to settings
     */
    protected open fun saveUpdatedSettings() {}

    /**
     * Close fragment and save settings
     */
    override fun onDismiss(dialog: DialogInterface) {
        super.onDismiss(dialog)
        saveUpdatedSettings()

        // notify parent fragment that dialog has closed
        if (dialogClosedRequestKey != null) {
            parentFragmentManager.setFragmentResult(dialogClosedRequestKey!!, bundleOf())
        }
    }
}
