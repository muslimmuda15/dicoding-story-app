package com.rachmad.training.dicodingstoryapp.util

import android.content.Context
import android.location.Address
import android.location.Geocoder
import java.lang.Exception
import java.util.*

class Geolocation(context: Context) {
    lateinit var geocoder: Geocoder
    lateinit var addresses: List<Address>

    init {
        geocoder = Geocoder(context, Locale.getDefault())
    }

    fun setLocation(lat: Double, long: Double) {
        addresses = geocoder.getFromLocation(lat, long, 1)
    }

    val address: String?
        get() = try {
            addresses[0].getAddressLine(0)
        } catch (e: Exception) {
            null
        }

    val city: String?
        get() = try {
            addresses[0].locality
        } catch (e: Exception) {
            null
        }

    val state: String?
        get() = try {
            addresses[0].adminArea
        } catch (e: Exception) {
            null
        }

    val country: String?
        get() = try {
            addresses[0].countryName
        } catch (e: Exception) {
            null
        }

    val postalCode: String?
        get() = try {
            addresses[0].postalCode
        } catch (e: Exception) {
            null
        }

    val knownName: String?
        get() = try {
            addresses[0].featureName
        } catch (e: Exception) {
            null
        }
}