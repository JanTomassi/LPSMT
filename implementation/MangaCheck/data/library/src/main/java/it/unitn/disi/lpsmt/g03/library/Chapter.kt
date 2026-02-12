package it.unitn.disi.lpsmt.g03.library

import android.net.Uri
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import java.time.ZonedDateTime


@Entity(foreignKeys = [ForeignKey(entity = Series::class,
    parentColumns = ["uid"],
    childColumns = ["seriesId"])])
data class Chapter(@PrimaryKey(autoGenerate = true) val uid: Long,
    @ColumnInfo("seriesId") val seriesId: Long,
    @ColumnInfo("chapter_title") val chapter: String,
    @ColumnInfo("chapter_num") val chapterNum: Int,
    @ColumnInfo("current_page") val currentPage: Int,
    @ColumnInfo("state") val state: ReadingState,
    @ColumnInfo("comic_file") val file: Uri?,
    @ColumnInfo("lastAccess") val lastAccess: ZonedDateTime)