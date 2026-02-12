package it.unitn.disi.lpsmt.g03.ui.reader

import android.os.Build
import android.os.Build.VERSION
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.bumptech.glide.Glide
import it.unitn.disi.lpsmt.g03.core.ImageLoader
import it.unitn.disi.lpsmt.g03.data.library.Chapter
import it.unitn.disi.lpsmt.g03.ui.reader.databinding.ReaderLayoutBinding

class Reader : Fragment() {

    private val viewModel: ReaderViewModel by viewModels()
    private var _chapter: Chapter? = null

    private var _binding: ReaderLayoutBinding? = null

    // This properties is only valid between onCreateView and
    // onDestroyView.
    private val mBinding get() = _binding!!
    private val mChapter get() = _chapter!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) _chapter = arguments?.getParcelable("chapter",
            Chapter::class.java)
        else if (VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU) _chapter = @Suppress("DEPRECATION") arguments?.getParcelable(
            "chapter")

        Log.d(this::class.simpleName, mChapter.lastAccess.toString())
    }

    override fun onCreateView(inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?): View {
        _binding = ReaderLayoutBinding.inflate(inflater, container, false)
        return mBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mBinding.pages.adapter = ReaderAdapter(ReaderAdapter.CbzMetadata(mChapter.file,
            ImageLoader.getPageInCbz(mChapter.file, requireContext().contentResolver)),
            Glide.with(this),
            requireContext().contentResolver)
    }

}