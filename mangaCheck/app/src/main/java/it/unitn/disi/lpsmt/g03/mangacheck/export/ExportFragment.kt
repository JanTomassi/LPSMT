package it.unitn.disi.lpsmt.g03.mangacheck.export

import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import it.unitn.disi.lpsmt.g03.mangacheck.R
import java.io.File
import java.nio.file.Files
import java.nio.file.Path

class ExportFragment : Fragment() {

    /**
     * Create a dummy fragment to launch the file selector action
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val fileReadingListPrivate = File(requireContext().filesDir, requireContext().getString(R.string.XML_file))
        if(fileReadingListPrivate.exists()) {
            val action =
                registerForActivityResult(ActivityResultContracts.CreateDocument("text/xml")) {
                    writeOnFile(it, fileReadingListPrivate)
                    findNavController().popBackStack()
                }
            action.launch("readingList")
        }
        else{
            findNavController().popBackStack()
            toaster("Reading list doesn't exist")
        }
    }

    /**
     * Write the reading list to the selected external file
     * @param [uri] selected file uri
     * @param [fileReadingListPrivate] File to the internal reading list
     */
    private fun writeOnFile(uri: Uri?, fileReadingListPrivate : File) {
        if (uri != null) {
            val pathReadingListPrivate: Path = fileReadingListPrivate.toPath()
            val cursor = requireContext().contentResolver.openOutputStream(uri)
            Files.copy(pathReadingListPrivate, cursor)
            cursor?.flush()
            cursor?.close()
        }
    }

    // Prepare a delicious Toast for you
    private fun toaster(msg: String) {
        Toast.makeText(requireContext(), msg, Toast.LENGTH_SHORT).show()
    }
}