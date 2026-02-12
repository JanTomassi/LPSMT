package it.unitn.disi.lpsmt.g03.mangacheck.add_reading.by_name

import android.os.Bundle
import android.util.Log
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
import it.unitn.disi.lpsmt.g03.mangacheck.add_reading.by_name.data.ReadingByNameAdapter
import it.unitn.disi.lpsmt.g03.mangacheck.databinding.AddReadingSelectByNameBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.net.ConnectException

class AddReadingByNameFragment : Fragment(R.layout.add_reading_select_by_name) {
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

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        searchButton = binding.submitButton
        textBox = binding.comicName
        linearLayout = binding.listsResultsQueryByName

        searchButton.setOnClickListener {
            // This trigger a waterfall of function
            // queryServerAndUpdateUI -> parsing -> updateUI
            linearLayout.post { queryServerAndUpdateUI() }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    // Make a coroutine to query the server for all the manga that has the text
    // in the TextBox in the name.
    // If everything go all right it calls the parsing then the updateUI.
    // If the server return a state not in 200 <= state <= 299 it create a
    // toast with the error code.
    // If the server is unreachable it catches the exception and produce a toast.
    private fun queryServerAndUpdateUI() {
        val client: HttpClient = HttpClient()
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
        linearLayout.post(Runnable {
            linearLayout.removeAllViews()
            if (response.isNotEmpty()) {
                response.forEachIndexed { index, internalArray ->
                    val comicEntry = ReadingByNameAdapter(
                        internalArray[1],
                        this@AddReadingByNameFragment.requireContext()
                    )
                    linearLayout.addView(comicEntry.getView(internalArray[0].toInt(), null, null))
                }
            } else {
                val comicEntry = ReadingByNameAdapter(
                    "Manga doesn't exist",
                    this@AddReadingByNameFragment.requireContext()
                )
                linearLayout.addView(comicEntry.getView(-1, null, null))
                toaster("Manga doesn't exist")
            }
        })
    }

    // Give the response string of the query it divides it in a matrix of string.
    // Given a row x in [x][0] we have the string containing the manga id
    // and in [x][1] the name of the manga.
    companion object {
        private fun parsing(response: String): Array<Array<String>> {
            val regex = Regex(
                """\[((?:(\d+),?|("[^,]+?"),?)+)]""" // Jan goes brrrrrrr
            )
            val matches: Sequence<MatchResult> = regex.findAll(response)
            if (matches.toList().isEmpty()) {
                return Array(0) { arrayOf("", "") }
            }
            val stringOfRegexGroups: String = matches.map {
                it.groupValues[1]
            }.joinToString()
            val listOfValues: List<String> = stringOfRegexGroups.split(",")
            var formattedResponse: Array<Array<String>> = Array(listOfValues.size / 2) { it ->
                arrayOf("", "")
            }
            var indexOfFormattedResponse: Int = 0
            for (index in listOfValues.indices step 2) {
                formattedResponse[indexOfFormattedResponse][0] =
                    listOfValues[index].removePrefix(" ") //id
                formattedResponse[indexOfFormattedResponse][1] =
                    listOfValues[index + 1].removeSurrounding("\"") //name
                indexOfFormattedResponse += 1
            }
            return formattedResponse
        }
    }

    // Prepare a delicious Toast for you
    private fun toaster(msg : String) : Unit{
        Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
    }
}