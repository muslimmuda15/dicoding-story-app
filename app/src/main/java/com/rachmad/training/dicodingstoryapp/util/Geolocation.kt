package com.rachmad.training.dicodingstoryapp.util

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.os.Build
import androidx.core.app.ActivityCompat
import com.google.android.gms.location.*
import java.lang.Exception
import java.util.*

class Geolocation {
    private lateinit var activity: Activity
    constructor(context: Context){
        geocoder = Geocoder(context, Locale.getDefault())
    }

    constructor(activity: Activity){
        this.activity = activity
        geocoder = Geocoder(activity, Locale.getDefault())
    }

    private var geocoder: Geocoder
    private var addresses: List<Address>? = null

    fun setLocation(lat: Double, long: Double) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            geocoder.getFromLocation(lat, long, 1) {
                addresses = it
            }
        } else {
            addresses = geocoder.getFromLocation(lat, long, 1)
        }
    }

    val address: String?
        get() = try {
            addresses?.get(0)?.getAddressLine(0)
        } catch (e: Exception) {
            null
        }

    val city: String?
        get() = try {
            addresses?.get(0)?.locality
        } catch (e: Exception) {
            null
        }

    val state: String?
        get() = try {
            addresses?.get(0)?.adminArea
        } catch (e: Exception) {
            null
        }

    val country: String?
        get() = try {
            addresses?.get(0)?.countryName
        } catch (e: Exception) {
            null
        }

    val postalCode: String?
        get() = try {
            addresses?.get(0)?.postalCode
        } catch (e: Exception) {
            null
        }

    val knownName: String?
        get() = try {
            addresses?.get(0)?.featureName
        } catch (e: Exception) {
            null
        }

    private var fusedLocation: FusedLocationProviderClient? = null
    private var locationRequest: LocationRequest? = null
    private var locationCallback: LocationCallback? = null
    fun getLocationUpdate(callback: (Location?) -> Unit){
        fusedLocation = LocationServices.getFusedLocationProviderClient(activity)
        locationRequest = LocationRequest.create().apply {
            interval = 60000        // set 1 menit update
            fastestInterval = 60000 // set 1 menit juga
            smallestDisplacement = 100f // titik terkecil 100 meter
            priority = Priority.PRIORITY_HIGH_ACCURACY
        }
        locationCallback = object: LocationCallback(){
            override fun onLocationResult(result: LocationResult) {
                if(result.locations.isNotEmpty()){
                    callback(result.lastLocation)
                }
            }
        }
    }

    fun startLocationUpdate(){
        if(locationRequest != null && locationCallback != null) {
            if (ActivityCompat.checkSelfPermission(
                    activity,
                    Manifest.permission.ACCESS_FINE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(
                    activity,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                ActivityCompat.requestPermissions(activity, arrayOf(Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION), LOCATION_PERMISSION_REQUEST)
            } else {
                fusedLocation?.requestLocationUpdates(
                    locationRequest!!,
                    locationCallback!!,
                    null
                )
            }
        }
    }

    fun stopLocationUpdate(){
        locationCallback?.let {
            fusedLocation?.removeLocationUpdates(it)
        }
    }

    companion object {
        const val LOCATION_PERMISSION_REQUEST = 1
    }
}