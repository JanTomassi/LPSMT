package it.unitn.disi.lpsmt.g03.tracking

import androidx.room.*

@Dao
interface TrackerSeriesDao {
    @Query("SELECT * FROM tracker_series")
    fun getAll(): List<TrackerSeries>

    @Query("SELECT * FROM tracker_series WHERE uid IN (:ids)")
    fun getAllById(ids: IntArray): List<TrackerSeries>

    @Insert
    fun insertAll(vararg series: TrackerSeries)

    @Insert
    fun insert(series: TrackerSeries)

    @Delete
    fun delete(series: TrackerSeries)
}