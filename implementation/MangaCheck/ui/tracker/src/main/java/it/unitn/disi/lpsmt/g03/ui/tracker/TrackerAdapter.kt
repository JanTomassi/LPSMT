package it.unitn.disi.lpsmt.g03.ui.tracker

import android.content.res.Resources
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.selection.ItemDetailsLookup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.RequestManager
import com.bumptech.glide.load.resource.bitmap.FitCenter
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions
import it.unitn.disi.lpsmt.g03.tracking.TrackerSeries
import it.unitn.disi.lpsmt.g03.ui.tracker.databinding.TrackerCardBinding

class TrackerAdapter(
    var dataSet: List<TrackerSeries>, private val glide: RequestManager
) : RecyclerView.Adapter<TrackerAdapter.ViewHolder>() {

    /**
     * Provide a reference to the type of views that you are using
     * (custom ViewHolder)
     */
    inner class ViewHolder(val view: TrackerCardBinding) : RecyclerView.ViewHolder(view.root) {
        fun getItem(): ItemDetailsLookup.ItemDetails<Long> =
            object : ItemDetailsLookup.ItemDetails<Long>() {
                override fun getPosition(): Int {
                    return adapterPosition
                }

                override fun getSelectionKey(): Long = dataSet[adapterPosition].uid
            }
    }

    // Create new views (invoked by the layout manager)
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = TrackerCardBinding.inflate(LayoutInflater.from(parent.context))
        return ViewHolder(view)
    }

    // Replace the contents of a view (invoked by the layout manager)
    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {
        val view = viewHolder.view

        val requestOptions = RequestOptions().transform(
            FitCenter(), RoundedCorners(
                TypedValue.applyDimension(
                    TypedValue.COMPLEX_UNIT_DIP, 8f, Resources.getSystem().displayMetrics
                ).toInt()
            )
        )

        view.seriesTitle.text = dataSet[position].title
        glide.load(dataSet[position].imageUri)
            .error(glide.load(R.drawable.baseline_broken_image_24))
            .apply(requestOptions).into(view.seriesCover)
    }

    // Return the size of your dataset (invoked by the layout manager)
    override fun getItemCount() = dataSet.size

    fun update(list: List<TrackerSeries>) {
        dataSet = list
        notifyItemRangeChanged(0, list.size)
    }
}