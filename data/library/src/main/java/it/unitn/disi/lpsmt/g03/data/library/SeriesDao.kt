package it.unitn.disi.lpsmt.g03.data.library

import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
interface SeriesDao {

    @Query("SELECT * FROM series")
    fun getAll(): LiveData<List<Series>>

    @Query("SELECT * FROM series WHERE status=(:status) ORDER BY lastAccess desc")
    fun getAllSortByLastAccess(status: ReadingState): LiveData<List<Series>>

    @Query("SELECT * FROM series WHERE uid IN (:ids)")
    fun getAllById(ids: IntArray): LiveData<List<Series>>

    @Query("SELECT * FROM series WHERE status=(:status)")
    fun getAllByStatus(status: ReadingState): LiveData<List<Series>>

    @Query("UPDATE series SET status=(:new_status) WHERE uid=(:id)")
    fun updateStatus(id: Long, new_status: ReadingState)

    @Query("UPDATE series SET `last chapter read`=(:newLastChapter) WHERE uid=(:id)")
    fun updateLastChapter(id: Long, newLastChapter: Int)

    @Insert
    fun insertAll(vararg series: Series)

    @Insert
    fun insert(series: Series)

    @Update
    fun update(series: Series)

    @Update
    fun updateAll(vararg series: Series)

    @Delete
    fun deleteAll(vararg series: Series)

    @Delete
    fun delete(series: Series)

}