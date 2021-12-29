package com.example.chatting.ChatFragment

import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.chatting.ChatRoomActivity
import com.example.chatting.Model.UserRoom
import com.example.chatting.MyApplication
import com.example.chatting.R
import com.example.chatting.databinding.RvitemChatBinding
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
                    for (field in document) binding.tvChatUsername.text = field["name"] as String
                }

            val imgRefProfile = MyApplication.storage.reference.child("${data.sender}/profile")
            Glide.with(binding.root.context)
                .load(imgRefProfile)
                .error(R.drawable.img_profile)
                .into(ivChatProfile)

            binding.tvChatPreviewmsg.text = data.lastmessage

            val year = SimpleDateFormat("yyyy")
            val date = SimpleDateFormat("MM.dd")

            //연도 체크
            if(year.format(data.timestamp) == year.format(today)) {
                //오늘인 경우
                if(date.format(data.timestamp) == date.format(today)) {
                    val sdf = SimpleDateFormat("hh:mm")
                    binding.tvChatTimestamp.text = sdf.format(data.timestamp)
                }
                //어제인 경우
                else if(date.format(data.timestamp) == date.format(yesterday)) {
                    binding.tvChatTimestamp.text = "어제"
                }
                else {
                    val sdf = SimpleDateFormat("MM월 dd일")
                    binding.tvChatTimestamp.text = sdf.format(data.timestamp)
                }
            } else {
                val sdf = SimpleDateFormat("yyyy.MM.dd")
                binding.tvChatTimestamp.text = sdf.format(data.timestamp)
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