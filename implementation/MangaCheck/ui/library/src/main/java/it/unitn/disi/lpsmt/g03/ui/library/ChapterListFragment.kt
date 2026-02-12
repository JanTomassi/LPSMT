package it.unitn.disi.lpsmt.g03.ui.library

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import it.unitn.disi.lpsmt.g03.ui.library.databinding.ChapterListLayoutBinding

class ChapterListFragment : Fragment() {
    private lateinit var mBinding: ChapterListLayoutBinding
    private val chaptersView: RecyclerView by lazy { mBinding.chaptersView }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        super.onCreateView(inflater, container, savedInstanceState)
        mBinding = ChapterListLayoutBinding.inflate(inflater)
        return mBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        chaptersView.adapter = null
    }
}