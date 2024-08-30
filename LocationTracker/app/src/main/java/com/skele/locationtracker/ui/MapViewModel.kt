package com.skele.locationtracker.ui

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.gms.maps.model.LatLng
import com.skele.locationtracker.model.LocationEntity
import com.skele.locationtracker.model.LocationRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MapViewModel
    @Inject
    constructor(
        private val locationRepository: LocationRepository,
    ) : ViewModel() {
        private var _mapScreenState = mutableStateOf<MapScreenState>(MapScreenState.Idle)
        val mapScreenState by _mapScreenState

        val locations = locationRepository.getLocationsAsFlow()

        fun setLocation(location: LatLng) {
            _mapScreenState.value = MapScreenState.Tracking(location)
        }

        fun saveLocation(location: LatLng) {
            viewModelScope.launch {
                locationRepository.saveLocation(
                    LocationEntity(
                        latitude = location.latitude,
                        longitude = location.longitude,
                    ),
                )
            }
        }

        fun deleteLocation(location: LocationEntity) {
            viewModelScope.launch {
                locationRepository.deleteLocation(location)
            }
        }

        suspend fun copyToClipboard(): String {
            val list = locationRepository.getLocations()
            val header = "id,latitude,longitude\n"
            val data =
                list
                    .mapIndexed { index, location ->
                        "${index + 1},${location.latitude},${location.longitude}"
                    }.joinToString("\n")

            return header + data
        }
    }

sealed interface MapScreenState {
    data object Idle : MapScreenState

    data class Tracking(
        val location: LatLng,
    ) : MapScreenState
}
