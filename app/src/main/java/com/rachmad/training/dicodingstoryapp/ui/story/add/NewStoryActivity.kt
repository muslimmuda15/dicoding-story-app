package com.rachmad.training.dicodingstoryapp.ui.story.add

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.view.MenuItem
import android.widget.Toast
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.ViewModelProvider
import com.rachmad.training.dicodingstoryapp.BaseActivity
import com.rachmad.training.dicodingstoryapp.R
import com.rachmad.training.dicodingstoryapp.databinding.ActivityNewStoryBinding
import com.rachmad.training.dicodingstoryapp.repository.UserPreference
import com.rachmad.training.dicodingstoryapp.util.Geolocation
import com.rachmad.training.dicodingstoryapp.util.ViewModelFactory
import com.rachmad.training.dicodingstoryapp.util.ui.gone
import com.rachmad.training.dicodingstoryapp.util.ui.visible

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "user")

class NewStoryActivity: BaseActivity<ActivityNewStoryBinding>() {
    private lateinit var viewModel: NewStoryViewModel
    private lateinit var geoLocation: Geolocation

    override fun onPause() {
        geoLocation.stopLocationUpdate()
        super.onPause()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        init()
        observer()
        listener()
    }

    private fun listener(){
        with(layout){
            location.setOnClickListener {
                loading.visible()
                geoLocation.getLocationUpdate{
                    it?.let {
                        geoLocation.setLocation(it.latitude, it.longitude)
                        location.text = geoLocation.address ?: geoLocation.city ?: geoLocation.state
                                ?: geoLocation.country ?: getString(
                            R.string.unknown_location
                        )
                        loading.gone()
                    } ?: run {
                        loading.gone()
                    }
                }
                geoLocation.startLocationUpdate()
            }
        }
    }

    private fun init(){
        viewModel = ViewModelProvider(this, ViewModelFactory(UserPreference.getInstance(dataStore)))[NewStoryViewModel::class.java]
        geoLocation = Geolocation(this)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)
        supportActionBar?.title = getString(R.string.create_story)


    }

    private fun observer(){
        viewModel.getUserLiveData().observe(this){
            layout.loading.gone()
            val items = arrayOf(it.name, getString(R.string.guest))
            layout.fullName.apply {
                setText(it.name)
                setSimpleItems(items)
            }
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        if(requestCode == Geolocation.LOCATION_PERMISSION_REQUEST){
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, R.string.permission_granted, Toast.LENGTH_SHORT).show()
                geoLocation.startLocationUpdate()
            } else if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_DENIED) {
                Toast.makeText(this, R.string.open_setting, Toast.LENGTH_SHORT).show()
                Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                    addCategory(Intent.CATEGORY_DEFAULT)
                    data = Uri.parse("package:$packageName")
                }.run(::startActivity)
                finish()
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId) {
            android.R.id.home -> onBackPressed()
        }
        return super.onOptionsItemSelected(item)
    }

    companion object {
        fun instance(context: Context): Intent = Intent(context, NewStoryActivity::class.java)
    }

    override fun getBinding(): ActivityNewStoryBinding = ActivityNewStoryBinding.inflate(layoutInflater)
}