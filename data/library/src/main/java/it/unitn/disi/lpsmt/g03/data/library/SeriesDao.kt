package it.unitn.disi.lpsmt.g03.data.library

import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
interface SeriesDao {

    @Query("SELECT * FROM series")
    fun getAll(): LiveData<List<Series>>

    @Query("SELECT * FROM series ORDER BY lastAccess desc")
    fun getAllSortByLastAccess(): LiveData<List<Series>>

    @Query("SELECT * FROM series WHERE uid IN (:ids)")
    fun getAllById(ids: IntArray): LiveData<List<Series>>

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