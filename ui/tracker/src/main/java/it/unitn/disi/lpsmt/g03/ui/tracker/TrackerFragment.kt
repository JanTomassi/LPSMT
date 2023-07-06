package it.unitn.disi.lpsmt.g03.ui.tracker

import android.os.Bundle
import android.util.Log
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

        val categoryAdapterList =
            createCategoryAdapter()

        val culo = AppDatabase.getInstance(context).trackerSeriesDao()
            .getAllByStatus(ReadingState.PLANNING)

        culo.observe(viewLifecycleOwner) {
            Log.v(TrackerAdapter::class.simpleName, it.toString())
        }

        trackerAdapter = TrackerAdapter(categoryAdapterList, requireContext())
        //trackerAdapter.notifyDataSetChanged()

        seriesGRV.apply {
            this.adapter = trackerAdapter
            this.layoutManager = LinearLayoutManager(requireContext())
        }

    }

    private fun createCategoryAdapter(): List<CategoryAdapter> {
        val adapters = mutableListOf<CategoryAdapter>()
        ReadingState.values().forEach { liveData ->
            adapters.add(
                CategoryAdapter(
                    requireContext(),
                    liveData,
                    Glide.with(this@TrackerFragment),
                    parentFragmentManager,
                    viewLifecycleOwner
                )
            )
        }
        return adapters
    }
}