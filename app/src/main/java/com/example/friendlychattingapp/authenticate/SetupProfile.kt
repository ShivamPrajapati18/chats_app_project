package com.example.friendlychattingapp.authenticate

import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.example.friendlychattingapp.MainActivity
import com.example.friendlychattingapp.model.UsersModel
import com.example.friendlychattingapp.databinding.ActivitySetupProfileBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.squareup.picasso.Picasso


class SetupProfile : AppCompatActivity() {

    private lateinit var binding: ActivitySetupProfileBinding
    private var selectedImageUri: Uri? = null
    private lateinit var auth: FirebaseAuth
    private lateinit var database: FirebaseDatabase
    private lateinit var storage: FirebaseStorage
    private lateinit var uid:String
    private lateinit var storageReference:StorageReference

    @SuppressLint("SuspiciousIndentation")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySetupProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance()
        storage = FirebaseStorage.getInstance()

        uid = auth.uid.toString()
        storageReference = storage.reference.child("profiles").child(uid)

        binding.profileImg.setOnClickListener {
            contracts.launch("image/*")
        }

        binding.setupBtn.setOnClickListener {
            val name = binding.nameBox.text.toString()
            val number = auth.currentUser?.phoneNumber.toString()

            if (name.isEmpty())
                binding.nameBox.setError("Enter The Name")
                if(selectedImageUri!=null) {
                    storageReference.putFile(selectedImageUri!!)
                        .addOnCompleteListener {
                            if (it.isSuccessful) {
                                storageReference.downloadUrl
                                    .addOnSuccessListener {
                                        val profileImg = it.toString()
                                        imageLoadingInDatabase(name, number, profileImg)
                                    }
                            } else {
                                Toast.makeText(
                                    this@SetupProfile,
                                    "Something went wrong",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        }
                }else{
                    imageLoadingInDatabase(name,number,"No Image")
                }
        }
    }

    private fun imageLoadingInDatabase(name:String, number: String, profileImg: String) {
        val user = UsersModel(name, uid, number, profileImg)
        database.reference
            .child("users")
            .child(uid)
            .setValue(user)
            .addOnSuccessListener {
                startActivity(Intent(this, MainActivity::class.java))
                finishAffinity()
            }

    }

    private val contracts=registerForActivityResult(ActivityResultContracts.GetContent()){
        if (it != null) {
            selectedImageUri=it
            binding.profileImg.setImageURI(it)
        }
    }
}