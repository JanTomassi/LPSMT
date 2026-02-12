package it.unitn.disi.lpsmt.g03.library

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import it.unitn.disi.lpsmt.g03.anilist.Anilist
import it.unitn.disi.lpsmt.g03.graphql.SearchByNameQuery
import it.unitn.disi.lpsmt.g03.library.databinding.SeriesSearchLayoutBinding
import it.unitn.disi.lpsmt.g03.mangacheck.databinding.SeriesSearchSelectorBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class SeriesSearchFragment : Fragment() {

    private var _binding: SeriesSearchLayoutBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    private val model: SeriesSearchModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = SeriesSearchLayoutBinding.inflate(inflater, container, false)

        binding.manualButton.setOnClickListener {
            findNavController().navigate(R.id.action_series_search_to_series_form)
        }

        binding.searchButton.setOnClickListener {
            it.isEnabled = false
            val textInput = binding.textInput
            val textInputString = textInput.text.toString()

            if (textInputString.isNotEmpty()) queryDB(it, textInputString)
            else {
                textInput.error = getString(R.string.empty_search)
                it.isEnabled = true
            }
        }

        val adapter = QueryAdapter()
        binding.searchResult.adapter = adapter
        binding.searchResult.layoutManager = LinearLayoutManager(context)
        model.selectorList.observe(viewLifecycleOwner) { newData -> adapter.updateData(newData) }

        return binding.root
    }

    private fun queryDB(button: View, search: String) {
        CoroutineScope(Dispatchers.IO).launch {
            withContext(Dispatchers.IO) {
                val res = Anilist.getInstance().searchByName(search).data?.Page?.media
                withContext(Dispatchers.Main) {
                    model.selectorList.value = res
                    button.isEnabled = true
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
    }

    /**
     * ViewModel class to store and manage liveData
     */
    class QueryAdapter : RecyclerView.Adapter<QueryAdapter.ViewHolder>() {
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
            val englishTitle = dataSet[position]?.title?.english
            val romajiTitle = dataSet[position]?.title?.romaji
            val nativeTitle = dataSet[position]?.title?.native

            holder.view.containerMangaName.text = englishTitle ?: romajiTitle ?: nativeTitle
        }

    }
}