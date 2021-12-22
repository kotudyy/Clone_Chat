package com.example.chatting


import android.annotation.SuppressLint
import android.content.DialogInterface
import android.content.Intent
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.chatting.ChatFragment.ChatFragment
import com.example.chatting.ChatFragment.RvItemChatAdapter
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
    var chatRoomId: String? = null
    private val database = Firebase.database
    private val messageRef = database.getReference("Messages")
    private val userRoomRef = database.getReference("UserRoom")
    private val Messages = mutableListOf<Messages>()
    private val UserRoom = mutableListOf<UserRoom>()
    lateinit var adapter: ChatRoomAdatpter
    lateinit var binding: ActivityChatRoomBinding
    lateinit var myRecyclerView: RecyclerView

    @SuppressLint("NotifyDataSetChanged")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityChatRoomBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.chatroomToolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        try { userEmail = intent.getSerializableExtra("userEmail") as String } catch (e: Exception) { }
        try { chatRoomId = intent.getStringExtra("chatRoomId") as String } catch (e: Exception) { }
        try {
            userName = intent.getStringExtra("userName") as String
            binding.chatroomToolbar.setTitle(userName)
        } catch (e: Exception) {
        }

        adapter = ChatRoomAdatpter(Messages)
        binding.rvChatroom.adapter = adapter
        binding.rvChatroom.layoutManager = LinearLayoutManager(this)
        myRecyclerView = binding.rvChatroom
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


        binding.btnSend.setOnClickListener {
            val msg = binding.etMessage.text.toString()   //msg
            val time = System.currentTimeMillis()
            if (msg != null) {
                val messageData = Messages(
                    message = msg,
                    timestamp = time,
                    sender = MyApplication.auth.currentUser?.email.toString()
                )
                val userRoom = UserRoom(
                    lastmessage = msg,
                    timestamp = time,
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
        val messagesDataListener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                var message = ""
                var timestamp: Long = 0
                var sender = ""
                for (chatRooms in snapshot.children) {
                    if (chatRooms.key as String == chatRoomId) {
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
                Log.d("grusie", "failed")
            }
        }
        messageRef.addListenerForSingleValueEvent(messagesDataListener)
    }

    //액션버튼 메뉴 액션바에 집어 넣기
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_chat_room, menu)
        return true
    }

    //액션버튼 클릭 했을 때
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item?.itemId){
            android.R.id.home -> {
                finish()
                return true
            }
            R.id.chatting_menu_exit -> {
                AlertDialog.Builder(this).apply {
                    setTitle("확인")
                    setMessage("채팅방을 나가시겠습니까?")
                    setPositiveButton(
                        "OK",
                        DialogInterface.OnClickListener { dialog, which ->
                            MyApplication.realtime.child("chatRoomUser")
                                .child(chatRoomId.toString())
                                .removeValue()

                            MyApplication.realtime.child("UserRoom")
                                .child(chatRoomId.toString())
                                .removeValue()

                            MyApplication.realtime.child("Messages")
                                .child(chatRoomId.toString())
                                .removeValue()
                            finish()
                        })
                    setNegativeButton(
                        "Cancel",
                        DialogInterface.OnClickListener { dialog, which ->
                            dialog.dismiss()
                        })
                    show()
                }
                return super.onOptionsItemSelected(item)
            }
            else -> return super.onOptionsItemSelected(item)
        }
    }
}

