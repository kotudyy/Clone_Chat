package com.example.chatting.ChatFragment

import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.chatting.ChatRoomActivity
import com.example.chatting.databinding.RvitemChatBinding

class RvItemChatViewHolder(val binding: RvitemChatBinding) : RecyclerView.ViewHolder(binding.root) {

    fun setData(data: ChatData) {
        binding.tvChatUsername.text = "${data.chatUsername}"
        binding.tvChatPreviewmsg.text = "${data.chatPreviewMsg}"
        binding.tvChatTimestamp.text = "${data.chatTime}"
    }
}

class RvItemChatAdapter(var chatData: MutableList<ChatData>) :
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
            intent.putExtra("userName", data.chatUsername)
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            ContextCompat.startActivity(holder.itemView.context, intent, null)
        }
    }

    override fun getItemCount(): Int = chatData.size

}

data class ChatData(
    val chatUsername: String,
    val chatPreviewMsg: String,
    val chatTime: String,
)