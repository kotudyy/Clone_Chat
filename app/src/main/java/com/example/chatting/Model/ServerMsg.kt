package com.example.chatting.Model

import android.net.Uri

data class ServerMsg(
    var name: String,
    var text: String,
    var timestamp: Long,
    var byteArray: ByteArray?
)
