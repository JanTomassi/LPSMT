package it.unitn.disi.lpsmt.g03.ui.tracker

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
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

class TrackerFragment : Fragment() {

    private lateinit var seriesGRV: RecyclerView
    private var _binding: TrackerLayoutBinding? = null
    private lateinit var trackerAdapter: TrackerAdapter
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

    private fun initUI() {
        CoroutineScope(Dispatchers.IO).launch {
            val queryList = queryDB()

            withContext(Dispatchers.Main) {
                val categoryAdapterList =
                    createCategoryAdapter(queryList)

                trackerAdapter = TrackerAdapter(categoryAdapterList, requireContext())
                trackerAdapter.notifyDataSetChanged()

//                readingList.observe(viewLifecycleOwner) { trackerAdapter.notifyDataSetChanged() }
//                planningList.observe(viewLifecycleOwner) { trackerAdapter.notifyDataSetChanged() }
//                completedList.observe(viewLifecycleOwner) { trackerAdapter.notifyDataSetChanged() }

                seriesGRV.apply {
                    this.adapter = trackerAdapter
                    this.layoutManager = LinearLayoutManager(requireContext())
                }
            }
        }
    }

    private fun createCategoryAdapter(query: List<LiveData<List<TrackerSeries>>>): List<CategoryAdapter> {
        val adapters = mutableListOf<CategoryAdapter>()
        query.forEachIndexed { index, liveData ->
            adapters.add(
                CategoryAdapter(
                    liveData,
                    ReadingState.values()[index].toString(),
                    Glide.with(this@TrackerFragment),
                    parentFragmentManager
                )
            )
        }
        return adapters
    }

    private fun queryDB(): List<LiveData<List<TrackerSeries>>> {
        val results = mutableListOf<LiveData<List<TrackerSeries>>>()
        ReadingState.values().forEach { readingState ->
            results.add(MutableLiveData<List<TrackerSeries>>().apply {
                postValue(
                    AppDatabase.getInstance(context).trackerSeriesDao()
                        .getAllByStatus(readingState)
                )
            })
        }
        return results
    }
}