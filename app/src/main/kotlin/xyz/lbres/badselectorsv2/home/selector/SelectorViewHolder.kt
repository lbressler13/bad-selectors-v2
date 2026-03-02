package xyz.lbres.badselectorsv2.home.selector

import androidx.recyclerview.widget.RecyclerView
import xyz.lbres.badselectorsv2.databinding.ViewHolderSelectorBinding

/**
 * ViewHolder for a single selector title
 *
 * @param binding [ViewHolderSelectorBinding]: view binding for the view holder
 */
class SelectorViewHolder(private val binding: ViewHolderSelectorBinding) :
    RecyclerView.ViewHolder(binding.root) {

    /**
     * Update UI to show information about current selector
     *
     * @param title [String]: value to display
     * @param onClick () -> Unit: action to take when view holder is clicked
     */
    fun updateForSelector(title: String, onClick: () -> Unit) {
        binding.itemTitle.text = title
        binding.root.setOnClickListener { onClick() }
    }
}
