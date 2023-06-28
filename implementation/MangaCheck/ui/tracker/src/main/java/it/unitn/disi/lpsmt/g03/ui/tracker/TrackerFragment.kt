package it.unitn.disi.lpsmt.g03.ui.tracker

import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.view.ActionMode
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import it.unitn.disi.lpsmt.g03.appdatabase.AppDatabase
import it.unitn.disi.lpsmt.g03.tracking.ReadingState
import it.unitn.disi.lpsmt.g03.tracking.TrackerSeries
import it.unitn.disi.lpsmt.g03.ui.tracker.category.CategoryAdapter
import it.unitn.disi.lpsmt.g03.ui.tracker.databinding.TrackerLayoutBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class TrackerFragment : Fragment(), ActionMode.Callback {

    private lateinit var seriesGRV: RecyclerView
    private var _binding: TrackerLayoutBinding? = null
    //private var categoryArray : ArrayList<TrackerAdapter> = ArrayList()

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

            val readingList: List<TrackerSeries> =
                AppDatabase.getInstance(context).trackerSeriesDao().getAllByStatus(ReadingState.READING)

            val readingAdapter: RecyclerView.Adapter<CategoryAdapter.ViewHolder> = CategoryAdapter(
                readingList, ReadingState.READING.toString(), Glide.with(this@TrackerFragment), requireContext()
            )

            val planningList: List<TrackerSeries> =
                AppDatabase.getInstance(context).trackerSeriesDao().getAllByStatus(ReadingState.PLANNING)

            val planningAdapter: RecyclerView.Adapter<CategoryAdapter.ViewHolder> = CategoryAdapter(
                planningList, ReadingState.PLANNING.toString(), Glide.with(this@TrackerFragment), requireContext()
            )

            val completedList: List<TrackerSeries> =
                AppDatabase.getInstance(context).trackerSeriesDao().getAllByStatus(ReadingState.COMPLETED)

            val completedAdapter: RecyclerView.Adapter<CategoryAdapter.ViewHolder> = CategoryAdapter(
                completedList, ReadingState.COMPLETED.toString(), Glide.with(this@TrackerFragment), requireContext()
            )

            withContext(Dispatchers.Main) {
                val compositeAdapter = CompositeAdapter(listOf(readingAdapter, planningAdapter, completedAdapter))
                seriesGRV.adapter = compositeAdapter
                seriesGRV.layoutManager = LinearLayoutManager(context)
            }
        }
    }

    override fun onCreateActionMode(mode: ActionMode?, menu: Menu?): Boolean {
        mode?.menuInflater?.inflate(R.menu.menu_selection, menu)
        return true
    }

    override fun onPrepareActionMode(mode: ActionMode?, menu: Menu?): Boolean = true

    override fun onActionItemClicked(mode: ActionMode?, item: MenuItem?): Boolean {
//        when(item?.itemId){
//            R.id.action_delete -> {
//                val trackerAdapter = binding.trackerView.adapter as CompositeAdapter
//
//                val selected = trackerAdapter.dataSet.filter {
//                    tracker.selection.contains(it.uid)
//                }.toMutableList()
//            }
//        }
        return true
    }

    override fun onDestroyActionMode(mode: ActionMode?) {
//        tracker.clearSelection()
//        actionMode = null
//
//        val adapter = (binding.libraryView.adapter as CategoryAdapter)
//        binding.libraryView.adapter = adapter
    }
}