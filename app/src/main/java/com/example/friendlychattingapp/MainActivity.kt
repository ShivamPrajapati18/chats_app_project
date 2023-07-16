package com.example.friendlychattingapp

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.friendlychattingapp.adapter.ChatRowAdapter
import com.example.friendlychattingapp.adapter.StatusAdapter
import com.example.friendlychattingapp.adapter.itemClicked
import com.example.friendlychattingapp.databinding.ActivityMainBinding
import com.example.friendlychattingapp.model.Status
import com.example.friendlychattingapp.model.UsersModel
import com.example.friendlychattingapp.model.UsersStatusModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage
import java.util.Date


class MainActivity : AppCompatActivity(), itemClicked {

    private lateinit var binding: ActivityMainBinding
    private lateinit var database: FirebaseDatabase
    private lateinit var chatRowAdapter: ChatRowAdapter
    private lateinit var currentUser:String
    private lateinit var storage:FirebaseStorage
    private var uid: String? =null
    private var currentUserData: UsersModel? =null
    private lateinit var statusAdapter:StatusAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        currentUser= FirebaseAuth.getInstance().currentUser?.uid.toString()
        database= FirebaseDatabase.getInstance()
        storage=FirebaseStorage.getInstance()

        displayingChatUsers()
        displayingStatuses()
        binding.imageView2.setOnClickListener {
            contracts.launch("image/*")
        }
    }

    private fun displayingStatuses() {
        statusAdapter=StatusAdapter()
        binding.statusRV.layoutManager=LinearLayoutManager(this,LinearLayoutManager.HORIZONTAL,false)
        binding.statusRV.adapter=statusAdapter
        database.reference.child("status").addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val userStatus = ArrayList<UsersStatusModel>()
                if (snapshot.exists()) {
                    for (data in snapshot.children) {
                        val userStatusObj = UsersStatusModel()
                        userStatusObj.name = data.child("name").getValue(String::class.java)
                        userStatusObj.profileImage = data.child("profileImg").getValue(String::class.java)
                        val statusArr= ArrayList<Status>()
                        for (statusImg in data.child("stories").children){
                            val img=statusImg.getValue(Status::class.java)
                            if (img != null) {
                                statusArr.add(img)
                            }
                        }
                        userStatusObj.statuses=statusArr
                        userStatus.add(userStatusObj)
                    }
                }
                statusAdapter.updatedItem(userStatus)
            }

            override fun onCancelled(error: DatabaseError) {
                // Handle onCancelled if needed
            }
        })

    }

    private fun displayingChatUsers() {
        chatRowAdapter= ChatRowAdapter(this)
        binding.chartsRV.layoutManager=LinearLayoutManager(this)
        binding.chartsRV.adapter=chatRowAdapter

        database.reference
            .child("users")
            .addValueEventListener(object :ValueEventListener{
                override fun onDataChange(snapshot: DataSnapshot) {
                    val users=ArrayList<UsersModel>()
                    for (data in snapshot.children){
                        val user =data.getValue(UsersModel::class.java)
                        if (user?.uid!=currentUser) {
                            users.add(user!!)
                        }
                        else{
                            currentUserData= data.getValue(UsersModel::class.java)!!
                        }
                    }
                    chatRowAdapter.updateItem(users)
                }
                override fun onCancelled(error: DatabaseError) {
                    Toast.makeText(this@MainActivity, "Something went wrong", Toast.LENGTH_SHORT).show()
                }
            })
    }


    override fun itemClicked(list: UsersModel) {
        val intent= Intent(this,ChatActivity::class.java)
        intent.putExtra("uid",list.uid)
        intent.putExtra("name", list.name)
        startActivity(intent)
    }

    private val contracts=registerForActivityResult(ActivityResultContracts.GetContent()){
        if(it!=null){
            val date= Date()//fetching current date
            val storageReference=storage.reference.child("status").child(date.time.toString())
            storageReference.putFile(it)//Uploading the data in firebase storage
                .addOnCompleteListener{
                    if (it.isSuccessful){
                        storageReference.downloadUrl.addOnSuccessListener {//downloading the uri of the image
                            if (currentUserData!=null) {
                                //Creating the HashMap for uploading the current user data
                                val obj=HashMap<String,Any>()
                                obj["name"]=currentUserData!!.name
                                obj["profileImg"]=currentUserData!!.profileImg
                                //uploading the users status
                                database.reference.child("status").child(currentUser).updateChildren(obj)
                                val status=Status(it.toString())
                                database.reference.child("status").child(currentUser)
                                    .child("stories")
                                    .push()
                                    .setValue(status)
                            }
                        }
                    }
                }

        }
    }
}