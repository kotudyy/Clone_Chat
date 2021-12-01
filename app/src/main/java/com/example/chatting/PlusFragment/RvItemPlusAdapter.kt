package com.example.chatting.PlusFragment

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.ViewGroupCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.chatting.Login.LoginActivity
import com.example.chatting.MainActivity
import com.example.chatting.UserFragment.RvItemUserViewHolder
import com.example.chatting.databinding.ActivityLoginBinding
import com.example.chatting.databinding.ActivityMainBinding
import com.example.chatting.databinding.RvitemPlusBinding
import com.example.chatting.databinding.RvitemUserBinding
import kotlinx.coroutines.NonCancellable

class RvItemPlusViewHolder(val binding: RvitemPlusBinding): RecyclerView.ViewHolder(binding.root){
    fun setData(data: RvItemPlusAdapter.PlusData){
        binding.textView.text = "${data.setting}"
        binding.imageView.setImageResource(data.img)
    }
}

class RvItemPlusAdapter(var plusData: MutableList<PlusData>): RecyclerView.Adapter<RvItemPlusViewHolder>(){
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RvItemPlusViewHolder =
        RvItemPlusViewHolder(RvitemPlusBinding.inflate(LayoutInflater.from(parent.context), parent, false))

    override fun onBindViewHolder(holder: RvItemPlusViewHolder, position: Int) {
        val data = plusData[position]
        holder.setData(data)
}
    override fun getItemCount(): Int = plusData.size

data class PlusData(val img: Int, val setting: String)
}