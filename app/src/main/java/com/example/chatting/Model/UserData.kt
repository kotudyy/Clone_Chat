package com.example.chatting.Model

import android.os.Parcelable
import com.google.firebase.storage.StorageReference
import kotlinx.parcelize.Parcelize

@Parcelize
data class UserData(
    var email: String,
    var name: String,
    var statusMsg: String,
    var profileMusic: String,
    var profile_image_url: String,
    var background_image_url: String
) : Parcelable