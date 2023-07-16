package com.example.friendlychattingapp.model

data class UsersStatusModel(
    var name: String? =null,
    var profileImage:String?=null,
//    val timeStamp: Long? =null,
    var statuses: ArrayList<Status> =ArrayList()
)
