package it.unitn.disi.lpsmt.g03.mangacheck.add_reading.set_status

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.NavDirections
import androidx.navigation.fragment.findNavController
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.statement.HttpResponse
import io.ktor.http.path
import it.unitn.disi.lpsmt.g03.mangacheck.R
import it.unitn.disi.lpsmt.g03.mangacheck.databinding.AddReadingSetStatusBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.net.ConnectException

class AddReadingSetStatusFragment : Fragment(R.layout.add_reading_set_status) {
    private var _binding: AddReadingSetStatusBinding? = null

    private lateinit var titleText: TextView
    private lateinit var statusSelector: Spinner
    private lateinit var submitButton: Button

    private lateinit var imageBase64: String
    private var mangaID: Int? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = AddReadingSetStatusBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        titleText = binding.mangaTitleSelected
        statusSelector = binding.statusSpinner
        submitButton = binding.submitButton

        titleText.text = requireArguments().getString("mangaTitle")
        mangaID = requireArguments().getInt("mangaID")

        ArrayAdapter.createFromResource(
            this@AddReadingSetStatusFragment.requireContext(),
            R.array.spinner_status,
            android.R.layout.simple_spinner_item
        ).also { adapter ->
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            statusSelector.adapter = adapter
        }

        submitButton.setOnClickListener {
            queryImage()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    // Prepare a delicious Toast for you
    private fun toaster(msg: String) {
        Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
    }

    // Check the query status then add the nav args depending on the spinner value
    private suspend fun checkQueryResult(response: HttpResponse) {
        if (response.status.value in 200..299) {
            imageBase64 = response.body()
            val list: String =
                when (statusSelector.getItemAtPosition(statusSelector.selectedItemPosition)
                    .toString()) {
                    "Reading" -> "reading_list"
                    "Planning" -> "planning_list"
                    "Completed" -> "completed_list"
                    else -> "reading_list"
                }
            val action: NavDirections =
                AddReadingSetStatusFragmentDirections.actionAddReadingSetStatusToReadingListFragment(
                    requireArguments().getInt("mangaID"), // The manga ID
                    requireArguments().getString("mangaTitle").toString(), // Manga title
                    list, // The select status, the final toString is to prevent null value
                    imageBase64 // The string in base64 of the image
                )
            withContext(Dispatchers.Main) {
                this@AddReadingSetStatusFragment.findNavController().navigate(action)
            }
        } else {
            withContext(Dispatchers.Main) {
                toaster("Error ${response.status.value}")
            }
        }
    }

    // Make a request to the server for the Manga cover image
    private fun queryImage() {
        val client = HttpClient()
        val scope = CoroutineScope(Dispatchers.Main)
        val ipAddr: String = requireContext().getString(R.string.ip_addr)
        val serverPort: Int = requireContext().getString(R.string.server_port).toInt()

        scope.launch {
            try {
                val response: HttpResponse = client.get {
                    url {
                        host = ipAddr
                        port = serverPort
                        path("image/${mangaID}")
                    }
                }
                checkQueryResult(response)
            } catch (e: ConnectException) {
                withContext(Dispatchers.Main) {
                    toaster("Connection Refused")
                }
            }
        }
    }
}