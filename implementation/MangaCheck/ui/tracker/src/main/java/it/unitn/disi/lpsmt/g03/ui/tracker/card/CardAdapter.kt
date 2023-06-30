package it.unitn.disi.lpsmt.g03.ui.tracker.card

import android.content.res.Resources
import android.util.Log
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.RequestManager
import com.bumptech.glide.load.resource.bitmap.FitCenter
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions
import it.unitn.disi.lpsmt.g03.tracking.TrackerSeries
import it.unitn.disi.lpsmt.g03.ui.tracker.R
import it.unitn.disi.lpsmt.g03.ui.tracker.databinding.TrackerCardBinding

class CardAdapter(
    private val dataSet: List<TrackerSeries>, private val glide: RequestManager
) : RecyclerView.Adapter<CardAdapter.ViewHolder>() {

    /**
     * Provide a reference to the type of views that you are using
     */
    inner class ViewHolder(view: TrackerCardBinding) :
        RecyclerView.ViewHolder(view.root) {
            var seriesCover : ImageView =  view.seriesCover
            var seriesTitle : TextView = view.seriesTitle
            var chCounter : TextView = view.chCounter
    }

    // Create new views (invoked by the layout manager)
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = TrackerCardBinding.inflate(LayoutInflater.from(parent.context))
        return ViewHolder(view)
    }

    // Replace the contents of a view (invoked by the layout manager)
    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {
        // Set the image in the card
        val requestOptions = RequestOptions().transform(
            FitCenter(), RoundedCorners(
                TypedValue.applyDimension(
                    TypedValue.COMPLEX_UNIT_DIP, 8f, Resources.getSystem().displayMetrics
                ).toInt()
            )
        )

        glide.load(dataSet[position].imageUri).error(glide.load(R.drawable.baseline_broken_image_24))
            .apply(requestOptions).into(viewHolder.seriesCover)

        // Set the Series title in the card
        viewHolder.seriesTitle.text = dataSet[position].title

        // Set the Series chapter counter in the card
        viewHolder.chCounter.text = dataSet[position].chapters.toString()
    }

    // Return the size of your dataset (invoked by the layout manager)
    override fun getItemCount() : Int {
        Log.v(CardAdapter::class.simpleName,"The comics are ${dataSet.size}")
        return dataSet.size
    }

}