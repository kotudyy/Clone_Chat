package com.example.chatting.ChatFragment

import android.content.Intent
import android.util.Log
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
lateinit var time : String
lateinit var name : String

class RvItemChatViewHolder(val binding: RvitemChatBinding) : RecyclerView.ViewHolder(binding.root) {

    fun setData(data: UserRoom) {
        time = SimpleDateFormat("hh:mm").format(data.timestamp)

        binding.apply {
            MyApplication.db.collection("profile")
                .whereEqualTo("email", data.sender)
                .get()
                .addOnSuccessListener {document -> for (field in document) tvChatUsername.text = field["name"] as String}
            tvChatPreviewmsg.text = "${data.lastMessage}"
            tvChatTimestamp.text = time
            val imgRef = MyApplication.storage.reference.child("${data.sender}/profile")
            Glide.with(binding.root.context)
                .load(imgRef)
                .error(R.drawable.img_profile)
                .into(ivChatProfile)
        }
    }
}

class RvItemChatAdapter(var userRoom: MutableList<UserRoom>) :
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
        val data = userRoom[position]
        holder.setData(data)
        var name : String = ""
        MyApplication.db.collection("profile")
            .whereEqualTo("email", data.sender)
            .get()
            .addOnSuccessListener {document -> for (field in document) name = field["name"] as String}

        holder.itemView.setOnClickListener {
            val intent = Intent(holder.itemView?.context, ChatRoomActivity::class.java)
            intent.putExtra("userName", name)
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            ContextCompat.startActivity(holder.itemView.context, intent, null)
        }
    }

    override fun getItemCount(): Int = userRoom.size

}