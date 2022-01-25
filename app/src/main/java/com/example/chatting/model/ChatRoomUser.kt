package com.example.chatting.model
import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class ChatRoomUser(
    var user1: String = "",
    var user2: String = ""
): Parcelable

