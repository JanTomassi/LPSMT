package it.unitn.disi.lpsmt.g03.data.library

import androidx.room.*

@Dao
interface ChapterDao {

    @Query("SELECT * FROM chapter")
    fun getAll(): List<Chapter>

    @Query("SELECT * FROM chapter ORDER BY lastAccess")
    fun getAllSorted(): List<Chapter>

    @Query("SELECT * FROM chapter WHERE seriesId = (:ids)")
    fun etWhereSeriesId(ids: IntArray): List<Chapter>

    @Query("SELECT * FROM chapter WHERE seriesId = (:seriesId) ORDER BY lastAccess")
    fun getWhereSeriesIdSorted(seriesId: Long): List<Chapter>

    @Insert
    fun insertAll(vararg chapter: Chapter)

    @Delete
    fun delete(chapter: Chapter)

}