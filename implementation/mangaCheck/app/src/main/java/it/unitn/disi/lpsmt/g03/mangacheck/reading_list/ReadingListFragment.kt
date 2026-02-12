package it.unitn.disi.lpsmt.g03.mangacheck.reading_list

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.LinearLayout
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import it.unitn.disi.lpsmt.g03.mangacheck.R
import it.unitn.disi.lpsmt.g03.mangacheck.databinding.ReadingListLayoutBinding
import it.unitn.disi.lpsmt.g03.mangacheck.reading_list.data.ReadingAdapter
import it.unitn.disi.lpsmt.g03.mangacheck.reading_list.data.ReadingModal

class ReadingListFragment: Fragment (R.layout.reading_list_layout){

    var readingList : ArrayList<ReadingModal> = ArrayList()
    var planningList: ArrayList<ReadingModal> = ArrayList()
    var completedList: ArrayList<ReadingModal> = ArrayList()

    lateinit var containerReading : LinearLayout
    lateinit var containerPlanning : LinearLayout
    lateinit var containerCompleted : LinearLayout
    lateinit var addButton : Button

    private var _binding: ReadingListLayoutBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        super.onCreateView(inflater, container, savedInstanceState)
        _binding = ReadingListLayoutBinding.inflate(inflater, container, false)

        containerReading = binding.readingList.readingContainer
        containerPlanning = binding.planningList.readingContainer
        containerCompleted = binding.completedList.readingContainer
        addButton = binding.addButton

        // REMOVABLE PART, TEST PURPOSE


        // Funny name has to change ASAP
        readingList.add(ReadingModal("Sex bomb", 2, 45))
        readingList.add(ReadingModal("Vampire sex", 5, 67))
        readingList.add(ReadingModal("Furry Sex", 56, 98))
        readingList.add(ReadingModal("Gay Sex", 10, 34))
        readingList.add(ReadingModal("<Jan> Sex", 999, 1000))

        planningList.add(ReadingModal("Sex bomb", 2, 45))
        planningList.add(ReadingModal("Vampire sex", 5, 67))
        planningList.add(ReadingModal("Furry Sex", 56, 98))
        planningList.add(ReadingModal("Gay Sex", 10, 34))
        planningList.add(ReadingModal("<Jan> Sex", 999, 1000))

        completedList.add(ReadingModal("Sex bomb", 2, 45))
        completedList.add(ReadingModal("Vampire sex", 5, 67))
        completedList.add(ReadingModal("Furry Sex", 56, 98))
        completedList.add(ReadingModal("Gay Sex", 10, 34))
        completedList.add(ReadingModal("<Jan> Sex", 999, 1000))

        // END REMOVABLE PART

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val readingListAdapter = ReadingAdapter(readingList,this@ReadingListFragment.requireContext())
        val planningListAdapter = ReadingAdapter(planningList,this@ReadingListFragment.requireContext())
        val complatedListAdapter = ReadingAdapter(completedList,this@ReadingListFragment.requireContext())


        // Insert in every container a fake comic one for every container
        for (i in 0 until readingList.size){
            containerReading.addView(readingListAdapter.getView(i,null,null))
            containerPlanning.addView(planningListAdapter.getView(i,null,null))
            containerCompleted.addView(complatedListAdapter.getView(i,null,null))
        }

        addButton.setOnClickListener {
            it.findNavController().navigate(R.id.action_readingListFragment_to_addReadingFragment)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding=null
    }
}