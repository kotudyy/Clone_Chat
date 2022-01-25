package com.kotudyy.chatting.chatRoom

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.kotudyy.chatting.model.Messages
import com.kotudyy.chatting.storage.MyApplication
import com.kotudyy.chatting.R
import com.kotudyy.chatting.databinding.ItemDatetxtBinding
import com.kotudyy.chatting.databinding.ItemReceivemsgBinding
import com.kotudyy.chatting.databinding.ItemSendmsgBinding
import com.kotudyy.chatting.model.UserData
import com.kotudyy.chatting.profileDetail.MyProfileDetailActivity
import java.lang.RuntimeException
import java.text.SimpleDateFormat


class ChatRoomAdatpter(var messages : MutableList<Messages>) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    lateinit var context: Context
    lateinit var time: String
    lateinit var userData : UserData
    var type: Int = 0

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            0 -> {
                val binding =
                    ItemSendmsgBinding.inflate(LayoutInflater.from(parent.context), parent, false)
                SendViewHolder(binding)
            }
            1 -> {
                val binding = ItemReceivemsgBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
                ReceiveViewHolder(binding)
            }
            2 -> {
                val binding =
                    ItemDatetxtBinding.inflate(LayoutInflater.from(parent.context), parent, false)
                    DataViewHolder(binding)
            }
            else -> throw RuntimeException("Error")
        }
    }

    inner class SendViewHolder(val binding: ItemSendmsgBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(data: Messages) {
            binding.apply {
                tvChatroomSendmsg.text = data.message
                tvChatroomSendtime.text = time

                if(data.read){
                    tvSendmsgRead.visibility = View.GONE
                } else {
                    tvSendmsgRead.visibility = View.VISIBLE
                }
            }
        }
    }


    inner class ReceiveViewHolder(private val binding: ItemReceivemsgBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(data: Messages) {
            binding.apply {
                MyApplication.db.collection("profile")
                    .whereEqualTo("email", data.sender)
                    .get()
                    .addOnSuccessListener { document ->
                        for (field in document) {
                            tvChatroomUsername.text = field["name"] as String
                            userData = UserData(
                                field["email"] as String,
                                field["name"] as String,
                                field["statusMsg"] as String,
                                field["profileMusic"] as String)
                        }
                        ivChatProfile.setOnClickListener {
                            val intent = Intent(binding.root.context, MyProfileDetailActivity::class.java)
                            intent.putExtra("userData", userData)
                            ContextCompat.startActivity(binding.root.context, intent, null)
                        }
                    }
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
        inner class DataViewHolder(private val binding: ItemDatetxtBinding) :
            RecyclerView.ViewHolder(binding.root) {
            @SuppressLint("SimpleDateFormat")
            fun bind(data: Messages) {
                binding.apply {
                    tvChatroomDatetxt.text =
                        SimpleDateFormat("yyyy년 MM월 dd일").format(data.timestamp)
                }
            }
        }

        @SuppressLint("SimpleDateFormat")
        override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
            val data = messages[position]
            time = SimpleDateFormat("hh:mm").format(data.timestamp)

            when (type) {
                0 -> (holder as SendViewHolder).bind(data)
                1 -> (holder as ReceiveViewHolder).bind(data)
                2 -> (holder as DataViewHolder).bind(data)
            }
        }

        override fun getItemViewType(position: Int): Int {
            if (messages[position].sender == MyApplication.auth.currentUser?.email)
                type = 0
            else if (messages[position].sender == "")
                type = 2
            else
                type = 1

            return type
        }

        override fun getItemCount(): Int {
            return messages.size
        }

        @SuppressLint("NotifyDataSetChanged")
        fun removeItem(removeMsg: Messages) {
            messages.remove(removeMsg)
            notifyDataSetChanged()
        }
    }