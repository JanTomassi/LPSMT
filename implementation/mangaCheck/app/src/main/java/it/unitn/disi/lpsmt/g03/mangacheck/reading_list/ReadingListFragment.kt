package it.unitn.disi.lpsmt.g03.mangacheck.reading_list

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.util.Xml
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.LinearLayout
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.navigation.findNavController
import it.unitn.disi.lpsmt.g03.mangacheck.R
import it.unitn.disi.lpsmt.g03.mangacheck.databinding.ReadingListLayoutBinding
import it.unitn.disi.lpsmt.g03.mangacheck.reading_list.data.ReadingAdapter
import it.unitn.disi.lpsmt.g03.mangacheck.utils.xml.MangaEntry
import it.unitn.disi.lpsmt.g03.mangacheck.utils.xml.XMLEncoder
import it.unitn.disi.lpsmt.g03.mangacheck.utils.xml.XMLParser
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream


class ReadingListFragment : Fragment(R.layout.reading_list_layout) {

    private lateinit var fileReadingListXML: String

    private lateinit var containerReading: LinearLayout
    private lateinit var containerPlanning: LinearLayout
    private lateinit var containerCompleted: LinearLayout
    private lateinit var addButton: Button
    private lateinit var fragManager: FragmentManager

    private var _binding: ReadingListLayoutBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        super.onCreateView(inflater, container, savedInstanceState)
        _binding = ReadingListLayoutBinding.inflate(inflater, container, false)

        fileReadingListXML = requireContext().getString(R.string.XML_file)
        containerReading = binding.readingList.readingContainer
        containerPlanning = binding.planningList.readingContainer
        containerCompleted = binding.completedList.readingContainer
        addButton = binding.addButton

        binding.readingList.status.text = getString(R.string.reading_list)
        binding.planningList.status.text = getString(R.string.planning_list)
        binding.completedList.status.text = getString(R.string.completed_list)

        createReadingListXML(fileReadingListXML)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val scope = CoroutineScope(Dispatchers.IO)

        scope.launch {
            testArgumentsAndWriteXML()
            withContext(Dispatchers.Main){
                populateReadingContainers(requireContext())
            }
        }

        addButton.setOnClickListener {
            it.findNavController().navigate(R.id.action_readingListFragment_to_addReadingFragment)
        }
        fragManager = parentFragmentManager
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    // Empty and repopulate the comics lists
    private fun populateReadingContainers(context: Context) {
        val readingListFile = File(context.filesDir, requireContext().getString(R.string.XML_file))

        val scope = CoroutineScope(Dispatchers.IO)
        scope.launch {

            val comicsListTuples: List<MangaEntry> = XMLParser(requireContext()).parseComics(readingListFile)

            withContext(Dispatchers.Main) {

                containerReading.removeAllViews()
                containerPlanning.removeAllViews()
                containerCompleted.removeAllViews()

                val readingListAdapter = ReadingAdapter(comicsListTuples, this@ReadingListFragment)
                val planningListAdapter = ReadingAdapter(comicsListTuples, this@ReadingListFragment)
                val completedListAdapter =
                    ReadingAdapter(comicsListTuples, this@ReadingListFragment)

                // Populate every lists depending on entry.list
                comicsListTuples.forEachIndexed { index, entry ->
                    when (entry.list) {
                        "reading_list" -> containerReading.addView(
                            readingListAdapter.getView(
                                index, null, null
                            )
                        )


                        "planning_list" -> containerPlanning.addView(
                            planningListAdapter.getView(
                                index, null, null
                            )
                        )


                        "completed_list" -> containerCompleted.addView(
                            completedListAdapter.getView(
                                index, null, null
                            )
                        )


                        else -> {
                            Log.e(
                                ReadingListFragment::class.simpleName,
                                "The element in position $index is malformed"
                            )
                        }
                    }
                }
            }
        }
    }

    // Check if this fragment is reached trough AddReadingSetStatus
    // if it is so generate a new XML MangaEntry with the new Manga added
    // then flush the argument
    private fun testArgumentsAndWriteXML() {
        try {
            val mangaId: Int = requireArguments().getInt("mangaID")
            val mangaName: String? = requireArguments().getString("mangaTitle")
            val mangaList: String? = requireArguments().getString("list")
            val mangaDescription: String? = requireArguments().getString("mangaDescription")
            if (mangaName != null && mangaList != null && mangaDescription != null) {
                XMLEncoder(requireContext()).addMangaEntry(
                    mangaList, mangaName, mangaId, mangaDescription
                )
                requireArguments().remove("mangaID")
                requireArguments().remove("mangaTitle")
                requireArguments().remove("list")
                requireArguments().remove("mangaDescription")
            }
        } catch (e: IllegalStateException) {
            Log.v(ReadingListFragment::class.simpleName, "Not from add reading")
        }
    }

    // Instantiate the XML if it doesn't exist
    private fun createReadingListXML(fileName: String) {

        val readingListFile = File(requireContext().filesDir, fileName)

        if (!readingListFile.exists()) {

            val outputFile: FileOutputStream =
                requireContext().openFileOutput(fileName, Context.MODE_PRIVATE)

            val serializer = Xml.newSerializer()
            serializer.setOutput(outputFile, "UTF-8")
            serializer.startDocument("UTF-8", true)

            serializer.startTag(null, "comics")

            serializer.endTag(null, "comics")

            serializer.endDocument()
            serializer.flush()

            outputFile.flush()
            outputFile.close()
        }
    }

    // Function to implement the update and the refresh
    fun onDataReceived(comic: MangaEntry, newList: String) {
        val scope = CoroutineScope(Dispatchers.IO)
        scope.launch {
            XMLEncoder(requireContext()).modifyEntry(comic, newList)
            withContext(Dispatchers.Main) {
                populateReadingContainers(requireContext())
            }
        }
    }
}