package com.rachmad.training.dicodingstoryapp.ui.story

import android.app.Activity
import android.content.Context
import android.graphics.drawable.Drawable
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.app.ActivityOptionsCompat
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.rachmad.training.dicodingstoryapp.R
import com.rachmad.training.dicodingstoryapp.databinding.FragmentStoryItemBinding
import com.rachmad.training.dicodingstoryapp.model.StoryData
import com.rachmad.training.dicodingstoryapp.util.Geolocation
import com.rachmad.training.dicodingstoryapp.util.TimeUtil
import com.rachmad.training.dicodingstoryapp.util.ui.CustomLoading
import com.rachmad.training.dicodingstoryapp.util.ui.gone
import com.rachmad.training.dicodingstoryapp.util.ui.visible
import androidx.core.util.Pair
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import com.rachmad.training.dicodingstoryapp.util.ui.isVisible

class StoryItemRecyclerViewAdapter(val context: Context, private val listener: OnSelectedStory): PagingDataAdapter<StoryData, StoryItemRecyclerViewAdapter.StoryViewHolder>(DIFF_CALLBACK) {
    private var geoLocation: Geolocation = Geolocation(context)
    private var timeUtil: TimeUtil = TimeUtil(context)

    private val values = ArrayList<StoryData>()
    private val requestOptions = RequestOptions()
        .diskCacheStrategy(DiskCacheStrategy.ALL)
        .dontTransform()
        .skipMemoryCache(true)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StoryViewHolder = StoryViewHolder(
        FragmentStoryItemBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
    )

    override fun onBindViewHolder(holder: StoryViewHolder, position: Int) {
        with(holder) {
            if(values.size > 0) {
                holder.root.visible()
                val item = values[position]

                loading.visible()
                val timeStamp = item.createdAt?.let { timeUtil.toDateLong(it) } ?: run { 0L }

                fullName.text = item.name
                date.text = timeUtil.toTime(timeStamp)
                description.text = item.description

                try {
                    if (item.lat != null && item.lon != null) {
                        location.visible()
                        geoLocation.setLocation(item.lat!!, item.lon!!)
//                    location.text = geolocation.city ?: geolocation.state ?: geolocation.country ?: context.getString(R.string.unknown_location)
                        location.text = geoLocation.getGlobalLocation()
                    } else {
                        location.gone()
                    }
                } catch (e: Exception) {
                    location.gone()
                }

                Glide.with(photo)
                    .load(item.photoUrl)
                    .apply(requestOptions)
                    .listener(object : RequestListener<Drawable> {
                        override fun onLoadFailed(
                            e: GlideException?,
                            model: Any?,
                            target: Target<Drawable>?,
                            isFirstResource: Boolean
                        ): Boolean {
                            loading.gone()
                            return false
                        }

                        override fun onResourceReady(
                            resource: Drawable?,
                            model: Any?,
                            target: Target<Drawable>?,
                            dataSource: DataSource?,
                            isFirstResource: Boolean
                        ): Boolean {
                            loading.gone()
                            return false
                        }

                    })
                    .into(photo)

                root.setOnClickListener {
                    val optionsCompat: ActivityOptionsCompat = if (location.isVisible) {
                        ActivityOptionsCompat.makeSceneTransitionAnimation(
                            root.context as Activity,
                            Pair(photo, "photo"),
                            Pair(fullName, "fullName"),
                            Pair(description, "description"),
                            Pair(date, "date"),
                            Pair(location, "location")
                        )
                    } else {
                        ActivityOptionsCompat.makeSceneTransitionAnimation(
                            root.context as Activity,
                            Pair(photo, "photo"),
                            Pair(fullName, "fullName"),
                            Pair(description, "description"),
                            Pair(date, "date")
                        )
                    }
                    listener.onClick(optionsCompat, item)
                }
            } else {
                holder.root.gone()
            }
        }
    }

    override fun getItemCount(): Int = values.size

    inner class StoryViewHolder(binding: FragmentStoryItemBinding): RecyclerView.ViewHolder(binding.root) {
        val root = binding.root
        val photo: ImageView = binding.photo
        val fullName: TextView = binding.fullName
        val date: TextView = binding.date
        val description: TextView = binding.description
        val location: TextView = binding.location
        val loading: CustomLoading = binding.loading
    }

    companion object {
        val DIFF_CALLBACK = object : DiffUtil.ItemCallback<StoryData>() {
            override fun areItemsTheSame(oldItem: StoryData, newItem: StoryData): Boolean {
                return oldItem == newItem
            }

            override fun areContentsTheSame(oldItem: StoryData, newItem: StoryData): Boolean {
                return oldItem.id == newItem.id
            }
        }
    }
}