package com.example.chatting.Model
import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

//채팅방별 유저
@Parcelize
data class chatRoomUser(
    var user1: String = "",
    var user2: String = ""
): Parcelable

