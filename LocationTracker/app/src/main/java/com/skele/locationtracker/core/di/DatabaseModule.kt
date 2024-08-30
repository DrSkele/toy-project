package com.skele.locationtracker.core.di

import android.content.Context
import androidx.room.Room
import com.skele.locationtracker.model.LocationDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {
    @Provides
    @Singleton
    fun provideTaskDatabase(
        @ApplicationContext context: Context,
    ): LocationDatabase =
        Room
            .databaseBuilder(
                context.applicationContext,
                LocationDatabase::class.java,
                "location.db",
            ).build()
}
