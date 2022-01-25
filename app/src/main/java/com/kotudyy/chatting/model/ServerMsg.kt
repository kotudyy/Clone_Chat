package com.kotudyy.chatting.model

data class ServerMsg(
    var name: String,
    var text: String,
    var timestamp: Long,
    var byteArray: ByteArray?
)
