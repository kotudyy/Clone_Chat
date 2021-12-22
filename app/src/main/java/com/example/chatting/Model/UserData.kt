package com.example.chatting.Model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class UserData(
    var email: String,
    var name: String,
    var statusMsg: String,
    var profileMusic: String
) : Parcelable