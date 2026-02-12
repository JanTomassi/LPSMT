package it.unitn.disi.lpsmt.g03.mangacheck.import

import android.net.Uri
import android.os.Bundle
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import it.unitn.disi.lpsmt.g03.mangacheck.R
import java.io.File
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.StandardCopyOption

class ImportFragment : Fragment(){

    /**
     * Dummy Fragment to launch the Activity for the file picker
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val action =
            registerForActivityResult(ActivityResultContracts.GetContent()) {
                importOnFile(it)
                findNavController().popBackStack()
            }
        action.launch("""text/xml""")
    }

    /**
     * Overwrite the internal reading list with the imported one
     * @param [uri] uri to the file selected
     */
    private fun importOnFile(uri : Uri?){
        if (uri != null){
            val fileReadingListPrivate = File(requireContext().filesDir, requireContext().getString(R.string.XML_file))
            val pathReadingListPrivate : Path = fileReadingListPrivate.toPath()
            val cursor = requireContext().contentResolver.openInputStream(uri)
            Files.copy(cursor, pathReadingListPrivate, StandardCopyOption.REPLACE_EXISTING)
            cursor?.close()
        }
    }
}