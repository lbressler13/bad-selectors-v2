package xyz.lbres.badselectorsv2.home.selectorgroup

import android.view.View
import androidx.core.os.bundleOf
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import xyz.lbres.badselectorsv2.BaseActivity
import xyz.lbres.badselectorsv2.R
import xyz.lbres.badselectorsv2.abstracts.TabFragment
import xyz.lbres.badselectorsv2.databinding.ViewHolderSelectorGroupBinding
import xyz.lbres.badselectorsv2.ext.view.gone
import xyz.lbres.badselectorsv2.ext.view.invisible
import xyz.lbres.badselectorsv2.ext.view.visible
import xyz.lbres.badselectorsv2.home.HomeViewModel
import xyz.lbres.badselectorsv2.home.selector.SelectorAdapter

/**
 * ViewHolder for a single group of selectors. Includes dropdown for individual selectors.
 *
 * @param binding [ViewHolderSelectorGroupBinding]: view binding for the view holder
 * @param activity [BaseActivity]
 * @param viewModel [HomeViewModel]: ViewModel with information about expanded/collapsed items
 */
class SelectorGroupViewHolder(
    private val binding: ViewHolderSelectorGroupBinding,
    private val activity: BaseActivity,
    private val viewModel: HomeViewModel,
) : RecyclerView.ViewHolder(binding.root) {

    /**
     * Update UI to show information about current group of selectors
     *
     * @param metadata [TabFragment.Metadata]: metadata about fragment containing selectors
     * @param position [Int]: position in list of item being displayed
     */
    fun updateForGroup(metadata: TabFragment.Metadata, position: Int) {
        binding.activityTitle.text = activity.getString(metadata.titleResId)

        initRecyclerView(metadata)
        initDropdown(position)

        if (viewModel.selectorsExpanded[position]) {
            setExpandedUi()
        } else {
            setCollapsedUi()
        }

        if (metadata.tabTitleResIds.isEmpty()) {
            binding.selectorRecycler.gone()
            binding.expandCollapseButton.invisible() // prevents title from being shifted to right
            binding.root.setOnClickListener {
                activity.runNavAction(metadata.navActionFromHomeId)
            }
        }
    }

    /**
     * Initialize recycler view to selectors in group
     *
     * @param metadata [TabFragment.Metadata]: metadata about group, including selector names and info for onClick
     */
    private fun initRecyclerView(metadata: TabFragment.Metadata) {
        val itemRecycler: RecyclerView = binding.selectorRecycler
        val tabKey = activity.getString(R.string.tab_index_key)

        val fragmentOnClick: (Int) -> Unit = { tabIndex ->
            activity.runNavAction(metadata.navActionFromHomeId, bundleOf(tabKey to tabIndex))
        }
        val adapter = SelectorAdapter(metadata.tabTitleResIds, fragmentOnClick, activity)
        itemRecycler.adapter = adapter
        itemRecycler.layoutManager = LinearLayoutManager(activity)
    }

    /**
     * Initialize dropdown
     */
    private fun initDropdown(position: Int) {
        // modify icon and values in adapter
        val onClick = View.OnClickListener {
            val expanded = viewModel.selectorsExpanded[position]

            if (expanded) {
                setCollapsedUi()
            } else {
                setExpandedUi()
            }

            viewModel.selectorsExpanded[position] = !expanded
        }

        binding.root.setOnClickListener(onClick)
        binding.expandCollapseButton.setOnClickListener(onClick)
    }

    /**
     * Display the list of selectors and update the expand/collapse button
     */
    private fun setExpandedUi() {
        val expandedIconId = R.drawable.ic_chevron_down
        val expandedCd = activity.getString(R.string.collapse_dropdown_cd)
        val button = binding.expandCollapseButton

        button.setImageResource(expandedIconId)
        button.contentDescription = expandedCd
        binding.internalDivider.visible()
        binding.selectorRecycler.visible()
    }

    /**
     * Hide the list of selectors and update the expand/collapse button
     */
    private fun setCollapsedUi() {
        val collapsedIconId = R.drawable.ic_chevron_right
        val collapsedCd = activity.getString(R.string.expand_dropdown_cd)
        val button = binding.expandCollapseButton

        button.setImageResource(collapsedIconId)
        button.contentDescription = collapsedCd
        binding.internalDivider.gone()
        binding.selectorRecycler.gone()
    }
}
