package com.example.chatting.ChatFragment

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.chatting.ChatRoomActivity
import com.example.chatting.Model.UserRoom
import com.example.chatting.MyApplication
import com.example.chatting.R
import com.example.chatting.databinding.RvitemChatBinding
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ktx.getValue
import java.text.SimpleDateFormat
import java.util.*

class RvItemChatViewHolder(val binding: RvitemChatBinding) : RecyclerView.ViewHolder(binding.root) {
    fun setData(data: UserRoom) {
        binding.apply {
            MyApplication.db.collection("profile")
                .whereEqualTo("email", data.sender)
                .get()
                .addOnSuccessListener { document ->
                    for (field in document) tvChatUsername.text = field["name"] as String
                }

            MyApplication.storage.reference.child("${data.sender}/profile")
                .downloadUrl
                .addOnSuccessListener {
                    Glide.with(binding.root.context)
                        .load(it)
                        .error(R.drawable.img_profile)
                        .into(ivChatProfile)
                }

            tvChatPreviewmsg.text = data.lastmessage

            dateCalc(data.timestamp)


            // 채팅방별 안 읽은 메시지 개수
            getChatNum(data.chatroomid.toString())
            updateChat(data.chatroomid.toString())
        }
    }

    // 안 읽은 메시지 수 체크하는 함수
    fun getChatNum(chatRoomId: String) {
        var chatNum = 0
        var flag = true
        MyApplication.realtime.child("Messages").child(chatRoomId).get().addOnSuccessListener {
            for (data in it.children) {
                for (data_chat in data.children) {
                    if(data_chat.key == "read" && !(data_chat.value as Boolean)) flag = false
                    if(!flag && data_chat.key == "sender" && data_chat.value != MyApplication.auth.currentUser?.email) chatNum++
                }
            }
            if (chatNum == 0) {
                binding.cardViewChatNum.visibility = View.INVISIBLE
            } else {
                binding.cardViewChatNum.visibility = View.VISIBLE
                binding.tvChatNum.text = chatNum.toString()
            }
        }
    }

    //Update LastMessage Information
    private fun updateChat(chatRoomID: String) {
        val chatRoomListener = object : ChildEventListener {
            override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
            }

            override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {
                if(snapshot.key == chatRoomID) {
                    var lastMessage = ""
                    var timestamp: Long = 0
                    val userRoomData = snapshot.getValue<UserRoom>()!!
                    lastMessage = userRoomData.lastmessage
                    timestamp = userRoomData.timestamp

                    binding.tvChatPreviewmsg.text = lastMessage
                    dateCalc(timestamp)
                    getChatNum(chatRoomID)
                }
            }

            override fun onChildRemoved(snapshot: DataSnapshot) {
                TODO("Not yet implemented")
            }

            override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {
                TODO("Not yet implemented")
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }
        }
        MyApplication.realtime.child("UserRoom").addChildEventListener(chatRoomListener)
    }

    fun dateCalc(timestamp: Long) {
        val cal = Calendar.getInstance()
        cal.time = Date()

        val today = cal.time
        cal.add(Calendar.DATE, -1)
        val yesterday = cal.time
        cal.add(Calendar.DATE, 1)
        val year = SimpleDateFormat("yyyy")
        val date = SimpleDateFormat("MM.dd")

        //연도 체크
        if (year.format(timestamp) == year.format(today)) {
            //오늘인 경우
            if (date.format(timestamp) == date.format(today)) {
                val sdf = SimpleDateFormat("hh:mm")
                binding.tvChatTimestamp.text = sdf.format(timestamp)
            }
            //어제인 경우
            else if (date.format(timestamp) == date.format(yesterday)) {
                binding.tvChatTimestamp.text = "어제"
            } else {
                val sdf = SimpleDateFormat("MM월 dd일")
                binding.tvChatTimestamp.text = sdf.format(timestamp)
            }
        } else {
            val sdf = SimpleDateFormat("yyyy.MM.dd")
            binding.tvChatTimestamp.text = sdf.format(timestamp)
        }
    }

    fun getUsername(): String {
        return binding.tvChatUsername.text.toString()
    }
}

class RvItemChatAdapter(var chatData: MutableList<UserRoom>) :
    RecyclerView.Adapter<RvItemChatViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RvItemChatViewHolder =
        RvItemChatViewHolder(
            RvitemChatBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )

    override fun onBindViewHolder(holder: RvItemChatViewHolder, position: Int) {
        val data = chatData[position]
        holder.setData(data)

        holder.itemView.setOnClickListener {
            val intent = Intent(holder.itemView.context, ChatRoomActivity::class.java)
            intent.putExtra("chatRoomId", data.chatroomid)
            intent.putExtra("userName", holder.getUsername())
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            ContextCompat.startActivity(holder.itemView.context, intent, null)
        }
    }

    override fun getItemCount(): Int = chatData.size
}