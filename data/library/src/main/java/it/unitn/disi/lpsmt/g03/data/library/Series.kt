package it.unitn.disi.lpsmt.g03.data.library

import android.net.Uri
import android.os.Parcel
import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import java.time.ZonedDateTime

@Entity(indices = [Index(value = ["title"], unique = true)])
data class Series(@ColumnInfo("title") val title: String,
    @ColumnInfo("status") val status: ReadingState,
    @ColumnInfo("is one device") val isOnDevice: Boolean,
    @ColumnInfo("description") val description: String?,
    @ColumnInfo("chapters") val chapters: Int?,
    @ColumnInfo("image url") val imageUri: Uri?,
    @ColumnInfo("is one-shot") val isOne_shot: Boolean,
    @ColumnInfo("lastAccess") val lastAccess: ZonedDateTime,
    @ColumnInfo("last chapter read") val lastChapterRead: Int,
    @PrimaryKey(autoGenerate = true) val uid: Long = 0
) : Parcelable {
    constructor(source: Parcel) : this(source.readString()!!,
        source.readString()?.let { ReadingState.valueOf(it) }!!,
        source.readInt() > 0,
        source.readString(),
        source.readInt(),
        source.readString()?.let { Uri.parse(it) },
        source.readInt() > 0,
        source.readString()?.let { ZonedDateTime.parse(it) }!!,
        source.readInt(),
        source.readLong())

    override fun describeContents() = 0

    override fun writeToParcel(dest: Parcel, flags: Int) = with(dest) {
        writeString(title)
        writeString(status.name)
        writeInt(if(isOnDevice) 1 else 0)
        writeString(description)
        writeInt(chapters ?: 0)
        writeString(imageUri.toString())
        writeInt(if (isOne_shot) 1 else 0)
        writeString(lastAccess.toString())
        writeInt(lastChapterRead)
        writeLong(uid)
    }

    companion object {
        @JvmField
        val CREATOR: Parcelable.Creator<Series> = object : Parcelable.Creator<Series> {
            override fun createFromParcel(source: Parcel): Series = Series(source)
            override fun newArray(size: Int): Array<Series?> = arrayOfNulls(size)
        }
    }
}