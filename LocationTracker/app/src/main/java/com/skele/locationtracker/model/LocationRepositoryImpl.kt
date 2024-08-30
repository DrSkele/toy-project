package com.skele.locationtracker.model

import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class LocationRepositoryImpl
    @Inject
    constructor(
        private val database: LocationDatabase,
    ) : LocationRepository {
        private val locationDao = database.locationDao()

        override suspend fun saveLocation(location: LocationEntity) {
            locationDao.insertLocation(location)
        }

        override suspend fun deleteLocation(location: LocationEntity) {
            locationDao.deleteLocation(location)
        }

        override fun getLocationsAsFlow(): Flow<List<LocationEntity>> = locationDao.getAllLocations()

        override suspend fun getLocations(): List<LocationEntity> = locationDao.getLocations()
    }
