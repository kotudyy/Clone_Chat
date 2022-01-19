package com.example.chatting.userFragment

import android.content.Intent
import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.chatting.model.UserData
import com.example.chatting.storage.MyApplication
import com.example.chatting.profileDetail.MyProfileDetailActivity
import com.example.chatting.R
import com.example.chatting.databinding.RvitemUserBinding


class RvItemUserViewHolder(val binding: RvitemUserBinding) : RecyclerView.ViewHolder(binding.root) {

    fun setData(data: UserData) {
        binding.run {
            username.text = data.name
            statusMessage.text = data.statusMsg
            profileMusic.text = data.profileMusic

            MyApplication.storage.reference.child("${data.email}/profile")
                .downloadUrl
                .addOnSuccessListener {
                    Glide.with(binding.root.context)
                        .load(it)
                        .error(R.drawable.img_profile)
                        .into(ivChatProfile)
                }
        }
    }

}

class RvItemUserAdapter(var userData: MutableList<UserData>) :
    RecyclerView.Adapter<RvItemUserViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RvItemUserViewHolder =
        RvItemUserViewHolder(
            RvitemUserBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )

    override fun onBindViewHolder(holder: RvItemUserViewHolder, position: Int) {
        val data = userData[position]
        holder.setData(data)

        if (position == 0) {
            holder.itemView.setBackgroundColor(Color.parseColor("#D0D0D0"))
        }

        holder.itemView.setOnClickListener {
            val intent = Intent(holder.itemView.context, MyProfileDetailActivity::class.java)
            intent.putExtra("userData", userData[position])
            ContextCompat.startActivity(holder.itemView.context, intent, null)
        }

        val imgRef = MyApplication.storage.reference.child("${data.email}/profile")
        Glide.with(holder.itemView.context)
            .load(imgRef)
            .error(R.drawable.img_profile)
            .into(holder.binding.ivChatProfile)
    }

    override fun getItemCount(): Int = userData.size

}