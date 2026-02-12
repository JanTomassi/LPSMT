package it.unitn.disi.lpsmt.g03.ui.library.home

import android.database.sqlite.SQLiteConstraintException
import android.net.TrafficStats
import android.net.Uri
import android.os.Bundle
import android.text.Html
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.navigation.navGraphViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.android.material.search.SearchView
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import it.unitn.disi.lpsmt.g03.core.CustomeActivity
import it.unitn.disi.lpsmt.g03.core.databinding.SeriesSearchSelectorBinding
import it.unitn.disi.lpsmt.g03.data.anilist.Anilist
import it.unitn.disi.lpsmt.g03.data.appdatabase.AppDatabase
import it.unitn.disi.lpsmt.g03.data.graphql.SearchByNameQuery
import it.unitn.disi.lpsmt.g03.data.library.ReadingState
import it.unitn.disi.lpsmt.g03.data.library.Series
import it.unitn.disi.lpsmt.g03.ui.library.R
import it.unitn.disi.lpsmt.g03.ui.library.databinding.SeriesFormLayoutBinding
import it.unitn.disi.lpsmt.g03.ui.library.databinding.SeriesSearchLayoutBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.time.ZonedDateTime
import java.util.InputMismatchException
import javax.inject.Inject
import kotlin.math.max
import it.unitn.disi.lpsmt.g03.core.R as RCore


@AndroidEntryPoint
class LibraryModifyFragment : Fragment() {

    private var _binding: SeriesSearchLayoutBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val mBinding get() = _binding!!
    private val mFormBinding by lazy { SeriesFormLayoutBinding.bind(mBinding.root) }
    val args : LibraryModifyFragmentArgs by navArgs()

    private val mModel: SeriesSearchModel by navGraphViewModels(R.id.library_nav)

    @Inject
    lateinit var db: AppDatabase.AppDatabaseInstance

    override fun onCreateView(inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?): View {
        (requireActivity() as CustomeActivity).hideNavBar()
        _binding = SeriesSearchLayoutBinding.inflate(inflater, null, false)

        initUI()

        return mBinding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        (requireActivity() as CustomeActivity).showNavBar()
        mModel.saveForm(mFormBinding.title.text.toString(),
            mFormBinding.description.text.toString(),
            0,
            mModel.imageUri.value)
    }

    private fun initUI() {
        mFormBinding.form.visibility = View.VISIBLE
        searchViewInit(mBinding.searchView)
        manualButtonInit(mBinding.manualButton)
        saveButtonInit(mBinding.saveButton)
        imagePicker(mFormBinding.pickImageButton)

        val adapter = queryAdapterInit(mFormBinding)
        modelInit(adapter)
    }

    private fun modelInit(adapter: QueryAdapter) {
        mModel.title.observe(viewLifecycleOwner) { newData ->
            mFormBinding.title.setText(if (!newData.isNullOrBlank()) newData else "")
        }
        mModel.description.observe(viewLifecycleOwner) { newData: String? ->
            mFormBinding.description.setText(if (!newData.isNullOrBlank()) Html.fromHtml(newData,
                Html.FROM_HTML_MODE_COMPACT)
            else "")
        }
        mModel.chapters.observe(viewLifecycleOwner) { newData: Int? ->
            if (newData != null) mFormBinding.numberOfChapter.setText(newData.toString())
        }
        mModel.imageUri.observe(viewLifecycleOwner) { newData: Uri? ->
            if (newData == null) return@observe

            Glide.with(this)
                .load(newData)
                .error(Glide.with(this).load(RCore.drawable.baseline_broken_image_24))
                .into(mFormBinding.imageView)
        }

        mModel.selectorList.observe(viewLifecycleOwner) { newData -> adapter.updateData(newData) }
    }

    private fun queryAdapterInit(form: SeriesFormLayoutBinding): QueryAdapter {
        val adapter = QueryAdapter(mModel) {
            mFormBinding.form.visibility = View.VISIBLE
            disableView(form.title, form.description, form.numberOfChapter, form.pickImageButton)
            mBinding.searchView.hide()
        }
        mBinding.result.adapter = adapter
        mBinding.result.layoutManager = LinearLayoutManager(context)
        return adapter
    }

    private fun imagePicker(pickImage: Button) {
        val getCoverImage = registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
            if (uri == null) {
                Snackbar.make(requireView(), "File not selected", Snackbar.ANIMATION_MODE_SLIDE)
                    .show()
                return@registerForActivityResult
            }
            mModel.imageUri.value = uri
        }
        pickImage.setOnClickListener {
            getCoverImage.launch("image/*")
        }
    }

    private fun saveButtonInit(saveButton: Button) {
        saveButton.setOnClickListener {
            CoroutineScope(Dispatchers.IO).launch {
                try {
                    testAndSetInputError(mFormBinding.title)
                    addSeries()
                    withContext(Dispatchers.Main) {
                        findNavController().popBackStack()
                    }
                } catch (mismatchException: InputMismatchException) {
                    withContext(Dispatchers.Main) {
                        mFormBinding.form.visibility = View.VISIBLE
                        Snackbar.make(requireContext(),
                            mBinding.root,
                            mismatchException.message.toString(),
                            Snackbar.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }

    private fun manualButtonInit(manualButton: Button) {
        manualButton.setOnClickListener {
            it.visibility = View.GONE
            mBinding.searchBar.visibility = View.GONE
            mFormBinding.form.visibility = View.VISIBLE
            mFormBinding.pickImageButton.visibility = View.VISIBLE
            enableView(mFormBinding.title,
                mFormBinding.description,
                mFormBinding.numberOfChapter,
                mFormBinding.pickImageButton)
        }
    }

    private fun searchViewInit(searchView: SearchView) {
        if (!searchView.isSetupWithSearchBar) searchView.setupWithSearchBar(mBinding.searchBar)
        searchView.addTransitionListener { _: SearchView, _: SearchView.TransitionState, newState: SearchView.TransitionState ->
            when (newState) {
                SearchView.TransitionState.SHOWING -> (requireActivity() as CustomeActivity).hideBars()
                SearchView.TransitionState.HIDING -> (requireActivity() as CustomeActivity).showBars()
                else -> return@addTransitionListener
            }
        }
        searchView.editText.setOnEditorActionListener { it, _, _ ->
            queryDB(it, it.text.toString())
            false
        }
    }

    private fun disableView(vararg views: View) {
        views.forEach { it.isEnabled = false }
    }

    private fun enableView(vararg views: View) {
        views.forEach { it.isEnabled = true }
    }

    private suspend fun addSeries() = withContext(Dispatchers.IO) {
        try {
            db.seriesDao()
                .update(Series(mFormBinding.title.text!!.toString(),
                    ReadingState.READING,
                    true,
                    mFormBinding.description.text?.toString(),
                    try {
                        mFormBinding.numberOfChapter.text?.toString()?.toInt()
                    } catch (_: NumberFormatException) {
                        null
                    },
                    mModel.imageUri.value,
                    (mFormBinding.numberOfChapter.text!!.toString() != "") && (mFormBinding.numberOfChapter.text!!.toString()
                        .toInt() == 1),
                    ZonedDateTime.now(),
                    0
                ))
        } catch (constraintException: SQLiteConstraintException) {
            Snackbar.make(requireContext(),
                mBinding.root,
                "${getString(R.string.entry_already_exist)} ${mFormBinding.title.text.toString()}",
                Snackbar.LENGTH_SHORT).show()
        }
    }

    private fun testAndSetInputError(input: EditText) {
        if (input.text.toString() != "") return

        input.error = getString(R.string.required_input)
        throw InputMismatchException(getString(R.string.required_inputs))
    }

    private fun queryDB(view: View, search: String) {
        view.isEnabled = false
        CoroutineScope(Dispatchers.IO).launch {
            withContext(Dispatchers.IO) {
                TrafficStats.setThreadStatsTag(Thread.currentThread().id.toInt())
                val res = Anilist.getInstance().searchByName(search).data?.Page?.media
                withContext(Dispatchers.Main) {
                    mModel.selectorList.value = res
                    view.isEnabled = true
                }
            }
        }
    }

    /**
     * ViewModel class to store and manage liveData
     */
    class SeriesSearchModel(private val state: SavedStateHandle) : ViewModel() {
        val selectorList: MutableLiveData<List<SearchByNameQuery.Medium?>> by lazy {
            MutableLiveData<List<SearchByNameQuery.Medium?>>()
        }
        val title: MutableLiveData<String> = state.getLiveData("title")
        val description: MutableLiveData<String> = state.getLiveData("description")
        val chapters: MutableLiveData<Int> = state.getLiveData("chapters")
        val imageUri: MutableLiveData<Uri> = state.getLiveData("imageUri")

        fun saveForm(title: String?, description: String?, chapters: Int?, imageUrl: Uri?) {
            state["title"] = title
            state["description"] = description
            state["chapters"] = chapters
            state["imageUrl"] = imageUrl
        }
    }

    /**
     * RecyclerView that manage the query result as a list of entry with only the name
     */
    class QueryAdapter(private val model: SeriesSearchModel, private val resultAction: () -> Unit) :
        RecyclerView.Adapter<QueryAdapter.ViewHolder>() {
        private var dataSet = List<SearchByNameQuery.Medium?>(0) { null }

        data class ViewHolder(val view: SeriesSearchSelectorBinding) : RecyclerView.ViewHolder(view.root)

        fun updateData(newData: List<SearchByNameQuery.Medium?>) {
            val prevSize = dataSet.size
            val newSize = newData.size
            dataSet = newData
            notifyItemRangeChanged(0, max(prevSize, newSize))
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val view = SeriesSearchSelectorBinding.inflate(LayoutInflater.from(parent.context))
            return ViewHolder(view)
        }

        override fun getItemCount(): Int {
            return dataSet.size
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            val view = holder.view

            val englishTitle = dataSet[position]?.title?.english
            val romajiTitle = dataSet[position]?.title?.romaji
            val nativeTitle = dataSet[position]?.title?.native

            val title = englishTitle ?: romajiTitle ?: nativeTitle
            val description = dataSet[position]?.description
            val chapters = dataSet[position]?.chapters
            val imageUrl = dataSet[position]?.coverImage?.large

            view.containerMangaName.text = title

            Glide.with(view.root).load(imageUrl).circleCrop().into(view.mangaCover)

            view.containerMangaName.isClickable = false

            setContainerClickListener(view, title, description, chapters, imageUrl)
        }

        private fun setContainerClickListener(view: SeriesSearchSelectorBinding,
            title: String?,
            description: String?,
            chapters: Int?,
            imageUrl: String?) {
            view.container.setOnClickListener {
                model.title.value = title
                model.description.value = description
                model.chapters.value = chapters
                model.imageUri.value = Uri.parse(imageUrl)
                resultAction()
            }
        }
    }
}