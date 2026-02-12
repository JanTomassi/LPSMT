package it.unitn.disi.lpsmt.g03.data.library

import androidx.room.*

@Dao
interface SeriesDao {

    @Query("SELECT * FROM series")
    fun getAll(): List<Series>

    @Query("SELECT * FROM series ORDER BY lastAccess desc")
    fun getAllSortByLastAccess(): List<Series>

    @Query("SELECT * FROM series WHERE uid IN (:ids)")
    fun getAllById(ids: IntArray): List<Series>

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