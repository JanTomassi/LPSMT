package it.unitn.disi.lpsmt.g03.mangacheck.add_chapter

import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import it.unitn.disi.lpsmt.g03.mangacheck.databinding.AddChapterLayoutBinding
import it.unitn.disi.lpsmt.g03.mangacheck.list_comic.xml.XMLEncoder
import it.unitn.disi.lpsmt.g03.mangacheck.list_comic.xml.XMLParser
import it.unitn.disi.lpsmt.g03.mangacheck.utils.xml.ChapterEntry
import java.io.File
import java.io.FileNotFoundException
import java.io.FileOutputStream
import java.io.InputStream

class AddChapterFragment : Fragment() {
    private var _binding: AddChapterLayoutBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    private val contentResolver by lazy { requireContext().contentResolver }

    private val args: AddChapterFragmentArgs by navArgs()
    private val navController: NavController by lazy { findNavController() }

    private val xmlEncoder by lazy { XMLEncoder(args.libraryID, requireContext()) }
    private val xmlParser by lazy { XMLParser(args.libraryID, requireContext()) }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = AddChapterLayoutBinding.inflate(inflater, container, false)
        return binding.root
    }

    /**
     * Check if the text filed is empty if is so throe an exception,
     * otherwise add the comic to the XML.
     */
    private fun tryAddComicsToXML() {
        val title = binding.comicName.text.toString()
        if (title == "") throw IllegalStateException("Please select a name")

        val num = binding.chapterSelector.chapterInput.input.text.toString()
        if (num == "") throw IllegalStateException("Please select a chapter")

        val newEntry = ChapterEntry(num.toInt(), title)
        if (xmlParser.alreadyInList(newEntry)) throw IllegalStateException("A chapter with the same number is already present")
        xmlEncoder.addEntry(newEntry)
    }

    /**
     * Transfer the .cbz into the application private space.
     * @param [uri] is the uri of the selected .cbz
     */
    private fun writeFileToLocal(uri: Uri) {
        val num = binding.chapterSelector.chapterInput.input.text.toString()
        if (num == "") throw IllegalStateException("Please select a chapter")

        val inputStream: InputStream = contentResolver.openInputStream(uri)
            ?: throw FileNotFoundException("There was an error in opening the selected file")
        val outputStream: FileOutputStream =
            File(requireContext().filesDir, "/${args.libraryID}/$num.cbz").outputStream()
        val buffer = ByteArray(65536)
        while (inputStream.read(buffer) > 0) outputStream.write(buffer)
        inputStream.close()
    }

    override fun onViewCreated(
        view: View, savedInstanceState: Bundle?
    ) {
        val getContent = registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
            super.onViewCreated(view, savedInstanceState)
            if (uri == null) {
                navController.popBackStack()
                return@registerForActivityResult
            }

            val addButton = binding.submitButton

            addButton.setOnClickListener {
                try {
                    tryAddComicsToXML()
                    writeFileToLocal(uri)
                    navController.popBackStack()
                } catch (e: FileNotFoundException) {
                    Toast.makeText(requireContext(), e.message, Toast.LENGTH_LONG).show()
                } catch (e: IllegalStateException) {
                    Toast.makeText(requireContext(), e.message, Toast.LENGTH_SHORT).show()
                }
            }
            binding.chapterSelector.chapterButtons.decButton.setOnClickListener {
                val editText = binding.chapterSelector.chapterInput.input
                val number = editText.text.toString()
                if (number == "") editText.setText("1")
                else if (number.toInt() > 1) {
                    val res = number.toInt() - 1
                    editText.setText("$res")
                }
            }
            binding.chapterSelector.chapterButtons.incButton.setOnClickListener {
                val editText = binding.chapterSelector.chapterInput.input
                val number = editText.text.toString()
                if (number == "") editText.setText("1")
                else if (number.toInt() > 0) {
                    val res = number.toInt() + 1
                    editText.setText("$res")
                }
            }
        }
        getContent.launch("application/x-cbz")
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}