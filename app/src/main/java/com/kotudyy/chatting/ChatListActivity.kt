package com.kotudyy.chatting

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.kotudyy.chatting.chatFragment.ChatFragment
import com.kotudyy.chatting.userFragment.UsersFragment
import com.kotudyy.chatting.databinding.ActivityChatListBinding
import com.google.android.material.tabs.TabLayout

class ChatListActivity : AppCompatActivity() {

    private var mBackWait:Long = 0
    val binding by lazy { ActivityChatListBinding.inflate(layoutInflater) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        supportFragmentManager.beginTransaction()
            .replace(R.id.mainFrameLayout, UsersFragment())
            .commit()

        binding.tabLayout.selectTab(binding.tabLayout.getTabAt(0))
        binding.tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener{
            override fun onTabSelected(tab: TabLayout.Tab?) {
                when(tab!!.position){
                    0 -> {
                        supportFragmentManager.beginTransaction()
                            .replace(R.id.mainFrameLayout, UsersFragment())
                            .commit()
                    }
                    1 -> {
                        supportFragmentManager.beginTransaction()
                            .replace(R.id.mainFrameLayout, ChatFragment())
                            .commit()
                    }
                }
            }
            override fun onTabUnselected(tab: TabLayout.Tab?) {}
            override fun onTabReselected(tab: TabLayout.Tab?) {}
        })
    }

    override fun onBackPressed() {
        if (System.currentTimeMillis() - mBackWait >= 2000) {
            mBackWait = System.currentTimeMillis()
            Toast.makeText(this, "뒤로가기 버튼을 한번 더 누르면 종료됩니다.", Toast.LENGTH_LONG).show()
        } else {
            finish()
        }
    }
}