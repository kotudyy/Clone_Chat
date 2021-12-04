package com.example.chatting.Login

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.example.chatting.MyApplication
import com.example.chatting.databinding.ActivityAuthBinding
import com.google.firebase.firestore.FirebaseFirestore

class AuthActivity : AppCompatActivity() {      //회원가입 액티비티
    lateinit var binding: ActivityAuthBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAuthBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.authBtn.setOnClickListener {
            //이메일,비밀번호 회원가입........................=
            val email: String = binding.authEmail.text.toString()
            val password:String = binding.authPassword.text.toString()
            MyApplication.auth.createUserWithEmailAndPassword(email,password).addOnCompleteListener(this) {
                    task -> binding.authEmail.text.clear()
                binding.authPassword.text.clear()
                if(task.isSuccessful) {     //파이어베이스 등록
                    MyApplication.auth.currentUser?.sendEmailVerification()?.addOnCompleteListener {
                            sendTask -> if(sendTask.isSuccessful){      //이메일 전송
                        Toast.makeText(this,"회원가입에 성공했습니다. 전송된 메일을 확인해 주세요.", Toast.LENGTH_SHORT).show()

                        // 회원가입 성공 시 profile 컬렉션에 회원 정보 저장
                        val data = hashMapOf(
                            "email" to email,
                            "music" to "testmusic",
                            "name" to "yeon",
                            "status_message" to "teststatus"
                        )
                        FirebaseFirestore.getInstance().collection("profile")
                            .add(data)
                            .addOnSuccessListener {
                                // 성공할 경우
                                Toast.makeText(this, "데이터가 추가되었습니다", Toast.LENGTH_SHORT).show()
                            }
                            .addOnFailureListener { exception ->
                                // 실패할 경우
                                Log.w("MainActivity", "Error getting documents: $exception")
                            }
                    }else {
                        Toast.makeText(this,"메일 전송 실패", Toast.LENGTH_SHORT).show()
                    }
                    }
                }else {
                    Toast.makeText(this,"회원가입 실패", Toast.LENGTH_SHORT).show()
                    Log.d("Grusie","${task.exception}")
                }
            }
        }
    }
}