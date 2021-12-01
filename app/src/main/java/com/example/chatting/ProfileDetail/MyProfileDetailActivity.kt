package com.example.chatting.ProfileDetail

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.example.chatting.R
import com.example.chatting.databinding.ActivityMainBinding
import com.example.chatting.databinding.ActivityMyProfileDetailBinding

class MyProfileDetailActivity : AppCompatActivity() {

    val binding by lazy{ActivityMyProfileDetailBinding.inflate(layoutInflater)}
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        //취소 버튼 클릭 시
        binding.myProfileBack.setOnClickListener { finish() }

        //즐겨찾기 버튼 클릭 시

        //프로필 사진 클릭 시

        //채팅 버튼 클릭 시

        //편집 버튼 클릭 시
        binding.myProfileEdit.setOnClickListener {

            binding.run {
                myProfileChat.visibility = View.GONE
                myProfileEdit.visibility = View.GONE
                myProfileNameEdit.visibility = View.VISIBLE
                myProfileStatusMsgEdit.visibility = View.VISIBLE
            }
        }
    }
}