package com.example.friendlychattingapp.model

data class UsersModel(
    val name:String="",
    val uid:String="",
    val number: String="",
    val profileImg:String="",
    var lastMsg: String? =null,
    var time: Long? =null,
)
