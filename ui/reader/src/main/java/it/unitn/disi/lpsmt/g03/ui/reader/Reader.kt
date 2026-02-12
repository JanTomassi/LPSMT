package it.unitn.disi.lpsmt.g03.ui.reader

import android.os.Build
import android.os.Build.VERSION
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.MutableLiveData
import androidx.viewpager2.widget.ViewPager2
import com.bumptech.glide.Glide
import it.unitn.disi.lpsmt.g03.core.LoadingDialog
import it.unitn.disi.lpsmt.g03.data.appdatabase.AppDatabase
import it.unitn.disi.lpsmt.g03.data.library.Chapter
import it.unitn.disi.lpsmt.g03.ui.reader.databinding.ReaderLayoutBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class Reader : Fragment() {

    private val viewModel: ReaderViewModel by viewModels()
    private var _chapter: Chapter? = null

    private var _binding: ReaderLayoutBinding? = null

    // This properties is only valid between onCreateView and
    // onDestroyView.
    private val mBinding get() = _binding!!
    private val mChapter get() = _chapter!!
    private lateinit var db: AppDatabase.AppDatabaseInstance

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) _chapter = arguments?.getParcelable("chapter",
            Chapter::class.java)
        else if (VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU) _chapter = @Suppress("DEPRECATION") arguments?.getParcelable(
            "chapter")

        db = AppDatabase.getInstance(context)
    }

    override fun onCreateView(inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?): View {
        _binding = ReaderLayoutBinding.inflate(inflater, container, false)
        return mBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mBinding.pageNum.text = "${mChapter.currentPage} / ${mChapter.pages}"

        val progress = MutableLiveData<Int>(0)

        val dialog = LoadingDialog()
        dialog.show(parentFragmentManager, this::class.simpleName)

        progress.observe(viewLifecycleOwner) {
            dialog.updatePageNum(it)
            if (it >= 100) dialog.dismiss()
        }

        mBinding.pages.adapter = ReaderAdapter(mChapter,
            Glide.with(this),
            requireContext(),
            requireContext().contentResolver,
            progress)
        mBinding.pages.currentItem = mChapter.currentPage
        mBinding.pages.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                mBinding.pageNum.text = "$position / ${mChapter.pages}"
                CoroutineScope(Dispatchers.IO).launch {
                    db.chapterDao().update(mChapter.copy(currentPage = position))
                }
            }
        })
    }

    override fun onDestroy() {
        super.onDestroy()
        db.close()
    }

}