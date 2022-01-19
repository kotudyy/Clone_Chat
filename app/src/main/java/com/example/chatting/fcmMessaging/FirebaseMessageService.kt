package com.example.chatting.fcmMessaging

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import com.example.chatting.chatRoom.ChatRoomActivity
import com.example.chatting.model.ServerMsg
import com.example.chatting.storage.MyApplication
import com.example.chatting.R
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage

import android.graphics.Bitmap
import android.util.Log
import android.graphics.BitmapFactory
import java.lang.Exception


class FirebaseMessageService : FirebaseMessagingService() {
    override fun onNewToken(p0: String){
        MyApplication.prefs.globalToken = p0
    }

    override fun onMessageReceived(p0: RemoteMessage){
        super.onMessageReceived(p0)

        val serverData = p0.data as Map<String, String>

        val chatRoomId = serverData["ChatRoomID"]

        val serverMsg = ServerMsg(
            "name",
            "text",
            0,
            null
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
                            MyApplication.db.collection("profile").document(sender)
                                .get()
                                .addOnSuccessListener { documentSnapShot ->
                                    serverMsg.name = documentSnapShot.getString("name")!!

                                    Log.d("test", "$sender")

                                    MyApplication.storage.reference.child("${sender}/profile")
                                        .getBytes(1024 * 1024) // 256 * 256 X
                                        .addOnSuccessListener {
                                            serverMsg.byteArray = it

                                            notifyMessage(serverMsg, chatRoomId)
                                        }
                                        .addOnFailureListener {
                                            notifyMessage(serverMsg, chatRoomId)
                                        }

                                }
                        }
                    }
                }
            }.addOnFailureListener {  }


    }

    private fun notifyMessage(message: ServerMsg, chatRoomId: String){
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

        var bitmap : Bitmap? = null
        try {
            bitmap = BitmapFactory.decodeByteArray(message.byteArray, 0, message.byteArray!!.size)
        } catch (e: Exception){}


        builder.apply {
            setSmallIcon(R.drawable.kakao_notification_icon)  //스몰 아이콘
            setContentTitle(message.name)
            setContentText(message.text)
            setWhen(message.timestamp)
            setLargeIcon(bitmap)

            val newMessageCount = 3
            setNumber(newMessageCount)

            val chatRoomIntent = Intent(this@FirebaseMessageService, ChatRoomActivity::class.java)
            chatRoomIntent.putExtra("chatRoomId", chatRoomId)
            val pendingIntent =
                PendingIntent.getActivity(this@FirebaseMessageService, 10, chatRoomIntent, PendingIntent.FLAG_UPDATE_CURRENT)
            setContentIntent(pendingIntent)
        }

        manager.notify(10, builder.build())
    }
}