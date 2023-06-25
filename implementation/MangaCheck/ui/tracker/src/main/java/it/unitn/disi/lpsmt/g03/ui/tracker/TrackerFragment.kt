package it.unitn.disi.lpsmt.g03.ui.tracker

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import androidx.recyclerview.widget.ConcatAdapter
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import it.unitn.disi.lpsmt.g03.appdatabase.AppDatabase
import it.unitn.disi.lpsmt.g03.tracking.ReadingState
import it.unitn.disi.lpsmt.g03.tracking.TrackerSeries
import it.unitn.disi.lpsmt.g03.ui.tracker.databinding.TrackerLayoutBinding
import it.unitn.disi.lpsmt.g03.ui.tracker.header.HeaderAdapter
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class TrackerFragment : Fragment() {

    private lateinit var seriesGRV: RecyclerView
    private var _binding: TrackerLayoutBinding? = null

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

            val layoutManager = GridLayoutManager(context, 1)

            val readingList: List<TrackerSeries> =
                AppDatabase.getInstance(context).trackerSeriesDao()
                    .getAllByStatus(ReadingState.READING)
            val readingHeader = HeaderAdapter(ReadingState.READING.toString())
            val readingAdapter = TrackerAdapter(readingList, Glide.with(this@TrackerFragment))

            withContext(Dispatchers.Main) {
                seriesGRV.apply {
                    this.adapter = ConcatAdapter(readingHeader, readingAdapter)
                    this.layoutManager = layoutManager
                }
            }

            val planningList: List<TrackerSeries> =
                AppDatabase.getInstance(context).trackerSeriesDao()
                    .getAllByStatus(ReadingState.PLANNING)
            val planningHeader = HeaderAdapter(ReadingState.PLANNING.toString())
            val planningAdapter = TrackerAdapter(planningList, Glide.with(this@TrackerFragment))

            withContext(Dispatchers.Main) {
                seriesGRV.apply {
                    this.adapter = ConcatAdapter(this.adapter, planningHeader, planningAdapter)
                    this.layoutManager = layoutManager
                }
            }

            val completedList: List<TrackerSeries> =
                AppDatabase.getInstance(context).trackerSeriesDao()
                    .getAllByStatus(ReadingState.COMPLETED)
            val completedHeader = HeaderAdapter(ReadingState.COMPLETED.toString())
            val completedAdapter = TrackerAdapter(completedList, Glide.with(this@TrackerFragment))

            withContext(Dispatchers.Main) {
                seriesGRV.apply {
                    this.adapter = ConcatAdapter(this.adapter, completedHeader, completedAdapter)
                    this.layoutManager = layoutManager
                }
            }
        }
    }
}