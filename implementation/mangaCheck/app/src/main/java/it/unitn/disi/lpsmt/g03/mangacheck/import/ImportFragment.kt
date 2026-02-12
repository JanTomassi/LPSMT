package it.unitn.disi.lpsmt.g03.mangacheck.import

import android.net.Uri
import android.os.Bundle
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import it.unitn.disi.lpsmt.g03.mangacheck.R
import java.io.File
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.StandardCopyOption

class ImportFragment : Fragment(){
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val action =
            registerForActivityResult(ActivityResultContracts.GetContent()) {
                importOnFile(it)
                requireActivity().supportFragmentManager.popBackStack()
            }
        action.launch("""text/xml""")
    }

    private fun importOnFile(uri : Uri?){
        val fileReadingListPrivate = File(requireContext().filesDir, requireContext().getString(R.string.XML_file))
        val pathReadingListPrivate : Path = fileReadingListPrivate.toPath()
        val cursor = requireContext().contentResolver.openInputStream(uri!!)
        Files.copy(cursor, pathReadingListPrivate, StandardCopyOption.REPLACE_EXISTING)
        cursor?.close()
    }
}