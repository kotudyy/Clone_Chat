package com.example.chatting

import android.util.Log
import androidx.multidex.MultiDexApplication
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.ktx.storage
import java.util.zip.Inflater

class MyApplication: MultiDexApplication() {
    companion object {
        lateinit var auth: FirebaseAuth
        var email: String? = null
        fun checkAuth(): Boolean {      //이메일 인증 완료해야만 true 반환
            val currentUser = auth.currentUser
            return currentUser?.let {   //유저가 저장되어있는지
                email = currentUser.email
                if (currentUser.isEmailVerified) {    //이메일 인증 완료 했는지.
                    true
                } else {
                    false
                }
            } ?: let {
                false
            }
        }

        lateinit var db: FirebaseFirestore
        lateinit var storage: FirebaseStorage
    }
    override fun onCreate() {
        super.onCreate()
        auth = Firebase.auth

        db = FirebaseFirestore.getInstance()
        storage = Firebase.storage
    }
}