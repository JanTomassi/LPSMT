package it.unitn.disi.lpsmt.g03.ui.library

import android.database.sqlite.SQLiteConstraintException
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
import androidx.fragment.app.viewModels
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.android.material.search.SearchView
import com.google.android.material.snackbar.Snackbar
import it.unitn.disi.lpsmt.g03.appdatabase.AppDatabase
import it.unitn.disi.lpsmt.g03.core.BarVisibility
import it.unitn.disi.lpsmt.g03.core.databinding.SeriesSearchSelectorBinding
import it.unitn.disi.lpsmt.g03.data.anilist.Anilist
import it.unitn.disi.lpsmt.g03.data.graphql.SearchByNameQuery
import it.unitn.disi.lpsmt.g03.library.ReadingState
import it.unitn.disi.lpsmt.g03.library.Series
import it.unitn.disi.lpsmt.g03.ui.library.databinding.SeriesFormLayoutBinding
import it.unitn.disi.lpsmt.g03.ui.library.databinding.SeriesSearchLayoutBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.sql.Date
import java.time.Instant
import java.util.InputMismatchException


class SeriesSearchFragment : Fragment() {

    private var _binding: SeriesSearchLayoutBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    private val model: SeriesSearchModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        (requireActivity() as BarVisibility).hideNavBar()
        _binding = SeriesSearchLayoutBinding.inflate(inflater, null, false)

        initAutocomplete()

        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        (requireActivity() as BarVisibility).showNavBar()
    }

    private fun initAutocomplete() {
        searchViewInit(binding.searchView)
        manualButtonInit(binding.manualButton)
        saveButtonInit(binding.saveButton)
        imagePicker(binding.form.pickImageButton)

        val adapter = queryAdapterInit(binding.form)
        modelInit(adapter)
    }

    private fun modelInit(adapter: QueryAdapter) {
        model.title.observe(viewLifecycleOwner) { newData ->
            binding.form.title.setText(if (!newData.isNullOrBlank()) newData else "")
        }
        model.description.observe(viewLifecycleOwner) { newData: String? ->
            binding.form.description.setText(
                if (!newData.isNullOrBlank()) Html.fromHtml(newData, Html.FROM_HTML_MODE_COMPACT)
                else ""
            )
        }
        model.chapters.observe(viewLifecycleOwner) { newData: Int? ->
            if (newData != null) binding.form.numberOfChapter.setText(newData.toString())
        }
        model.imageUri.observe(viewLifecycleOwner) { newData: Uri? ->
            if (newData == null) return@observe

            Glide.with(this).load(newData).error(Glide.with(this).load(R.drawable.baseline_broken_image_24))
                .into(binding.form.imageView)
        }

        model.selectorList.observe(viewLifecycleOwner) { newData -> adapter.updateData(newData) }
    }

    private fun queryAdapterInit(form: SeriesFormLayoutBinding): QueryAdapter {
        val adapter = QueryAdapter(model) {
            binding.form.root.visibility = View.VISIBLE
            disableView(
                form.title, form.description, form.numberOfChapter, form.pickImageButton
            )
            binding.searchView.hide()
        }
        binding.result.adapter = adapter
        binding.result.layoutManager = LinearLayoutManager(context)
        return adapter
    }

    private fun imagePicker(pickImage: Button) {
        val getCoverImage = registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
            if (uri == null) {
                Snackbar.make(requireView(), "File not selected", Snackbar.ANIMATION_MODE_SLIDE).show()
                return@registerForActivityResult
            }
            model.imageUri.value = uri
        }
        pickImage.setOnClickListener {
            getCoverImage.launch("image/*")
        }
    }

    private fun saveButtonInit(saveButton: Button) {
        saveButton.setOnClickListener {
            try {
                testAndSetInputError(binding.form.title)
                addSeries()
                findNavController().popBackStack()
            } catch (mismatchException: InputMismatchException) {
                binding.form.root.visibility = View.VISIBLE
                Snackbar.make(
                    requireContext(), binding.root, mismatchException.message.toString(), Snackbar.LENGTH_SHORT
                ).show()
            }
        }
    }

    private fun manualButtonInit(manualButton: Button) {
        manualButton.setOnClickListener {
            it.visibility = View.GONE
            binding.searchBar.visibility = View.GONE
            binding.form.root.visibility = View.VISIBLE
            binding.form.pickImageButton.visibility = View.VISIBLE
            enableView(
                binding.form.title, binding.form.description, binding.form.numberOfChapter, binding.form.pickImageButton
            )
        }
    }

    private fun searchViewInit(searchView: SearchView) {
        if (!searchView.isSetupWithSearchBar) searchView.setupWithSearchBar(binding.searchBar)
        searchView.addTransitionListener { _: SearchView, _: SearchView.TransitionState, newState: SearchView.TransitionState ->
            when (newState) {
                SearchView.TransitionState.SHOWING -> (requireActivity() as BarVisibility).hideBars()
                SearchView.TransitionState.HIDING -> (requireActivity() as BarVisibility).showBars()

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

    private fun addSeries() {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                AppDatabase.getInstance(context).seriesDao().insertAll(
                    Series(
                        title = binding.form.title.text!!.toString(),
                        status = ReadingState.READING,
                        isOne_shot = (binding.form.numberOfChapter.text!!.toString() != "") && (binding.form.numberOfChapter.text!!.toString()
                            .toInt() == 1),
                        lastAccess = Date(
                            Instant.now().toEpochMilli()
                        ),
                        description = binding.form.description.text?.toString(),
                        imageUri = model.imageUri.value,
                        chapters = try {
                            binding.form.numberOfChapter.text?.toString()?.toInt()
                        } catch (_: NumberFormatException) {
                            null
                        }
                    )
                )
            } catch (constraintException: SQLiteConstraintException) {
                Snackbar.make(
                    requireContext(),
                    binding.root,
                    "${getString(R.string.entry_already_exist)} ${binding.form.title.text.toString()}",
                    Snackbar.LENGTH_SHORT
                ).show()
            }
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
                val res = Anilist.getInstance().searchByName(search).data?.Page?.media
                withContext(Dispatchers.Main) {
                    model.selectorList.value = res
                    view.isEnabled = true
                }
            }
        }
    }

    /**
     * ViewModel class to store and manage liveData
     */
    class SeriesSearchModel : ViewModel() {
        val selectorList: MutableLiveData<List<SearchByNameQuery.Medium?>> by lazy {
            MutableLiveData<List<SearchByNameQuery.Medium?>>()
        }
        val title: MutableLiveData<String> by lazy { MutableLiveData<String>() }
        val description: MutableLiveData<String> by lazy { MutableLiveData<String>() }
        val chapters: MutableLiveData<Int> by lazy { MutableLiveData<Int>() }
        val imageUri: MutableLiveData<Uri> by lazy { MutableLiveData<Uri>() }
    }

    /**
     * RecyclerView that manage the query result as a list of entry with only the name
     */
    class QueryAdapter(private val model: SeriesSearchModel, private val resultAction: () -> Unit) :
        RecyclerView.Adapter<QueryAdapter.ViewHolder>() {
        private var dataSet = List<SearchByNameQuery.Medium?>(0) { null }

        data class ViewHolder(val view: SeriesSearchSelectorBinding) : RecyclerView.ViewHolder(view.root)

        fun updateData(newData: List<SearchByNameQuery.Medium?>) {
            dataSet = newData
            notifyDataSetChanged()
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

        private fun setContainerClickListener(
            view: SeriesSearchSelectorBinding, title: String?, description: String?, chapters: Int?, imageUrl: String?
        ) {
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