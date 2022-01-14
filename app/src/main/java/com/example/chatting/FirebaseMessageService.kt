package com.example.chatting

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Intent
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import com.example.chatting.Model.ServerMsg
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage

class FirebaseMessageService : FirebaseMessagingService() {
    override fun onNewToken(p0: String){
        Log.d("grusie", "Refreshed token: $p0")
        MyApplication.prefs.globalToken = p0
    }

    override fun onMessageReceived(p0: RemoteMessage){
        super.onMessageReceived(p0)
        Log.d("grusie", "fcm message : ${p0.notification}")
        Log.d("grusie","fcm message : ${p0.data}")

        val serverData = p0.data as Map<String, String>

        val chatRoomId = serverData["ChatRoomID"]
        Log.d("grusie", "serverData : $serverData")
        Log.d("grusie", "chatRoomID : $chatRoomId")
        Log.d("test", chatRoomId ?: "nothing")

        val serverMsg = ServerMsg(
            "name",
            "text",
            0
        )

        MyApplication.realtime.child("UserRoom").child(chatRoomId!!)
            .get()
            .addOnSuccessListener { dataSnapShot ->
                for (info in dataSnapShot.children){
                    when(info.key){
                        "lastmessage" -> {
                            serverMsg.text = info.value as String
                        }

                        "timestamp" -> {
                            serverMsg.timestamp = info.value as Long
                        }

                        "sender" -> {
                            val sender = info.value as String
                            Log.d("Test", sender)
                            MyApplication.db.collection("profile").document(sender)
                                .get()
                                .addOnSuccessListener { documentSnapShot ->
                                    Log.d("test", "success")
                                    serverMsg.name = documentSnapShot.getString("name")!!
                                    Log.d("test", serverMsg.toString())
                                    Log.d("grusie","serverMsg : $serverMsg")
                                    notifyMessage(serverMsg)
                                }
                        }
                    }
                }
            }.addOnFailureListener { Log.d("grusie","fail") }


    }

    private fun notifyMessage(message: ServerMsg){
        val manager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        val builder: NotificationCompat.Builder

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channelId = "Receive Message"
            val channelName = "chatRoomID"
            val importance = NotificationManager.IMPORTANCE_HIGH
            val channel =
                NotificationChannel(channelId, channelName, importance).apply {
                    description = "New Message"
                    setShowBadge(true)
                }

            // NotificationManager에 채널 등록
            manager.createNotificationChannel(channel)

            // 채널 이용해 빌더 생성
            builder = NotificationCompat.Builder(this, channelId)
        } else {
            builder = NotificationCompat.Builder(this)
        }

        builder.apply {
            setSmallIcon(android.R.drawable.ic_notification_overlay)  //스몰 아이콘
            setContentTitle(message.name)
            setContentText(message.text)
            setWhen(message.timestamp)

            val newMessageCount = 3
            setNumber(newMessageCount)

            val chatRoomIntent = Intent(this@FirebaseMessageService, ChatRoomActivity::class.java)
            val pendingIntent =
                PendingIntent.getActivity(this@FirebaseMessageService, 10, chatRoomIntent, PendingIntent.FLAG_UPDATE_CURRENT)
            setContentIntent(pendingIntent)
        }

        manager.notify(10, builder.build())
    }
}