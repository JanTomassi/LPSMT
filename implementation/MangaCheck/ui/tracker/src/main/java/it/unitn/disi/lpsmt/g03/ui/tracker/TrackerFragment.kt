package it.unitn.disi.lpsmt.g03.ui.tracker

import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.view.ActionMode
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import androidx.recyclerview.selection.SelectionPredicates
import androidx.recyclerview.selection.SelectionTracker
import androidx.recyclerview.selection.StorageStrategy
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import it.unitn.disi.lpsmt.g03.appdatabase.AppDatabase
import it.unitn.disi.lpsmt.g03.tracking.ReadingState
import it.unitn.disi.lpsmt.g03.ui.tracker.category.CategoryAdapter
import it.unitn.disi.lpsmt.g03.ui.tracker.databinding.TrackerLayoutBinding
import it.unitn.disi.lpsmt.g03.ui.tracker.selection.ItemsDetailsLookup
import it.unitn.disi.lpsmt.g03.ui.tracker.selection.ItemsKeyProvider
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class TrackerFragment : Fragment(), ActionMode.Callback {

    private lateinit var seriesGRV: RecyclerView
    private var _binding: TrackerLayoutBinding? = null
    private lateinit var seriesSelected: SelectionTracker<Long>
    private var actionMode: ActionMode? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = TrackerLayoutBinding.inflate(inflater, container, false)

        // initializing variables of the various container

        seriesGRV = binding.trackerView

        binding.addButton.setOnClickListener {
            it.findNavController().navigate(R.id.action_trackerFragment_to_seriesSearchFragment)
        }

        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initUI()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun initUI() {
        CoroutineScope(Dispatchers.IO).launch {

            val readingAdapter = CategoryAdapter(
                AppDatabase.getInstance(context).trackerSeriesDao()
                    .getAllByStatus(ReadingState.READING),
                ReadingState.READING.toString(),
                Glide.with(this@TrackerFragment),
                requireContext()
            )

            val planningAdapter = CategoryAdapter(
                AppDatabase.getInstance(context).trackerSeriesDao()
                    .getAllByStatus(ReadingState.PLANNING),
                ReadingState.PLANNING.toString(),
                Glide.with(this@TrackerFragment),
                requireContext()
            )

            val completedAdapter = CategoryAdapter(
                AppDatabase.getInstance(context).trackerSeriesDao()
                    .getAllByStatus(ReadingState.COMPLETED),
                ReadingState.COMPLETED.toString(),
                Glide.with(this@TrackerFragment),
                requireContext()
            )

            withContext(Dispatchers.Main) {
                val tmp = mutableListOf(readingAdapter, planningAdapter, completedAdapter)
                val trackerAdapter = TrackerAdapter(tmp)
                trackerAdapter.notifyDataSetChanged()
                seriesGRV.apply {
                    this.adapter = trackerAdapter
                    this.layoutManager = LinearLayoutManager(context)
                }
                seriesSelected = SelectionTracker.Builder(
                    "selectionItemForTracker",
                    binding.trackerView,
                    ItemsKeyProvider(trackerAdapter),
                    ItemsDetailsLookup(binding.trackerView),
                    StorageStrategy.createLongStorage()
                ).withSelectionPredicate(SelectionPredicates.createSelectAnything()).build()

                seriesSelected.addObserver(object : SelectionTracker.SelectionObserver<Long>() {
                    override fun onSelectionChanged() {
                        super.onSelectionChanged()

                        if (actionMode == null) {
                            val currentActivity = activity as AppCompatActivity
                            actionMode =
                                currentActivity.startSupportActionMode(this@TrackerFragment)
                        }
                        val items = seriesSelected.selection.size()
                        if (items > 0) {
                            actionMode?.title = "$items selected"
                        } else {
                            actionMode?.finish()
                        }
                    }
                })
            }
        }
    }

    override fun onCreateActionMode(mode: ActionMode?, menu: Menu?): Boolean {
        mode?.menuInflater?.inflate(R.menu.menu_selection, menu)
        return true
    }

    override fun onPrepareActionMode(mode: ActionMode?, menu: Menu?): Boolean = true

    override fun onActionItemClicked(mode: ActionMode?, item: MenuItem?): Boolean {
        return when (item?.itemId) {
            R.id.action_modify -> {
                val trackerAdapter = binding.trackerView.adapter as TrackerAdapter
                val selected = trackerAdapter.getEntryList().filter {
                    seriesSelected.selection.contains(it.uid)
                }.toMutableList()
                val newDataSet = trackerAdapter.getAdapters().toMutableList()
                //newDataSet.removeAll(selected.toSet())

                //trackerAdapter.update(newDataSet)
                actionMode?.finish()
                true
            }

            else -> true
        }
    }

    override fun onDestroyActionMode(mode: ActionMode?) {
//        tracker.clearSelection()
//        actionMode = null
//        val adapter = (binding.libraryView.adapter as CategoryAdapter)
//        binding.libraryView.adapter = adapter
    }
}