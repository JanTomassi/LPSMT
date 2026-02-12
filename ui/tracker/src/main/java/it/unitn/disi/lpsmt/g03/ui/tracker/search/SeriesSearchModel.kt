package it.unitn.disi.lpsmt.g03.ui.tracker.search

import android.net.Uri
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import it.unitn.disi.lpsmt.g03.data.graphql.SearchByNameQuery

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
    val status: MutableLiveData<String> by lazy { MutableLiveData<String>() }
}