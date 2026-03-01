package xyz.lbres.badselectorsv2.abstracts

import android.annotation.SuppressLint
import android.view.View
import androidx.core.os.bundleOf
import androidx.fragment.app.FragmentManager
import androidx.navigation.fragment.NavHostFragment
import com.google.android.material.bottomnavigation.BottomNavigationMenuView
import xyz.lbres.badselectorsv2.BaseActivity
import xyz.lbres.badselectorsv2.R
import xyz.lbres.badselectorsv2.home.HomeFragment

/**
 * Abstract fragment to handle common functionality involving the BaseActivity
 */
abstract class BaseFragment : NavHostFragment() {

    /**
     * Resource ID of title to display in action bar
     */
    protected open var actionBarTitleResId: Int = R.string.title_action_bar

    /**
     * Resource IDs for actions to navigate to other fragments
     */
    protected open var navToHomeResId: Int? = null

    /**
     * Re-add action bar settings when fragment is shown.
     */
    override fun onResume() {
        super.onResume()
        requireBaseActivity().binding.actionBar.title = getString(actionBarTitleResId)
        setupNavbar()
        dialogFragmentManager = childFragmentManager
    }

    private fun setupNavbar() {
        @SuppressLint("RestrictedApi")
        val navbarItems = requireBaseActivity().binding.navbar.getChildAt(0) as BottomNavigationMenuView

        val home = navbarItems.getChildAt(0)

        requireBaseActivity().binding.navbar.selectedItemId = when (this) {
            is HomeFragment -> home.id
            else -> home.id
        }

        addNavOnClick(home, navToHomeResId, tabFragment = false)
    }

    /**
     * Set on click action for a single button in the navbar
     *
     * @param button [android.view.View]: button to set onClick for
     * @param actionResId [Int]?: navigation action to take when button is clicked. `null` for the current fragment.
     * @param tabFragment [Boolean]: `true` if the destination fragment is a tab fragment, `false` otherwise
     */
    private fun addNavOnClick(button: View, actionResId: Int?, tabFragment: Boolean) {
        val tabKey = getString(R.string.tab_index_key)
        val tabArgs = bundleOf(tabKey to 0)

        button.setOnClickListener {
            when {
                tabFragment && actionResId != null -> requireBaseActivity().runNavAction(actionResId, tabArgs)
                actionResId != null -> requireBaseActivity().runNavAction(actionResId)
            }
        }
    }

    /**
     * Close the fragment
     */
    protected fun closeFragment() = requireBaseActivity().popBackStack()

    /**
     * Get current activity as [xyz.lbres.badselectorsv2.BaseActivity].
     *
     * @return [xyz.lbres.badselectorsv2.BaseActivity]
     */
    protected fun requireBaseActivity(): BaseActivity = requireActivity() as BaseActivity

    companion object {
        /**
         * Fragment manager for current fragment, used when displaying the dev tools dialog
         */
        var dialogFragmentManager: FragmentManager? = null
    }
}