package it.unitn.disi.lpsmt.g03.ui.library

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import it.unitn.disi.lpsmt.g03.ui.library.databinding.ChapterFormLayoutBinding

class ChapterFormFragment : Fragment() {

    private var _binding: ChapterFormLayoutBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = ChapterFormLayoutBinding.inflate(inflater, container, false)

        binding.nextButton.setOnClickListener {
            findNavController().navigate(R.id.back_to_home)
        }

        return binding.root
    }
}