package it.unitn.disi.lpsmt.g03.ui.tracker.selection

import androidx.recyclerview.selection.ItemKeyProvider
import it.unitn.disi.lpsmt.g03.tracking.TrackerSeries
import it.unitn.disi.lpsmt.g03.ui.tracker.TrackerAdapter

class ItemsKeyProvider(private val adapter: TrackerAdapter) : ItemKeyProvider<Long>(
    SCOPE_MAPPED
) {

    override fun getKey(position: Int): Long {
        val singleEntryList: MutableList<TrackerSeries> = mutableListOf<TrackerSeries>()
        for (item in adapter.getAdapters()) {
            singleEntryList.addAll(item.getDataSet())
        }
        return singleEntryList[position].uid
    }

    override fun getPosition(key: Long): Int {
        val singleEntryList: MutableList<TrackerSeries> = mutableListOf<TrackerSeries>()
        for (item in adapter.getAdapters()) {
            singleEntryList.addAll(item.getDataSet())
        }
        return singleEntryList.indexOfFirst { it.uid == key }
    }
}