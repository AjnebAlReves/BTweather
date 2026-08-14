package com.example.util

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Build
import android.os.Bundle
import androidx.core.content.ContextCompat
import com.example.data.remote.NetworkClient
import com.example.data.repository.WeatherRepositoryImpl
import com.example.domain.model.LocationCoordinates
import com.example.domain.model.WorldClockItem
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withContext
import kotlinx.coroutines.withTimeoutOrNull
import java.util.Locale
import java.util.TimeZone
import kotlin.coroutines.resume

sealed class LocationResult {
    data class Success(val coordinates: LocationCoordinates, val cityItem: WorldClockItem) : LocationResult()
    data class Error(val reason: LocationErrorType, val message: String) : LocationResult()
}

enum class LocationErrorType {
    PERMISSION_DENIED,
    SERVICES_DISABLED,
    UNAVAILABLE,
    GEOCODING_FAILED
}

object LocationServiceManager {

    fun hasLocationPermission(context: Context): Boolean {
        val fineLocation = ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
        val coarseLocation = ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_COARSE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
        return fineLocation || coarseLocation
    }

    fun isLocationServiceEnabled(context: Context): Boolean {
        val locationManager = context.getSystemService(Context.LOCATION_SERVICE) as? LocationManager ?: return false
        val gpsEnabled = try { locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) } catch (e: Exception) { false }
        val networkEnabled = try { locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER) } catch (e: Exception) { false }
        return gpsEnabled || networkEnabled
    }

    suspend fun getCurrentLocationAndCity(context: Context): LocationResult = withContext(Dispatchers.IO) {
        if (!hasLocationPermission(context)) {
            return@withContext LocationResult.Error(
                LocationErrorType.PERMISSION_DENIED,
                "Location permission not granted. Please allow location access to fetch weather for your exact position."
            )
        }

        if (!isLocationServiceEnabled(context)) {
            return@withContext LocationResult.Error(
                LocationErrorType.SERVICES_DISABLED,
                "Location services are turned off on this device. Please turn on GPS or Location in system settings."
            )
        }

        val coords = fetchLocationCoordinates(context)
            ?: return@withContext LocationResult.Error(
                LocationErrorType.UNAVAILABLE,
                "Unable to retrieve current GPS coordinates. Ensure high accuracy is enabled or try again."
            )

        val cityItem = reverseGeocode(context, coords.latitude, coords.longitude)
        LocationResult.Success(coordinates = coords, cityItem = cityItem)
    }

    private suspend fun fetchLocationCoordinates(context: Context): LocationCoordinates? {
        val locationManager = context.getSystemService(Context.LOCATION_SERVICE) as? LocationManager ?: return null

        // 1. Try last known location first for instantaneous response
        val lastGps = try { locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER) } catch (e: SecurityException) { null }
        val lastNetwork = try { locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER) } catch (e: SecurityException) { null }

        val bestLast = when {
            lastGps != null && lastNetwork != null -> if (lastGps.time > lastNetwork.time) lastGps else lastNetwork
            lastGps != null -> lastGps
            else -> lastNetwork
        }

        if (bestLast != null && (System.currentTimeMillis() - bestLast.time < 15 * 60 * 1000L)) {
            return LocationCoordinates(
                latitude = bestLast.latitude,
                longitude = bestLast.longitude,
                accuracyMeters = bestLast.accuracy,
                provider = bestLast.provider ?: "GPS"
            )
        }

        // 2. Request single fresh update with coroutine timeout
        return withTimeoutOrNull(6000L) {
            suspendCancellableCoroutine { continuation ->
                val listener = object : LocationListener {
                    override fun onLocationChanged(location: Location) {
                        try { locationManager.removeUpdates(this) } catch (e: Exception) {}
                        if (continuation.isActive) {
                            continuation.resume(
                                LocationCoordinates(
                                    latitude = location.latitude,
                                    longitude = location.longitude,
                                    accuracyMeters = location.accuracy,
                                    provider = location.provider ?: "GPS"
                                )
                            )
                        }
                    }
                    override fun onProviderDisabled(provider: String) {}
                    override fun onProviderEnabled(provider: String) {}
                    @Deprecated("Deprecated in Java")
                    override fun onStatusChanged(provider: String?, status: Int, extras: Bundle?) {}
                }

                continuation.invokeOnCancellation {
                    try { locationManager.removeUpdates(listener) } catch (e: Exception) {}
                }

                try {
                    if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                        locationManager.requestLocationUpdates(
                            LocationManager.GPS_PROVIDER,
                            0L,
                            0f,
                            listener,
                            context.mainLooper
                        )
                    }
                    if (locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
                        locationManager.requestLocationUpdates(
                            LocationManager.NETWORK_PROVIDER,
                            0L,
                            0f,
                            listener,
                            context.mainLooper
                        )
                    }
                } catch (e: SecurityException) {
                    if (continuation.isActive) continuation.resume(null)
                }
            }
        } ?: bestLast?.let {
            LocationCoordinates(
                latitude = it.latitude,
                longitude = it.longitude,
                accuracyMeters = it.accuracy,
                provider = it.provider ?: "Cached"
            )
        }
    }

    suspend fun reverseGeocode(context: Context, latitude: Double, longitude: Double): WorldClockItem = withContext(Dispatchers.IO) {
        var cityName = "Current Location"
        var countryName = "GPS"
        var adminArea = ""
        var countryCode = ""

        // 1. Try Android Geocoder
        try {
            if (Geocoder.isPresent()) {
                val geocoder = Geocoder(context, Locale.getDefault())
                val addresses = geocoder.getFromLocation(latitude, longitude, 1)
                val address = addresses?.firstOrNull()
                if (address != null) {
                    cityName = address.locality ?: address.subAdminArea ?: address.featureName ?: "My Location"
                    countryName = address.countryName ?: ""
                    adminArea = address.adminArea ?: ""
                    countryCode = address.countryCode ?: ""
                }
            }
        } catch (e: Exception) {
            // Geocoder failed, will fallback to network reverse geocode
        }

        // 2. If Android Geocoder didn't return city name, fallback to Nominatim reverse
        if (cityName == "Current Location" || countryName == "GPS") {
            try {
                val nominatimResult = NetworkClient.nominatimApi.reverse(lat = latitude, lon = longitude)
                val addr = nominatimResult.address
                if (addr != null) {
                    val nCity = addr.city ?: addr.town ?: addr.village ?: addr.municipality ?: addr.county
                    if (!nCity.isNullOrBlank()) {
                        cityName = nCity
                        countryName = addr.country ?: ""
                        adminArea = addr.state ?: ""
                        countryCode = addr.countryCode?.uppercase(Locale.ROOT) ?: ""
                    }
                }
            } catch (e2: Exception) {
                // Keep default names
            }
        }

        val timezoneId = TimeZone.getDefault().id ?: "UTC"
        val timeDetails = WeatherRepositoryImpl.calculateTimeDetails(timezoneId)

        WorldClockItem(
            id = "gps_current_location",
            cityName = cityName,
            countryName = if (adminArea.isNotBlank() && countryName.isNotBlank()) "$adminArea, $countryName" else countryName,
            adminArea = adminArea,
            countryCode = countryCode,
            timezoneId = timezoneId,
            latitude = latitude,
            longitude = longitude,
            formattedTime = timeDetails.formattedTime,
            formattedAmPm = timeDetails.amPm,
            formattedDate = timeDetails.formattedDate,
            timeDiffHours = timeDetails.diffHours,
            isDay = timeDetails.isDay,
            isGpsLocation = true
        )
    }
}
