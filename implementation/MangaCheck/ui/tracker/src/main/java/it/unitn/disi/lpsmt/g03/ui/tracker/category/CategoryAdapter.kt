package it.unitn.disi.lpsmt.g03.ui.tracker.category

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.RequestManager
import it.unitn.disi.lpsmt.g03.tracking.TrackerSeries
import it.unitn.disi.lpsmt.g03.ui.tracker.databinding.TrackerCardBinding

class CategoryAdapter(
    private var dataSet: List<TrackerSeries>, private val glide: RequestManager
) : RecyclerView.Adapter<CategoryViewHolder>() {

    // Create new views (invoked by the layout manager)
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CategoryViewHolder {
        val view = TrackerCardBinding.inflate(LayoutInflater.from(parent.context))
        return CategoryViewHolder(view)
    }

    // Replace the contents of a view (invoked by the layout manager)
    override fun onBindViewHolder(viewHolder: CategoryViewHolder, position: Int) {
        val view = viewHolder.view

    }

    // Return the size of your dataset (invoked by the layout manager)
    override fun getItemCount() = dataSet.size
}