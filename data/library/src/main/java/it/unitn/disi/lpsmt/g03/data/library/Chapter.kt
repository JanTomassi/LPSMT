package it.unitn.disi.lpsmt.g03.data.library

import android.net.Uri
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import java.time.ZonedDateTime


@Entity(foreignKeys = [ForeignKey(entity = Series::class,
    parentColumns = ["uid"],
    childColumns = ["seriesId"],
    onDelete = ForeignKey.CASCADE,
    onUpdate = ForeignKey.CASCADE)],
    indices = [Index("seriesId"), Index("chapter_num"), Index("lastAccess")])
data class Chapter(@ColumnInfo("seriesId") val seriesId: Long,
    @ColumnInfo("chapter_title") val chapter: String,
    @ColumnInfo("chapter_num") val chapterNum: Int,
    @ColumnInfo("current_page") val currentPage: Int,
    @ColumnInfo("state") val state: ReadingState,
    @ColumnInfo("comic_file") val file: Uri?,
    @ColumnInfo("lastAccess") val lastAccess: ZonedDateTime,
    @PrimaryKey(autoGenerate = true) val uid: Long = 0)