package com.example.chatting.model
import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class chatRoomUser(
    var user1: String = "",
    var user2: String = ""
): Parcelable

