package it.unitn.disi.lpsmt.g03.data.library

import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
interface ChapterDao {

    @Query("SELECT * FROM chapter")
    fun getAll(): LiveData<List<Chapter>>

    @Query("SELECT * FROM chapter ORDER BY lastAccess")
    fun getAllSorted(): LiveData<List<Chapter>>

    @Query("SELECT * FROM chapter WHERE seriesId = (:ids)")
    fun getWhereSeriesId(ids: IntArray): LiveData<List<Chapter>>

    @Query("SELECT * FROM chapter WHERE seriesId = (:seriesId) ORDER BY lastAccess")
    fun getWhereSeriesIdSorted(seriesId: Long): LiveData<List<Chapter>>

    @Query("SELECT * FROM chapter WHERE seriesId = (:seriesId) AND chapter_num = (:chNum)")
    fun getChapterFromChNum(seriesId: Long, chNum: Int): LiveData<Chapter>

    @Insert
    fun insertAll(vararg chapter: Chapter)

    @Update
    fun update(chapter: Chapter)

    @Update
    fun updateAll(vararg chapters: Chapter)

    @Delete
    fun delete(chapter: Chapter)

    @Delete
    fun deleteAll(vararg chapter: Chapter)

}