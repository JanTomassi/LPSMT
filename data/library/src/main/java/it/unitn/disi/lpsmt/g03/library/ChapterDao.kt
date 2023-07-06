package it.unitn.disi.lpsmt.g03.library

import androidx.room.*

@Dao
interface ChapterDao {

    @Query("SELECT * FROM chapter")
    fun getAll(): List<Chapter>

    @Query("SELECT * FROM chapter WHERE uid IN (:ids)")
    fun getAllById(ids: IntArray): List<Chapter>

    @Insert
    fun insertAll(vararg chapter: Chapter)

    @Delete
    fun delete(chapter: Chapter)

}