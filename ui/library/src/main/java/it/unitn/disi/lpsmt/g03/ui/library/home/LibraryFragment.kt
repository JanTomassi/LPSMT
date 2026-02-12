package it.unitn.disi.lpsmt.g03.ui.library.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.annotation.MainThread
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.view.ActionMode
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.selection.SelectionPredicates
import androidx.recyclerview.selection.SelectionTracker
import androidx.recyclerview.selection.StorageStrategy
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import it.unitn.disi.lpsmt.g03.data.appdatabase.AppDatabase
import it.unitn.disi.lpsmt.g03.data.library.Series
import it.unitn.disi.lpsmt.g03.ui.library.R
import it.unitn.disi.lpsmt.g03.ui.library.common.RecyclerViewGridDecoration
import it.unitn.disi.lpsmt.g03.ui.library.databinding.LibraryLayoutBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


class LibraryFragment : Fragment() {

    private lateinit var seriesGRV: RecyclerView
    private var _binding: LibraryLayoutBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!
    private val db: AppDatabase.AppDatabaseInstance by lazy {
        AppDatabase.getInstance(requireContext())
    }
    private var actionMode: ActionMode? = null
    private lateinit var tracker: SelectionTracker<Long>
    private val navController: NavController by lazy { findNavController() }

    override fun onCreateView(inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?): View {
        _binding = LibraryLayoutBinding.inflate(inflater,
            container,
            false) // initializing variables of grid view with their ids.
        seriesGRV = binding.libraryView
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initUI()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        actionMode?.finish()
        tracker.clearSelection()
        _binding = null
    }

    @MainThread
    private fun initUI() {
        val decoration = RecyclerViewGridDecoration(2, 16, true)
        val layoutManager = GridLayoutManager(context, 2)
        val adapter = LibraryAdapter(emptyList(), Glide.with(this@LibraryFragment), navController)
        seriesGRV.apply {
            this.addItemDecoration(decoration)
            this.layoutManager = layoutManager
            this.adapter = adapter
        }

        CoroutineScope(Dispatchers.IO).launch {
            val dataSet: List<Series> = AppDatabase.getInstance(context)
                .seriesDao()
                .getAllSortByLastAccess()
            withContext(Dispatchers.Main) {
                (seriesGRV.adapter as LibraryAdapter).update(dataSet)
            }
        }

        tracker = SelectionTracker.Builder("selectionItemForLibrary",
            binding.libraryView,
            LibraryAdapter.ItemsKeyProvider(adapter),
            LibraryAdapter.ItemsDetailsLookup(binding.libraryView),
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
        binding.addButton.setOnClickListener {
            navController.navigate(R.id.action_library_to_series_series)
        }
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
                    val libraryAdapter = binding.libraryView.adapter as LibraryAdapter
                    val selected: List<Series> = (libraryAdapter.dataSet.filter {
                        tracker.selection.contains(it.uid)
                    })
                    CoroutineScope(Dispatchers.IO).launch {
                        db.seriesDao().deleteAll(*selected.toTypedArray())
                    }
                    libraryAdapter.update(libraryAdapter.dataSet.toMutableList()
                        .apply { removeAll(selected) })
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