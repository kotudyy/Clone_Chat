package com.example.chatting.Model

//마지막 메시지
data class UserRoom(
    var chatroomid: String? ="",
    var lastmessage: String ="",
    var timestamp: Long = 0,
    var sender: String =""
) {
    constructor(lastmessage: String, timestamp: Long, sender: String)
    : this(null, lastmessage, timestamp, sender)
}

