package it.unitn.disi.lpsmt.g03.mangacheck

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.Fragment
import it.unitn.disi.lpsmt.g03.mangacheck.databinding.AddComicsLayoutBinding

class AddComicsFragment : Fragment() {
    private var _binding: AddComicsLayoutBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = AddComicsLayoutBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?
    ) {
        super.onViewCreated(view, savedInstanceState)

        val addButton : Button = binding.submitButton
        val nameText : EditText = binding.comicName
        val chapterSelector : EditText = binding.chapterSelector.chapterNumber

        addButton.setOnClickListener {
            val fileExplorerIntent: Intent = Intent(Intent.ACTION_GET_CONTENT).apply {
                type = "application/x-cbz"
                putExtra(Intent.EXTRA_ALLOW_MULTIPLE, false)
            }
            startActivity(Intent.createChooser(fileExplorerIntent, "Pick a file"))
        }

    }
}