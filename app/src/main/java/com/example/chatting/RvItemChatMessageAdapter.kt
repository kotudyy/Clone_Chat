package com.example.chatting

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.chatting.Model.MessageData
import com.example.chatting.databinding.ItemReceivemsgBinding
import com.example.chatting.databinding.ItemSendmsgBinding
import java.text.SimpleDateFormat
import java.util.*

class RvItemChatMessageAdapter(private val context: MutableList<MessageData>)
    : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    var datas = mutableListOf<MessageData>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) : RecyclerView.ViewHolder {
        val view : View?
        return when(viewType) {
            0 -> {
                ReceiveMessageViewHolder(
                    ItemReceivemsgBinding.inflate(
                        LayoutInflater.from(parent.context),
                        parent,
                        false
                    )
                )
            }
            1 -> {
                SendMessageViewHolder(
                    ItemSendmsgBinding.inflate(
                        LayoutInflater.from(parent.context),
                        parent,
                        false
                    )
                )
            }
            else -> {
                SendMessageViewHolder(
                    ItemSendmsgBinding.inflate(
                        LayoutInflater.from(parent.context),
                        parent,
                        false
                    )
                )
            }
        }
    }
    override fun getItemCount(): Int = datas.size

    override fun getItemViewType(position: Int): Int {
        if (datas[position].sender == MyApplication.auth.currentUser?.email) return 1
        else return 0
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when(getItemViewType(position)) {
            0 -> {
                (holder as ReceiveMessageViewHolder).setData(datas[position])
                holder.setIsRecyclable(false)
            }
            1 -> {
                (holder as SendMessageViewHolder).setData(datas[position])
                holder.setIsRecyclable(false)
            }
            else -> {
                (holder as SendMessageViewHolder).setData(datas[position])
                holder.setIsRecyclable(false)
            }
        }
    }

    inner class ReceiveMessageViewHolder(val binding: ItemReceivemsgBinding) : RecyclerView.ViewHolder(binding.root) {
        fun setData(data: MessageData) {
            val sdf = SimpleDateFormat("MM.dd hh:mm")
//            val stamp = System.currentTimeMillis()
//            val date = Date(stamp.day)
            binding.apply{
                MyApplication.db.collection("profile")
                    .whereEqualTo("email", data.sender)
                    .get()
                    .addOnSuccessListener {
                            document -> for (field in document) tvChatroomUsername.text = field["name"] as String
                    }
                tvChatroomReceivemsg.text = data.message
                tvChatroomReceivetime.text = sdf.format(data.timestamp)

                val imgRef = MyApplication.storage.reference.child("${data.sender}/profile")
                Glide.with(binding.root.context)
                    .load(imgRef)
                    .error(R.drawable.img_profile)
                    .into(ivChatProfile)
            }
        }
    }

    inner class SendMessageViewHolder(val binding: ItemSendmsgBinding) : RecyclerView.ViewHolder(binding.root) {
        fun setData(data: MessageData) {
            binding.tvChatroomSendmsg.text = "${data.message}"
            binding.tvChatroomSendtime.text = "${data.timestamp}"
        }
    }
}
