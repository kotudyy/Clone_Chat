package com.example.chatting


import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.chatting.Model.MessageData
import com.example.chatting.Model.Messages
import com.example.chatting.Model.UserRoom
import com.example.chatting.databinding.ActivityChatRoomBinding
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.firestore.auth.User
import com.google.firebase.ktx.Firebase
import java.lang.Exception

class ChatRoomActivity : AppCompatActivity() {
    lateinit var userName: String
    lateinit var userEmail: String
    var chatRoomId:String ?= null
    private val database = Firebase.database
    private val messageRef = database.getReference("Messages")
    private val userRoomRef = database.getReference("UserRoom")
    private val Messages = mutableListOf<Messages>()
    private val UserRoom = mutableListOf<UserRoom>()
    lateinit var adapter : ChatRoomAdatpter
    lateinit var binding: ActivityChatRoomBinding
    var messageData = mutableListOf<MessageData>()

    @SuppressLint("NotifyDataSetChanged")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityChatRoomBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)


        try {
            userName = intent.getSerializableExtra("userName") as String
            binding.toolbar.setTitle(userName)
        } catch (e:Exception){}
        try { userEmail = intent.getSerializableExtra("userEmail") as String }catch (e:Exception){}
        try{ chatRoomId = intent.getStringExtra("chatRoomId") as String } catch (e:Exception){}
        loadChatData()

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
            val time = System.currentTimeMillis()
            if(msg != null) {
                val messageData = Messages(
                    message = msg,
                    timestamp = time ,
                    sender = MyApplication.auth.currentUser?.email.toString()
                )
                val userRoom = UserRoom(
                    chatroomid = chatRoomId!!,
                    lastmessage = msg,
                    timestamp = time ,
                    sender = MyApplication.auth.currentUser?.email.toString()
                )
                binding.etMessage.text.clear()
                Messages.add(messageData)
                UserRoom.add(userRoom)
                messageRef.child("$chatRoomId").push().setValue(messageData)
                userRoomRef.child("$chatRoomId").push().setValue(userRoom)
            }
        }
    }

    private fun loadChatData() {
        val messagesDataListener = object: ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                var message = ""
                var timestamp: Long = 0
                var sender = ""
                for(chatRooms in snapshot.children){
                    if(chatRooms.key as String == chatRoomId) {
                        for (messageDoc in chatRooms.children) {
                                for (messages in messageDoc.children) {
                                    when (messages.key) {
                                        "message" -> {
                                            message = messages.value as String
                                        }
                                        "timestamp" -> {
                                            timestamp = messages.value as Long
                                        }
                                        "sender" -> {
                                            sender = messages.value as String
                                        }
                                    }
                                }
                            val msgData = Messages(
                                message = message,
                                timestamp = timestamp,
                                sender = sender
                            )
                            Messages.add(msgData)
                        }
                    }
                }
            }
            override fun onCancelled(error: DatabaseError) {
                Log.d("grusie","failed")
            }
        }
        messageRef.addListenerForSingleValueEvent(messagesDataListener)
    }
}

