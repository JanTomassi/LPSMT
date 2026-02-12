package it.unitn.disi.lpsmt.g03.ui.reader

import android.content.ContentResolver
import android.net.Uri
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.RequestManager
import it.unitn.disi.lpsmt.g03.core.ImageLoader
import it.unitn.disi.lpsmt.g03.data.library.Chapter
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import kotlin.math.max

class ReaderAdapter(
    private var cbzMetadata: CbzMetadata,
    private val glide: RequestManager,
    private val contentResolver: ContentResolver,
) : RecyclerView.Adapter<ReaderAdapter.ViewHolder>() {
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
        return cbzMetadata.pages
    }

    fun update(newChapter: Chapter) {
        val newSize = ImageLoader.getPagesInCbz(newChapter.file, contentResolver)
        cbzMetadata = CbzMetadata(newChapter.file, newSize)
        notifyItemRangeChanged(0, max(newSize, cbzMetadata.pages))

    }

    data class CbzMetadata(val uri: Uri?, val pages: Int)

    inner class ViewHolder(private val view: ImageView) : RecyclerView.ViewHolder(view) {
        private lateinit var loadingJob: Job
        fun bind(page: Int) {
            if (this::loadingJob.isInitialized) loadingJob.cancel("New image to be loaded")
            loadingJob = CoroutineScope(Dispatchers.IO).launch {
                ImageLoader.setImageFromCbz(cbzMetadata.uri, contentResolver, glide, view, page)
            }
        }
    }
}
