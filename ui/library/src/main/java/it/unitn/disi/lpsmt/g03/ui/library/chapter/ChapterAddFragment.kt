package it.unitn.disi.lpsmt.g03.ui.library.chapter

import android.app.AlertDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.view.children
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.bumptech.glide.Glide
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.textfield.TextInputLayout
import it.unitn.disi.lpsmt.g03.core.CustomeActivity
import it.unitn.disi.lpsmt.g03.core.ImageLoader
import it.unitn.disi.lpsmt.g03.core.getFileName
import it.unitn.disi.lpsmt.g03.core.isCbz
import it.unitn.disi.lpsmt.g03.data.appdatabase.AppDatabase
import it.unitn.disi.lpsmt.g03.data.library.Chapter
import it.unitn.disi.lpsmt.g03.data.library.ReadingState
import it.unitn.disi.lpsmt.g03.ui.library.R
import it.unitn.disi.lpsmt.g03.ui.library.databinding.ChapterAddLayoutBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.time.ZonedDateTime
import java.util.InputMismatchException

class ChapterAddFragment : Fragment() {

    private var _binding: ChapterAddLayoutBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val mBinding get() = _binding!!
    private val args: ChapterAddFragmentArgs by navArgs()
    private lateinit var getChapter: ActivityResultLauncher<Array<String>>
    private val model: ChapterAddViewModel by viewModels()

    class ChapterAddViewModel : ViewModel() {
        val title: MutableLiveData<String> by lazy { MutableLiveData<String>() }
        val chNumber: MutableLiveData<String> by lazy { MutableLiveData<String>() }
        val fileUri: MutableLiveData<Uri> by lazy { MutableLiveData<Uri>() }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        getChapter = registerForActivityResult(ActivityResultContracts.OpenDocument()) {
            it?.let { uri: Uri ->
                val takeFlags: Int = Intent.FLAG_GRANT_READ_URI_PERMISSION
                requireContext().contentResolver.takePersistableUriPermission(uri, takeFlags)
                if (uri.path.toString().contains(".cbz$".toRegex())) model.fileUri.value = uri
            }
        }
    }

    override fun onCreateView(inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?): View {
        _binding = ChapterAddLayoutBinding.inflate(inflater, container, false)

        mBinding.saveButton.setOnClickListener {
            val title: String
            val chNumber: Int
            try {
                title = testAndSetInputError(mBinding.form.title)
                chNumber = testAndSetInputError(mBinding.form.number).toInt()
            } catch (e: InputMismatchException) {
                Snackbar.make(requireView(), e.message.toString(), Snackbar.LENGTH_SHORT).show()
                return@setOnClickListener
            } catch (e: NumberFormatException) {
                Snackbar.make(requireView(),
                    "Chapter Number is not well formatted",
                    Snackbar.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            AlertDialog.Builder(context)
                .setTitle("File management")
                .setMessage("There will be no copy of the selected file. \n\nIf you move this file the application will not be able to access it again.")
                .setPositiveButton("Continue") { _, _ ->
                    chapterAddition(title, chNumber)
                }
                .setNegativeButton("Abort") { _, _ -> findNavController().popBackStack() }
                .create()
                .show()
        }

        (activity as CustomeActivity).hideNavBar()

        return mBinding.root
    }

    private fun chapterAddition(title: String, chNumber: Int) {
        val progress = (requireActivity() as CustomeActivity).progressBarState

        CoroutineScope(Dispatchers.IO).launch {
            withContext(Dispatchers.Main) {
                mBinding.saveButton.isEnabled = false
                mBinding.form.root.children.forEach { it.isEnabled = false }
            }
            AppDatabase.getInstance(context)
                .chapterDao()
                .insertAll(Chapter(args.series.uid,
                    title,
                    chNumber,
                    ImageLoader.getPagesInCbz(model.fileUri.value,
                        requireContext().contentResolver,
                        progress),
                    0,
                    ReadingState.PLANNING,
                    model.fileUri.value,
                    ZonedDateTime.now()))
            withContext(Dispatchers.Main) {
                try {
                    findNavController().popBackStack()
                } catch (_: IllegalStateException) {
                    Log.v(this@ChapterAddFragment::class.simpleName,
                        "Moving before completing insert")
                }
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mBinding.form.title.editText?.setOnEditorActionListener { v, _, _ ->
            if (v.text.isEmpty()) mBinding.form.title.error = "Required"
            else model.title.value = v.text.toString()
            false
        }
        mBinding.form.number.editText?.setOnEditorActionListener { v, _, _ ->
            if (v.text.isEmpty()) mBinding.form.number.error = "Required"
            else try {
                model.chNumber.value = v.text.toString()
            } catch (e: NumberFormatException) {
                mBinding.form.number.error = "Not a Number"
            }
            false
        }
        mBinding.form.pickFile.setOnClickListener {
            getChapter.launch(arrayOf("application/x-cbz"))
        }
        model.fileUri.observe(viewLifecycleOwner) { uri ->
            CoroutineScope(Dispatchers.IO).launch {
                withContext(Dispatchers.IO) {
                    if (uri.isCbz(context?.contentResolver)) {
                        val fileName = uri.getFileName(context?.contentResolver)
                        withContext(Dispatchers.Main) {
                            mBinding.form.coverContainer.visibility = View.VISIBLE
                            mBinding.form.fileName.text = fileName
                            mBinding.form.title.editText?.setText(fileName)
                            mBinding.form.pickFile.text = resources.getText(R.string.pick_another_file)

                            ImageLoader.setImageFromCbzUri(uri,
                                requireContext().contentResolver,
                                Glide.with(this@ChapterAddFragment),
                                mBinding.form.cover)
                        }
                    }
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        (activity as CustomeActivity).showNavBar()
    }

    private fun testAndSetInputError(input: TextInputLayout): String {
        if (!input.editText?.text?.toString()
                .isNullOrBlank()) return input.editText?.text?.toString()!!

        input.error = getString(R.string.required_input)
        throw InputMismatchException(getString(R.string.required_inputs))
    }
}