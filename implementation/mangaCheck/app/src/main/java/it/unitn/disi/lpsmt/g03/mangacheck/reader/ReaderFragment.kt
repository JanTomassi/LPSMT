package it.unitn.disi.lpsmt.g03.mangacheck.reader

import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.view.get
import androidx.fragment.app.Fragment
import it.unitn.disi.lpsmt.g03.mangacheck.databinding.ReaderLayoutBinding
import it.unitn.disi.lpsmt.g03.mangacheck.reader.data.ReaderAdapter
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


class ReaderFragment : Fragment() {

    private var currentPage: Int = 0
    private var _binding: ReaderLayoutBinding? = null
    private val binding get() = _binding!!

    private lateinit var readerAdapter: ReaderAdapter
    // private val argument: ReaderFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        super.onCreateView(inflater, container, savedInstanceState)
        _binding = ReaderLayoutBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val getContent = registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
            if (uri != null) {
                CoroutineScope(Dispatchers.IO).launch {
                    readerAdapter = ReaderAdapter(uri, requireContext())

                    withContext(Dispatchers.Main) {
                        binding.mangaView.addView(
                            readerAdapter.getView(
                                currentPage, null, binding.mangaView.width, binding.mangaView.height
                            ), 0
                        )
                        binding.bottomBar.previous.setOnClickListener {
                            if (currentPage > 0) {
                                readerAdapter.getView(
                                    --currentPage,
                                    binding.mangaView[0],
                                    binding.mangaView.width,
                                    binding.mangaView.height
                                )
                            }
                        }
                        binding.bottomBar.forward.setOnClickListener {
                            if (currentPage < readerAdapter.getCount()) {
                                readerAdapter.getView(
                                    ++currentPage,
                                    binding.mangaView[0],
                                    binding.mangaView.width,
                                    binding.mangaView.height
                                )
                            }
                        }
                    }
                }
            }
        }
        getContent.launch("application/x-cbz")
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        val TAG: String? = ReaderFragment::class.simpleName
    }
}