package com.example.chatting

import android.annotation.SuppressLint
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.chatting.Model.MessageData
import com.example.chatting.databinding.ActivityChatRoomBinding

class ChatRoomActivity : AppCompatActivity() {
    lateinit var binding: ActivityChatRoomBinding
    lateinit var userName: String
    lateinit var adapter: RvItemChatMessageAdapter
    var messageData = mutableListOf<MessageData>()
    var userEmail = MyApplication.auth.currentUser?.email!!

    @SuppressLint("NotifyDataSetChanged")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityChatRoomBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        userName = intent.getSerializableExtra("userName") as String
        binding.toolbar.setTitle(userName)

        //RecyclerView Adapter 연결
        adapter = RvItemChatMessageAdapter(messageData)
        binding.rvChatroom.adapter = adapter
        binding.rvChatroom.layoutManager = LinearLayoutManager(this)

        binding.etMessage.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(p0: Editable?) {
                if (binding.etMessage.length() > 0) {
                    binding.btnSend.setBackgroundColor(applicationContext.resources.getColor(R.color.yellow))
                    binding.btnSend.isEnabled = true
                } else {
                    binding.btnSend.setBackgroundColor(Color.WHITE)
                    binding.btnSend.isEnabled = false
                }
            }
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                if (binding.etMessage.length() > 0) {
                    binding.btnSend.setBackgroundColor(applicationContext.resources.getColor(R.color.yellow))
                    binding.btnSend.isEnabled = true
                } else {
                    binding.btnSend.setBackgroundColor(Color.WHITE)
                    binding.btnSend.isEnabled = false
                }
            }
            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                if (binding.etMessage.length() > 0) {
                    binding.btnSend.setBackgroundColor(applicationContext.resources.getColor(R.color.yellow))
                    binding.btnSend.isEnabled = true
                } else {
                    binding.btnSend.setBackgroundColor(Color.WHITE)
                    binding.btnSend.isEnabled = false
                }
            }
        })

        //전송 버튼
        binding.btnSend.setOnClickListener {
            if (binding.etMessage.length() > 0) {
                val stamp = System.currentTimeMillis()
                val data = MessageData(
                    binding.etMessage.text.toString(),
                    userEmail,
                    stamp,
                    1
                )
                messageData.add(data)
                adapter.notifyDataSetChanged()

                MyApplication.realtime.child("Messages").child("test")
                    .setValue(data)
            }
            binding.etMessage.text.clear()
        }

    }
}