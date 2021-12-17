package com.example.chatting.Model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

//마지막 메시지
@Parcelize
data class UserRoom(
    var lastmessage: String,
    var timestamp: Long,
    var sender: String
): Parcelable
