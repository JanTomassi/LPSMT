package it.unitn.disi.lpsmt.g03.data.appdatabase

import android.content.Context
import androidx.room.Room
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import it.unitn.disi.lpsmt.g03.data.library.ChapterDao
import it.unitn.disi.lpsmt.g03.data.library.SeriesDao
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
class DatabaseProvider {
    @Provides
    fun provideChapterDao(appDatabase: AppDatabase.AppDatabaseInstance): ChapterDao {
        return appDatabase.chapterDao()
    }

    @Provides
    fun provideSeriesDao(appDatabase: AppDatabase.AppDatabaseInstance): SeriesDao {
        return appDatabase.seriesDao()
    }

    @Provides
    @Singleton
    fun provideAppDatabase(@ApplicationContext appContext: Context): AppDatabase.AppDatabaseInstance {
        return Room.databaseBuilder(
            appContext,
            AppDatabase.AppDatabaseInstance::class.java,
            "mangaCheck-db"
        ).build()
    }
}