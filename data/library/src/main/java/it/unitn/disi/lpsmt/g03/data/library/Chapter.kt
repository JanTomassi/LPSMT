package it.unitn.disi.lpsmt.g03.data.library

import android.net.Uri
import android.os.Parcel
import android.os.Parcelable
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
    indices = [Index("seriesId"), Index("chapter_num"), Index("state"), Index("lastAccess")])
data class Chapter(@ColumnInfo("seriesId") val seriesId: Long,
    @ColumnInfo("chapter_title") val chapter: String,
    @ColumnInfo("chapter_num") val chapterNum: Int,
    @ColumnInfo("pages") val pages: Int,
    @ColumnInfo("current_page") val currentPage: Int,
    @ColumnInfo("state") val state: ReadingState,
    @ColumnInfo("comic_file") val file: Uri?,
    @ColumnInfo("lastAccess") val lastAccess: ZonedDateTime,
    @PrimaryKey(autoGenerate = true) val uid: Long = 0) : Parcelable {
    constructor(source: Parcel) : this(source.readLong(), // seriesId
        source.readString()!!,                            // chapter
        source.readInt(),                                 // chapterNum
        source.readInt(),                                 // pages
        source.readInt(),                                 // currentPage
        ReadingState.values()[source.readInt()],          // state
        Uri.parse(source.readString()),                   // file
        ZonedDateTime.parse(source.readString()),         // lastAccess
        source.readLong()                                 // uid
    )

    override fun describeContents() = 0

    override fun writeToParcel(dest: Parcel, flags: Int) = with(dest) {
        writeLong(seriesId)
        writeString(chapter)
        writeInt(chapterNum)
        writeInt(pages)
        writeInt(currentPage)
        writeInt(state.ordinal)
        writeString(file.toString())
        writeString(lastAccess.toString())
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