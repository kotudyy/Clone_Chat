package com.example.chatting.Model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class UserData(
    var email: String,
    var name: String,
    var status_message: String,
    var profile_music: String
) : Parcelable