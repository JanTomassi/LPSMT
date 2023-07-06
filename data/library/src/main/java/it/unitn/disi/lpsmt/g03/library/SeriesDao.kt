package it.unitn.disi.lpsmt.g03.library

import androidx.room.*

@Dao
interface SeriesDao {

    @Query("SELECT * FROM series")
    fun getAll(): List<Series>

    @Query("SELECT * FROM series ORDER BY lastAccess")
    fun getAllByLastAccess(): List<Series>

    @Query("SELECT * FROM series WHERE uid IN (:ids)")
    fun getAllById(ids: IntArray): List<Series>

    @Insert
    fun insertAll(vararg series: Series)

    @Insert
    fun insert(series: Series)

    @Delete
    fun delete(series: Series)

}