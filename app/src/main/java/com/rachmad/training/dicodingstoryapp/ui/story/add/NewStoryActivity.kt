package com.rachmad.training.dicodingstoryapp.ui.story.add

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.rachmad.training.dicodingstoryapp.BaseActivity
import com.rachmad.training.dicodingstoryapp.R
import com.rachmad.training.dicodingstoryapp.databinding.ActivityNewStoryBinding
import com.rachmad.training.dicodingstoryapp.repository.UserPreference
import com.rachmad.training.dicodingstoryapp.ui.story.add.CameraActivity.Companion.CAMERA_RESULT_CODE
import com.rachmad.training.dicodingstoryapp.ui.story.add.CameraActivity.Companion.GALLERY_RESULT
import com.rachmad.training.dicodingstoryapp.ui.story.add.CameraActivity.Companion.GALLERY_RESULT_CODE
import com.rachmad.training.dicodingstoryapp.ui.story.add.CameraActivity.Companion.IMAGE_RESULT
import com.rachmad.training.dicodingstoryapp.util.Geolocation
import com.rachmad.training.dicodingstoryapp.util.ViewModelFactory
import com.rachmad.training.dicodingstoryapp.util.loadImage
import com.rachmad.training.dicodingstoryapp.util.ui.gone
import com.rachmad.training.dicodingstoryapp.util.ui.visible

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "user")

class NewStoryActivity: BaseActivity<ActivityNewStoryBinding>() {
    private lateinit var viewModel: NewStoryViewModel
    private lateinit var geoLocation: Geolocation
    private var bitmap: Bitmap? = null

    private lateinit var activityResult: ActivityResultLauncher<Intent>

    override fun onPause() {
        geoLocation.stopLocationUpdate()
        super.onPause()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        init()
        initResult()
        observer()
        listener()
    }

    private fun initResult(){
        activityResult = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            when (result.resultCode) {
                CAMERA_RESULT_CODE -> {
                    val isResultSuccess = result.data?.getBooleanExtra(IMAGE_RESULT, false)
                    if (isResultSuccess == true) {
                        bitmap?.recycle()

                        bitmap = loadImage(this)
                        Glide.with(layout.image)
                            .asBitmap()
                            .load(bitmap)
                            .skipMemoryCache(true)
                            .into(layout.image)

                        layout.image.visible()
                        layout.takeImage.text = getString(R.string.update_camera_image)
                    } else {
                        Toast.makeText(
                            this,
                            getString(R.string.unknown_load_photo),
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
                GALLERY_RESULT_CODE -> {
                    val resultUri = result.data?.getParcelableExtra<Uri>(GALLERY_RESULT)
                    bitmap?.recycle()
                    Glide.with(layout.image)
                        .load(resultUri)
                        .skipMemoryCache(true)
                        .into(layout.image)

                    layout.image.visible()
                    layout.takeImage.text = getString(R.string.update_camera_image)
                }
            }
        }
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
                        geoLocation.stopLocationUpdate()
                    } ?: run {
                        loading.gone()
                        geoLocation.stopLocationUpdate()
                    }
                }
                geoLocation.startLocationUpdate()
            }

            takeImage.setOnClickListener {
                activityResult.launch(CameraActivity.instance(this@NewStoryActivity))
            }

            posting.setOnClickListener {
                
            }
        }
    }

    private fun init(){
        viewModel = ViewModelProvider(this, ViewModelFactory(UserPreference.getInstance(dataStore)))[NewStoryViewModel::class.java]
        geoLocation = Geolocation(this)

        setSupportActionBar(layout.toolbar)
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
            android.R.id.home -> finish()
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onDestroy() {
        bitmap?.recycle()
        super.onDestroy()
    }

    companion object {
        fun instance(context: Context): Intent = Intent(context, NewStoryActivity::class.java)
    }

    override fun getBinding(): ActivityNewStoryBinding = ActivityNewStoryBinding.inflate(layoutInflater)
}