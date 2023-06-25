package it.unitn.disi.lpsmt.g03.ui.tracker.search

import android.net.Uri
import android.os.Bundle
import android.text.Html
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.google.android.material.search.SearchView
import com.google.android.material.snackbar.Snackbar
import it.unitn.disi.lpsmt.g03.core.BarVisibility
import it.unitn.disi.lpsmt.g03.data.anilist.Anilist
import it.unitn.disi.lpsmt.g03.tracking.ReadingState
import it.unitn.disi.lpsmt.g03.ui.tracker.R
import it.unitn.disi.lpsmt.g03.ui.tracker.databinding.SeriesFormLayoutBinding
import it.unitn.disi.lpsmt.g03.ui.tracker.databinding.SeriesSearchLayoutBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
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

            Glide.with(this).load(newData)
                .error(Glide.with(this).load(R.drawable.baseline_broken_image_24))
                .into(binding.form.imageView)
        }

        model.selectorList.observe(viewLifecycleOwner) { newData -> adapter.updateData(newData) }

        val readingStateAdapter: ArrayAdapter<ReadingState> = ArrayAdapter<ReadingState>(
            requireContext(),
            R.layout.dropdown_menu_popup_item,
            ReadingState.values()
        )

        binding.form.spinner.setAdapter(readingStateAdapter)

        model.status.observe(viewLifecycleOwner) {
            binding.form.spinner.setAdapter(readingStateAdapter)
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

    private fun manualButtonInit(manualButton: Button) {
        manualButton.setOnClickListener {
            it.visibility = View.GONE
            binding.searchBar.visibility = View.GONE
            binding.form.root.visibility = View.VISIBLE
            binding.form.pickImageButton.visibility = View.VISIBLE
            enableView(
                binding.form.title,
                binding.form.description,
                binding.form.numberOfChapter,
                binding.form.pickImageButton
            )
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
                    requireContext(),
                    binding.root,
                    mismatchException.message.toString(),
                    Snackbar.LENGTH_SHORT
                ).show()
            }
        }
    }

    private fun imagePicker(pickImage: Button) {
        val getCoverImage =
            registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
                if (uri == null) {
                    Snackbar.make(requireView(), "File not selected", Snackbar.ANIMATION_MODE_SLIDE)
                        .show()
                    return@registerForActivityResult
                }
                model.imageUri.value = uri
            }
        pickImage.setOnClickListener {
            getCoverImage.launch("image/*")
        }
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

    private fun disableView(vararg views: View) {
        views.forEach { it.isEnabled = false }
    }

    private fun enableView(vararg views: View) {
        views.forEach { it.isEnabled = true }
    }

    private fun testAndSetInputError(input: EditText) {
        if (input.text.toString() != "") return

        input.error = getString(R.string.required_input)
        throw InputMismatchException(getString(R.string.required_inputs))
    }

    private fun addSeries() {}
}