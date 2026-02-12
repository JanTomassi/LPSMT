package it.unitn.disi.lpsmt.g03.ui.reader

import android.content.ContentResolver
import android.content.Context
import android.content.res.Resources
import android.util.TypedValue
import android.view.ViewGroup
import android.widget.ImageView
import androidx.annotation.ColorInt
import androidx.lifecycle.MutableLiveData
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.CircularProgressDrawable
import com.bumptech.glide.RequestManager
import it.unitn.disi.lpsmt.g03.core.ImageLoader
import it.unitn.disi.lpsmt.g03.core.copyToLocalStorage
import it.unitn.disi.lpsmt.g03.data.library.Chapter
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import java.util.zip.ZipEntry
import java.util.zip.ZipFile

class ReaderAdapter(private var chapter: Chapter,
    private val glide: RequestManager,
    private val context: Context,
    private val contentResolver: ContentResolver,
    progress: MutableLiveData<Int>) : RecyclerView.Adapter<ReaderAdapter.ViewHolder>() {
    private lateinit var zipFile: ZipFile
    private lateinit var zipEntries: List<ZipEntry>
    private val initJob: Job

    init {
        initJob = CoroutineScope(Dispatchers.IO).launch(Dispatchers.IO) {
            zipFile = ZipFile(chapter.file?.copyToLocalStorage(context, contentResolver, progress))
            zipEntries = zipFile.entries().toList().filter {
                !it.isDirectory and it.name.contains(".(png|jpeg|webp|gif|jpg)$".toRegex(RegexOption.IGNORE_CASE))
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ReaderAdapter.ViewHolder {
        val imageView = ImageView(parent.context)
        imageView.layoutParams = ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.MATCH_PARENT)
        return ViewHolder(imageView)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(position)
    }

    override fun getItemCount(): Int {
        return chapter.pages
    }

    override fun onViewRecycled(holder: ViewHolder) {
        super.onViewRecycled(holder)
        holder.setToLoading()
    }

    /*fun update(newChapter: Chapter) {
        val oldSize = chapter.pages
        val newSize = newChapter.pages
        chapter = newChapter
        notifyItemRangeChanged(0, max(oldSize, newSize))
    }*/

    inner class ViewHolder(private val view: ImageView) : RecyclerView.ViewHolder(view) {
        private lateinit var loadingJob: Job
        fun bind(page: Int) {
            if (this::loadingJob.isInitialized) loadingJob.cancel("New image to be loaded")
            loadingJob = CoroutineScope(Dispatchers.IO).launch {
                initJob.join()
                ImageLoader.setImageFromCbzFile(zipFile, zipEntries, view, page)
            }
        }

        fun setToLoading() {
            val circularProgressDrawable = CircularProgressDrawable(view.context).apply {
                strokeWidth = view.width / 30.0f
                centerRadius = view.width / 10.0f

                val typedValue = TypedValue()
                val theme: Resources.Theme = view.context.theme
                theme.resolveAttribute(com.google.android.material.R.attr.colorOutline,
                    typedValue,
                    true)
                @ColorInt val color = typedValue.data

                setColorSchemeColors(color)
            }

            circularProgressDrawable.start()
            glide.load(circularProgressDrawable).into(view)
        }
    }
}
