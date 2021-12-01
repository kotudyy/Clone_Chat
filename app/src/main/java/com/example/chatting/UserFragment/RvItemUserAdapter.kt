package com.example.chatting.UserFragment

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.chatting.databinding.RvitemUserBinding

class RvItemUserViewHolder(val binding: RvitemUserBinding): RecyclerView.ViewHolder(binding.root){

    fun setData(data: UserData){
        binding.username.text = "${data.text1}"
        binding.statusMessage.text = "${data.text2}"
    }
}

class RvItemUserAdapter(var chatData: MutableList<UserData>): RecyclerView.Adapter<RvItemUserViewHolder>(){
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RvItemUserViewHolder =
        RvItemUserViewHolder(RvitemUserBinding.inflate(LayoutInflater.from(parent.context), parent, false))

    override fun onBindViewHolder(holder: RvItemUserViewHolder, position: Int) {
        val data = chatData[position]
        holder.setData(data)
    }

    override fun getItemCount(): Int = chatData.size

}

data class UserData(val text1: String, val text2: String)