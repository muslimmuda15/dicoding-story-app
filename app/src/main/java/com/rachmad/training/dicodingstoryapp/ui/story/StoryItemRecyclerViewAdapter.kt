package com.rachmad.training.dicodingstoryapp.ui.story

import android.content.Context
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import com.bumptech.glide.Glide
import com.rachmad.training.dicodingstoryapp.R
import com.rachmad.training.dicodingstoryapp.databinding.FragmentStoryItemBinding
import com.rachmad.training.dicodingstoryapp.model.StoryData
import com.rachmad.training.dicodingstoryapp.util.Geolocation
import com.rachmad.training.dicodingstoryapp.util.TimeUtil
import com.rachmad.training.dicodingstoryapp.util.ui.gone
import com.rachmad.training.dicodingstoryapp.util.ui.visible

class StoryItemRecyclerViewAdapter(val context: Context, private val listener: OnSelectedStory): RecyclerView.Adapter<StoryItemRecyclerViewAdapter.StoryViewHolder>() {
    private var geolocation: Geolocation = Geolocation(context)
    private var timeUtil: TimeUtil = TimeUtil(context)

    private val values = ArrayList<StoryData>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StoryViewHolder = StoryViewHolder(
        FragmentStoryItemBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
    )

    fun addData(list: ArrayList<StoryData>?){
        if(values.size > 0) {
            notifyItemRangeRemoved(0, values.size)
            values.clear()
        }
        list?.let {
            for (i in 0 until list.size) {
                values.add(list[i])
                notifyItemInserted(i)
            }
        }
    }

    override fun onBindViewHolder(holder: StoryViewHolder, position: Int) {
        with(holder) {
            val item = values[position]

            val timeStamp = item.createdAt?.let { timeUtil.toDateLong(it) } ?: run { 0L }

            fullName.text = item.name
            date.text = timeUtil.toTime(timeStamp)
            description.text = item.description

            try {
                if (item.lat != null && item.lon != null) {
                    location.visible()
                    geolocation.setLocation(item.lat!!, item.lon!!)
                    location.text = geolocation.city ?: geolocation.state ?: geolocation.country ?: context.getString(R.string.unknown_location)
                } else {
                    location.gone()
                }
            } catch (e: Exception){
                location.gone()
            }

            Glide.with(photo)
                .load(item.photoUrl)
                .into(photo)

            mainContainer.setOnClickListener {
                listener.onClick(item)
            }
        }
    }

    override fun getItemCount(): Int = values.size

    inner class StoryViewHolder(binding: FragmentStoryItemBinding): RecyclerView.ViewHolder(binding.root) {
        val photo: ImageView = binding.photo
        val fullName: TextView = binding.fullName
        val date: TextView = binding.date
        val description: TextView = binding.description
        val mainContainer: RelativeLayout = binding.mainContainer
        val location: TextView = binding.location
    }
}