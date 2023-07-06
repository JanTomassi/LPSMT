package it.unitn.disi.lpsmt.g03.ui.tracker.search

import android.net.Uri
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import it.unitn.disi.lpsmt.g03.core.databinding.SeriesSearchSelectorBinding
import it.unitn.disi.lpsmt.g03.data.graphql.SearchByNameQuery

/**
 * RecyclerView that manage the query result as a list of entry with only the name
 */
class QueryAdapter(private val model: SeriesSearchModel, private val resultAction: () -> Unit) :
        RecyclerView.Adapter<QueryAdapter.ViewHolder>() {
    private var dataSet = List<SearchByNameQuery.Medium?>(0) { null }

    data class ViewHolder(val view: SeriesSearchSelectorBinding) :
            RecyclerView.ViewHolder(view.root)

    fun updateData(newData: List<SearchByNameQuery.Medium?>) {
        dataSet = newData
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = SeriesSearchSelectorBinding.inflate(LayoutInflater.from(parent.context))
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return dataSet.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val view = holder.view

        val englishTitle = dataSet[position]?.title?.english
        val romajiTitle = dataSet[position]?.title?.romaji
        val nativeTitle = dataSet[position]?.title?.native

        val title = englishTitle ?: romajiTitle ?: nativeTitle
        val description = dataSet[position]?.description
        val chapters = dataSet[position]?.chapters
        val imageUrl = dataSet[position]?.coverImage?.large

        view.containerMangaName.text = title

        Glide.with(view.root).load(imageUrl).circleCrop().into(view.mangaCover)

        view.containerMangaName.isClickable = false

        setContainerClickListener(view, title, description, chapters, imageUrl)
    }

    private fun setContainerClickListener(
            view: SeriesSearchSelectorBinding,
            title: String?,
            description: String?,
            chapters: Int?,
            imageUrl: String?
    ) {
        view.container.setOnClickListener {
            model.title.value = title
            model.description.value = description
            model.chapters.value = chapters
            model.imageUri.value = Uri.parse(imageUrl)
            resultAction()
        }
    }
}