package com.rachmad.training.dicodingstoryapp.ui.story.add

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.*
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import com.google.common.util.concurrent.ListenableFuture
import com.rachmad.training.dicodingstoryapp.App
import com.rachmad.training.dicodingstoryapp.BaseActivity
import com.rachmad.training.dicodingstoryapp.databinding.ActivityCameraBinding
import com.rachmad.training.dicodingstoryapp.util.ui.visible
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject
import com.rachmad.training.dicodingstoryapp.R
import com.rachmad.training.dicodingstoryapp.util.getParcelableArrayListExtra
import com.rachmad.training.dicodingstoryapp.util.save
import com.rachmad.training.dicodingstoryapp.util.ui.gone
import com.sangcomz.fishbun.FishBun
import com.sangcomz.fishbun.adapter.image.impl.GlideAdapter

class CameraActivity: BaseActivity<ActivityCameraBinding>() {
    @Inject lateinit var cameraProviderFuture: ListenableFuture<ProcessCameraProvider>
    private lateinit var preview: Preview
    private lateinit var imageCapture: ImageCapture
    private lateinit var fishbunActivityResult: ActivityResultLauncher<Intent>
    private var camera: Camera? = null

    private var cameraProvider: ProcessCameraProvider? = null

    init {
        App.appComponent.inject(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        initResult()
        initPermission()
        listener()
    }

    private fun initResult(){
        fishbunActivityResult = registerForActivityResult(ActivityResultContracts.StartActivityForResult()){ result ->
            val resultCode = result.resultCode
            val data = result.data
            if(resultCode == RESULT_OK){
                val imagePath: ArrayList<Uri>? = getParcelableArrayListExtra(data, FishBun.INTENT_PATH, Uri::class.java)
                setResult(GALLERY_RESULT_CODE, Intent().apply {
                    putExtra(GALLERY_RESULT, imagePath?.get(0))
                })
                layout.loading.gone()
                finish()
            }
        }
    }

    private fun listener(){
        layout.take.setOnClickListener {
            layout.loading.visible()
            takeImage()
        }
        layout.cameraSelector.setOnClickListener {
            cameraSelector = when(cameraSelector){
                CameraSelector.DEFAULT_BACK_CAMERA -> {
                    CameraSelector.DEFAULT_FRONT_CAMERA
                }
                else -> {
                    CameraSelector.DEFAULT_BACK_CAMERA
                }
            }
            startCamera()
        }
        layout.gallery.setOnClickListener {
            FishBun.with(this)
                .setImageAdapter(GlideAdapter())
                .setActionBarColor(ContextCompat.getColor(this, R.color.md_blue_400))
                .setMaxCount(1)
                .setReachLimitAutomaticClose(true)
                .setHomeAsUpIndicatorDrawable(ContextCompat.getDrawable(this, R.drawable.ic_baseline_arrow_back_24))
                .startAlbumWithActivityResultCallback(fishbunActivityResult)
        }
    }

    private fun initPermission(){
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.CAMERA), CAMERA_PERMISSION_REQUEST)
        } else {
            startCamera()
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        if(requestCode == CAMERA_PERMISSION_REQUEST){
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, R.string.permission_granted, Toast.LENGTH_SHORT).show()
                startCamera()
            } else if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_DENIED) {
                if(shouldShowRequestPermissionRationale(Manifest.permission.CAMERA)){
                    ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.CAMERA), CAMERA_PERMISSION_REQUEST)
                } else {
                    Toast.makeText(this, getString(R.string.open_setting), Toast.LENGTH_SHORT)
                        .show()
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

    private var cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA
    private fun startCamera(){
        cameraProviderFuture.addListener({
            cameraProvider = cameraProvider ?: cameraProviderFuture.get()
            try {
                cameraProvider?.unbindAll()
                initCamera()
                camera = cameraProvider?.bindToLifecycle(this, cameraSelector, preview, imageCapture)
            } catch (e: Exception){
                Toast.makeText(this, "Camera failed", Toast.LENGTH_SHORT).show()
            }
        }, ContextCompat.getMainExecutor(this))
    }

    private fun initCamera(){
        preview = Preview.Builder()
            .setTargetAspectRatio(AspectRatio.RATIO_16_9)
            .build()
            .also {
                it.setSurfaceProvider(layout.preview.surfaceProvider)
            }

        imageCapture = ImageCapture.Builder()
            .setCaptureMode(ImageCapture.CAPTURE_MODE_MAXIMIZE_QUALITY)
            .setTargetAspectRatio(AspectRatio.RATIO_16_9)
            .build()
    }

    private fun takeImage(){
        layout.loading.visible()
        cameraProvider?.unbind(preview)
        imageCapture.flashMode = ImageCapture.FLASH_MODE_AUTO

        lifecycleScope.launch {
            /**
             * Ada beberapa case
             * Jadi somehow ada error *Request is canceled* di lognya
             * Sulit di tracking penyebabnya
             * Sementara solvenya di kasih delay 300ms supaya menghindari error tersebut
             */
            delay(300)
            imageCapture.takePicture(ContextCompat.getMainExecutor(this@CameraActivity), object: ImageCapture.OnImageCapturedCallback() {
                @SuppressLint("UnsafeOptInUsageError")
                override fun onCaptureSuccess(image: ImageProxy) {
                    image.image?.save(this@CameraActivity) { result ->
                        setResult(CAMERA_RESULT_CODE, Intent().apply {
                            putExtra(IMAGE_RESULT, result)
                        })
                        layout.loading.gone()
                        finish()
                    }
                }

                override fun onError(exception: ImageCaptureException) {
                    super.onError(exception)
                    Toast.makeText(this@CameraActivity, getString(R.string.unknown_take_photo), Toast.LENGTH_SHORT).show()
                    layout.loading.gone()
                    finish()
                }
            })
        }
    }

    override fun getBinding(): ActivityCameraBinding = ActivityCameraBinding.inflate(layoutInflater)

    companion object {
        const val CAMERA_PERMISSION_REQUEST = 2
        const val CAMERA_RESULT_CODE = 1000
        const val GALLERY_RESULT_CODE = 2000
        const val IMAGE_RESULT = "image-result"
        const val GALLERY_RESULT = "gallery-result"

        fun instance(context: Context): Intent = Intent(context, CameraActivity::class.java)
    }
}