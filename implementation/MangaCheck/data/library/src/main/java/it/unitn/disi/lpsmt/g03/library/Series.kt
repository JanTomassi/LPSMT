package it.unitn.disi.lpsmt.g03.library

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Series(
    @PrimaryKey(autoGenerate = true) val uid: Int,
    @ColumnInfo("title") val title: String,
    @ColumnInfo("status") val status: ReadingState,
    @ColumnInfo("is online") val isOnline: Boolean,
    @ColumnInfo("is one-shot") val isOne_shot: Boolean
)
