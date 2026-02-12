package it.unitn.disi.lpsmt.g03.library

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import it.unitn.disi.lpsmt.g03.library.databinding.SeriesFormLayoutBinding

class SeriesFormFragment : Fragment() {

    private var _binding: SeriesFormLayoutBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = SeriesFormLayoutBinding.inflate(inflater, container, false)

        binding.nextButton.setOnClickListener {
            findNavController().navigate(R.id.action_series_form_to_chapter_form)
        }

        return binding.root
    }
}