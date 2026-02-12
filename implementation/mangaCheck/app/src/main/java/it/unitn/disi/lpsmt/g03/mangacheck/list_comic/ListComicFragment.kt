package it.unitn.disi.lpsmt.g03.mangacheck.list_comic

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import it.unitn.disi.lpsmt.g03.mangacheck.databinding.ListComicLayoutBinding
import it.unitn.disi.lpsmt.g03.mangacheck.list_comic.xml.XMLEncoder
import it.unitn.disi.lpsmt.g03.mangacheck.list_comic.xml.XMLParser

class ListComicFragment : Fragment() {
    private var _binding: ListComicLayoutBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    private val args: ListComicFragmentArgs by navArgs()

    private lateinit var xmlParser: XMLParser
    private lateinit var xmlEncoder: XMLEncoder

    private val navController: NavController by lazy { findNavController() }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        super.onCreateView(inflater, container, savedInstanceState)
        _binding = ListComicLayoutBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initXML()
        val direction = ListComicFragmentDirections.actionListComicFragmentToAddChapterFragment(args.libraryID)

        navController.navigate(direction)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun initXML() {
        xmlEncoder = XMLEncoder(args.libraryID, requireContext())
        xmlParser = XMLParser()
    }
}