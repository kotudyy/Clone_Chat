package com.example.chatting.UserFragment

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.chatting.MyApplication
import com.example.chatting.R
import com.example.chatting.databinding.RvitemUserBinding
import com.example.chatting.model.infoData

class RvItemUserViewHolder(val binding: RvitemUserBinding): RecyclerView.ViewHolder(binding.root){

}

class RvItemUserAdapter(var userData: MutableList<infoData>): RecyclerView.Adapter<RvItemUserViewHolder>(){
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RvItemUserViewHolder =
        RvItemUserViewHolder(RvitemUserBinding.inflate(LayoutInflater.from(parent.context), parent, false))

    override fun getItemCount(): Int = userData.size

    override fun onBindViewHolder(holder: RvItemUserViewHolder, position: Int) {
        val data = userData[position]
        holder.binding.run {
            username.text = data.name
            statusMessage.text = data.status_message
            music.text = data.music
        }
        //스토리지 이미지 다운로드
        val imgRef = MyApplication.storage.reference.child("profile_image/${data.docId}.jpg")
        Glide.with(holder.itemView.context).load(imgRef).error(R.drawable.img_profile).into(holder.binding.ivChatProfile)
    }
}