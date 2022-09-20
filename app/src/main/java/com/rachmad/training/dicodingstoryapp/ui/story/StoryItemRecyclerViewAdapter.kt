package com.rachmad.training.dicodingstoryapp.ui.story

import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import com.bumptech.glide.Glide
import com.rachmad.training.dicodingstoryapp.databinding.FragmentStoryItemBinding
import com.rachmad.training.dicodingstoryapp.model.StoryData
import com.rachmad.training.dicodingstoryapp.util.toDateLong
import com.rachmad.training.dicodingstoryapp.util.toTime

class StoryItemRecyclerViewAdapter(private val listener: OnSelectedStory): RecyclerView.Adapter<StoryItemRecyclerViewAdapter.StoryViewHolder>() {

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
            fullName.text = item.name
            date.text = item.createdAt?.toDateLong()?.toTime()
            description.text = item.description

            Glide.with(photo).load(item.photoUrl).into(photo)

            mainContainer.setOnClickListener {
                listener.onClick(item.id)
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
    }

}