package com.skele.locationtracker.model

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface LocationDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLocation(location: LocationEntity)

    @Delete
    suspend fun deleteLocation(location: LocationEntity)

    @Query("SELECT * FROM location_table")
    fun getAllLocations(): Flow<List<LocationEntity>>

    @Query("SELECT * FROM location_table")
    suspend fun getLocations(): List<LocationEntity>
}
