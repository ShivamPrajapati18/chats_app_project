package com.example.friendlychattingapp.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.friendlychattingapp.MainActivity
import com.example.friendlychattingapp.databinding.StatutsRvSampleBinding
import com.example.friendlychattingapp.model.UsersStatusModel
import com.squareup.picasso.Picasso
import omari.hamza.storyview.StoryView
import omari.hamza.storyview.model.MyStory


class StatusAdapter():RecyclerView.Adapter<StatusAdapter.MyViewHolder>(){
    private val status=ArrayList<UsersStatusModel>()
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val binding = StatutsRvSampleBinding.inflate(
            LayoutInflater.from(parent.context),
            parent, false
        )
        val view= MyViewHolder(binding)
        return view
    }

    override fun getItemCount(): Int {
        return status.size
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val currentItem = status[position]
        val statusCount = currentItem.statuses.size

        if (statusCount > 0) {
            holder.binding.circularStatusView.setPortionsCount(statusCount)
            Picasso.get()
                .load(currentItem.statuses[statusCount - 1].statusImage)
                .into(holder.binding.stastusIcon)
        }

        holder.binding.circularStatusView.setOnClickListener{
            val myStories = ArrayList<MyStory>()
            for (stories in currentItem.statuses){
                myStories.add(MyStory(stories.statusImage))
            }

            StoryView.Builder((holder.itemView.context as MainActivity).supportFragmentManager)
                .setStoriesList(myStories) // Required
                .setStoryDuration(5000) // Default is 2000 Millis (2 Seconds)
                .setTitleText(currentItem.name) // Default is Hidden
                .setSubtitleText("") // Default is Hidden
                .setTitleLogoUrl(currentItem.profileImage) // Default is Hidden
                .build() // Must be called before calling show method
                .show()
        }

    }

    @SuppressLint("NotifyDataSetChanged")
    fun updatedItem(userStatus: ArrayList<UsersStatusModel>) {
        status.clear()
        status.addAll(userStatus)
        notifyDataSetChanged()
    }

    inner class MyViewHolder(val binding: StatutsRvSampleBinding) : RecyclerView.ViewHolder(binding.root)
}