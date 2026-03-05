package xyz.lbres.badselectorsv2.abstracts

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import xyz.lbres.badselectorsv2.R
import xyz.lbres.badselectorsv2.databinding.TabFragmentBinding
import xyz.lbres.kotlinutils.list.IntList

/**
 * Abstract fragment to handle common functionality for fragments that contain a tab layout.
 */
abstract class TabFragment : BaseFragment() {
    /**
     * Metadata about the current fragment
     */
    protected abstract var metadata: Metadata

    protected abstract var binding: TabFragmentBinding

    /**
     * Get fragment for a tab position
     *
     * @param position [Int]
     * @return [Fragment]
     */
    protected abstract fun getFragmentFromPosition(position: Int): Fragment

    /**
     * Set up tab layout
     */
    protected fun setUpTabs() {
        val tabs = binding.tabs
        val viewPager = binding.viewPager

        viewPager.adapter = getAdapter()
        TabLayoutMediator(tabs, viewPager) { tab, position ->
            val titleResId = metadata.tabTitleResIds[position]
            tab.text = getString(titleResId)
        }.attach()

        val tabKey = getString(R.string.tab_index_key)
        viewPager.currentItem = arguments?.getInt(tabKey) ?: 0

        tabs.tabMode = TabLayout.MODE_AUTO
    }

    /**
     * Get adapter for tab layout
     *
     * @return [FragmentStateAdapter]
     */
    private fun getAdapter(): FragmentStateAdapter {
        return object : FragmentStateAdapter(this) {
            override fun getItemCount(): Int = metadata.tabTitleResIds.size

            override fun createFragment(position: Int): Fragment = getFragmentFromPosition(position)
        }
    }

    /**
     * Metadata about a [TabFragment].
     *
     * @param titleResId [Int]: resource ID for the string that should be displayed in the app title bar
     * @param tabTitleResIds [IntList]: list of resource IDs for strings that should be displayed as tab titles
     * @param navActionFromHomeId [Int]: resource ID for the navigation action used to launch the given fragment from the home fragment
     */
    data class Metadata(
        val titleResId: Int,
        val tabTitleResIds: IntList,
        val navActionFromHomeId: Int,
    )
}
