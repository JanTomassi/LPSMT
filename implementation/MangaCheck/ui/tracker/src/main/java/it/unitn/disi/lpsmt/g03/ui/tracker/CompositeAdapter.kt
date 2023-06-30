package it.unitn.disi.lpsmt.g03.ui.tracker

import android.util.Log
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import it.unitn.disi.lpsmt.g03.ui.tracker.category.CategoryAdapter

class CompositeAdapter(
    private var adapters: List<CategoryAdapter>
) : RecyclerView.Adapter<CategoryAdapter.ViewHolder>() {

    private var itemCountSoFar = 0
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CategoryAdapter.ViewHolder {
        for (adapter in adapters) {
            if (adapter.itemCount != 0 && !(adapter.rendered)) {
                adapter.rendered = true
                Log.v(
                    CompositeAdapter::class.simpleName,
                    "Adapter ${adapter.name} has ${adapter.itemCount} elements"
                )
                return adapter.onCreateViewHolder(parent, viewType)
            }
        }
        throw IllegalArgumentException("Unknown viewType: $viewType")
    }

    override fun onBindViewHolder(holder: CategoryAdapter.ViewHolder, position: Int) {
        val adapterItemCount = adapters[position].itemCount
        if (position < itemCountSoFar + adapterItemCount) {
            adapters[position].onBindViewHolder(holder, position - itemCountSoFar)
                //return
        }
        itemCountSoFar += adapterItemCount
    }

    override fun getItemCount(): Int {
        var tot = 0
        for (adapter in adapters) {
            if (adapter.itemCount != 0) tot += 1
        }
        Log.v(CompositeAdapter::class.simpleName, "The total elements are $tot")
        return tot
    }

    override fun getItemViewType(position: Int): Int {
        var itemCount = 0
        for (adapter in adapters) {
            val adapterItemCount = adapter.itemCount
            if (position < itemCount + adapterItemCount) {
                return adapter.getItemViewType(position - itemCount)
            }
            itemCount += adapterItemCount
        }
        throw IllegalArgumentException("Invalid position: $position")
    }
}