package com.skele.locationtracker.core.di

import com.skele.locationtracker.model.LocationRepository
import com.skele.locationtracker.model.LocationRepositoryImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
interface RepositoryModule {
    @Binds
    @Singleton
    fun provideLocationRepository(locationRepositoryImpl: LocationRepositoryImpl): LocationRepository
}
