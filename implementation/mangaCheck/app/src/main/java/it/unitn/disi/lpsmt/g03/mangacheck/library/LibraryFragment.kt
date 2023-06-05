package it.unitn.disi.lpsmt.g03.mangacheck.library

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.util.Xml
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.GridView
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import it.unitn.disi.lpsmt.g03.mangacheck.R
import it.unitn.disi.lpsmt.g03.mangacheck.databinding.LibraryLayoutBinding
import it.unitn.disi.lpsmt.g03.mangacheck.library.data.LibraryAdapter
import it.unitn.disi.lpsmt.g03.mangacheck.utils.xml.LibraryEntry
import it.unitn.disi.lpsmt.g03.mangacheck.utils.xml.XMLEncoder
import it.unitn.disi.lpsmt.g03.mangacheck.utils.xml.XMLParser
import java.io.File
import java.io.FileOutputStream

class LibraryFragment : Fragment() {

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

        createLibraryListXML(requireContext().getString(R.string.library_XML))

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        testArgumentsAndWriteXML()

        populateLibrary(requireContext())

        binding.addButton.setOnClickListener {
            it.findNavController().navigate(R.id.action_libraryFragment_to_addLibraryFragment)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    // Empty the grid view and parse the xml to ripopulate the view
    private fun populateLibrary(context: Context){
        seriesGRV.emptyView

        val readingListFile =
            File(context.filesDir, requireContext().getString(R.string.library_XML))

        val librariesTuples: List<LibraryEntry> = XMLParser().parseLibrary(readingListFile)

        val libraryAdapter = LibraryAdapter(librariesTuples, requireContext())

        seriesGRV.adapter = libraryAdapter

    }

    // Instantiate the XML if it doesn't exist
    private fun createLibraryListXML(fileName: String) {

        val readingListFile = File(requireContext().filesDir, fileName)

        if (!readingListFile.exists()) {

            Log.e(LibraryFragment::class.simpleName, "The XMl file doesn't exist")

            val outputFile: FileOutputStream =
                requireContext().openFileOutput(fileName, Context.MODE_PRIVATE)

            val serializer = Xml.newSerializer()
            serializer.setOutput(outputFile, "UTF-8")
            serializer.startDocument("UTF-8", true)

            serializer.startTag(null, "libraries")

            serializer.endTag(null, "libraries")

            serializer.endDocument()
            serializer.flush()

            outputFile.flush()
            outputFile.close()
        }
        Log.e(
            LibraryFragment::class.simpleName,
            requireContext().applicationContext!!.openFileInput(fileName).bufferedReader()
                .readText()
        )
    }

    // Test if the arguments are present if so create a new entry in the xml
    private fun testArgumentsAndWriteXML() {
        try {
            val libraryId: Int = requireArguments().getInt("libraryID")
            val libraryName: String? = requireArguments().getString("libraryTitle")
            val libraryImageBase64: String? = requireArguments().getString("libraryImage")
            if (libraryName != null &&  libraryImageBase64 != null) {
                XMLEncoder(requireContext()).addLibraryEntry(
                    libraryName,
                    libraryId,
                    libraryImageBase64,
                )
                requireArguments().remove("libraryID")
                requireArguments().remove("libraryTitle")
                requireArguments().remove("libraryImage")
            }
        } catch (e: IllegalStateException) {
            Log.v(LibraryFragment::class.simpleName, "Generate an empty home")
        }
    }
}