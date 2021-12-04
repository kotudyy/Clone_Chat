package com.example.chatting.UserFragment

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.chatting.databinding.RvitemUserBinding

class RvItemUserViewHolder(val binding: RvitemUserBinding) : RecyclerView.ViewHolder(binding.root) {

    fun setData(data: UserData) {
        binding.username.text = "${data.name}"
        binding.statusMessage.text = "${data.statusmsg}"
        binding.chip4.text = "${data.music}"
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
    }

    override fun getItemCount(): Int = userData.size

}

data class UserData(
    val email: String,
    val music: String,
    val name: String,
    val statusmsg: String
)