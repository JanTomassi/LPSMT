package it.unitn.disi.lpsmt.g03.ui.tracker

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.view.ActionMode
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import dagger.hilt.android.AndroidEntryPoint
import it.unitn.disi.lpsmt.g03.data.appdatabase.AppDatabase
import it.unitn.disi.lpsmt.g03.data.library.ReadingState
import it.unitn.disi.lpsmt.g03.data.library.Series
import it.unitn.disi.lpsmt.g03.ui.tracker.category.CategoryAdapter
import it.unitn.disi.lpsmt.g03.ui.tracker.databinding.TrackerLayoutBinding
import javax.inject.Inject

@AndroidEntryPoint
class TrackerFragment : Fragment() {

    private lateinit var trackerRV: RecyclerView
    private var _binding: TrackerLayoutBinding? = null
    private lateinit var trackerAdapter: TrackerAdapter
    private val binding get() = _binding!!

    @Inject
    lateinit var db: AppDatabase.AppDatabaseInstance
    private val selectionManager = SelectionManager(null,null)

    override fun onCreateView(inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?): View {
        _binding = TrackerLayoutBinding.inflate(inflater, container, false)

        trackerRV = binding.trackerView

        binding.addButton.setOnClickListener {
            findNavController().navigate(R.id.action_tracker_to_seriesSearch)
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
        val categoryAdapterList = createCategoryAdapter()
        trackerAdapter = TrackerAdapter(categoryAdapterList, requireContext())

        trackerRV.apply {
            this.adapter = trackerAdapter
            this.layoutManager = LinearLayoutManager(requireContext())
        }
    }

    private fun createCategoryAdapter(): List<CategoryAdapter> {
        val adapters = mutableListOf<CategoryAdapter>()
        ReadingState.values().forEach { statusName ->
            adapters.add(CategoryAdapter(requireContext(),
                activity as AppCompatActivity,
                statusName,
                Glide.with(this@TrackerFragment),
                parentFragmentManager,
                viewLifecycleOwner,
                findNavController(),
                selectionManager))
        }
        return adapters
    }
}