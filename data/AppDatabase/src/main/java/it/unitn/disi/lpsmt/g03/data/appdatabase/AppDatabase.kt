package it.unitn.disi.lpsmt.g03.data.appdatabase

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import it.unitn.disi.lpsmt.g03.data.library.Chapter
import it.unitn.disi.lpsmt.g03.data.library.ChapterDao
import it.unitn.disi.lpsmt.g03.data.library.Series
import it.unitn.disi.lpsmt.g03.data.library.SeriesDao

class AppDatabase private constructor() {
    @Database(entities = [Series::class, Chapter::class], version = 1, exportSchema = false)
    @TypeConverters(Converters::class)
    abstract class AppDatabaseInstance : RoomDatabase() {
        abstract fun chapterDao(): ChapterDao
        abstract fun seriesDao(): SeriesDao
    }
}