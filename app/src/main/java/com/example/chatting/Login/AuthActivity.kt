package com.example.chatting.Login

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.example.chatting.Model.ProfileData
import com.example.chatting.MyApplication
import com.example.chatting.databinding.ActivityAuthBinding

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

                        //회원가입 시 firebase에 새로운 ProfileData 추가 -> 이메일 인증 성공을 전제
                        val userEmail = MyApplication.auth.currentUser?.email!!
                        val profileData = ProfileData(
                            userEmail, userEmail, "", "", ""
                        )

                        MyApplication.db.collection("profile_dongk00").document("$userEmail")
                            .set(profileData)
                            .addOnSuccessListener { Toast.makeText(this,"프로필 정보 추가 완료", Toast.LENGTH_SHORT).show() }
                            .addOnFailureListener { Toast.makeText(this,"프로필 정보 추가 실패", Toast.LENGTH_SHORT).show()  }

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