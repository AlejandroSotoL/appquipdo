package com.tramites1cero1.centralizacion.data.network

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import androidx.core.content.ContextCompat
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.suspendCancellableCoroutine
import javax.inject.Inject
import kotlin.coroutines.resume

interface LocationProvider {
    suspend fun getCurrentLocation(): Location?
}

class LocationProviderImpl @Inject constructor(
    @ApplicationContext private val context: Context
) : LocationProvider {
    override suspend fun getCurrentLocation(): Location? {
        val hasFineLocationPermission = ContextCompat.checkSelfPermission(
            context, Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED

        if (!hasFineLocationPermission) {
            return null
        }

        val fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)

        return suspendCancellableCoroutine { continuation ->
            fusedLocationClient.getCurrentLocation(Priority.PRIORITY_HIGH_ACCURACY, null)
                .addOnSuccessListener { location ->
                    continuation.resume(location)
                }
                .addOnFailureListener {
                    continuation.resume(null)
                }
                .addOnCanceledListener {
                    continuation.cancel()
                }
        }
    }
}