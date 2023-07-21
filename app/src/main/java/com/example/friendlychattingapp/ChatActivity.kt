package com.example.friendlychattingapp

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.WindowManager
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.friendlychattingapp.adapter.MessageAdapter
import com.example.friendlychattingapp.databinding.ActivityChatBinding
import com.example.friendlychattingapp.databinding.CustomToolbarBinding
import com.example.friendlychattingapp.model.MessageModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.squareup.picasso.Picasso
import java.util.Date

class ChatActivity : AppCompatActivity() {
    private lateinit var binding: ActivityChatBinding
    private lateinit var database: FirebaseDatabase
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var customToolbar: CustomToolbarBinding

    //private lateinit var message: ArrayList<MessageModel>
    private lateinit var chatAdapter: MessageAdapter
    private lateinit var senderRoom: String
    private lateinit var recieverRoom: String

    @SuppressLint("AppCompatMethod")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityChatBinding.inflate(layoutInflater)
        setContentView(binding.root)
        customToolbar=CustomToolbarBinding.inflate(layoutInflater)
        database = FirebaseDatabase.getInstance()
        firebaseAuth = FirebaseAuth.getInstance()
        //message= ArrayList()

        val reciverUid = intent.getStringExtra("uid")
        val recivername = intent.getStringExtra("name")
        val reciverImg = intent.getStringExtra("profile_img")
        val senderUid = firebaseAuth.currentUser?.uid

        senderRoom = reciverUid + senderUid
        recieverRoom = senderUid + reciverUid

        setupCustomToolbar(recivername, reciverImg)

        displayingChats()

        binding.sendImageView.setOnClickListener {
            sendingMessage(senderUid)
        }
    }

    private fun setupCustomToolbar(receiverName: String?, receiverImage: String?) {
        val toolbar = binding.toolbar
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayShowTitleEnabled(false)
        supportActionBar?.setDisplayHomeAsUpEnabled(false)
        supportActionBar?.setDisplayShowCustomEnabled(true)
        supportActionBar?.setCustomView(R.layout.custom_toolbar)

        val customToolbar = supportActionBar?.customView?.findViewById<LinearLayout>(R.id.customToolbar)
        val backButton = customToolbar?.findViewById<ImageButton>(R.id.backButton)
        val toolbarProfileImg = customToolbar?.findViewById<ImageView>(R.id.toolbarProfileImg)
        val toolbarTitle = customToolbar?.findViewById<TextView>(R.id.toolbarTitle)

        toolbarTitle?.text = receiverName
        Picasso.get().load(receiverImage)
            .placeholder(R.drawable.default_avatar_profile)
            .into(toolbarProfileImg)

        backButton?.setOnClickListener {
            // Handle back button click here
            startActivity(Intent(this,MainActivity::class.java))
            finishAffinity()
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

        val lastMsgObj= HashMap<String, Any>()
        lastMsgObj["lastMsg"]=messageText
        lastMsgObj["time"]= date.time.toString()
        setupLastMsg(lastMsgObj)
    }

    private fun setupLastMsg(lastMsgObj: HashMap<String, Any>) {
        database.reference.child("chats")
            .child(senderRoom)
            .child("lastMsg")
            .updateChildren(lastMsgObj)
            .addOnSuccessListener {
                database.reference.child("chats")
                    .child(recieverRoom)
                    .child("lastMsg")
                    .updateChildren(lastMsgObj)
            }
    }

    private fun displayingChats() {
        chatAdapter = MessageAdapter()
        val layoutManager = LinearLayoutManager(this)
        layoutManager.stackFromEnd = true
        binding.ChatRecyclerView.layoutManager = layoutManager
        binding.ChatRecyclerView.adapter = chatAdapter
//        the user will see the last (latest) message initially
        binding.ChatRecyclerView.post {
            binding.ChatRecyclerView.scrollToPosition(chatAdapter.itemCount - 1)
        }
        // Set the on layout change listener
        binding.ChatRecyclerView.addOnLayoutChangeListener { view, i, i2, i3, i4, i5, i6, i7, i8 ->
            binding.ChatRecyclerView.scrollToPosition(chatAdapter.itemCount - 1)
        }
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
                    Toast.makeText(this@ChatActivity, "Something Went Wrong", Toast.LENGTH_SHORT)
                        .show()
                }
            })
    }
}