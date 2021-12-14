package com.example.chatting.Model

data class UserRoom(
    val chatRoomId : String,
    val lastMessage : String,
    val timestamp : Int,
    val sendEmail : String
)
