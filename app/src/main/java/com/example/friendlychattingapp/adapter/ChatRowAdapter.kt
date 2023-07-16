package com.example.friendlychattingapp.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.friendlychattingapp.R
import com.example.friendlychattingapp.databinding.ChatsRowRvSampleBinding
import com.example.friendlychattingapp.model.UsersModel
import com.squareup.picasso.Picasso

class ChatRowAdapter(
    private val listner: itemClicked
)
    : RecyclerView.Adapter<ChatRowAdapter.myViewHolder>() {

    private val list=ArrayList<UsersModel>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): myViewHolder {
        val binding=ChatsRowRvSampleBinding.inflate(
            LayoutInflater.from(parent.context),parent,false)

        val view= myViewHolder(binding)
        view.itemView.setOnClickListener {
            listner.itemClicked(list[view.adapterPosition])
        }
        return view
    }

    override fun getItemCount(): Int =list.size

    override fun onBindViewHolder(holder: myViewHolder, position: Int) {
        val currentItem=list[position]
        holder.binding.name.text=currentItem.name
        Picasso.get().load(currentItem.profileImg)
            .placeholder(R.drawable.default_avatar_profile)
            .into(holder.binding.circleImageView)
        if (currentItem.lastMsg!=null){
            holder.binding.chat.text=currentItem.lastMsg
        }
        else{
            holder.binding.chat.text="Tap to Chat"
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    fun updateItem(users:ArrayList<UsersModel>) {
        list.clear()
        list.addAll(users)
        notifyDataSetChanged()
    }


    inner class myViewHolder(val binding: ChatsRowRvSampleBinding) : RecyclerView.ViewHolder(binding.root)
}

interface itemClicked {
    fun itemClicked(list:UsersModel)
}
