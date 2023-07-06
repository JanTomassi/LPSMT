package it.unitn.disi.lpsmt.g03.library

import android.net.Uri
import android.os.Parcel
import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import java.sql.Date

@Entity(indices = [Index(value = ["title"], unique = true)])
data class Series(
    @PrimaryKey(autoGenerate = true) val uid: Long = 0,
    @ColumnInfo("title") val title: String,
    @ColumnInfo("status") val status: ReadingState,
    @ColumnInfo("description") val description: String?,
    @ColumnInfo("chapters") val chapters: Int?,
    @ColumnInfo("image url") val imageUri: Uri?,
    @ColumnInfo("is one-shot") val isOne_shot: Boolean,
    @ColumnInfo("lastAccess") val lastAccess: Date
) : Parcelable {
    constructor(source: Parcel) : this(
        source.readLong(),
        source.readString()!!,
        source.readString()?.let { ReadingState.valueOf(it) }!!,
        source.readString(),
        source.readInt(),
        source.readString()?.let { Uri.parse(it) },
        source.readInt() > 0,
        source.readString()?.let { Date.valueOf(it) }!!
    )

    override fun describeContents() = 0

    override fun writeToParcel(dest: Parcel, flags: Int) = with(dest) {
        writeLong(uid)
        writeString(title)
        writeString(status.name)
        writeString(description)
        writeInt(chapters ?: 0)
        writeString(imageUri.toString())
        writeInt(if (isOne_shot) 1 else 0)
        writeString(lastAccess.toString())
    }

    companion object {
        @JvmField
        val CREATOR: Parcelable.Creator<Series> = object : Parcelable.Creator<Series> {
            override fun createFromParcel(source: Parcel): Series = Series(source)
            override fun newArray(size: Int): Array<Series?> = arrayOfNulls(size)
        }
    }
}