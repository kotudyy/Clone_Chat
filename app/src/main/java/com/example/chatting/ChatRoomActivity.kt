package com.example.chatting


import android.annotation.SuppressLint
import android.content.DialogInterface
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Message
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.chatting.Model.Messages
import com.example.chatting.Model.UserRoom
import com.example.chatting.databinding.ActivityChatRoomBinding
import com.example.chatting.databinding.ItemReceivemsgBinding
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.database.ktx.getValue
import com.google.firebase.ktx.Firebase
import java.lang.Exception
import java.text.SimpleDateFormat
import kotlin.properties.Delegates

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
    var lastDate:Long ?= 0
    var currentDate:Long ?= 0
    var loadMsg : Messages ?= null

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

        setMessagesRead()

        adapter = ChatRoomAdatpter(Messages, chatRoomId!!)

        binding.rvChatroom.adapter = adapter
        binding.rvChatroom.layoutManager = LinearLayoutManager(this)
        myRecyclerView = binding.rvChatroom

        adapter.notifyDataSetChanged()
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

        myRecyclerView.addOnLayoutChangeListener { view, i, i2, i3, bottom, i5, i6, i7, oldBottom ->
            if(bottom < oldBottom)
                binding.rvChatroom.scrollBy(0, oldBottom - bottom)
        }

        //메세지 보내기 버튼 클릭시
        binding.btnSend.setOnClickListener {
            val msg = binding.etMessage.text.toString()   //msg
            val time = System.currentTimeMillis()
            if (msg != "") {
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
                currentDate = messageData.timestamp
                UserRoom.add(userRoom)
                messageRef.child("$chatRoomId").push().setValue(messageData)
                userRoomRef.child("$chatRoomId").setValue(userRoom)
                adapter.notifyDataSetChanged()
                myRecyclerView.scrollToPosition(adapter.itemCount-1)
            }
        }
    }

    private fun setMessagesRead() {
        val keys = mutableListOf<String>()

        messageRef.child(chatRoomId!!)
            .get()
            .addOnSuccessListener { messages ->
                for (message in messages.children){
                    keys.add(message.key!!)
                }

                for (key in keys){
                    messageRef.child(chatRoomId!!).child(key).child("sender")
                        .get()
                        .addOnSuccessListener {
                            if(it.value != MyApplication.auth.currentUser?.email){
                                messageRef.child(chatRoomId!!).child(key).child("read").setValue(true)
                            }
                        }
                }
            }
    }

    private fun loadChatData() {
        val messagesDataListener = object : ChildEventListener{
            override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                lastDate = loadMsg?.timestamp
                loadMsg = snapshot.getValue<Messages>()!!
                currentDate = loadMsg?.timestamp
                dateCalc()
                Messages.add(loadMsg!!)
                adapter.notifyDataSetChanged()
                myRecyclerView.scrollToPosition(adapter.itemCount-1)
            }

            override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {
                adapter.notifyDataSetChanged()
            }

            override fun onChildRemoved(snapshot: DataSnapshot) {
                snapshot.getValue<Messages>()?.let { adapter.removeItem(it) }
                if(Messages[Messages.size-1].sender == ""){
                    Messages.removeAt(Messages.size-1)
                }
            }

            override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {
            }

            override fun onCancelled(error: DatabaseError) {
            }
        }
        messageRef.child("$chatRoomId").addChildEventListener(messagesDataListener)
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

    fun dateCalc() {
        if(lastDate != null && currentDate !=null) {
            if(SimpleDateFormat("yyyy년 MM월 dd일").format(lastDate) < SimpleDateFormat("yyyy년 MM월 dd일").format(currentDate)) {
                lastDate = currentDate
                addDate()
            }
        }
        else if(lastDate == null){
            addDate()
        }
    }

    fun addDate() {
        Messages.add(
            Messages(
                message = "",
                timestamp = currentDate!!,
                sender = ""
            ))
    }
}

