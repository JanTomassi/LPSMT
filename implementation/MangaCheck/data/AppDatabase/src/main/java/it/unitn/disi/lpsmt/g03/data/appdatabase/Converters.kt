package it.unitn.disi.lpsmt.g03.data.appdatabase

import android.net.Uri
import androidx.room.TypeConverter
import java.time.ZonedDateTime

class Converters {
    @TypeConverter
    fun fromTimestamp(value: String?): ZonedDateTime? {
        return value?.let { ZonedDateTime.parse(value) }
    }

    @TypeConverter
    fun dateToTimestamp(date: ZonedDateTime?): String? {
        return date?.toString()
    }

    @TypeConverter
    fun fromStringToUri(value: String?): Uri? {
        return value?.let { Uri.parse(it) }
    }

    @TypeConverter
    fun dateToTimestamp(uri: Uri?): String? {
        return uri?.toString()
    }
}
