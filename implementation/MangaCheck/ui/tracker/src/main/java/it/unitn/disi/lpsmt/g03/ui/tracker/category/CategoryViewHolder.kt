package it.unitn.disi.lpsmt.g03.ui.tracker.category

import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import it.unitn.disi.lpsmt.g03.ui.tracker.databinding.TrackerCardBinding

/**
 * Provide a reference to the type of views that you are using
 * (custom ViewHolder)
 */
class CategoryViewHolder(val view: TrackerCardBinding) :
    RecyclerView.ViewHolder(view.root) {

    var seriesCover: ImageView = view.seriesCover
    var seriesTitle: TextView = view.seriesTitle
    var chCounter: TextView = view.chCounter
}