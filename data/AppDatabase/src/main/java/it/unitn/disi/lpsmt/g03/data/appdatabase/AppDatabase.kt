package it.unitn.disi.lpsmt.g03.data.appdatabase

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import it.unitn.disi.lpsmt.g03.data.library.Chapter
import it.unitn.disi.lpsmt.g03.data.library.ChapterDao
import it.unitn.disi.lpsmt.g03.data.library.Series
import it.unitn.disi.lpsmt.g03.data.library.SeriesDao

class AppDatabase private constructor() {
    companion object {

        @Volatile
        private var instance: AppDatabaseInstance? = null

        fun getInstance(context: Context?) = instance ?: synchronized(this) {
            instance ?: Room.databaseBuilder(context
                    ?: throw NullPointerException("Trying to instantiate database without a context"),
                AppDatabaseInstance::class.java,
                "database-name").fallbackToDestructiveMigration().build()
        }
    }

    @Database(entities = [Series::class, Chapter::class], version = 1, exportSchema = false)
    @TypeConverters(Converters::class)
    abstract class AppDatabaseInstance : RoomDatabase() {
        abstract fun chapterDao(): ChapterDao
        abstract fun seriesDao(): SeriesDao
    }
}