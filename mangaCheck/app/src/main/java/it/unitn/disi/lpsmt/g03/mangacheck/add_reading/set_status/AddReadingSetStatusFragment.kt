package it.unitn.disi.lpsmt.g03.mangacheck.add_reading.set_status

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.Spinner
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.navigation.NavDirections
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import it.unitn.disi.lpsmt.g03.mangacheck.R
import it.unitn.disi.lpsmt.g03.mangacheck.databinding.AddReadingSetStatusBinding
import it.unitn.disi.lpsmt.g03.mangacheck.utils.http.ServerRequest
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class AddReadingSetStatusFragment : Fragment(R.layout.add_reading_set_status) {
    private var _binding: AddReadingSetStatusBinding? = null

    private val args : AddReadingSetStatusFragmentArgs by navArgs()

    private lateinit var titleText: TextView
    private lateinit var statusSelector: Spinner
    private lateinit var submitButton: Button

    private var mangaID: Int? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = AddReadingSetStatusBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        titleText = binding.mangaTitleSelected
        statusSelector = binding.statusSpinner
        submitButton = binding.submitButton

        titleText.text = args.mangaTitle
        mangaID = args.mangaID

        // Create the spinner from resources strings
        ArrayAdapter.createFromResource(
            this@AddReadingSetStatusFragment.requireContext(),
            R.array.spinner_status,
            android.R.layout.simple_spinner_item
        ).also { adapter ->
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            statusSelector.adapter = adapter
        }

        // Make the query for the image and call the navigation function
        submitButton.setOnClickListener {
            submitButton.isClickable = false
            submitButton.text = getString(R.string.add_comic_fetching)
            val requestManager = ServerRequest(requireContext(), mangaID!!.toInt())
            CoroutineScope(Dispatchers.IO).launch {
                requestManager.queryImage()
                val description: String = requestManager.queryDescription()
                val list: String = retrieveSpinnerValue()
                withContext(Dispatchers.Main) {
                    sendData(list, description)
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    /**
     * Function to craft the argument to navigate to the next fragment a encapsulates the data
     * @param [list] list selected from the spinner
     * @param [descriptionResponse] comic description retrieved by API
     */
    private fun sendData(
        list: String, descriptionResponse: String
    ) {
        val action: NavDirections =
            AddReadingSetStatusFragmentDirections.actionAddReadingSetStatusToReadingListFragment(
                args.mangaTitle, // Manga title
                list, // The select status, the final toString is to prevent null value
                descriptionResponse, // The manga description
                args.mangaID // The manga ID
            )
        this.findNavController().navigate(action)
    }

    /**
     * dummy switch case to convert in string the value of the spinner
     * @return one string among "reading_list", "planning_list" and "completed_list"
     */
    private fun retrieveSpinnerValue(): String {
        return when (statusSelector.getItemAtPosition(statusSelector.selectedItemPosition)
            .toString()) {
            "Reading" -> "reading_list"
            "Planning" -> "planning_list"
            "Completed" -> "completed_list"
            else -> "reading_list"
        }
    }
}