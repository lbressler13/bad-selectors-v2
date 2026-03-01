package xyz.lbres.badselectorsv2.home.selectorgroup

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import xyz.lbres.badselectorsv2.BaseActivity
import xyz.lbres.badselectorsv2.abstracts.TabFragment
import xyz.lbres.badselectorsv2.databinding.ViewHolderSelectorGroupBinding
import xyz.lbres.badselectorsv2.home.HomeViewModel

/**
 * Adapter for groups of selectors for the RecyclerView in the HomeFragment
 *
 * @param fragmentList List<[TabFragment.Metadata]>: list of metadata about groups
 * @param activity [BaseActivity]
 * @param viewModel [HomeViewModel]: ViewModel with information about expanded/collapsed items
 */
class SelectorGroupAdapter(
    private val fragmentList: List<TabFragment.Metadata>,
    private val activity: BaseActivity,
    private val viewModel: HomeViewModel,
) : RecyclerView.Adapter<SelectorGroupViewHolder>() {

    /**
     * Initialize a single view holder
     */
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SelectorGroupViewHolder {
        val layoutInflater: LayoutInflater = LayoutInflater.from(parent.context)

        val binding = ViewHolderSelectorGroupBinding.inflate(layoutInflater, parent, false)
        return SelectorGroupViewHolder(binding, activity, viewModel)
    }

    /**
     * Update a view holder for a specific group
     */
    override fun onBindViewHolder(holder: SelectorGroupViewHolder, position: Int) {
        val fragment = fragmentList[position]
        holder.updateForFragment(fragment, position)
    }

    /**
     * Number of items in adapter
     */
    override fun getItemCount(): Int = fragmentList.size
}
