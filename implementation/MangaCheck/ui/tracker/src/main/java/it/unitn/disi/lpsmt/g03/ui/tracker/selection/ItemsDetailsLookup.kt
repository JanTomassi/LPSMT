package it.unitn.disi.lpsmt.g03.ui.tracker.selection

import android.view.MotionEvent
import androidx.recyclerview.selection.ItemDetailsLookup
import androidx.recyclerview.widget.RecyclerView
import it.unitn.disi.lpsmt.g03.ui.tracker.category.CategoryAdapter

class ItemsDetailsLookup(private val recyclerView: RecyclerView) : ItemDetailsLookup<Long>() {
    override fun getItemDetails(e: MotionEvent): ItemDetails<Long>? {
        val view = recyclerView.findChildViewUnder(e.x, e.y)
        if (view != null) {
            return (recyclerView.getChildViewHolder(view) as CategoryAdapter.ViewHolder).getItem()
        }
        return null
    }
}