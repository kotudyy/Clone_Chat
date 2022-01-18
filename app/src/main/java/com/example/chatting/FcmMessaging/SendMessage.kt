package com.example.chatting.FcmMessaging

import com.example.chatting.storage.MyApplication
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody
import org.json.JSONObject
import java.lang.Exception

class SendMessage {
        fun sendNoti(token: String, ChatRoomID: String){
            Thread(Runnable {   //TODO 메인스래드에서는 네트워크 통신 안되서 새로 만들어야됨
                try {
                    val root = JSONObject()     //TODO JSON 구조
                    val notification = JSONObject()
                    root.put("notification", notification)

                    MyApplication.realtime.child("UserRoom").child(ChatRoomID!!)
                        .get()
                        .addOnSuccessListener { dataSnapShot ->
                            for (info in dataSnapShot.children){
                                when(info.key){
                                    "lastmessage" -> {
                                        val lastMsg = info.value as String
                                        notification.put("body", "$lastMsg")
                                    }

                                    "sender" -> {
                                        val sender = info.value as String
                                        MyApplication.db.collection("profile").document(sender)
                                            .get()
                                            .addOnSuccessListener { documentSnapShot ->
                                                val senderName = documentSnapShot.getString("name")!!
                                                notification.put("title", "$senderName")
                                            }
                                    }
                                }
                            }
                        }

                    val data = JSONObject()
                    root.put("data", data)
                    data.put("ChatRoomID","$ChatRoomID")

                    // TODO to 에 상대방 토큰값 넣으면 보내짐
                    root.put("to", "$token");

                    postMsg(root.toString())
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }).start()
        }

        fun postMsg(js: String) {
            val FCM_MESSAGE_URL = "https://fcm.googleapis.com/fcm/send" //TODO 기본 URL
            val SERVER_KEY =    //TODO 서버키 넣기
                "AAAAs7wNw1Y:APA91bFh7C74RQxm7Qd3AKIGLLfCvIGzn7vVunX14nU6LVqVIbPQNu3Bho63i7LB6PsQOi0KfJwbNqp75JljDj2ejXHd-37nHzXDuW56IiRdsVjtB-kk01kRixTmbEQIcitrKEt43DNJ"
            val body = RequestBody.create("application/json;".toMediaTypeOrNull(), js);
            val req = Request.Builder()
                .url(FCM_MESSAGE_URL)
                .post(body)
                .addHeader("Authorization", "key=${SERVER_KEY}")
                .build()
            OkHttpClient().newCall(req).execute()
        }
}