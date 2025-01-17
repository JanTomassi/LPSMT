package it.unitn.disi.lpsmt.g03.ui.library.chapter

//import it.unitn.disi.lpsmt.g03.ui.library.common.CustomAdapter
import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.navigation.NavController
import androidx.recyclerview.selection.ItemDetailsLookup
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
        liveDataset = myLibraryAdapterEntryPoint.provideChapterDao().getWhereSeriesId(seriesId)
        liveDataset.observe(lifecycleOwner) {
            val maxSize = max(dataSet.size, it.size)
            dataSet = it
            notifyItemRangeChanged(0, maxSize)
        }
        db = myLibraryAdapterEntryPoint.provideAppDatabase()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = ChapterListCardBinding.inflate(LayoutInflater.from(parent.context),
            parent,
            false)
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

        fun bind(item: Chapter) {
            view.text.text = item.chapter
            view.chapterNum.text = item.chapterNum.toString()
            CoroutineScope(Dispatchers.IO).launch {
                ImageLoader.setImageFromCbzUri(item.file,
                    context.contentResolver,
                    view.image)
            }
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
}