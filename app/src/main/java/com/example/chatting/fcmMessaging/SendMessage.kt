package com.example.chatting.fcmMessaging

import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject
import java.lang.Exception

class SendMessage {
        fun sendNoti(token: String, ChatRoomID: String){
            Thread {   //메인스래드에서는 네트워크 통신 안되서 새로 만들어야됨
                try {
                    val root = JSONObject()     //JSON 구조

                    val data = JSONObject()
                    root.put("data", data)
                    data.put("ChatRoomID", ChatRoomID)

                    // TODO to 에 상대방 토큰값 넣으면 보내짐
                    root.put("to", token)

                    postMsg(root.toString())
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }.start()
        }

        fun postMsg(js: String) {
            val fcmMessageUrl = "https://fcm.googleapis.com/fcm/send" //기본 URL
            val serverKey =    //서버키 넣기
                "AAAAs7wNw1Y:APA91bFh7C74RQxm7Qd3AKIGLLfCvIGzn7vVunX14nU6LVqVIbPQNu3Bho63i7LB6PsQOi0KfJwbNqp75JljDj2ejXHd-37nHzXDuW56IiRdsVjtB-kk01kRixTmbEQIcitrKEt43DNJ"
            val body = js.toRequestBody("application/json;".toMediaTypeOrNull())
            val req = Request.Builder()
                .url(fcmMessageUrl)
                .post(body)
                .addHeader("Authorization", "key=${serverKey}")
                .build()
            OkHttpClient().newCall(req).execute()
        }
}