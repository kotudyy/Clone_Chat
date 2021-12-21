package com.example.chatting.ChatRoom


import android.annotation.SuppressLint
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.WindowManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.chatting.Model.Messages
import com.example.chatting.Model.UserRoom
import com.example.chatting.MyApplication
import com.example.chatting.R
import com.example.chatting.databinding.ActivityChatRoomBinding
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
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
    lateinit var myRecyclerView: RecyclerView

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
        try { chatRoomId = intent.getStringExtra("chatRoomId") as String } catch (e:Exception){}


        myRecyclerView = binding.rvChatroom
        adapter = ChatRoomAdatpter(Messages)
        binding.rvChatroom.adapter = adapter
        binding.rvChatroom.layoutManager = LinearLayoutManager(this)
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
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
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
                    //chatroomid = chatRoomId!!,
                    lastmessage = msg,
                    timestamp = time ,
                    sender = MyApplication.auth.currentUser?.email.toString()
                )
                binding.etMessage.text.clear()
                Messages.add(messageData)
                UserRoom.add(userRoom)
                messageRef.child("$chatRoomId").push().setValue(messageData)
                userRoomRef.child("$chatRoomId").setValue(userRoom)
                myRecyclerView.scrollToPosition(adapter.itemCount-1)
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
                adapter.notifyDataSetChanged()
                myRecyclerView.scrollToPosition(adapter.itemCount-1)
            }
            override fun onCancelled(error: DatabaseError) {
                Log.d("grusie","failed")
            }
        }
        messageRef.addListenerForSingleValueEvent(messagesDataListener)
    }
}

