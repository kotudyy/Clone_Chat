package com.example.chatting

import android.util.Log
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage

class FirebaseMessageService : FirebaseMessagingService() {
    override fun onNewToken(p0: String){
            Log.d("grusie", "Refreshed token: $p0")
    }
    override fun onMessageReceived(p0: RemoteMessage){
        super.onMessageReceived(p0)
        Log.d("grusie", "fcm message : ${p0.notification}")
        Log.d("grusie","fcm message : ${p0.data}")
    }
}