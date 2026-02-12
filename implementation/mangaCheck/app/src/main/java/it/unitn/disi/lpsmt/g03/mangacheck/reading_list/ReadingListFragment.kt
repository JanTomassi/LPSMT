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
import androidx.navigation.findNavController
import it.unitn.disi.lpsmt.g03.mangacheck.R
import it.unitn.disi.lpsmt.g03.mangacheck.databinding.ReadingListLayoutBinding
import it.unitn.disi.lpsmt.g03.mangacheck.reading_list.data.ReadingAdapter
import it.unitn.disi.lpsmt.g03.mangacheck.utils.xml.Entry
import it.unitn.disi.lpsmt.g03.mangacheck.utils.xml.XMLParser
import it.unitn.disi.lpsmt.g03.mangacheck.utils.xml.XMLEncoder
import java.io.File
import java.io.FileOutputStream


class ReadingListFragment : Fragment(R.layout.reading_list_layout) {


    private lateinit var fileReadingListXML: String

    private lateinit var containerReading: LinearLayout
    private lateinit var containerPlanning: LinearLayout
    private lateinit var containerCompleted: LinearLayout
    private lateinit var addButton: Button


    private var _binding: ReadingListLayoutBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        super.onCreateView(inflater, container, savedInstanceState)
        _binding = ReadingListLayoutBinding.inflate(inflater, container, false)

        fileReadingListXML = requireContext().getString(R.string.XML_file)
        containerReading = binding.readingList.readingContainer
        containerPlanning = binding.planningList.readingContainer
        containerCompleted = binding.completedList.readingContainer
        addButton = binding.addButton

        createReadingListXML(fileReadingListXML)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        containerReading.removeAllViews()
        containerPlanning.removeAllViews()
        containerCompleted.removeAllViews()

        testArgumentsAndWriteXML()

        val readingListFile: File =
            File(requireContext().filesDir, requireContext().getString(R.string.XML_file))

        val comicsListTuples: List<Entry> = XMLParser().testParse(readingListFile)

        val readingListAdapter =
            ReadingAdapter(comicsListTuples, this@ReadingListFragment.requireContext())
        val planningListAdapter =
            ReadingAdapter(comicsListTuples, this@ReadingListFragment.requireContext())
        val completedListAdapter =
            ReadingAdapter(comicsListTuples, this@ReadingListFragment.requireContext())

        // Populate every lists depending on entry.list
        comicsListTuples.forEachIndexed { index, entry ->
            when (entry.list) {
                "reading_list" -> containerReading.addView(
                    readingListAdapter.getView(
                        index,
                        null,
                        null
                    )
                )

                "planning_list" -> containerPlanning.addView(
                    planningListAdapter.getView(
                        index,
                        null,
                        null
                    )
                )

                "completed_list" -> containerCompleted.addView(
                    completedListAdapter.getView(
                        index,
                        null,
                        null
                    )
                )

                else -> {
                    Log.e("Malformed XML", "The element in position $index is malformed")
                }
            }
        }

        addButton.setOnClickListener {
            it.findNavController().navigate(R.id.action_readingListFragment_to_addReadingFragment)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun testArgumentsAndWriteXML() {
        try {
            val mangaId: Int = requireArguments().getInt("mangaID")
            val mangaName: String? = requireArguments().getString("mangaTitle")
            val mangaList: String? = requireArguments().getString("list")
            val mangaImageBase64: String? = requireArguments().getString("mangaImage")
            if(mangaId != null && mangaName != null && mangaList != null && mangaImageBase64 != null) {
                XMLEncoder(requireContext()).addEntry(
                    mangaId,
                    mangaName,
                    mangaList,
                    mangaImageBase64
                )
                requireArguments().remove("mangaID")
                requireArguments().remove("mangaTitle")
                requireArguments().remove("list")
                requireArguments().remove("mangaImage")
            }
        } catch (e: IllegalStateException) {
            Log.v(ReadingListFragment::class.simpleName, "Not from add reading")
        }
    }

    // Instantiate the XML if empty
    private fun createReadingListXML(fileName: String): Unit {

        val readingListFile: File = File(requireContext().filesDir, fileName)

        if (!readingListFile.exists()) {

            Log.v(ReadingListFragment::class.simpleName, "The XMl file doesn't exist")

            val outputFile: FileOutputStream =
                requireContext().openFileOutput(fileName, Context.MODE_PRIVATE)

            val serializer = Xml.newSerializer()
            serializer.setOutput(outputFile, "UTF-8")
            serializer.startDocument("UTF-8", true)

            serializer.startTag(null, "lists")

            serializer.startTag(null, "reading_list")
            serializer.endTag(null, "reading_list")

            serializer.startTag(null, "planning_list")
            serializer.endTag(null, "planning_list")

            serializer.startTag(null, "completed_list")
            serializer.endTag(null, "completed_list")

            serializer.endTag(null, "lists")

            serializer.endDocument()
            serializer.flush()

            outputFile.flush()
            outputFile.close()
        }
        Log.e(
            ReadingListFragment::class.simpleName,
            context?.applicationContext!!.openFileInput(fileName).bufferedReader().readText()
        )
    }
}