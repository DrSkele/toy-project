package com.skele.locationtracker.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "location_table")
data class LocationEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val latitude: Double,
    val longitude: Double
)
