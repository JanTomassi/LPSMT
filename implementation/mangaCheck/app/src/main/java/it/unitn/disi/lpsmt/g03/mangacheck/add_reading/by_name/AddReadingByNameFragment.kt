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
import it.unitn.disi.lpsmt.g03.mangacheck.R
import it.unitn.disi.lpsmt.g03.mangacheck.add_reading.by_name.data.ReadingByNameAdapter
import it.unitn.disi.lpsmt.g03.mangacheck.databinding.AddReadingSelectByNameBinding
import it.unitn.disi.lpsmt.g03.mangacheck.utils.http.ServerRequest
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class AddReadingByNameFragment : Fragment(R.layout.add_reading_select_by_name) {
    private var _binding: AddReadingSelectByNameBinding? = null

    private lateinit var searchButton: Button
    private lateinit var textBox: EditText
    private lateinit var linearLayout: LinearLayout

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

        searchButton = binding.submitButton
        textBox = binding.comicName
        linearLayout = binding.listsResultsQueryByName

        searchButton.setOnClickListener {
            searchButton.isClickable = false
            searchButton.text = getString(R.string.add_comic_fetching)
            Log.v("Prova bottone",searchButton.text.toString())
            CoroutineScope(Dispatchers.Main).launch {
                updateUI(
                    ServerRequest(
                        this@AddReadingByNameFragment.requireContext(), null
                    ).queryNames(textBox.text.toString())
                )
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    // If the response is empty it creates a dummy button with ID -1 and an error as a text
    private fun updateUI(response: Array<Array<String>>) {
        linearLayout.post {
            linearLayout.removeAllViews()
            if (response.isNotEmpty()) {
                response.forEachIndexed { index, internalArray ->
                    val comicEntry = ReadingByNameAdapter(
                        internalArray[1], this@AddReadingByNameFragment.requireContext()
                    )
                    linearLayout.addView(comicEntry.getView(internalArray[0].toInt(), null, null))
                    enableButton()
                }
            } else {
                val comicEntry = ReadingByNameAdapter(
                    "Manga doesn't exist", this@AddReadingByNameFragment.requireContext()
                )
                linearLayout.addView(comicEntry.getView(-1, null, null))
                toaster("Manga doesn't exist")
                enableButton()
            }
        }
    }

    private fun enableButton(){
        searchButton.isClickable = true
        searchButton.text = getString(R.string.add_library_submit)
    }

    // Prepare a delicious Toast for you
    private fun toaster(msg: String) {
        Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
    }
}