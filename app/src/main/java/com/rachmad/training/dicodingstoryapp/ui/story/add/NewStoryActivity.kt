package com.rachmad.training.dicodingstoryapp.ui.story.add

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.view.MenuItem
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.rachmad.training.dicodingstoryapp.BaseActivity
import com.rachmad.training.dicodingstoryapp.R
import com.rachmad.training.dicodingstoryapp.databinding.ActivityNewStoryBinding
import com.rachmad.training.dicodingstoryapp.model.CreateStoryRequestData
import com.rachmad.training.dicodingstoryapp.ui.story.add.CameraActivity.Companion.CAMERA_RESULT_CODE
import com.rachmad.training.dicodingstoryapp.ui.story.add.CameraActivity.Companion.GALLERY_RESULT
import com.rachmad.training.dicodingstoryapp.ui.story.add.CameraActivity.Companion.GALLERY_RESULT_CODE
import com.rachmad.training.dicodingstoryapp.ui.story.add.CameraActivity.Companion.IMAGE_RESULT
import com.rachmad.training.dicodingstoryapp.util.*
import com.rachmad.training.dicodingstoryapp.util.ui.gone
import com.rachmad.training.dicodingstoryapp.util.ui.visible
import timber.log.Timber
import java.io.File

class NewStoryActivity: BaseActivity<ActivityNewStoryBinding>() {
    private val viewModel: NewStoryViewModel by viewModels()
    private lateinit var geoLocation: Geolocation
    private var bitmap: Bitmap? = null
    private var token: String? = null
    private var lat: Double? = null
    private var long: Double? = null
    private var file: File? = null

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
                        file = loadFile(this)
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
                    val resultUri = getParcelableExtra(result.data, GALLERY_RESULT, Uri::class.java)
                    file = File(getPath(this, resultUri!!))
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
                        lat = it.latitude
                        long = it.longitude

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
                errorImage.gone()
                if(description.text.toString().isBlank()){
                    descriptionLayout.isErrorEnabled = true
                    descriptionLayout.error = getString(R.string.empty_description)
                } else {
                    descriptionLayout.isErrorEnabled = false
                }

                if(file == null){
                    takeImage.error = getString(R.string.fill_image)
                    errorImage.visible()
                }
                if(description.text.toString().isNotBlank() && file != null) {
                    val userToken: String? = if(fullName.text.toString() == getString(R.string.guest))
                        null
                    else
                        token

                    loading.visible()
                    val requestData = CreateStoryRequestData(
                        description = description.text.toString(),
                        lat = lat,
                        lon = long,
                        photo = file!!
                    )
                    viewModel.addStory(userToken, requestData, {
                       loading.gone()
                        setResult(RESULT_REQUEST_STORY, Intent().apply {
                            putExtra(IS_STORY_SUCCESS, true)
                        })
                        finish()
                    }, {
                        setResult(RESULT_REQUEST_STORY, Intent().apply {
                            putExtra(IS_STORY_SUCCESS, false)
                        })
                        loading.gone()
                        Toast.makeText(this@NewStoryActivity, it?.message, Toast.LENGTH_SHORT)
                            .show()
                        Timber.i(it?.message)
                        finish()
                    }, {
                        setResult(RESULT_REQUEST_STORY, Intent().apply {
                            putExtra(IS_STORY_SUCCESS, false)
                        })
                        loading.gone()
                        Toast.makeText(this@NewStoryActivity, it?.message, Toast.LENGTH_SHORT)
                            .show()
                        Timber.i(it?.message)
                        finish()
                    })
                }
            }
        }
    }

    private fun init(){
        geoLocation = Geolocation(this)

        setSupportActionBar(layout.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)
        supportActionBar?.title = getString(R.string.create_story)
    }

    private fun observer(){
        viewModel.getUserLiveData().observe(this){
            it?.let {
                layout.loading.gone()
                token = it.token
                val items = arrayOf(it.name, getString(R.string.guest))
                layout.fullName.apply {
                    setText(it.name)
                    setSimpleItems(items)
                }
            } ?: run {

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
                if(shouldShowRequestPermissionRationale(Manifest.permission.ACCESS_FINE_LOCATION) && shouldShowRequestPermissionRationale(Manifest.permission.ACCESS_COARSE_LOCATION)) {
                    geoLocation.startLocationUpdate()
                } else {
                    Toast.makeText(this, R.string.open_setting, Toast.LENGTH_SHORT).show()
                    Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                        addCategory(Intent.CATEGORY_DEFAULT)
                        data = Uri.parse("package:$packageName")
                    }.run(::startActivity)
                    finish()
                }
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
        const val RESULT_REQUEST_STORY = 7000
        const val IS_STORY_SUCCESS = "is-story-success"

        fun instance(context: Context): Intent = Intent(context, NewStoryActivity::class.java)
    }

    override fun getBinding(): ActivityNewStoryBinding = ActivityNewStoryBinding.inflate(layoutInflater)
}