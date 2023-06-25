package it.unitn.disi.lpsmt.g03.ui.tracker

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.RequestManager
import it.unitn.disi.lpsmt.g03.tracking.TrackerSeries
import it.unitn.disi.lpsmt.g03.ui.tracker.category.CategoryAdapter
import it.unitn.disi.lpsmt.g03.ui.tracker.databinding.TrackerCategoryBinding

class TrackerAdapter(
    var dataSet: List<TrackerSeries>, private val glide: RequestManager
) : RecyclerView.Adapter<TrackerAdapter.ViewHolder>() {

    /**
     * Provide a reference to the type of views that you are using
     * (custom ViewHolder)
     */
    inner class ViewHolder(private val view: TrackerCategoryBinding) :
        RecyclerView.ViewHolder(view.root) {
        init {
            view.root.adapter = CategoryAdapter(dataSet, glide)
        }
    }

    // Create new views (invoked by the layout manager)
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = TrackerCategoryBinding.inflate(LayoutInflater.from(parent.context))
        return ViewHolder(view)
    }

    // Replace the contents of a view (invoked by the layout manager)
    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {

    }

    // Return the size of your dataset (invoked by the layout manager)
    override fun getItemCount() = dataSet.size

    fun update(list: List<TrackerSeries>) {
        dataSet = list
        notifyItemRangeChanged(0, list.size)
    }
}