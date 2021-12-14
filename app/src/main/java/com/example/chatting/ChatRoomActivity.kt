package com.example.chatting

import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.chatting.Model.Messages
import com.example.chatting.Model.UserData
import com.example.chatting.databinding.ActivityChatRoomBinding
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

class ChatRoomActivity : AppCompatActivity() {
    lateinit var userName: String
    lateinit var userEmail: String
    private val database = Firebase.database
    private val myRef = database.getReference("Messages")
    private val Messages = mutableListOf<Messages>()
    lateinit var adapter : ChatRoomAdatpter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityChatRoomBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        userName = intent.getSerializableExtra("userName") as String
        userEmail = intent.getSerializableExtra("userEmail") as String
        binding.toolbar.setTitle(userName)

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

        adapter = ChatRoomAdatpter(Messages)
        binding.rvChatroom.adapter = adapter
        binding.rvChatroom.layoutManager = LinearLayoutManager(this)

        binding.btnSend.setOnClickListener{
            val msg = binding.etMessage.text.toString()   //msg
            if(msg != null) {
                val messageData = Messages(
                    message = msg,
                    timeStamp = System.currentTimeMillis() ,
                    sendEmail = MyApplication.auth.currentUser?.email.toString(),
                    type = 1
                )
                binding.etMessage.text.clear()
                Messages.add(messageData)
                Log.d("grusie", "${Messages[0].sendEmail}, ${Messages[0].message}")
                myRef.push().setValue(Messages[Messages.size-1])
                Toast.makeText(this,"$userEmail",Toast.LENGTH_SHORT).show()
            }
        }
    }
}
