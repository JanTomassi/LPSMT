package it.unitn.disi.lpsmt.g03.tracking

import android.net.Uri
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity("tracker_series", indices = [Index(value = ["title"], unique = true)])
data class TrackerSeries(
    @PrimaryKey(autoGenerate = true) val uid: Int = 0,
    @ColumnInfo("title") val title: String,
    @ColumnInfo("status") val status: ReadingState,
    @ColumnInfo("description") val description: String?,
    @ColumnInfo("chapters") val chapters: Int?,
    @ColumnInfo("image url") val imageUri: Uri?,
    @ColumnInfo("is one-shot") val isOne_shot: Boolean,
)