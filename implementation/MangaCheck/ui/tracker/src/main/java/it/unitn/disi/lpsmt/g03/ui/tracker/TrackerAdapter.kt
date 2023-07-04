package it.unitn.disi.lpsmt.g03.ui.tracker

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import it.unitn.disi.lpsmt.g03.ui.tracker.category.CategoryAdapter

class TrackerAdapter(
        private var adapters: List<CategoryAdapter>
) : RecyclerView.Adapter<CategoryAdapter.ViewHolder>() {

    private var itemCountOnBind = 0
    private var itemCountViewType = 0

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CategoryAdapter.ViewHolder {
        for (adapter in adapters) {
            return adapter.onCreateViewHolder(parent, viewType)
        }
        throw IllegalArgumentException("Unknown viewType: $viewType")
    }

    override fun onBindViewHolder(holder: CategoryAdapter.ViewHolder, position: Int) {
        val adapterItemCount = adapters[position].itemCount
        if (position <= itemCountOnBind + adapterItemCount) {
            adapters[position].onBindViewHolder(holder, position - itemCountOnBind)
        }
        itemCountOnBind += adapterItemCount
    }

    override fun getItemCount(): Int {
        cleanUpInput()
        return adapters.size
    }

    override fun getItemViewType(position: Int): Int {
        val adapterItemCount = adapters[position].itemCount
        if (position < itemCount + adapterItemCount) {
            itemCountViewType += adapterItemCount
            return adapters[position].getItemViewType(position - itemCount)
        }
        throw IllegalArgumentException("Invalid position: $position")
    }

    /**
     * Remove all the empty categories from the input
     */
    private fun cleanUpInput() {
        val tmpInputs: MutableList<CategoryAdapter> = adapters as MutableList<CategoryAdapter>
        tmpInputs.removeAll { it.itemCount == 0 }
        adapters = tmpInputs
    }
}