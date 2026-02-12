package it.unitn.disi.lpsmt.g03.mangacheck.library

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.GridView
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import it.unitn.disi.lpsmt.g03.mangacheck.R
import it.unitn.disi.lpsmt.g03.mangacheck.library.data.LibraryAdapter
import it.unitn.disi.lpsmt.g03.mangacheck.library.data.LibraryModal
import it.unitn.disi.lpsmt.g03.mangacheck.databinding.LibraryLayoutBinding

class LibraryFragment: Fragment() {
    // on below line we are creating
    // variables for grid view and course list
    lateinit var courseGRV: GridView
    lateinit var courseList: ArrayList<LibraryModal>
    private var _binding: LibraryLayoutBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = LibraryLayoutBinding.inflate(inflater, container, false)
        // initializing variables of grid view with their ids.
        courseGRV = binding.view
        courseList = ArrayList()

        courseList.add(LibraryModal("Sex bomb", R.drawable.add_button))
        courseList.add(LibraryModal("Vampire sex", R.drawable.exit_icon))
        courseList.add(LibraryModal("Furry Sex", R.drawable.forward_icon_reader))
        courseList.add(LibraryModal("Gay Sex", R.drawable.ic_launcher_background))
        courseList.add(LibraryModal("<Jan> Sex", R.drawable.add_button))

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val libraryAdapter = LibraryAdapter(seriesList = courseList, this@LibraryFragment.requireContext())
        binding.view.adapter = libraryAdapter

        binding.addButton.setOnClickListener {
            it.findNavController().navigate(R.id.action_libraryFragment_to_addLibraryFragment)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}