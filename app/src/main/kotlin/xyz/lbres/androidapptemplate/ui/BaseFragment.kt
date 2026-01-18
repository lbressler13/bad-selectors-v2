package xyz.lbres.androidapptemplate.ui

import androidx.fragment.app.FragmentManager
import androidx.navigation.fragment.NavHostFragment
import xyz.lbres.androidapptemplate.BaseActivity
import xyz.lbres.androidapptemplate.R

/**
 * Abstract fragment to handle common functionality involving the BaseActivity
 */
abstract class BaseFragment : NavHostFragment() {

    /**
     * Resource ID of title to display in action bar
     */
    protected open var actionBarTitleResId: Int = R.string.title_action_bar

    /**
     * Re-add action bar settings when fragment is shown.
     */
    override fun onResume() {
        super.onResume()
        requireBaseActivity().binding.actionBar.title.text = getString(actionBarTitleResId)
        dialogFragmentManager = childFragmentManager
    }

    /**
     * Close the fragment
     */
    protected fun closeFragment() = requireBaseActivity().popBackStack()

    /**
     * Get current activity as [BaseActivity].
     *
     * @return [BaseActivity]
     */
    protected fun requireBaseActivity(): BaseActivity = requireActivity() as BaseActivity

    companion object {
        /**
         * Fragment manager for current fragment, used when displaying the dev tools dialog
         */
        var dialogFragmentManager: FragmentManager? = null
    }
}
