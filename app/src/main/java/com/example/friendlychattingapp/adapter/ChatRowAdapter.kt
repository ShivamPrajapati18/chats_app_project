package com.example.friendlychattingapp.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.friendlychattingapp.R
import com.example.friendlychattingapp.databinding.ChatsRowRvSampleBinding
import com.example.friendlychattingapp.model.UsersModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.squareup.picasso.Picasso
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class ChatRowAdapter(
    private val listner: ItemClicked
)
    : RecyclerView.Adapter<ChatRowAdapter.MyViewHolder>() {

    private val list=ArrayList<UsersModel>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val binding=ChatsRowRvSampleBinding.inflate(
            LayoutInflater.from(parent.context),parent,false)

        val view= MyViewHolder(binding)
        view.itemView.setOnClickListener {
            listner.itemClicked(list[view.adapterPosition])
        }
        return view
    }

    override fun getItemCount(): Int =list.size

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val currentItem=list[position]
        holder.binding.name.text=currentItem.name
        Picasso.get().load(currentItem.profileImg)
            .placeholder(R.drawable.default_avatar_profile)
            .into(holder.binding.circleImageView)
        // Retrieve last message and its timestamp from Firebase
        FirebaseDatabase.getInstance().reference.child("chats")
            .child(
                FirebaseAuth.getInstance().currentUser!!.uid+
                        currentItem.uid)
            .child("lastMsg")
            .addValueEventListener(object :ValueEventListener{
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists()){
                        // Get the last message and its timestamp as String values
                        val lastMsg=snapshot.child("lastMsg").getValue(String::class.java)
                        val lastMsgTime=snapshot.child("time").getValue(String::class.java)

                        // Set the last message text
                        holder.binding.chat.text=lastMsg

//                        // Format the timestamp and set it in "hh:mm" format
                        val formattedTime = lastMsgTime?.let { formatTime(it.toLong()) }
                        holder.binding.time.text = formattedTime
                    }else{
                        // No last message exists, display "Tap to Chat"
                        holder.binding.chat.text="Tap to Chat"
                        holder.binding.time.text = ""
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    TODO("Not yet implemented")
                }
            })
    }
    @SuppressLint("NotifyDataSetChanged")
    fun updateItem(users:ArrayList<UsersModel>) {
        list.clear()
        list.addAll(users)
        notifyDataSetChanged()
    }
    inner class MyViewHolder(val binding: ChatsRowRvSampleBinding) : RecyclerView.ViewHolder(binding.root)
}
fun formatTime(timestamp: Long): String {
    val date = Date(timestamp)
    val format = SimpleDateFormat("hh:mm", Locale.getDefault())
    return format.format(date)
}
interface ItemClicked {
    fun itemClicked(list:UsersModel)
}
