package com.example.friendlychattingapp.adapter

import android.annotation.SuppressLint
import android.os.Message
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.friendlychattingapp.databinding.ReceiverMsgRvSampleBinding
import com.example.friendlychattingapp.databinding.SenderMsgRvSampleBinding
import com.example.friendlychattingapp.model.MessageModel
import com.google.firebase.auth.FirebaseAuth

class MessageAdapter ():
    RecyclerView.Adapter<RecyclerView.ViewHolder>(){
    private val message=ArrayList<MessageModel>()
    companion object{
        const val SENDER_TEXT = 1
        const val RECEIVER_TEXT=2
    }

    @SuppressLint("NotifyDataSetChanged")
    fun updateData(messages: ArrayList<MessageModel>) {
        message.clear()
        message.addAll(messages)
        notifyDataSetChanged()
    }

    @SuppressLint("SuspiciousIndentation")
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return if (viewType== SENDER_TEXT){
            val view=
                SenderMsgRvSampleBinding.inflate(LayoutInflater.from(parent.context),parent,false)
                SenderMessage(view)
        }else{
            val view=
                ReceiverMsgRvSampleBinding.inflate(LayoutInflater.from(parent.context),parent,false)
            ReceiverMessage(view)
        }
    }

    override fun getItemViewType(position: Int): Int {
        val message=message[position]
        return if (message.senderId==FirebaseAuth.getInstance().currentUser?.uid){
            SENDER_TEXT
        }else{
            RECEIVER_TEXT
        }

    }

    override fun getItemCount(): Int =message.size

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val message=message[position]
         if (holder.itemViewType== SENDER_TEXT){
            val senderMessage=holder as SenderMessage
             senderMessage.binding.SenderMessage.text=message.message
        }else{
            val receiverMessage=holder as ReceiverMessage
             receiverMessage.binding.ReciverText.text=message.message
         }
    }

    inner class SenderMessage(val binding: SenderMsgRvSampleBinding) : RecyclerView.ViewHolder(binding.root)
    inner class ReceiverMessage(val binding:ReceiverMsgRvSampleBinding) : RecyclerView.ViewHolder(binding.root)

}