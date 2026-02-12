package it.unitn.disi.lpsmt.g03.ui.library.common

import androidx.recyclerview.selection.ItemDetailsLookup
import androidx.recyclerview.selection.ItemKeyProvider
import androidx.recyclerview.selection.SelectionTracker
import androidx.recyclerview.widget.RecyclerView
import androidx.viewbinding.ViewBinding

abstract class CustomAdapter<binding : ViewBinding, dataSetType, keyType>(var dataSet: List<dataSetType>) :
    RecyclerView.Adapter<CustomAdapter<binding, dataSetType, keyType>.ViewHolder>() {

    lateinit var tracker: SelectionTracker<keyType>

    abstract fun update(list: List<dataSetType>)

    abstract inner class ViewHolder(val view: binding) : RecyclerView.ViewHolder(view.root) {
        abstract fun getItem(): ItemDetailsLookup.ItemDetails<keyType>
        abstract fun bind(item: dataSetType)
    }

    abstract class ItemsDetailsLookup<keyType> : ItemDetailsLookup<keyType>()

    abstract class ItemsKeyProvider<keyType> : ItemKeyProvider<keyType>(SCOPE_MAPPED)
}