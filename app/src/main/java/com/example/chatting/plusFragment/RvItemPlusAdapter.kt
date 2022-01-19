package com.example.chatting.plusFragment

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.chatting.databinding.RvitemPlusBinding

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