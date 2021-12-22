package com.example.chatting.Model
import android.os.Parcelable
import kotlinx.parcelize.Parcelize

//마지막 메시지
@Parcelize
data class UserRoom(
    var chatroomid: String?,
    var lastmessage: String,
    var timestamp: Long,
    var sender: String
): Parcelable {
    constructor(
        lastmessage: String,
        timestamp: Long,
        sender: String
    ) : this(null, lastmessage, timestamp, sender)
}

