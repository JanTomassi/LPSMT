package it.unitn.disi.lpsmt.g03.library

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey


@Entity(foreignKeys = [ForeignKey(entity = Series::class, parentColumns = ["uid"], childColumns = ["seriesId"])])
data class Chapter(
    @PrimaryKey(autoGenerate = true) val uid: Int,
    val seriesId: Int,
    @ColumnInfo(name = "chapter_title") val chapter: String,
    @ColumnInfo(name = "chapter_num") val chapterNum: Int,
    @ColumnInfo(name = "current_page") val currentPage: Int,
    @ColumnInfo(name = "state") val state: ReadingState
)