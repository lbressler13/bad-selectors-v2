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
import xyz.lbres.badselectorsv2.BaseActivity
import xyz.lbres.badselectorsv2.R

/**
 * Abstract dialog to handle common functionality in a dialog, including init, dismiss, and saving changes
 */
abstract class BaseDialog<T : ViewBinding> : DialogFragment() {
    protected lateinit var binding: T

    /**
     * Request key to notify parent that dialog has closed. Defaults to null
     */
    protected open val dialogClosedRequestKey: String? = null

    /**
     * Resource id for dialog title
     */
    protected open val titleResId: Int = R.string.title_settings

    /**
     * Initialize dialog
     */
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        binding = inflateLayout()

        val doneText = getString(R.string.done)
        val title = getString(titleResId)

        return AlertDialog.Builder(requireContext())
            .setView(binding.root)
            .setMessage(title)
            .setPositiveButton(doneText) { _, _ -> }
            .create()
    }

    /**
     * Set initial UI
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
     * Update UI to show initial state
     */
    protected abstract fun setInitialUi()

    /**
     * Save changes made in dialog
     */
    protected open fun saveChanges() {}

    /**
     * Get current activity as [BaseActivity]
     */
    protected fun requireBaseActivity(): BaseActivity = requireActivity() as BaseActivity

    /**
     * Close fragment and save changes
     */
    override fun onDismiss(dialog: DialogInterface) {
        super.onDismiss(dialog)
        saveChanges()

        // notify parent fragment that dialog has closed
        if (dialogClosedRequestKey != null) {
            parentFragmentManager.setFragmentResult(dialogClosedRequestKey!!, bundleOf())
        }
    }
}
