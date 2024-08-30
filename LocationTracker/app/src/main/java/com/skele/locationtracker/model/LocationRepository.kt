package com.skele.locationtracker.model

import kotlinx.coroutines.flow.Flow

interface LocationRepository {
    suspend fun saveLocation(location: LocationEntity)

    suspend fun deleteLocation(location: LocationEntity)

    fun getLocationsAsFlow(): Flow<List<LocationEntity>>

    suspend fun getLocations(): List<LocationEntity>
}
