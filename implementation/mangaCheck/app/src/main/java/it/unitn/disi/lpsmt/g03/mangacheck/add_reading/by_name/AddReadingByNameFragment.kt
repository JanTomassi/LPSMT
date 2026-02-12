package it.unitn.disi.lpsmt.g03.mangacheck.add_reading.by_name

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ScrollView
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.engine.cio.CIO
import io.ktor.client.plugins.websocket.WebSockets
import io.ktor.client.request.HttpResponseData
import io.ktor.client.request.get
import io.ktor.client.statement.HttpResponse
import io.ktor.http.ContentType
import io.ktor.http.HttpMethod
import io.ktor.http.charset
import io.ktor.http.contentType
import io.ktor.http.encodedPath
import io.ktor.http.path
import it.unitn.disi.lpsmt.g03.mangacheck.R
import it.unitn.disi.lpsmt.g03.mangacheck.add_reading.by_name.data.ReadingByNameAdapter
import it.unitn.disi.lpsmt.g03.mangacheck.databinding.AddReadingSelectByNameBinding
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import kotlin.coroutines.CoroutineContext

class AddReadingByNameFragment : Fragment(R.layout.add_reading_select_by_name) {
    private var _binding: AddReadingSelectByNameBinding? = null

    private lateinit var searchButton: Button
    private lateinit var textBox: EditText
    private lateinit var scrollView: ScrollView

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
        scrollView = binding.listsResultsQueryByName

        searchButton.setOnClickListener {
            scrollView.post{ queryServerAnUpdateUI() }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun queryServerAnUpdateUI() {
        val client: HttpClient = HttpClient()
        lateinit var formattedResponse: Array<Array<String>>
        val scope = CoroutineScope(Dispatchers.IO)
        val ipAddr: String = this.requireContext().getString(R.string.ip_addr)
        val serverPort: Int = this.requireContext().getString(R.string.server_port).toInt()
        scope.launch {
            Log.v("Server side", "Sent request")
            val response: HttpResponse = client.get {
                url {
                    host = ipAddr
                    port = serverPort
                    path("search/${textBox.text}")
                }
            }
            if (response.status.value in 200..299) {
                Log.v("Server side", response.status.value.toString())
                formattedResponse = parsing(response.body())
                updateUI(formattedResponse)
            } else {
                withContext(Dispatchers.Main) {
                    Toast.makeText(context, "Error ${response.status.value}", Toast.LENGTH_SHORT)
                        .show()
                }
            }
        }
    }

    private fun updateUI(response: Array<Array<String>>) {
        Runnable {
            Log.v("Runnable","Inside Runnable")
            response.forEachIndexed { index, internalArray ->
                val comicDapter = ReadingByNameAdapter(
                    internalArray[1],
                    this@AddReadingByNameFragment.requireContext()
                )
                scrollView.addView(comicDapter.getView(index, null, null))
            }
        }
    }

    companion object {
        private fun parsing(response: String): Array<Array<String>> {
            val regex = Regex(
                """\[((?:(\d+),?|("[^,]+?"),?)+)]"""
            )
            val matches = regex.findAll(response)
            val stringOfRegexGroups: String = matches.map {
                it.groupValues[1]
            }.joinToString()
            val listOfValues: List<String> = stringOfRegexGroups.split(",")
            var formattedResponse: Array<Array<String>> = Array(listOfValues.size / 2) { it ->
                arrayOf("", "")
            }
            var indexOfFormattedResponse: Int = 0
            for (index in listOfValues.indices step 2) {
                formattedResponse[indexOfFormattedResponse][0] = listOfValues[index] //id
                formattedResponse[indexOfFormattedResponse][1] = listOfValues[index + 1] //name
                indexOfFormattedResponse += 1
            }
            for ( index in formattedResponse.indices){
                Log.v(index.toString(), formattedResponse[index][1])
            }
            return formattedResponse
        }
    }
}