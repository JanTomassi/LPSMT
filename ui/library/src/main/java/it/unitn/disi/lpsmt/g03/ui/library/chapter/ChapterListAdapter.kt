package it.unitn.disi.lpsmt.g03.ui.library.chapter

import android.content.Context
import android.content.res.TypedArray
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
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
import it.unitn.disi.lpsmt.g03.data.appdatabase.AppDatabase
import it.unitn.disi.lpsmt.g03.data.library.Chapter
import it.unitn.disi.lpsmt.g03.data.library.ChapterDao
import it.unitn.disi.lpsmt.g03.ui.library.databinding.ChapterListCardBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.lang.Integer.max

internal class ChapterListAdapter(private val context: Context,
    private val navController: NavController,
    lifecycleOwner: LifecycleOwner,
    private val seriesId: Long) : RecyclerView.Adapter<ChapterListAdapter.ViewHolder>() {

    @EntryPoint
    @InstallIn(SingletonComponent::class)
    interface ChapterListAdapterEntryPoint {
        fun provideAppDatabase(): AppDatabase.AppDatabaseInstance
        fun provideChapterDao(): ChapterDao
    }

    var db: AppDatabase.AppDatabaseInstance
    var liveDataset: LiveData<List<Chapter>>
    var dataSet: List<Chapter> = emptyList()
    lateinit var tracker: SelectionTracker<Long>

    init {
        val myLibraryAdapterEntryPoint = EntryPointAccessors.fromApplication(context,
            ChapterListAdapterEntryPoint::class.java)
        liveDataset = myLibraryAdapterEntryPoint.provideChapterDao().getAllSorted()
        liveDataset.observe(lifecycleOwner) {
            val maxSize = max(dataSet.size, it.size)
            dataSet = it
            notifyItemRangeChanged(0, maxSize)
        }
        db = myLibraryAdapterEntryPoint.provideAppDatabase()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = ChapterListCardBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(dataSet[position])
    }

    override fun getItemCount(): Int = dataSet.size

    inner class ViewHolder(private val view: ChapterListCardBinding) : RecyclerView.ViewHolder(view.root) {
        fun getItem(): ItemDetailsLookup.ItemDetails<Long> = object : ItemDetailsLookup.ItemDetails<Long>() {
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

        fun bind(item: Chapter) {
            tracker.let { selector ->
                val color = getColor()
                if (selector.isSelected(item.uid)) {
                    view.root.setCardBackgroundColor(color.colorSurfaceVariant)
                } else view.root.setCardBackgroundColor(color.colorSurface)
            }
            view.chapterName.text = item.chapter
            view.chapterNum.text = item.chapterNum.toString()
            CoroutineScope(Dispatchers.IO).launch {
                ImageLoader.setImageFromCbzUri(item.file, context.contentResolver, view.image)
            }
            view.progress.max = item.pages
            view.progress.progress = item.currentPage
            view.root.setOnClickListener {
                CoroutineScope(Dispatchers.IO).launch {
                    db.seriesDao().updateLastChapter(seriesId, item.chapterNum)
                }
                val bundle = bundleOf("chapter" to item)
                val direction = ChapterListFragmentDirections.actionChapterListToReaderNav()
                direction.arguments.putAll(bundle)
                navController.navigate(direction)
            }
        }
    }

    class ItemsDetailsLookup(private val recyclerView: RecyclerView) : ItemDetailsLookup<Long>() {
        override fun getItemDetails(e: MotionEvent): ItemDetails<Long>? {
            val view = recyclerView.findChildViewUnder(e.x, e.y)
            return (view?.let { recyclerView.getChildViewHolder(it) } as ViewHolder?)?.getItem()
        }
    }

    class ItemsKeyProvider(private val adapter: ChapterListAdapter,
        scope: Int) : ItemKeyProvider<Long>(scope) {
        override fun getKey(position: Int): Long = adapter.dataSet[position].uid
        override fun getPosition(key: Long): Int = adapter.dataSet.indexOfFirst { it.uid == key }
    }
}