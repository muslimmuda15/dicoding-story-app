package com.rachmad.training.dicodingstoryapp.ui.story.detail

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.method.ScrollingMovementMethod
import com.bumptech.glide.Glide
import com.rachmad.training.dicodingstoryapp.BaseActivity
import com.rachmad.training.dicodingstoryapp.R
import com.rachmad.training.dicodingstoryapp.databinding.ActivityStoryDetailsBinding
import com.rachmad.training.dicodingstoryapp.model.StoryData
import com.rachmad.training.dicodingstoryapp.util.Geolocation
import com.rachmad.training.dicodingstoryapp.util.TimeUtil
import com.rachmad.training.dicodingstoryapp.util.ui.gone
import com.rachmad.training.dicodingstoryapp.util.ui.visible

class StoryDetailsActivity: BaseActivity<ActivityStoryDetailsBinding>() {
    private lateinit var storyData: StoryData
    private lateinit var geolocation: Geolocation
    private var timeUtil: TimeUtil = TimeUtil(this)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        init()
    }

    @SuppressLint("SetTextI18n")
    private fun init(){
        geolocation = Geolocation(this)
        storyData = intent.getSerializableExtra(STORY_INTENT_DATA) as StoryData

        with(layout){
            layout.description.movementMethod = ScrollingMovementMethod.getInstance()

            val timeStamp = storyData.createdAt?.let { timeUtil.toDateLong(it) } ?: run { 0L }

            fullName.text = storyData.name
            date.text = "${timeUtil.toDescriptionDateTime(timeStamp)} WIB"
            description.text = storyData.description

            try {
                if (storyData.lat != null && storyData.lon != null) {
                    location.visible()
                    geolocation.setLocation(storyData.lat!!, storyData.lon!!)
                    location.text = geolocation.address ?: geolocation.city ?: geolocation.state
                            ?: geolocation.country ?: getString(
                        R.string.unknown_location
                    )
                } else {
                    location.gone()
                }
            } catch (e: Exception){
                location.gone()
            }

            Glide.with(photo)
                .load(storyData.photoUrl)
                .into(photo)
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