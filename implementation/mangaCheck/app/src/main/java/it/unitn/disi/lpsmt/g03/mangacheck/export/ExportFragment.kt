package it.unitn.disi.lpsmt.g03.mangacheck.export

import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import it.unitn.disi.lpsmt.g03.mangacheck.R
import java.io.File
import java.nio.file.Files
import java.nio.file.Path

class ExportFragment : Fragment() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val fileReadingListPrivate = File(requireContext().filesDir, requireContext().getString(R.string.XML_file))
        if(fileReadingListPrivate.exists()) {
            val action =
                registerForActivityResult(ActivityResultContracts.CreateDocument("text/xml")) {
                    writeOnFile(it, fileReadingListPrivate)
                    requireActivity().supportFragmentManager.popBackStack()
                }
            action.launch("readingList")
        }
        else{
            requireActivity().supportFragmentManager.popBackStack()
            toaster("Reading list doesn't exist")
        }
    }

    private fun writeOnFile(uri: Uri?, fileReadingListPrivate : File) {
            val pathReadingListPrivate : Path = fileReadingListPrivate.toPath()
            val cursor = requireContext().contentResolver.openOutputStream(uri!!)
            Files.copy(pathReadingListPrivate, cursor)
            cursor?.flush()
            cursor?.close()
    }

    // Prepare a delicious Toast for you
    private fun toaster(msg: String) {
        Toast.makeText(requireContext(), msg, Toast.LENGTH_SHORT).show()
    }
}