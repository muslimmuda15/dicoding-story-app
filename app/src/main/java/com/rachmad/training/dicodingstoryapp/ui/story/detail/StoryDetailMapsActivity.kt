package com.rachmad.training.dicodingstoryapp.ui.story.detail

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle

import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.rachmad.training.dicodingstoryapp.BaseActivity
import com.rachmad.training.dicodingstoryapp.R
import com.rachmad.training.dicodingstoryapp.databinding.ActivityStoryDetailMapsBinding
import com.rachmad.training.dicodingstoryapp.util.Geolocation
import com.rachmad.training.dicodingstoryapp.util.getParcelableExtra

class StoryDetailMapsActivity: BaseActivity<ActivityStoryDetailMapsBinding>(), OnMapReadyCallback {
    private lateinit var geoLocation: Geolocation
    private lateinit var mMap: GoogleMap
    private var latlong: LatLng? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        init()
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }

    private fun init(){
        latlong = getParcelableExtra(intent, LATLONG, LatLng::class.java)
        latlong?.let {
            geoLocation = Geolocation(this).apply {
                setLocation(it.latitude, it.longitude)
            }
        }
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        mMap.addMarker(MarkerOptions().position(latlong!!).title(geoLocation.getGlobalLocation()))
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latlong!!, 15f))
    }

    companion object {
        const val LATLONG = "latlong"

        fun instance(context: Context, latlong: LatLng): Intent = Intent(context, StoryDetailMapsActivity::class.java).apply {
            putExtra(LATLONG, latlong)
        }
    }

    override fun getBinding() = ActivityStoryDetailMapsBinding.inflate(layoutInflater)
}