package com.skele.locationtracker.ui

import android.Manifest
import android.annotation.SuppressLint
import android.os.Looper
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapProperties
import com.google.maps.android.compose.MapUiSettings
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.Polyline
import com.google.maps.android.compose.rememberCameraPositionState
import com.google.maps.android.compose.rememberMarkerState
import com.skele.locationtracker.model.LocationEntity
import com.skele.locationtracker.util.MultiplePermissionsHandler
import kotlinx.coroutines.launch

@SuppressLint("MissingPermission")
@Composable
fun MapScreen(
    modifier: Modifier,
    viewModel: MapViewModel = hiltViewModel(),
    snackbarHostState: SnackbarHostState,
) {
    val context = LocalContext.current
    val clipboardManager = LocalClipboardManager.current

    val currentState = viewModel.mapScreenState
    val locations by viewModel.locations.collectAsState(initial = emptyList())

    val cameraPositionState = rememberCameraPositionState()
    val uiSettings =
        remember(currentState) {
            MapUiSettings(myLocationButtonEnabled = currentState is MapScreenState.Tracking)
        }
    val properties by remember(currentState) {
        mutableStateOf(MapProperties(isMyLocationEnabled = currentState is MapScreenState.Tracking))
    }
    var isMapLoaded by remember {
        mutableStateOf(false)
    }

    val fusedLocationClient = remember { LocationServices.getFusedLocationProviderClient(context) }
    var hasPermission by remember { mutableStateOf(false) }

    var selected by remember { mutableStateOf<LocationEntity?>(null) }

    val coroutineScope = rememberCoroutineScope()

    // 현재 위치가 바뀌면 카메라를 현 위치로 이동
    LaunchedEffect(currentState) {
        if (currentState is MapScreenState.Tracking) {
            cameraPositionState.animate(
                CameraUpdateFactory.newCameraPosition(
                    CameraPosition(currentState.location, 17f, 0f, 0f),
                ),
                1000,
            )
        }
    }

    MultiplePermissionsHandler(
        permissions = listOf(Manifest.permission.ACCESS_FINE_LOCATION),
    ) { permissionResults ->
        if (permissionResults.all { permissions -> permissions.value }) {
            hasPermission = true

//            fusedLocationClient.lastLocation.addOnSuccessListener { location ->
//                viewModel.setLocation(LatLng(location.latitude, location.longitude))
//            }

            // get finer location update
            val locationRequest =
                LocationRequest
                    .Builder(Priority.PRIORITY_HIGH_ACCURACY, 10000)
                    .apply {
                        setMinUpdateIntervalMillis(5000)
                    }.build()

            val locationCallback =
                object : LocationCallback() {
                    override fun onLocationResult(locationResult: LocationResult) {
                        for (location in locationResult.locations) {
                            viewModel.setLocation(LatLng(location.latitude, location.longitude))
                        }
                    }
                }

            fusedLocationClient.requestLocationUpdates(
                locationRequest,
                locationCallback,
                Looper.getMainLooper(),
            )
        } else {
            // On permission denied
        }
    }

    Box(
        modifier = modifier.fillMaxSize(),
    ) {
        GoogleMap(
            cameraPositionState = cameraPositionState,
            onMapLoaded = { isMapLoaded = true },
            uiSettings = uiSettings,
            properties = properties,
            contentPadding = PaddingValues(bottom = 100.dp),
        ) {
            if (isMapLoaded) {
                for (location in locations) {
                    key(location.id) {
                        Marker(
                            state =
                                rememberMarkerState(
                                    position =
                                        LatLng(
                                            location.latitude,
                                            location.longitude,
                                        ),
                                ),
                            onClick = {
                                selected = location
                                false
                            },
                        )
                    }
                }
                Polyline(points = locations.map { LatLng(it.latitude, it.longitude) })
            }
        }
        if (!isMapLoaded) {
            Box(modifier = Modifier.fillMaxSize()) {
                Text(modifier = Modifier.align(Alignment.Center), text = "Loading...")
            }
        }
        if (hasPermission && currentState is MapScreenState.Tracking) {
            Text(
                modifier = Modifier.align(Alignment.TopCenter).padding(16.dp),
                text = currentState.location.toString(),
                color = Color.DarkGray,
            )
        }
        Row(
            modifier =
                Modifier
                    .align(Alignment.BottomEnd)
                    .padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            FloatingActionButton(
                onClick = {
                    coroutineScope.launch {
                        clipboardManager.setText(AnnotatedString(viewModel.copyToClipboard()))
                        snackbarHostState.showSnackbar("copied to clipboard")
                    }
                },
            ) {
                Icon(imageVector = Icons.Default.Share, contentDescription = "Share Location")
            }
            FloatingActionButton(
                onClick = {
                    if (hasPermission) {
                        viewModel.saveLocation(cameraPositionState.position.target)
                    }
                },
            ) {
                Icon(imageVector = Icons.Default.Add, contentDescription = "Save Location")
            }
        }
        if (selected != null) {
            MapDialog(
                onConfirm = {
                    viewModel.deleteLocation(selected!!)
                    selected = null
                },
                onDismiss = { selected = null },
            )
        }
    }
}

@Composable
fun MapScreenContent(modifier: Modifier = Modifier) {
}
