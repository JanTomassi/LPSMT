package it.unitn.disi.lpsmt.g03.mangacheck.library

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.GridView
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import androidx.navigation.fragment.navArgs
import it.unitn.disi.lpsmt.g03.mangacheck.R
import it.unitn.disi.lpsmt.g03.mangacheck.databinding.LibraryLayoutBinding
import it.unitn.disi.lpsmt.g03.mangacheck.library.data.LibraryAdapter
import it.unitn.disi.lpsmt.g03.mangacheck.library.xml.XMLEncoder
import it.unitn.disi.lpsmt.g03.mangacheck.library.xml.XMLParser
import it.unitn.disi.lpsmt.g03.mangacheck.utils.fileManager.ManageFiles
import it.unitn.disi.lpsmt.g03.mangacheck.utils.xml.LibraryEntry
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File

class LibraryFragment : Fragment() {

    private val args: LibraryFragmentArgs by navArgs()

    private lateinit var seriesGRV: GridView
    private var _binding: LibraryLayoutBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = LibraryLayoutBinding.inflate(inflater, container, false)
        // initializing variables of grid view with their ids.
        seriesGRV = binding.libraryGridView

        val scope = CoroutineScope(Dispatchers.IO)

        scope.launch {
            ManageFiles(requireContext()).createImageCacheFolder()
            XMLEncoder(requireContext())
        }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val scope = CoroutineScope(Dispatchers.IO)

        scope.launch {
            testArgumentsAndWriteXML()
            withContext(Dispatchers.Main) {
                populateLibrary(requireContext())
            }
        }

        binding.addButton.setOnClickListener {
            it.findNavController().navigate(R.id.action_libraryFragment_to_addLibraryFragment)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    // Empty the grid view and parse the xml to repopulate the view
    private fun populateLibrary(context: Context) {
        val readingListFile =
            File(context.filesDir, requireContext().getString(R.string.library_XML))

        val scope = CoroutineScope(Dispatchers.IO)
        scope.launch {
            val librariesTuples: List<LibraryEntry> = XMLParser().parse(readingListFile)

            // Create the subfolder to store the chapters for every library
            for (element in librariesTuples.iterator()) {
                ManageFiles(requireContext()).createLibraryFolder(element)
            }

            withContext(Dispatchers.Main) {
                seriesGRV.emptyView

                val libraryAdapter = LibraryAdapter(librariesTuples, this@LibraryFragment)

                seriesGRV.adapter = libraryAdapter
            }
        }

    }

    // Test if the arguments are present if so create a new entry in the xml
    private fun testArgumentsAndWriteXML() {
        try {
            val libraryId: Int = args.libraryID
            val libraryName: String? = args.libraryTitle
            if (libraryName != null) {
                XMLEncoder(requireContext()).addEntry(
                    LibraryEntry(
                        libraryName,
                        libraryId
                    )
                )
                requireArguments().remove("libraryID")
                requireArguments().remove("libraryTitle")
            }
        } catch (e: Exception) {
            Log.v(LibraryFragment::class.simpleName, "Generate an empty home")
        }
    }

    // Function to implement the update and the refresh
    fun onDataReceived(library: LibraryEntry) {
        val scope = CoroutineScope(Dispatchers.IO)
        scope.launch {
            ManageFiles(requireContext()).deleteLibraryFolder(library)
            XMLEncoder(requireContext()).removeEntry(library)
            withContext(Dispatchers.Main) {
                populateLibrary(requireContext())
            }
        }
    }
}