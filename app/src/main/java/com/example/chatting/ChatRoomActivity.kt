package com.example.chatting

import android.annotation.SuppressLint
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.chatting.Model.MessageData
import com.example.chatting.databinding.ActivityChatRoomBinding
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener

class ChatRoomActivity : AppCompatActivity() {
    lateinit var chatRoomID : String
    lateinit var chatRoomUser : String

    lateinit var binding: ActivityChatRoomBinding
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

        chatRoomID = intent.getSerializableExtra("chatRoomID") as String
        chatRoomUser = (chatRoomID.split("-")[1]).replace(",",".")

        getChatRoomData()

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
                    stamp
                )
                messageData.add(data)
                adapter.notifyDataSetChanged()

                MyApplication.realtime.child("Messages").child("test")
                    .setValue(data)
            }
            binding.etMessage.text.clear()
        }

    }


    //채팅방 메시지 가져오는 함수
    private fun getChatRoomData() {
        //setChatRoomTitle
        MyApplication.db.collection("profile")
            .document(chatRoomUser)
            .get()
            .addOnSuccessListener {
                val chatUserName = it["name"] as String
                binding.toolbar.title = chatUserName
            }

        //setChatRoomContent
        val msgListener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {

                //snapshot은 채팅방 내 message들 정보 전체 ->
                //{Message3={sender=dongk000@gmail.com, message=hello2, timestamp=123151615},
                // Message2={sender=dddongk00@gmail.com, message=hello, timestamp=12314142313},
                // Message1=asdfsaf

                for (msgInfo in snapshot.children){

                    // snapshot.children은 message 각각의 정보 ->
                    // {sender=dongk000@gmail.com, message=hello2, timestamp=123151615} ...

                    for (msgInfoChild in msgInfo.children){

                        // msgInfo.children은 message 내 각각의 정보들
                        // sender, message, timestamp

                        val sender : String
                        val message : String
                        val timestamp : Long

                        when(msgInfoChild!!.key){
                            "sender" -> {
                                sender = msgInfoChild.value as String
                            }
                            "message" -> {
                                message = msgInfoChild.value as String
                            }
                            "timestamp" -> {
                                timestamp = msgInfoChild.value as Long
                            }
                        }

                    }

                    Log.d("test", "${msgInfo.value}")
                }

            }

            override fun onCancelled(error: DatabaseError) {
                Log.d("test","Failed")
            }
        }

        MyApplication.realtime.child(chatRoomID).addValueEventListener(msgListener)
    }

}