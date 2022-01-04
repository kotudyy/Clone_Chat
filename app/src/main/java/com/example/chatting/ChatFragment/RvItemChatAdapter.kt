package com.example.chatting.ChatFragment

import android.annotation.SuppressLint
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.chatting.ChatRoomActivity
import com.example.chatting.Model.Messages
import com.example.chatting.Model.UserRoom
import com.example.chatting.MyApplication
import com.example.chatting.R
import com.example.chatting.databinding.RvitemChatBinding
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import java.text.SimpleDateFormat
import java.util.*

class RvItemChatViewHolder(val binding: RvitemChatBinding) : RecyclerView.ViewHolder(binding.root) {

    fun setData(data: UserRoom) {
        val cal = Calendar.getInstance()
        cal.time = Date()

        val today = cal.time
        cal.add(Calendar.DATE, -1)
        val yesterday = cal.time
        cal.add(Calendar.DATE, 1)

        binding.apply {
            MyApplication.db.collection("profile")
                .whereEqualTo("email", data.sender)
                .get()
                .addOnSuccessListener { document ->
                    for (field in document) tvChatUsername.text = field["name"] as String
                }

            val imgRefProfile = MyApplication.storage.reference.child("${data.sender}/profile")
            Glide.with(binding.root.context)
                .load(imgRefProfile)
                .error(R.drawable.img_profile)
                .into(ivChatProfile)

            tvChatPreviewmsg.text = data.lastmessage

            val year = SimpleDateFormat("yyyy")
            val date = SimpleDateFormat("MM.dd")

            //연도 체크
            if (year.format(data.timestamp) == year.format(today)) {
                //오늘인 경우
                if (date.format(data.timestamp) == date.format(today)) {
                    val sdf = SimpleDateFormat("hh:mm")
                    tvChatTimestamp.text = sdf.format(data.timestamp)
                }
                //어제인 경우
                else if (date.format(data.timestamp) == date.format(yesterday)) {
                    tvChatTimestamp.text = "어제"
                } else {
                    val sdf = SimpleDateFormat("MM월 dd일")
                    tvChatTimestamp.text = sdf.format(data.timestamp)
                }
            } else {
                val sdf = SimpleDateFormat("yyyy.MM.dd")
                tvChatTimestamp.text = sdf.format(data.timestamp)
            }


            // 채팅방별 안 읽은 메시지 개수
            var myIdKey = ""

            Log.d("@@chatRoomId", data.chatroomid.toString())
            MyApplication.realtime.child("chatRoomUser").child(data.chatroomid.toString())
                .get()
                .addOnSuccessListener {
                    for (data in it.children) {
                        when (data.value) {
                            MyApplication.auth.currentUser?.email -> {
                                myIdKey = data.key as String
                            }
                        }
                    }
                    getLastVisitedTime(data.chatroomid.toString(), myIdKey)
                }
        }
    }

    // 채팅방 마지막 접속 시간 가져오는 함수
    fun getLastVisitedTime(chatRoomId: String, idKey: String) {
        var time: Long = 0

        MyApplication.realtime.child("UserLastVisited").child(chatRoomId)
            .get()
            .addOnSuccessListener {
                for (data in it.children) {
                    if(data.key == idKey){
                        time = data.value as Long
                        getChatNum(chatRoomId, time)
                    }
                    Log.d("@@lastVisitedTime", time.toString())
                }
            }
    }

    // 안 읽은 메시지 수 체크하는 함수
    fun getChatNum(chatRoomId: String, lastVisitedTime: Long) {
        var chatNum = 0
        MyApplication.realtime.child("Messages").child(chatRoomId).get().addOnSuccessListener {
            var timestamp: Long = 0

            for (data in it.children) {
                for (data_time in data.children) {
                    when (data_time.key) {
                        "timestamp" -> timestamp = data_time.value as Long
                    }

                    if (timestamp > lastVisitedTime) {
                        Log.d("@@timestamp++", timestamp.toString())
                        Log.d("@@lastvisitedTime++", lastVisitedTime.toString())

                        chatNum++
                        Log.d("@@chatNum++", chatNum.toString())
                    }
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
            val intent = Intent(holder.itemView?.context, ChatRoomActivity::class.java)
            intent.putExtra("chatRoomId", data.chatroomid)
            intent.putExtra("userName", holder.getUsername())
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            ContextCompat.startActivity(holder.itemView.context, intent, null)
        }
    }

    override fun getItemCount(): Int = chatData.size
}