package it.unitn.disi.lpsmt.g03.ui.tracker

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import it.unitn.disi.lpsmt.g03.ui.tracker.category.CategoryAdapter

class CompositeAdapter(private val adapters: List<RecyclerView.Adapter<CategoryAdapter.ViewHolder>>) : RecyclerView.Adapter<CategoryAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CategoryAdapter.ViewHolder {
        val adapterIndex = getAdapterIndex(viewType)
        return adapters[adapterIndex].onCreateViewHolder(parent, viewType)
    }

    override fun onBindViewHolder(holder: CategoryAdapter.ViewHolder, position: Int) {
        val adapterIndex = getAdapterIndexForPosition(position)
        adapters[adapterIndex].onBindViewHolder(holder, getPositionInAdapter(position, adapterIndex))
    }

    override fun getItemCount(): Int {
        return adapters.sumOf { it.itemCount }
    }

    override fun getItemViewType(position: Int): Int {
        val adapterIndex = getAdapterIndexForPosition(position)
        return adapters[adapterIndex].getItemViewType(getPositionInAdapter(position, adapterIndex))
    }

    private fun getAdapterIndex(viewType: Int): Int {
        return adapters.indexOfFirst { it.getItemViewType(0) == viewType }
    }

    private fun getAdapterIndexForPosition(position: Int): Int {
        var cumulativeItemCount = 0
        for ((adapterIndex, adapter) in adapters.withIndex()) {
            val itemCount = adapter.itemCount
            if (position < cumulativeItemCount + itemCount) {
                return adapterIndex
            }
            cumulativeItemCount += itemCount
        }
        throw IndexOutOfBoundsException("Invalid position: $position")
    }

    private fun getPositionInAdapter(position: Int, adapterIndex: Int): Int {
        var cumulativeItemCount = 0
        for (i in 0 until adapterIndex) {
            cumulativeItemCount += adapters[i].itemCount
        }
        return position - cumulativeItemCount
    }
}