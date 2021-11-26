package com.example.chatting

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.example.chatting.databinding.ActivityLoginBinding

class LoginActivity : AppCompatActivity() {     //로그인 액티비티
    lateinit var binding : ActivityLoginBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

/*        if(MyApplication.checkAuth()){
            changeLoginStatus("login")   //로그인 상태를 나타냄
        }else {
            changeLoginStatus("logout")
        }*/

        binding.logoutBtn.setOnClickListener {
            //로그아웃...........
            MyApplication.auth.signOut()
            MyApplication.email = null
            changeLoginStatus("logout")
        }

        binding.authBtn.setOnClickListener {
            startActivity(Intent(this, AuthActivity::class.java))
        }
    }
    fun changeLoginStatus(status:String){
        if(status == "login"){
            binding.run {
                binding.loginTextView.text = "${MyApplication.email} 님 반갑습니다."
                binding.loginLinear.visibility = View.GONE
                binding.btnLinear.visibility = View.GONE
                binding.logoutBtn.visibility = View.VISIBLE
            }
        }
        else if(status == "logout"){
            binding.run {
                binding.loginTextView.text = "로그인"
                binding.loginLinear.visibility = View.VISIBLE
                binding.btnLinear.visibility = View.VISIBLE
                binding.logoutBtn.visibility = View.GONE
            }
        }
    }
}