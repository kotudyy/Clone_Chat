package com.kotudyy.chatting.login

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import com.kotudyy.chatting.ChatListActivity
import com.kotudyy.chatting.storage.MyApplication
import com.kotudyy.chatting.databinding.ActivityLoginBinding

class LoginActivity : AppCompatActivity() {
    //로그인 액티비티
    lateinit var binding : ActivityLoginBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        if(MyApplication.checkAuth()){
            changeLoginStatus("login")   //로그인 상태를 나타냄
        }else {
            changeLoginStatus("logout")
        }

        binding.logoutBtn.setOnClickListener {
            //로그아웃...........
            MyApplication.auth.signOut()
            MyApplication.email = null
            changeLoginStatus("logout")
        }

        binding.authBtn.setOnClickListener {    //회원가입 버튼 클릭
            startActivity(Intent(this, AuthActivity::class.java))
        }
        binding.loginBtn.setOnClickListener {   //로그인 버튼 클릭
            val email:String = binding.authEmail.text.toString()
            val password: String = binding.authPassword.text.toString()
            MyApplication.auth.signInWithEmailAndPassword(email,password).addOnCompleteListener(this){
                    task -> binding.authEmail.text.clear()
                binding.authPassword.text.clear()
                if(task.isSuccessful){
                    if(MyApplication.checkAuth()){
                        //로그인 성공
                        MyApplication.email = email
                        changeLoginStatus("login")
                    }else{
                        //발송된 메일로 인증 확인을 안 한 경우
                        Toast.makeText(baseContext,"전송된 메일로 이메일 인증이 되지 않았습니다.", Toast.LENGTH_SHORT).show()
                    }
                }else {     //없는 계정을 입력하는 경우 등
                    Toast.makeText(baseContext,"로그인 실패", Toast.LENGTH_SHORT).show()
                }
            }
        }

        binding.openChatListBtn.setOnClickListener{
            saveToken()
            startActivity(Intent(this, ChatListActivity::class.java))
            finish()
        }
    }

    //로그인 성공시 토큰 저장
    private fun saveToken() {
        val map= mutableMapOf<String, Any>()
        map["token"] ="${MyApplication.prefs.globalToken}"
        MyApplication.db.collection("profile").document("${MyApplication.email}").update(map)
    }

    @SuppressLint("SetTextI18n")
    fun changeLoginStatus(status:String){
        if(status == "login"){
            binding.run {
                binding.loginTextView.text = "${MyApplication.email} 님 반갑습니다."
                binding.loginLinear.visibility = View.GONE
                binding.btnLinear.visibility = View.GONE
                binding.logoutBtn.visibility = View.VISIBLE
                binding.openChatListBtn.visibility = View.VISIBLE
            }
        }
        else if(status == "logout"){
            binding.run {
                binding.loginTextView.text = "로그인"
                binding.loginLinear.visibility = View.VISIBLE
                binding.btnLinear.visibility = View.VISIBLE
                binding.logoutBtn.visibility = View.GONE
                binding.openChatListBtn.visibility = View.GONE
            }
        }
    }

}