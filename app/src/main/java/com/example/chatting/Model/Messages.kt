package com.example.chatting.Model

data class Messages(
    val message: String,
    val timeStamp: Long,
    val sendEmail: String,
    var type : Int
)
