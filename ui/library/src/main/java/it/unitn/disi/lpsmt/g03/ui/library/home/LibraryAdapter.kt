package it.unitn.disi.lpsmt.g03.ui.library.home

import android.content.res.TypedArray
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.ViewGroup
import androidx.navigation.NavController
import androidx.recyclerview.selection.ItemDetailsLookup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.RequestManager
import it.unitn.disi.lpsmt.g03.core.ImageLoader
import it.unitn.disi.lpsmt.g03.data.appdatabase.AppDatabase
import it.unitn.disi.lpsmt.g03.data.library.Series
import it.unitn.disi.lpsmt.g03.ui.library.common.CustomAdapter
import it.unitn.disi.lpsmt.g03.ui.library.databinding.LibraryCardBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.time.ZonedDateTime
import kotlin.math.max

internal class LibraryAdapter(dataSet: List<Series>,
    private val glide: RequestManager,
    val navController: NavController) : CustomAdapter<LibraryCardBinding, Series, Long>(dataSet) {

    // Create new views (invoked by the layout manager)
    override fun onCreateViewHolder(parent: ViewGroup,
        viewType: Int): CustomAdapter<LibraryCardBinding, Series, Long>.ViewHolder {
        val view = LibraryCardBinding.inflate(LayoutInflater.from(parent.context))
        return ViewHolder(view)
    }

    // Replace the contents of a view (invoked by the layout manager)
    override fun onBindViewHolder(holder: CustomAdapter<LibraryCardBinding, Series, Long>.ViewHolder,
        position: Int) {
        holder.bind(dataSet[position])
    }

    // Return the size of your dataset (invoked by the layout manager)
    override fun getItemCount() = dataSet.size

    override fun update(list: List<Series>) {
        val oldItemCount = dataSet.size
        val newItemCount = list.size
        dataSet = list
        notifyItemRangeChanged(0, max(newItemCount, oldItemCount))
    }

    /**
     * Provide a reference to the type of views that you are using
     * (custom ViewHolder)
     */
    inner class ViewHolder(view: LibraryCardBinding) : CustomAdapter<LibraryCardBinding, Series, Long>.ViewHolder(
        view) {
        override fun getItem() = object : ItemDetailsLookup.ItemDetails<Long>() {
            override fun getPosition(): Int = bindingAdapterPosition
            override fun getSelectionKey(): Long = dataSet[bindingAdapterPosition].uid
        }

        private inner class SurfaceColor(val colorSurface: Int, val colorSurfaceVariant: Int)

        private fun getColor(): SurfaceColor {
            val typedValue = TypedValue()

            val a: TypedArray = view.root.context.obtainStyledAttributes(typedValue.data,
                intArrayOf(com.google.android.material.R.attr.colorSurface,
                    com.google.android.material.R.attr.colorSurfaceVariant))
            val colorSurface = a.getColor(0, 0)
            val colorSurfaceVariant = a.getColor(a.getIndex(1), 0)
            a.recycle()
            return SurfaceColor(colorSurface, colorSurfaceVariant)
        }

        override fun bind(item: Series) {
            tracker.let { selector ->
                val color = getColor()
                if (selector.isSelected(item.uid)) {
                    view.root.setCardBackgroundColor(color.colorSurfaceVariant)
                } else view.root.setCardBackgroundColor(color.colorSurface)
            }
            view.text.text = item.title
            view.root.setOnClickListener {
                CoroutineScope(Dispatchers.IO).launch {
                    val db = AppDatabase.getInstance(view.root.context)
                    db.seriesDao().update(item.copy(lastAccess = ZonedDateTime.now()))
                }
                val direction = LibraryFragmentDirections.actionLibraryToChapterList(item)
                navController.navigate(direction)
            }
            ImageLoader.setCoverImageFromImage(item.imageUri, glide, view.image)
        }
    }

    class ItemsDetailsLookup(private val recyclerView: RecyclerView) : CustomAdapter.ItemsDetailsLookup<Long>() {
        override fun getItemDetails(e: MotionEvent): ItemDetails<Long>? {
            val view = recyclerView.findChildViewUnder(e.x, e.y)
            return (view?.let { recyclerView.getChildViewHolder(it) } as ViewHolder?)?.getItem()
        }
    }

    class ItemsKeyProvider(private val adapter: LibraryAdapter) : CustomAdapter.ItemsKeyProvider<Long>() {
        override fun getKey(position: Int): Long = adapter.dataSet[position].uid
        override fun getPosition(key: Long): Int = adapter.dataSet.indexOfFirst { it.uid == key }
    }

}