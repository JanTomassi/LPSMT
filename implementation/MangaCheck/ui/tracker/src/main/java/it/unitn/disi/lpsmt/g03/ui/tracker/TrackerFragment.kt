package it.unitn.disi.lpsmt.g03.ui.tracker

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import it.unitn.disi.lpsmt.g03.appdatabase.AppDatabase
import it.unitn.disi.lpsmt.g03.tracking.ReadingState
import it.unitn.disi.lpsmt.g03.ui.tracker.category.CategoryAdapter
import it.unitn.disi.lpsmt.g03.ui.tracker.databinding.TrackerLayoutBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class TrackerFragment : Fragment() {

    private lateinit var seriesGRV: RecyclerView
    private var _binding: TrackerLayoutBinding? = null
    private lateinit var trackerAdapter : TrackerAdapter

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = TrackerLayoutBinding.inflate(inflater, container, false)

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

    private fun updateView(){
        trackerAdapter.notifyDataSetChanged()
    }

    private fun initUI() {
        CoroutineScope(Dispatchers.IO).launch {

            val readingAdapter = CategoryAdapter(
                    AppDatabase.getInstance(context).trackerSeriesDao()
                            .getAllByStatus(ReadingState.READING),
                    ReadingState.READING.toString(),
                    Glide.with(this@TrackerFragment),
                    requireContext(),
                    ::updateView
            )

            val planningAdapter = CategoryAdapter(
                    AppDatabase.getInstance(context).trackerSeriesDao()
                            .getAllByStatus(ReadingState.PLANNING),
                    ReadingState.PLANNING.toString(),
                    Glide.with(this@TrackerFragment),
                    requireContext(),
                    ::updateView
            )

            val completedAdapter = CategoryAdapter(
                    AppDatabase.getInstance(context).trackerSeriesDao()
                            .getAllByStatus(ReadingState.COMPLETED),
                    ReadingState.COMPLETED.toString(),
                    Glide.with(this@TrackerFragment),
                    requireContext(),
                    ::updateView
            )

            withContext(Dispatchers.Main) {
                val listOfCategory = mutableListOf(readingAdapter, planningAdapter, completedAdapter)
                trackerAdapter = TrackerAdapter(listOfCategory)
                trackerAdapter.notifyDataSetChanged()
                seriesGRV.apply {
                    this.adapter = trackerAdapter
                    this.layoutManager = LinearLayoutManager(context)
                }
            }
        }
    }
}