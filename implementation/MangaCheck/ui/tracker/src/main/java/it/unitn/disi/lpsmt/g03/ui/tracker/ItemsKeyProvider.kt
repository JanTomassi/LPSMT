package it.unitn.disi.lpsmt.g03.ui.tracker

import androidx.recyclerview.selection.ItemKeyProvider


class ItemsKeyProvider(private val adapter: TrackerAdapter) : ItemKeyProvider<Long>(SCOPE_MAPPED) {
    override fun getKey(position: Int): Long = adapter.dataSet[position].uid
    override fun getPosition(key: Long): Int = adapter.dataSet.indexOfFirst { it.uid == key }
}
