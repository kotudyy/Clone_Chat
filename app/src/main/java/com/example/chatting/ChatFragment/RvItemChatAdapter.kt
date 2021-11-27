package com.example.chatting.ChatFragment

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.chatting.databinding.RvitemChatBinding

class RvItemChatViewHolder(val binding: RvitemChatBinding): RecyclerView.ViewHolder(binding.root){

    fun setData(data: ChatData){
        binding.text1.text = "${data.text1}"
        binding.text2.text = "${data.text2}"
    }
}

class RvItemChatAdapter(var chatData: MutableList<ChatData>): RecyclerView.Adapter<RvItemChatViewHolder>(){
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RvItemChatViewHolder =
        RvItemChatViewHolder(RvitemChatBinding.inflate(LayoutInflater.from(parent.context), parent, false))

    override fun onBindViewHolder(holder: RvItemChatViewHolder, position: Int) {
        val data = chatData[position]
        holder.setData(data)
    }

    override fun getItemCount(): Int = chatData.size

}

data class ChatData(val text1: String, val text2: String)