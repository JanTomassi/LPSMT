package it.unitn.disi.lpsmt.g03.ui.reader

import android.animation.ObjectAnimator
import android.os.Build
import android.os.Build.VERSION
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.viewpager2.widget.ViewPager2
import com.bumptech.glide.Glide
import dagger.hilt.android.AndroidEntryPoint
import it.unitn.disi.lpsmt.g03.core.CustomeActivity
import it.unitn.disi.lpsmt.g03.data.appdatabase.AppDatabase
import it.unitn.disi.lpsmt.g03.data.library.Chapter
import it.unitn.disi.lpsmt.g03.ui.reader.databinding.ReaderLayoutBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class Reader : Fragment() {

    private fun animateBars(show: Boolean, view: View?, hidePosition: Float) {
        if (show) {
            view?.translationY = hidePosition
            ObjectAnimator.ofFloat(view,
                "translationY", 0.0f).apply {
                duration = 500
                start()
            }
        } else {
            ObjectAnimator.ofFloat(view,
                "translationY",
                hidePosition).apply {
                duration = 500

                start()
            }
        }
    }

    private var _bars: Boolean = true
        set(value) {
            if (field == value)
                return
            field = value
            (activity as CustomeActivity?)?.isFullscreen = !value
            CoroutineScope(Dispatchers.Main).launch {
                //while ((_binding?.topBar?.isLaidOut == false) or (_binding?.bottomBar?.isLaidOut == false))
                //delay(100)
                animateBars(value,
                    _binding?.bottomBar,
                    (_binding?.bottomBar?.height?.toFloat() ?: 0.0f))
                animateBars(value,
                    _binding?.topBar,
                    -(_binding?.topBar?.height?.toFloat() ?: 0.0f))
            }
        }

    private var _chapter: Chapter? = null

    private var _binding: ReaderLayoutBinding? = null

    // This properties is only valid between onCreateView and
    // onDestroyView.
    private val mBinding get() = _binding!!
    private val mChapter get() = _chapter!!

    @Inject
    lateinit var db: AppDatabase.AppDatabaseInstance

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) _chapter = arguments?.getParcelable("chapter",
            Chapter::class.java)
        else if (VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU) _chapter = @Suppress("DEPRECATION") arguments?.getParcelable(
            "chapter")
    }

    override fun onCreateView(inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?): View {
        _binding = ReaderLayoutBinding.inflate(inflater, container, false)
        (requireActivity() as CustomeActivity).hideBars()
        _bars = false
        return mBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mBinding.pageNum.text = "${mChapter.currentPage} / ${mChapter.pages}"

        val progress = (requireActivity() as CustomeActivity).progressBarState

        mBinding.pages.adapter = ReaderAdapter(mChapter,
            Glide.with(this),
            requireContext(),
            requireContext().contentResolver,
            progress) { _bars = !_bars }
        mBinding.pages.currentItem = mChapter.currentPage
        mBinding.pages.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                mBinding.pageNum.text = "$position / ${mChapter.pages}"
                mBinding.pageSlider.value = position.toFloat()
                CoroutineScope(Dispatchers.IO).launch {
                    db.chapterDao().update(mChapter.copy(currentPage = position))
                }
            }
        })
        mBinding.backButton.setOnClickListener {
            findNavController().popBackStack()
        }
        mBinding.mangaName.text = mChapter.chapter
        mBinding.pageSlider.valueTo = mChapter.pages.toFloat()
        mBinding.pageSlider.value = mChapter.currentPage.toFloat()
        mBinding.pageSlider.addOnChangeListener { _, value, _ -> mBinding.pages.currentItem = value.toInt() }
    }

    override fun onDestroy() {
        super.onDestroy()
        _bars = true
        (requireActivity() as CustomeActivity).showBars()
    }
}