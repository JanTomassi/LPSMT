package it.unitn.disi.lpsmt.g03.ui.library

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.selection.ItemDetailsLookup
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.bumptech.glide.RequestManager
import it.unitn.disi.lpsmt.g03.core.CbzLoadImage
import it.unitn.disi.lpsmt.g03.data.appdatabase.AppDatabase
import it.unitn.disi.lpsmt.g03.data.library.Chapter
import it.unitn.disi.lpsmt.g03.ui.library.databinding.ChapterListCardBinding
import it.unitn.disi.lpsmt.g03.ui.library.databinding.ChapterListLayoutBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.lang.Integer.max

class ChapterListFragment : Fragment() {
    private lateinit var mBinding: ChapterListLayoutBinding
    private val db: AppDatabase.AppDatabaseInstance by lazy { AppDatabase.getInstance(requireContext()) }
    private lateinit var glide: RequestManager
    private val args: ChapterListFragmentArgs by navArgs()
    private val navController: NavController by lazy { findNavController() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        glide = Glide.with(requireParentFragment())
        activity?.title = args.series.title
    }

    override fun onCreateView(inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?): View {
        super.onCreateView(inflater, container, savedInstanceState)
        mBinding = ChapterListLayoutBinding.inflate(inflater)
        return mBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initUI()
    }

    private fun initUI() {
        mBinding.chaptersView.adapter = ChapterListAdapter(emptyList(), glide, requireContext())
        mBinding.chaptersView.layoutManager = LinearLayoutManager(context)
        CoroutineScope(Dispatchers.IO).launch {
            val chapterList = db.chapterDao().getWhereSeriesIdSorted(args.series.uid)
            withContext(Dispatchers.Main) {
                (mBinding.chaptersView.adapter as ChapterListAdapter).update(chapterList)
            }
        }
        mBinding.addButton.setOnClickListener {
            navController.navigate(ChapterListFragmentDirections.actionChapterListToChapterAdd(args.series))
        }
    }

    private class ChapterListAdapter(dataSet: List<Chapter>,
        private val glide: RequestManager,
        private val context: Context) : CustomAdapter<ChapterListCardBinding, Chapter, Long>(dataSet) {
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
                CbzLoadImage.setCoverImage(item.file, context.contentResolver, glide, view.image)
            }

        }
    }
}