package it.unitn.disi.lpsmt.g03.ui.library.chapter

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.bumptech.glide.RequestManager
import dagger.hilt.android.AndroidEntryPoint
import it.unitn.disi.lpsmt.g03.data.appdatabase.AppDatabase
import it.unitn.disi.lpsmt.g03.ui.library.databinding.ChapterListLayoutBinding
import javax.inject.Inject

@AndroidEntryPoint
class ChapterListFragment : Fragment() {
    private lateinit var mBinding: ChapterListLayoutBinding

    @Inject
    lateinit var db: AppDatabase.AppDatabaseInstance
    private lateinit var glide: RequestManager
    private val args: ChapterListFragmentArgs by navArgs()
    private val navController: NavController by lazy { findNavController() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        glide = Glide.with(requireParentFragment())
        activity?.title = args.series.title
    }

    override fun onCreateView(inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?): View {
        super.onCreateView(inflater, container, savedInstanceState)
        mBinding = ChapterListLayoutBinding.inflate(inflater)
        return mBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initUI()
    }

    private fun initUI() {
        mBinding.chaptersView.adapter = ChapterListAdapter(requireContext(), navController, this)
        mBinding.chaptersView.layoutManager = LinearLayoutManager(context)
        mBinding.addButton.setOnClickListener {
            navController.navigate(ChapterListFragmentDirections.actionChapterListToChapterAdd(args.series))
        }
    }


}