package it.unitn.disi.lpsmt.g03.ui.tracker

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.RequestManager
import it.unitn.disi.lpsmt.g03.tracking.TrackerSeries
import it.unitn.disi.lpsmt.g03.ui.tracker.card.CardAdapter
import it.unitn.disi.lpsmt.g03.ui.tracker.databinding.TrackerCategoryBinding


class TrackerAdapter(
    private var dataSet: List<TrackerSeries>, private val listName : String, private val glide: RequestManager
) : RecyclerView.Adapter<TrackerAdapter.ViewHolder>() {

    inner class ViewHolder(view: TrackerCategoryBinding): RecyclerView.ViewHolder(view.root) {
        var containerName : TextView = view.containerName
        var trackerView : RecyclerView = view.trackerView
    }

    // Create new views (invoked by the layout manager)
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = TrackerCategoryBinding.inflate(LayoutInflater.from(parent.context))
        return ViewHolder(view)
    }

    // Replace the contents of a view (invoked by the layout manager)
    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {
        viewHolder.containerName.text = "hello"

        viewHolder.trackerView.adapter = CardAdapter(dataSet, glide)
    }

    // Return the size of your dataset (invoked by the layout manager)
    override fun getItemCount() = dataSet.size

    fun update(list: List<TrackerSeries>) {
        dataSet = list
        notifyItemRangeChanged(0, list.size)
    }
}