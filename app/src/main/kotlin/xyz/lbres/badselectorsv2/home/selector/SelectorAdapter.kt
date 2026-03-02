package xyz.lbres.badselectorsv2.home.selector

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import xyz.lbres.badselectorsv2.databinding.ViewHolderSelectorBinding
import xyz.lbres.kotlinutils.list.IntList

/**
 * Adapter for list of selectors for the RecyclerView in selector group
 *
 * @param itemResIds [IntList]: list of resource IDs for values to be displayed in adapter
 * @param onClick (Int) -> Unit: function to take when an element is clicked, based on its position
 * @param context [Context]: application context
 */
class SelectorAdapter(
    private val itemResIds: IntList,
    private val onClick: (Int) -> Unit,
    private val context: Context,
) : RecyclerView.Adapter<SelectorViewHolder>() {

    /**
     * Initialize a single view holder
     */
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SelectorViewHolder {
        val layoutInflater: LayoutInflater = LayoutInflater.from(parent.context)

        val binding = ViewHolderSelectorBinding.inflate(layoutInflater, parent, false)
        return SelectorViewHolder(binding)
    }

    /**
     * Update a view holder for a specific title
     */
    override fun onBindViewHolder(holder: SelectorViewHolder, position: Int) {
        val titleId = itemResIds[position]
        val positionOnClick: () -> Unit = { onClick(position) }
        holder.updateForSelector(context.getString(titleId), positionOnClick)
    }

    /**
     * Number of items in adapter
     */
    override fun getItemCount(): Int = itemResIds.size
}
