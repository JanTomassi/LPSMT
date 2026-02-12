package it.unitn.disi.lpsmt.g03.mangacheck

import android.app.Activity
import android.content.ContentResolver.MimeTypeInfo
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.MimeTypeMap
import android.widget.Button
import android.widget.Toast
import androidx.core.graphics.drawable.toIcon
import androidx.fragment.app.Fragment
import it.unitn.disi.lpsmt.g03.mangacheck.databinding.AddComicsLayoutBinding

class AddComicsFragment : Fragment() {
    private var _binding: AddComicsLayoutBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = AddComicsLayoutBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val addButton : Button = binding.submitButton
        addButton.setOnClickListener {
            // Code here executes on main thread after user presses button
            //Toast.makeText(this.context, "Premuto il pulsanton", Toast.LENGTH_SHORT).show()
            fileManagerRequest()
        }
    }

    //Custom functions definition

    private fun fileManagerRequest(){
        val fileExplorerIntent: Intent = Intent(Intent.ACTION_GET_CONTENT).apply {
            type = "*/*"
            putExtra(Intent.EXTRA_ALLOW_MULTIPLE, false)
        }
        Toast.makeText(this.context, fileExplorerIntent.type, Toast.LENGTH_SHORT).show()
        //startActivity(Intent.createChooser(fileExplorerIntent, "Pick a file"))
        startActivityForResult(Intent.createChooser(fileExplorerIntent, "Pick a file"), 111)
        /*fileExplorerIntent.data?.let {Uri->
            MimeTypeMap.getFileExtensionFromUrl(Uri.toString())
        }
        Log.e("Dio merda", fileExplorerIntent.data.toString())*/
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 111 && resultCode == Activity.RESULT_OK){
            val selectedFile = (data?.data).toString() // The URI with the location of the file
            Log.e("A dire il vero tutto ok",selectedFile)
            val mimeType = MimeTypeMap.getFileExtensionFromUrl(selectedFile)
            Log.e("A dire il vero tutto ok",mimeType)
        }
    }
}