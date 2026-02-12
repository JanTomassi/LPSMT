package it.unitn.disi.lpsmt.g03.ui.library.chapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.navigation.NavController
import androidx.recyclerview.selection.ItemDetailsLookup
import com.bumptech.glide.RequestManager
import it.unitn.disi.lpsmt.g03.core.ImageLoader
import it.unitn.disi.lpsmt.g03.data.library.Chapter
import it.unitn.disi.lpsmt.g03.ui.library.common.CustomAdapter
import it.unitn.disi.lpsmt.g03.ui.library.databinding.ChapterListCardBinding
import java.lang.Integer.max

internal class ChapterListAdapter(dataSet: List<Chapter>,
    private val glide: RequestManager,
    private val context: Context,
    private val navController: NavController) : CustomAdapter<ChapterListCardBinding, Chapter, Long>(
    dataSet) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = ChapterListCardBinding.inflate(LayoutInflater.from(parent.context))
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: CustomAdapter<ChapterListCardBinding, Chapter, Long>.ViewHolder,
        position: Int) {
        holder.bind(dataSet[position])
    }

    override fun getItemCount(): Int = dataSet.size

    override fun update(list: List<Chapter>) {
        val oldSize: Int = dataSet.size
        val newSize: Int = list.size
        dataSet = list
        notifyItemRangeChanged(0, max(oldSize, newSize))
    }

    inner class ViewHolder(view: ChapterListCardBinding) : CustomAdapter<ChapterListCardBinding, Chapter, Long>.ViewHolder(
        view) {
        override fun getItem(): ItemDetailsLookup.ItemDetails<Long> = object : ItemDetailsLookup.ItemDetails<Long>() {
            override fun getPosition(): Int = bindingAdapterPosition
            override fun getSelectionKey(): Long = dataSet[bindingAdapterPosition].uid
        }

        override fun bind(item: Chapter) {
            view.text.text = item.chapter
            view.chapterNum.text = item.chapterNum.toString()
            ImageLoader.setCoverImageFromCbz(item.file, context.contentResolver, glide, view.image)
            view.root.setOnClickListener {
                val bundle = bundleOf("chapter" to item)
                val direction = ChapterListFragmentDirections.actionChapterListToReaderNav()
                direction.arguments.putAll(bundle)
                navController.navigate(direction)
            }
        }
    }
}