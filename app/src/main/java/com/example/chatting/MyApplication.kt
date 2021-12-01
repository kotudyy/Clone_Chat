package com.example.chatting

import androidx.multidex.MultiDexApplication
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
class MyApplication: MultiDexApplication() {
    companion object {
        lateinit var auth : FirebaseAuth
        var email: String? = null
        fun checkAuth(): Boolean {      //이메일 인증 완료해야만 true 반환
            val currentUser = auth.currentUser
            return currentUser?.let {   //유저가 저장되어있는지
                email = currentUser.email
                if(currentUser.isEmailVerified){    //이메일 인증 완료 했는지.
                    true
                } else {
                    false
                }
            } ?: let{
                false
            }
        }
    }

    override fun onCreate() {
        super.onCreate()
        auth = Firebase.auth
    }
}