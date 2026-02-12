package it.unitn.disi.lpsmt.g03.mangacheck.add_library

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.util.Xml
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.Toast
import androidx.fragment.app.Fragment
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.statement.HttpResponse
import io.ktor.http.path
import it.unitn.disi.lpsmt.g03.mangacheck.R
import it.unitn.disi.lpsmt.g03.mangacheck.add_library.data.AddLibraryAdapter
import it.unitn.disi.lpsmt.g03.mangacheck.databinding.AddReadingSelectByNameBinding
import it.unitn.disi.lpsmt.g03.mangacheck.utils.xml.XMLEncoder
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import java.net.ConnectException

class AddLibraryFragment : Fragment() {

    private var _binding: AddReadingSelectByNameBinding? = null

    private lateinit var searchButton: Button
    private lateinit var textBox: EditText
    private lateinit var linearLayout: LinearLayout

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = AddReadingSelectByNameBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        createLibraryListXML(requireContext().getString(R.string.library_XML))

        testArgumentsAndWriteXML()

        searchButton = binding.submitButton
        textBox = binding.comicName
        linearLayout = binding.listsResultsQueryByName

        searchButton.setOnClickListener {
            // This trigger a waterfall of function
            // queryServerAndUpdateUI -> parsing -> updateUI
            linearLayout.post {
                queryServerAndUpdateUI()
            }
        }
    }

    private fun queryServerAndUpdateUI() {
        val client = HttpClient()
        lateinit var formattedResponse: Array<Array<String>>
        val scope = CoroutineScope(Dispatchers.IO)
        val ipAddr: String = this.requireContext().getString(R.string.ip_addr)
        val serverPort: Int = this.requireContext().getString(R.string.server_port).toInt()
        scope.launch {
            try {
                val response: HttpResponse = client.get {
                    url {
                        host = ipAddr
                        port = serverPort
                        path("search/${textBox.text}")
                    }
                }
                if (response.status.value in 200..299) {
                    formattedResponse = parsing(response.body())
                    updateUI(formattedResponse)
                } else {
                    withContext(Dispatchers.Main) {
                        toaster("Error ${response.status.value}")
                    }
                }

            } catch (e: ConnectException) {
                withContext(Dispatchers.Main) {
                    toaster("Connection Refused")
                }
            }
        }
    }

    // If the response is empty it creates a dummy button with ID -1 and an error as a text
    private fun updateUI(response: Array<Array<String>>) {
        linearLayout.post {
            linearLayout.removeAllViews()
            if (response.isNotEmpty()) {
                response.forEachIndexed { index, internalArray ->
                    val comicEntry = AddLibraryAdapter(
                        internalArray[1],
                        this@AddLibraryFragment.requireContext()
                    )
                    linearLayout.addView(comicEntry.getView(internalArray[0].toInt(), null, null))
                }
            } else {
                val comicEntry = AddLibraryAdapter(
                    "Manga doesn't exist",
                    this@AddLibraryFragment.requireContext()
                )
                linearLayout.addView(comicEntry.getView(-1, null, null))
                toaster("Manga doesn't exist")
            }
        }
    }

    // Give the response string of the query it divides it in a matrix of string.
    // Given a row x in [x][0] we have the string containing the manga id
    // and in [x][1] the name of the manga.
    private fun parsing(response: String): Array<Array<String>> {
        val regex = Regex(
            """\(((?:(\d+), |(".+?")|('.+?'))+)\)""" // Jan goes brrrrrrr
        )
        val matches: Sequence<MatchResult> = regex.findAll(response)
        if (matches.toList().isEmpty()) {
            return Array(0) { arrayOf("", "") }
        }
        val listOfValues: List<String> = splitOnIdAndName(matches)
        val formattedResponse: Array<Array<String>> = Array(listOfValues.size / 2) {
            arrayOf("", "")
        }
        var indexOfFormattedResponse = 0
        for (index in listOfValues.indices step 2) {
            formattedResponse[indexOfFormattedResponse][0] =
                listOfValues[index] //id
            formattedResponse[indexOfFormattedResponse][1] =
                listOfValues[index + 1].removePrefix(" ").removeSurrounding("\'")
                    .removeSurrounding("\"") //name
            indexOfFormattedResponse += 1
        }
        return formattedResponse
    }

    // Due to the manga
    // "Banished from the Hero's Party, I Decided to Live a Quiet Life in the Countryside"
    // Reimplemented the split on first comma
    private fun splitOnIdAndName(sequence: Sequence<MatchResult>): List<String> {
        val listToReturn: MutableList<String> = mutableListOf()
        for (item in sequence.iterator()) {
            val separation = item.groupValues[1].split(",", limit = 2)
            listToReturn.add(separation[0])
            listToReturn.add(separation[1])
        }
        return listToReturn
    }

    // Prepare a delicious Toast for you
    private fun toaster(msg: String) {
        Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
    }

    private fun testArgumentsAndWriteXML() {
        try {
            val libraryId: Int = requireArguments().getInt("libraryID")
            val libraryName: String? = requireArguments().getString("libraryTitle")
            val libraryImageBase64: String? = requireArguments().getString("mangaImage")
            if (libraryName != null &&  libraryImageBase64 != null) {
                XMLEncoder(requireContext()).addLibraryEntry(
                    libraryName,
                    libraryId,
                    libraryImageBase64,
                )
                requireArguments().remove("mangaID")
                requireArguments().remove("mangaTitle")
                requireArguments().remove("list")
                requireArguments().remove("mangaImage")
                requireArguments().remove("mangaDescription")
            }
        } catch (e: IllegalStateException) {
            Log.v(AddLibraryAdapter::class.simpleName, "Generate an empty home")
        }
    }

    // Instantiate the XML if it doesn't exist
    private fun createLibraryListXML(fileName: String) {

        val readingListFile = File(requireContext().filesDir, fileName)

        if (!readingListFile.exists()) {

            Log.v(AddLibraryFragment::class.simpleName, "The XMl file doesn't exist")

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
            AddLibraryFragment::class.simpleName,
            requireContext().applicationContext!!.openFileInput(fileName).bufferedReader()
                .readText()
        )
    }

}