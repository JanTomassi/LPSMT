package it.unitn.disi.lpsmt.g03.ui.tracker

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
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


        binding.addButton.setOnClickListener {
            //it.findNavController().navigate(R.id.action_libraryFragment_to_series_series)
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
                AppDatabase.getInstance(context).trackerSeriesDao()
                    .getAllByStatus(ReadingState.READING)
            val readingHeader = HeaderAdapter()
            //readingHeader.modifyText(HeaderViewHolder(R.layout.container_header), "Reading")
            val readingAdapter = TrackerAdapter(readingList, Glide.with(this@TrackerFragment))

            val completedList: List<TrackerSeries> =
                AppDatabase.getInstance(context).trackerSeriesDao()
                    .getAllByStatus(ReadingState.COMPLETED)
            val completedAdapter = TrackerAdapter(readingList, Glide.with(this@TrackerFragment))

            val planningList: List<TrackerSeries> =
                AppDatabase.getInstance(context).trackerSeriesDao()
                    .getAllByStatus(ReadingState.PLANNING)
            val planningAdapter = TrackerAdapter(readingList, Glide.with(this@TrackerFragment))

        }
    }
}