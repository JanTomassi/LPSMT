package it.unitn.disi.lpsmt.g03.ui.tracker.category

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.selection.ItemDetailsLookup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.RequestManager
import it.unitn.disi.lpsmt.g03.tracking.TrackerSeries
import it.unitn.disi.lpsmt.g03.ui.tracker.card.CardAdapter
import it.unitn.disi.lpsmt.g03.ui.tracker.databinding.TrackerCategoryBinding

class CategoryAdapter(
    private val dataSet: List<TrackerSeries>,
    private val name: String,
    private val glide: RequestManager,
    private val ctx: Context
) : RecyclerView.Adapter<CategoryAdapter.ViewHolder>() {

    /**
     * Provide a reference to the type of views that you are using
     */
    inner class ViewHolder(view: TrackerCategoryBinding) :
        RecyclerView.ViewHolder(view.root) {
        var containerName: TextView = view.containerName
        var trackerView: RecyclerView = view.trackerView

        fun getItem(): ItemDetailsLookup.ItemDetails<Long> =
            object : ItemDetailsLookup.ItemDetails<Long>() {
                override fun getPosition(): Int {
                    return adapterPosition
                }

                override fun getSelectionKey(): Long = dataSet[layoutPosition].uid
            }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = TrackerCategoryBinding.inflate(LayoutInflater.from(parent.context))
        return ViewHolder(view)
    }

    override fun getItemCount(): Int = dataSet.size
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.containerName.text = name
        holder.trackerView.adapter = CardAdapter(dataSet, glide)
        holder.trackerView.layoutManager = LinearLayoutManager(ctx)
    }

    fun getDataSet(): List<TrackerSeries> = dataSet
}