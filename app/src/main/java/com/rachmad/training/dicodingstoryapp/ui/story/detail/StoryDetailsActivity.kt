package com.rachmad.training.dicodingstoryapp.ui.story.detail

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.method.ScrollingMovementMethod
import android.widget.Toast
import com.bumptech.glide.Glide
import com.google.android.gms.maps.model.LatLng
import com.rachmad.training.dicodingstoryapp.BaseActivity
import com.rachmad.training.dicodingstoryapp.R
import com.rachmad.training.dicodingstoryapp.databinding.ActivityStoryDetailsBinding
import com.rachmad.training.dicodingstoryapp.model.StoryData
import com.rachmad.training.dicodingstoryapp.ui.story.detail.StoryDetailMapsActivity.Companion.LATLONG
import com.rachmad.training.dicodingstoryapp.util.Geolocation
import com.rachmad.training.dicodingstoryapp.util.TimeUtil
import com.rachmad.training.dicodingstoryapp.util.getSerializable
import com.rachmad.training.dicodingstoryapp.util.ui.gone
import com.rachmad.training.dicodingstoryapp.util.ui.visible

class StoryDetailsActivity: BaseActivity<ActivityStoryDetailsBinding>() {
    private lateinit var storyData: StoryData
    private lateinit var geoLocation: Geolocation
    private var timeUtil: TimeUtil = TimeUtil(this)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        init()
        buttonListener()
    }

    @SuppressLint("SetTextI18n")
    private fun init(){
        geoLocation = Geolocation(this)
        storyData = getSerializable(this.intent, STORY_INTENT_DATA, StoryData::class.java)

        with(layout){
            layout.description.movementMethod = ScrollingMovementMethod.getInstance()

            val timeStamp = storyData.createdAt?.let { timeUtil.toDateLong(it) } ?: run { 0L }

            fullName.text = storyData.name
            date.text = "${timeUtil.toDescriptionDateTime(timeStamp)} WIB"
            description.text = storyData.description

            try {
                if (storyData.lat != null && storyData.lon != null) {
                    locationLayout.visible()
                    geoLocation.setLocation(storyData.lat!!, storyData.lon!!)

                    location.text = geoLocation.getGlobalLocation()
                } else {
                    locationLayout.gone()
                }
            } catch (e: Exception){
                locationLayout.gone()
            }

            Glide.with(photo)
                .load(storyData.photoUrl)
                .into(photo)
        }
    }

    private fun buttonListener(){
        layout.viewMap.setOnClickListener {
            if(storyData.lat != null && storyData.lon != null) {
                startActivity(
                    StoryDetailMapsActivity.instance(
                        this,
                        LatLng(storyData.lat!!, storyData.lon!!)
                    )
                )
            } else {
                Toast.makeText(this, getString(R.string.coordinate_failed), Toast.LENGTH_SHORT).show()
            }
        }
    }

    companion object {
        const val STORY_INTENT_DATA = "story-intent-data"
        fun instance(context: Context, storyData: StoryData) = Intent(context, StoryDetailsActivity::class.java).apply {
            putExtra(STORY_INTENT_DATA, storyData)
        }
    }

    override fun getBinding(): ActivityStoryDetailsBinding = ActivityStoryDetailsBinding.inflate(layoutInflater)
}