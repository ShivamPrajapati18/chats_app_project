package com.example.friendlychattingapp

import android.annotation.SuppressLint
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.friendlychattingapp.adapter.MessageAdapter
import com.example.friendlychattingapp.databinding.ActivityChatBinding
import com.example.friendlychattingapp.model.MessageModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import java.util.Date

class ChatActivity : AppCompatActivity() {
    private lateinit var binding: ActivityChatBinding
    private lateinit var database: FirebaseDatabase
    private lateinit var firebaseAuth: FirebaseAuth

    //private lateinit var message: ArrayList<MessageModel>
    private lateinit var chatAdapter: MessageAdapter
    private lateinit var senderRoom: String
    private lateinit var recieverRoom: String

    @SuppressLint("AppCompatMethod")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityChatBinding.inflate(layoutInflater)
        setContentView(binding.root)

        database = FirebaseDatabase.getInstance()
        firebaseAuth = FirebaseAuth.getInstance()
        //message= ArrayList()

        val reciverUid = intent.getStringExtra("uid")
        val recivername = intent.getStringExtra("name")
        val senderUid = firebaseAuth.currentUser?.uid

        actionBar?.title =recivername
        senderRoom  = reciverUid + senderUid
        recieverRoom = senderUid + reciverUid

        chatAdapter = MessageAdapter()
        val layoutManager = LinearLayoutManager(this)
        layoutManager.stackFromEnd = true
        binding.ChatRecyclerView.layoutManager = layoutManager
        binding.ChatRecyclerView.adapter = chatAdapter

        displayingChats()

        binding.sendImageView.setOnClickListener {
            sendingMessage(senderUid)
        }
    }

    private fun sendingMessage(senderUid: String?) {
        val messageText = binding.editTextText.text.toString()
        val date= Date()
        val messages = MessageModel(messageText, senderUid!!)
        database.reference.child("chats")
            .child(senderRoom)
            .child("messages")
            .push()
            .setValue(messages)
            .addOnSuccessListener {
                database.reference.child("chats").child(recieverRoom).child("messages").push()
                    .setValue(messages)
            }

        binding.editTextText.setText("")
    }

    private fun displayingChats() {
        database.reference.child("chats").child(senderRoom).child("messages")
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val messageList = ArrayList<MessageModel>()
                    for (chatSnapshot in snapshot.children) {
                        val message = chatSnapshot.getValue(MessageModel::class.java)
                        if (message != null) {
                            messageList.add(message)
                        }
                    }
                    chatAdapter.updateData(messageList)
                }

                override fun onCancelled(error: DatabaseError) {
                    Toast.makeText(this@ChatActivity, "Something Went Wrong", Toast.LENGTH_SHORT).show()
                }
            })
    }
}