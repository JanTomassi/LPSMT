package it.unitn.disi.lpsmt.g03.mangacheck.export

import android.net.Uri
import android.os.Bundle
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import it.unitn.disi.lpsmt.g03.mangacheck.R
import java.io.File
import java.nio.file.Files
import java.nio.file.Path

class ExportFragment : Fragment() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val intent =
            registerForActivityResult(ActivityResultContracts.CreateDocument("application/xml")) {
                writeOnFile(it)
                requireActivity().supportFragmentManager.popBackStack()
            }
        intent.launch("readingList")
    }

    private fun writeOnFile(uri: Uri?) {
        val fileReadingListPrivate = File(requireContext().filesDir, requireContext().getString(R.string.XML_file))
        val pathReadingListPrivate : Path = fileReadingListPrivate.toPath()
        val cursor = requireContext().contentResolver.openOutputStream(uri!!)
        Files.copy(pathReadingListPrivate, cursor)
        cursor?.flush()
        cursor?.close()
    }

}