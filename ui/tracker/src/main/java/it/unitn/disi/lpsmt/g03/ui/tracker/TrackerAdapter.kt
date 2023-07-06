package it.unitn.disi.lpsmt.g03.ui.tracker

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import it.unitn.disi.lpsmt.g03.ui.tracker.category.CategoryAdapter
import it.unitn.disi.lpsmt.g03.ui.tracker.databinding.TrackerCategoryBinding

class TrackerAdapter(
    private var adapters: List<CategoryAdapter>, private val ctx: Context
) : RecyclerView.Adapter<TrackerAdapter.ViewHolder>() {

    /**
     * Provide a reference to the type of views that you are using
     */
    inner class ViewHolder(view: TrackerCategoryBinding) : RecyclerView.ViewHolder(view.root) {
        private var containerName: TextView = view.containerName
        private var trackerView: RecyclerView = view.trackerView

        fun bind(adapter: CategoryAdapter) {
            containerName.text = adapter.name.toString()
            trackerView.adapter = adapter
            trackerView.layoutManager = LinearLayoutManager(ctx)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TrackerAdapter.ViewHolder {
        return ViewHolder(
            TrackerCategoryBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: TrackerAdapter.ViewHolder, position: Int) {
        Log.v(TrackerAdapter::class.simpleName, "onBindViewHolder on position $position")
        adapters[position].view = holder.itemView
        holder.bind(adapters[position])
    }

    override fun getItemCount(): Int {
        //cleanUpInput()
        return adapters.size
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