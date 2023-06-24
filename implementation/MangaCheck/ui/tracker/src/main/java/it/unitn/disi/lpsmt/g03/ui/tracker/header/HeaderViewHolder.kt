package it.unitn.disi.lpsmt.g03.ui.tracker.header

import android.view.View
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import it.unitn.disi.lpsmt.g03.ui.tracker.R


class HeaderViewHolder(view: View) : RecyclerView.ViewHolder(view) {
    private val containerName: TextView = itemView.findViewById(R.id.container_name)

    fun setContainerName(newName: String) {
        containerName.text = newName
    }
}