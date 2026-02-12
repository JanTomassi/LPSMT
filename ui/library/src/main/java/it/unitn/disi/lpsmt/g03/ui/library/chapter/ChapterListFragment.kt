package it.unitn.disi.lpsmt.g03.ui.library.chapter

import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.view.ActionMode
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.selection.ItemKeyProvider
import androidx.recyclerview.selection.SelectionPredicates
import androidx.recyclerview.selection.SelectionTracker
import androidx.recyclerview.selection.StorageStrategy
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.bumptech.glide.RequestManager
import dagger.hilt.android.AndroidEntryPoint
import it.unitn.disi.lpsmt.g03.data.appdatabase.AppDatabase
import it.unitn.disi.lpsmt.g03.data.library.Chapter
import it.unitn.disi.lpsmt.g03.ui.library.R
import it.unitn.disi.lpsmt.g03.ui.library.databinding.ChapterListLayoutBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class ChapterListFragment : Fragment() {
    private lateinit var mBinding: ChapterListLayoutBinding
    private lateinit var tracker: SelectionTracker<Long>
    private var actionMode: ActionMode? = null

    @Inject
    lateinit var db: AppDatabase.AppDatabaseInstance
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
        val adapter = ChapterListAdapter(requireContext(), navController, this, args.series.uid)
        mBinding.chaptersView.adapter = adapter
        mBinding.chaptersView.layoutManager = LinearLayoutManager(context)
        mBinding.addButton.setOnClickListener {
            navController.navigate(ChapterListFragmentDirections.actionChapterListToChapterAdd(args.series))
        }
        tracker = SelectionTracker.Builder("selectionItemForLibrary",
            mBinding.chaptersView,
            ChapterListAdapter.ItemsKeyProvider(adapter, ItemKeyProvider.SCOPE_MAPPED),
            ChapterListAdapter.ItemsDetailsLookup(mBinding.chaptersView),
            StorageStrategy.createLongStorage())
            .withSelectionPredicate(SelectionPredicates.createSelectAnything())
            .build()
        tracker.addObserver(object : SelectionTracker.SelectionObserver<Long>() {
            override fun onSelectionChanged() {
                super.onSelectionChanged()

                if (actionMode == null) {
                    val currentActivity = activity as AppCompatActivity
                    actionMode = currentActivity.startSupportActionMode(SelectionCallback())
                }
                val items = tracker.selection.size()
                if (items > 0) {
                    actionMode?.title = "$items selected"
                } else {
                    actionMode?.finish()
                }
            }
        })
        adapter.tracker = tracker
    }

    private inner class SelectionCallback : ActionMode.Callback {
        override fun onCreateActionMode(mode: ActionMode?, menu: Menu?): Boolean {
            mode?.menuInflater?.inflate(R.menu.menu_actions, menu)
            return true
        }

        override fun onPrepareActionMode(mode: ActionMode?, menu: Menu?): Boolean = true

        override fun onActionItemClicked(mode: ActionMode?, item: MenuItem?): Boolean {
            return when (item?.itemId) {
                R.id.action_delete -> {
                    val libraryAdapter = mBinding.chaptersView.adapter as ChapterListAdapter
                    val selected: List<Chapter> = (libraryAdapter.dataSet.filter {
                        tracker.selection.contains(it.uid)
                    })
                    CoroutineScope(Dispatchers.IO).launch {
                        db.chapterDao().deleteAll(*selected.toTypedArray())
                    }
                    actionMode?.finish()
                    true
                }

                else -> false
            }
        }

        override fun onDestroyActionMode(mode: ActionMode?) {
            tracker.clearSelection()
            actionMode = null
        }
    }

}