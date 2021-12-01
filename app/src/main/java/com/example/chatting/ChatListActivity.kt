package com.example.chatting

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.chatting.ChatFragment.ChatFragment
import com.example.chatting.PlusFragment.PlusFragment
import com.example.chatting.UserFragment.UsersFragment
import com.example.chatting.databinding.ActivityChatListBinding
import com.example.chatting.databinding.ActivityMainBinding
import com.google.android.material.tabs.TabLayout

class ChatListActivity : AppCompatActivity() {

    val binding by lazy { ActivityChatListBinding.inflate(layoutInflater) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        val transaction = supportFragmentManager.beginTransaction()
            .add(R.id.mainFrameLayout, ChatFragment())
            .commit()

        binding.tabLayout.selectTab(binding.tabLayout.getTabAt(1))
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
                    2-> {
                        supportFragmentManager.beginTransaction()
                            .replace(R.id.mainFrameLayout, PlusFragment())
                            .commit()
                    }
                }
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {
            }

            override fun onTabReselected(tab: TabLayout.Tab?) {
                // 최상단으로 스크롤
            }

        })
    }
}