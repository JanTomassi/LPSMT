package it.unitn.disi.lpsmt.g03.ui.library

import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.bumptech.glide.Glide
import it.unitn.disi.lpsmt.g03.core.BarVisibility
import it.unitn.disi.lpsmt.g03.core.CbzLoadImage
import it.unitn.disi.lpsmt.g03.core.getFileName
import it.unitn.disi.lpsmt.g03.core.isCbz
import it.unitn.disi.lpsmt.g03.ui.library.databinding.ChapterAddLayoutBinding
import it.unitn.disi.lpsmt.g03.ui.library.databinding.ChapterFormLayoutBinding

class ChapterAddFragment : Fragment() {

    private var _binding: ChapterAddLayoutBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val mBinding get() = _binding!!
    private val mFormBinding: ChapterFormLayoutBinding by lazy {
        ChapterFormLayoutBinding.bind(mBinding.root)
    }
    private val args: ChapterListFragmentArgs by navArgs()
    private lateinit var getChapter: ActivityResultLauncher<String>
    private val model: ChapterAddViewModel by viewModels()

    class ChapterAddViewModel : ViewModel() {
        val title: MutableLiveData<String> by lazy { MutableLiveData<String>() }
        val chNumber: MutableLiveData<Int> by lazy { MutableLiveData<Int>() }
        val fileUri: MutableLiveData<Uri> by lazy { MutableLiveData<Uri>() }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        getChapter = registerForActivityResult(ActivityResultContracts.GetContent()) {
            it?.let { uri: Uri ->
                if (uri.path.toString().contains(".cbz$".toRegex())) model.fileUri.value = uri
            }
        }
    }

    override fun onCreateView(inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?): View {
        _binding = ChapterAddLayoutBinding.inflate(inflater, container, false)

        mBinding.saveButton.setOnClickListener {
            findNavController().popBackStack()
        }

        (activity as BarVisibility).hideNavBar()

        return mBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mFormBinding.title.editText?.setOnEditorActionListener { v, _, _ ->
            if (v.text.isEmpty()) mFormBinding.title.error = "Required"
            else model.title.value = v.text.toString()
            false
        }
        mFormBinding.number.editText?.setOnEditorActionListener { v, _, _ ->
            if (v.text.isEmpty()) mFormBinding.number.error = "Required"
            else try {
                model.chNumber.value = v.text.toString().toInt()
            } catch (e: NumberFormatException) {
                mFormBinding.number.error = "Not a Number"
            }
            false
        }
        mFormBinding.pickFile.setOnClickListener {
            getChapter.launch("application/x-cbz")
        }
        model.fileUri.observe(viewLifecycleOwner) { uri ->
            if (!uri.isCbz(context?.contentResolver)) return@observe
            mFormBinding.coverContainer.visibility = View.VISIBLE
            mFormBinding.fileName.text = uri.getFileName(context?.contentResolver)
            mFormBinding.title.editText?.setText(uri.getFileName(context?.contentResolver))
            mFormBinding.pickFile.text = resources.getText(R.string.pick_another_file)
            CbzLoadImage.setCoverImage(uri,
                requireContext().contentResolver,
                Glide.with(this),
                mFormBinding.cover)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        (activity as BarVisibility).showNavBar()
    }
}