package it.unitn.disi.lpsmt.g03.mangacheck.reading_list

import android.content.Context
import android.os.Bundle
import android.os.StrictMode
import android.os.StrictMode.ThreadPolicy
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
import it.unitn.disi.lpsmt.g03.mangacheck.reading_list.data.ReadingModal
import it.unitn.disi.lpsmt.g03.mangacheck.utils.xml.Entry
import it.unitn.disi.lpsmt.g03.mangacheck.utils.xml.XMLParser
import org.xmlpull.v1.XmlPullParser
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream


class ReadingListFragment: Fragment (R.layout.reading_list_layout){

    private var readingList : ArrayList<ReadingModal> = ArrayList(4)
    private var planningList : ArrayList<ReadingModal> = ArrayList()
    private var completedList : ArrayList<ReadingModal> = ArrayList()
    private var fileReadingListXML : String = "readingList.xml"

    lateinit var containerReading : LinearLayout
    lateinit var containerPlanning : LinearLayout
    lateinit var containerCompleted : LinearLayout
    lateinit var addButton : Button

    private var _binding: ReadingListLayoutBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        super.onCreateView(inflater, container, savedInstanceState)
        _binding = ReadingListLayoutBinding.inflate(inflater, container, false)

        val policy = ThreadPolicy.Builder().permitAll().build()
        StrictMode.setThreadPolicy(policy)

        containerReading = binding.readingList.readingContainer
        containerPlanning = binding.planningList.readingContainer
        containerCompleted = binding.completedList.readingContainer
        addButton = binding.addButton

        createReadingListXML(fileReadingListXML)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val comicsListTuples : List<Entry> = readReadingListXML(fileReadingListXML)

        val readingListAdapter = ReadingAdapter(comicsListTuples,this@ReadingListFragment.requireContext())
        val planningListAdapter = ReadingAdapter(comicsListTuples,this@ReadingListFragment.requireContext())
        val completedListAdapter = ReadingAdapter(comicsListTuples,this@ReadingListFragment.requireContext())

        // Populate every lists depending on entry.list
        comicsListTuples.forEachIndexed { index, entry ->
            when(entry.list){
                "reading_list" -> containerReading.addView(readingListAdapter.getView(index,null,null))
                "planning_list" -> containerPlanning.addView(planningListAdapter.getView(index,null,null))
                "completed_list" -> containerCompleted.addView(completedListAdapter.getView(index,null,null))
                else -> {
                    Log.e("Malformed XML","The element in position $index is malformed")
                }
            }
        }

        addButton.setOnClickListener {
            it.findNavController().navigate(R.id.action_readingListFragment_to_addReadingFragment)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding=null
    }

    // Instantiate the XML if empty
    private fun createReadingListXML(fileName : String) : Unit {
        val readingListFile : File = File(fileName)
        if (! readingListFile.exists()){
//            Log.e("Creo il file di lista", "Creazione in corso")
            val outputFile: FileOutputStream? = context?.applicationContext!!.openFileOutput(fileName, Context.MODE_PRIVATE)

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

            outputFile!!.close()
        }
//        Log.e("File finito", context?.applicationContext!!.openFileInput(fileName).bufferedReader().readLine())
    }

    private fun readReadingListXML( fileName: String) : List <Entry> {
        val readingListFile : FileInputStream = context?.applicationContext!!.openFileInput(fileName)
        return XMLParser().parse(readingListFile)
    }
}