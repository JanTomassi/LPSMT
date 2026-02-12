package it.unitn.disi.lpsmt.g03.ui.library.home

import android.content.Context
import android.content.res.TypedArray
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.ViewGroup
import androidx.lifecycle.LifecycleOwner
import androidx.navigation.NavController
import androidx.recyclerview.selection.ItemDetailsLookup
import androidx.recyclerview.selection.ItemKeyProvider
import androidx.recyclerview.selection.SelectionTracker
import androidx.recyclerview.widget.RecyclerView
import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import dagger.hilt.android.EntryPointAccessors
import dagger.hilt.components.SingletonComponent
import it.unitn.disi.lpsmt.g03.core.ImageLoader
import it.unitn.disi.lpsmt.g03.data.library.Series
import it.unitn.disi.lpsmt.g03.data.library.SeriesDao
import it.unitn.disi.lpsmt.g03.ui.library.databinding.LibraryCardBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.time.ZonedDateTime
import kotlin.math.max

internal class LibraryAdapter(context: Context,
    val navController: NavController,
    lifecycleOwner: LifecycleOwner) : RecyclerView.Adapter<LibraryAdapter.ViewHolder>() {

    @EntryPoint
    @InstallIn(SingletonComponent::class)
    interface LibraryAdapterEntryPoint {
        fun provideSeriesDao(): SeriesDao
    }

    var seriesDao: SeriesDao
    var dataSet: List<Series> = emptyList()
    lateinit var tracker: SelectionTracker<Long>

    init {
        val myLibraryAdapterEntryPoint = EntryPointAccessors.fromApplication(context,
            LibraryAdapterEntryPoint::class.java)
        val liveDataset = myLibraryAdapterEntryPoint.provideSeriesDao().getAllSortByLastAccess()
        liveDataset.observe(lifecycleOwner) {
            val maxSize = max(dataSet.size, it.size)
            dataSet = it
            notifyItemRangeChanged(0, maxSize)
        }
        seriesDao = myLibraryAdapterEntryPoint.provideSeriesDao()
    }

    // Create new views (invoked by the layout manager)
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LibraryCardBinding.inflate(LayoutInflater.from(parent.context))
        return ViewHolder(view)
    }

    // Replace the contents of a view (invoked by the layout manager)
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(dataSet[position])
    }

    // Return the size of your dataset (invoked by the layout manager)
    override fun getItemCount() = dataSet.size

    /**
     * Provide a reference to the type of views that you are using
     * (custom ViewHolder)
     */
    inner class ViewHolder(private val view: LibraryCardBinding) : RecyclerView.ViewHolder(view.root) {
        fun getItem() = object : ItemDetailsLookup.ItemDetails<Long>() {
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

        fun bind(item: Series) {
            tracker.let { selector ->
                val color = getColor()
                if (selector.isSelected(item.uid)) {
                    view.root.setCardBackgroundColor(color.colorSurfaceVariant)
                } else view.root.setCardBackgroundColor(color.colorSurface)
            }
            view.chapterName.text = item.title
            view.root.setOnClickListener {
                CoroutineScope(Dispatchers.IO).launch {
                    seriesDao.update(item.copy(lastAccess = ZonedDateTime.now()))
                }
                val direction = LibraryFragmentDirections.actionLibraryToChapterList(item)
                navController.navigate(direction)
            }
            ImageLoader.setCoverImageFromImage(item.imageUri, view.image)
        }
    }

    class ItemsDetailsLookup(private val recyclerView: RecyclerView) : ItemDetailsLookup<Long>() {
        override fun getItemDetails(e: MotionEvent): ItemDetails<Long>? {
            val view = recyclerView.findChildViewUnder(e.x, e.y)
            return (view?.let { recyclerView.getChildViewHolder(it) } as ViewHolder?)?.getItem()
        }
    }

    class ItemsKeyProvider(private val adapter: LibraryAdapter, scope: Int) : ItemKeyProvider<Long>(
        scope) {
        override fun getKey(position: Int): Long = adapter.dataSet[position].uid
        override fun getPosition(key: Long): Int = adapter.dataSet.indexOfFirst { it.uid == key }
    }

}