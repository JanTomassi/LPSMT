package it.unitn.disi.lpsmt.g03.tracking

import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
interface TrackerSeriesDao {
    @Query("SELECT * FROM tracker_series")
    fun getAll(): List<TrackerSeries>

    @Query("SELECT * FROM tracker_series WHERE status=(:status)")
    fun getAllByStatus(status: ReadingState): LiveData<List<TrackerSeries>>

    @Query("SELECT * FROM tracker_series WHERE uid IN (:ids)")
    fun getAllById(ids: IntArray): List<TrackerSeries>

    @Query("UPDATE tracker_series SET status=(:new_status) WHERE uid=(:id)")
    fun updateStatus(id: Long, new_status: ReadingState)

    @Insert
    fun insertAll(vararg series: TrackerSeries)

    @Insert
    fun insert(series: TrackerSeries)

    @Delete
    fun delete(series: TrackerSeries)
}