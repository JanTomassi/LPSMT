package it.unitn.disi.lpsmt.g03.appdatabase

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import it.unitn.disi.lpsmt.g03.library.Chapter
import it.unitn.disi.lpsmt.g03.library.ChapterDao
import it.unitn.disi.lpsmt.g03.library.Series

class AppDatabase private constructor() {
    companion object {

        @Volatile
        private var instance: AppDatabaseInstance? = null

        fun getInstance(context: Context) = instance ?: synchronized(this) {
            instance ?: Room.databaseBuilder(
                context, AppDatabaseInstance::class.java, "database-name"
            ).fallbackToDestructiveMigration().build()
        }
    }

    @Database(entities = [Series::class, Chapter::class], version = 1, exportSchema = false)
    abstract class AppDatabaseInstance : RoomDatabase() {
        abstract fun chapterDao(): ChapterDao
    }
}