package com.example.chatting.Model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class MessageData(
    var message: String,
    var sender: String,
    var timestamp: Long
): Parcelable
