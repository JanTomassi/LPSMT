package it.unitn.disi.lpsmt.g03.mangacheck.add_reading.by_name

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import it.unitn.disi.lpsmt.g03.mangacheck.R
import it.unitn.disi.lpsmt.g03.mangacheck.add_reading.by_name.data.ReadingByNameAdapter
import it.unitn.disi.lpsmt.g03.mangacheck.databinding.AddReadingSelectByNameBinding
import it.unitn.disi.lpsmt.g03.mangacheck.utils.http.ServerRequest
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class AddReadingByNameFragment : Fragment(R.layout.add_reading_select_by_name) {
    private var _binding: AddReadingSelectByNameBinding? = null

    private val searchButton by lazy { binding.submitButton }
    private val textBox by lazy { binding.comicName }
    private val listView by lazy { binding.listView }

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = AddReadingSelectByNameBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        searchButton.setOnClickListener {
            disableButton()
            Log.v("Prova bottone", searchButton.text.toString())
            CoroutineScope(Dispatchers.Main).launch {
                updateUI(ServerRequest(requireContext(), null).queryNames(textBox.text.toString()))
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    // If the response is empty it creates a dummy button with ID -1 and an error as a text
    private fun updateUI(response: Array<Array<String>>) {
        listView.post {
            listView.adapter = ReadingByNameAdapter(response, requireContext(), findNavController())
            if (listView.adapter.count == 0) toaster(getString(R.string.response_empty))
            enableButton()
        }
    }

    private fun disableButton() {
        searchButton.isClickable = false
        searchButton.text = getString(R.string.add_comic_fetching)
    }

    private fun enableButton() {
        searchButton.isClickable = true
        searchButton.text = getString(R.string.add_library_submit)
    }

    // Prepare a delicious Toast for you
    private fun toaster(msg: String) {
        Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
    }
}