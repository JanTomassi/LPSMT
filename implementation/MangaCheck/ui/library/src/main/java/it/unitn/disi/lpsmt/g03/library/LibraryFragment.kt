package it.unitn.disi.lpsmt.g03.library

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.GridView
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import it.unitn.disi.lpsmt.g03.appdatabase.AppDatabase
import it.unitn.disi.lpsmt.g03.library.databinding.LibraryLayoutBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class LibraryFragment : Fragment() {

    private lateinit var seriesGRV: GridView
    private var _binding: LibraryLayoutBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!
    private val db: AppDatabase.AppDatabaseInstance by lazy { AppDatabase.getInstance(requireContext()) }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = LibraryLayoutBinding.inflate(inflater, container, false)
        // initializing variables of grid view with their ids.
        seriesGRV = binding.libraryGridView

        binding.addButton.setOnClickListener {
            it.findNavController().navigate(R.id.action_libraryFragment_to_series_series)
        }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        CoroutineScope(Dispatchers.IO).launch {
            val chapters: List<Chapter>
            withContext(Dispatchers.IO) {
                chapters = db.chapterDao().getAll()
            }
            withContext(Dispatchers.Main) {
                populateLibrary(chapters)
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    // Empty the grid view and parse the xml to repopulate the view
    private fun populateLibrary(chapters: List<Chapter>) {

        CoroutineScope(Dispatchers.IO).launch {
//            val librariesTuples: List<LibraryEntry> = XMLParser(requireContext()).parse()

            // Create the subfolder to store the chapters for every library
//            for (element in librariesTuples.iterator()) {
//                ManageFiles(requireContext()).createLibraryFolder(element)
//            }

            withContext(Dispatchers.Main) {
                seriesGRV.emptyView

//                val libraryAdapter = LibraryAdapter(librariesTuples, this@LibraryFragment)
//                seriesGRV.adapter = libraryAdapter
//                if (libraryAdapter.count > 0) binding.helpMsg.visibility = View.GONE
            }
        }

    }
}