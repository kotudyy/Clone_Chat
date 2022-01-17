package com.example.chatting

import androidx.multidex.MultiDexApplication
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ktx.database
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.ktx.storage

class MyApplication: MultiDexApplication() {
    companion object {
        lateinit var auth : FirebaseAuth
        lateinit var db: FirebaseFirestore
        lateinit var storage: FirebaseStorage
        lateinit var realtime: DatabaseReference
        lateinit var prefs : MySharedPreferences
        var email: String? = null
        fun checkAuth(): Boolean {      //이메일 인증 완료해야만 true 반환
            val currentUser = auth.currentUser
            return currentUser?.let {   //유저가 저장되어있는지
                email = currentUser.email
                currentUser.isEmailVerified
            } ?: let{
                false
            }
        }
    }

    override fun onCreate() {
        prefs = MySharedPreferences(applicationContext)
        super.onCreate()
        auth = Firebase.auth
        db = FirebaseFirestore.getInstance()
        storage = Firebase.storage
        realtime = Firebase.database.reference
    }
}