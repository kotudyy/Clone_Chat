package com.example.chatting.UserFragment

import android.content.Intent
import android.graphics.Color
import android.os.Parcelable
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.chatting.ChatRoomActivity
import com.example.chatting.ProfileDetail.MyProfileDetailActivity
import com.example.chatting.R
import com.example.chatting.databinding.RvitemUserBinding
import kotlinx.parcelize.Parcelize

class RvItemUserViewHolder(val binding: RvitemUserBinding): RecyclerView.ViewHolder(binding.root){

    fun setData(data: UserData){
        binding.run {
            if(data.image == ""){
                this.ivChatProfile.setImageResource(R.drawable.img_profile)
            }else{
                Glide.with(itemView)
                    .load(data.image)
                    .placeholder(R.drawable.img_profile)
                    .into(this.ivChatProfile)
            }

            username.text = data.username
            statusMessage.text = data.status_message
            profileMusic.text = data.profile_music
        }
    }
}

class RvItemUserAdapter(var userData: MutableList<UserData>): RecyclerView.Adapter<RvItemUserViewHolder>(){
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RvItemUserViewHolder =
        RvItemUserViewHolder(RvitemUserBinding.inflate(LayoutInflater.from(parent.context), parent, false))

    override fun onBindViewHolder(holder: RvItemUserViewHolder, position: Int) {
        val data = userData[position]
        holder.setData(data)

        if (position == 0) {
            holder.itemView.setBackgroundColor(Color.parseColor("#D0D0D0"))
        }

        holder.itemView.setOnClickListener {
            val intent = Intent(holder.itemView?.context, MyProfileDetailActivity::class.java)
            intent.putExtra("userData", userData[position])
            ContextCompat.startActivity(holder.itemView.context, intent, null)
        }
    }

    override fun getItemCount(): Int = userData.size

}

@Parcelize
data class UserData(
    var image: String,
    var username: String,
    var status_message: String,
    var profile_music: String ) : Parcelable