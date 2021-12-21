package com.example.chatting.ChatRoom

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.chatting.Model.Messages
import com.example.chatting.MyApplication
import com.example.chatting.R
import com.example.chatting.databinding.ItemReceivemsgBinding
import com.example.chatting.databinding.ItemSendmsgBinding
import java.lang.RuntimeException
import java.text.SimpleDateFormat


class ChatRoomAdatpter(var messages : MutableList<Messages>) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    lateinit var context : Context
    lateinit var time : String
    var type : Int = 0

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when(viewType){
            0 -> {
                val binding = ItemSendmsgBinding.inflate(LayoutInflater.from(parent.context),parent,false)
                sendViewHolder(binding)
            }
            1 -> {
                val binding = ItemReceivemsgBinding.inflate(LayoutInflater.from(parent.context),parent,false)
                receiveViewHolder(binding)
            }
            else -> throw RuntimeException("Error")
        }
    }
    inner class sendViewHolder(private val binding: ItemSendmsgBinding) :RecyclerView.ViewHolder(binding.root){
        fun bind(data: Messages){
            binding.apply{
                tvChatroomSendmsg.text = data.message
                tvChatroomSendtime.text = time
            }
        }
    }
    inner class receiveViewHolder(private val binding: ItemReceivemsgBinding) :RecyclerView.ViewHolder(binding.root){
        fun bind(data: Messages){
            binding.apply{
                MyApplication.db.collection("profile")
                        .whereEqualTo("email", data.sender)
                        .get()
                        .addOnSuccessListener {document -> for (field in document) tvChatroomUsername.text = field["name"] as String}
                tvChatroomReceivemsg.text = data.message
                tvChatroomReceivetime.text = time

                val imgRef = MyApplication.storage.reference.child("${data.sender}/profile")
                Glide.with(binding.root.context)
                    .load(imgRef)
                    .error(R.drawable.img_profile)
                    .into(ivChatProfile)
            }
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val data = messages[position]
        time = SimpleDateFormat("hh:mm").format(data.timestamp)
        when (type){
            0 -> (holder as sendViewHolder).bind(data)
            1 -> (holder as receiveViewHolder).bind(data)
        }
    }

    override fun getItemViewType(position: Int): Int {
        if(messages[position].sender == MyApplication.auth.currentUser?.email)
            type = 0
        else
            type = 1
        return type
    }

    override fun getItemCount(): Int {
        if(messages!=null) return messages.size
        else return 0
    }


}