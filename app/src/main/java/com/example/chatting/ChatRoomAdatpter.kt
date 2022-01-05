package com.example.chatting

import android.content.Context
import android.os.Message
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.chatting.Model.Messages
import com.example.chatting.databinding.ItemDatetxtBinding
import com.example.chatting.databinding.ItemReceivemsgBinding
import com.example.chatting.databinding.ItemSendmsgBinding
import java.lang.RuntimeException
import java.text.SimpleDateFormat


class ChatRoomAdatpter(var messages : MutableList<Messages>, val chatRoomID: String) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    lateinit var context: Context
    lateinit var time: String
    lateinit var lastDate: String
    lateinit var currentDate: String
    var type: Int = 0

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            0 -> {
                val binding =
                    ItemSendmsgBinding.inflate(LayoutInflater.from(parent.context), parent, false)
                sendViewHolder(binding)
            }
            1 -> {
                val binding = ItemReceivemsgBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
                receiveViewHolder(binding)
            }
            2 -> {
                val binding =
                    ItemDatetxtBinding.inflate(LayoutInflater.from(parent.context), parent, false)
                dateViewHolder(binding)
            }
            else -> throw RuntimeException("Error")
        }
    }

    inner class sendViewHolder(private val binding: ItemSendmsgBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(data: Messages) {
            binding.apply {
                tvChatroomSendmsg.text = data.message
                tvChatroomSendtime.text = time

                var myUser = ""
                var count = 1
                val msgTimeStamp = data.timestamp
                var userLastVisitedInTimeStamp: Long = 0
                var userLastVisitedOutTimeStamp: Long = 0

                MyApplication.realtime.child("chatRoomUser").child(chatRoomID)
                    .get()
                    .addOnSuccessListener { users ->
                        for (user in users.children) {
                            if (user.value as String == MyApplication.auth.currentUser?.email) {
                                myUser = user.key as String
                            }
                        }

                        MyApplication.realtime.child("UserLastVisitedIn").child(chatRoomID)
                            .get()
                            .addOnSuccessListener { InTimestamps ->
                                for (timestamp in InTimestamps.children) {
                                    if (timestamp.key != myUser) {
                                        userLastVisitedInTimeStamp = timestamp.value as Long
                                        Log.d("test-userLastVisitedIn", userLastVisitedInTimeStamp.toString())
                                    }
                                }

                                MyApplication.realtime.child("UserLastVisitedOut").child(chatRoomID)
                                    .get()
                                    .addOnSuccessListener { OutTimestamps ->
                                        for (timestamp in OutTimestamps.children) {
                                            if (timestamp.key != myUser) {
                                                userLastVisitedOutTimeStamp = timestamp.value as Long
                                                Log.d("test-userLastVisitedOut", userLastVisitedOutTimeStamp.toString())
                                            }
                                        }

                                        if (userLastVisitedInTimeStamp < userLastVisitedOutTimeStamp) {
                                            if (msgTimeStamp < userLastVisitedOutTimeStamp) {
                                                count -= 1
                                            }
                                        } else {
                                            count -= 1
                                        }

                                        if (count == 0) {
                                            tvSendmsgRead.visibility = View.GONE
                                        } else {
                                            tvSendmsgRead.text = count.toString()
                                        }
                                    }
                            }
                    }
            }
        }
    }

    inner class receiveViewHolder(private val binding: ItemReceivemsgBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(data: Messages) {
            binding.apply {
                MyApplication.db.collection("profile")
                    .whereEqualTo("email", data.sender)
                    .get()
                    .addOnSuccessListener { document ->
                        for (field in document) tvChatroomUsername.text = field["name"] as String
                    }
                tvChatroomReceivemsg.text = data.message
                tvChatroomReceivetime.text = time

                val imgRef = MyApplication.storage.reference.child("${data.sender}/profile")
                Glide.with(binding.root.context)
                    .load(imgRef)
                    .error(R.drawable.img_profile)
                    .into(ivChatProfile)

                var myUser = ""
                var count = 1
                val msgTimeStamp = data.timestamp
                var userLastVisitedInTimeStamp: Long = 0
                var userLastVisitedOutTimeStamp: Long = 0

                MyApplication.realtime.child("chatRoomUser").child(chatRoomID)
                    .get()
                    .addOnSuccessListener { users ->
                        for (user in users.children) {
                            if (user.value as String == MyApplication.auth.currentUser?.email) {
                                myUser = user.key as String
                            }
                        }

                        MyApplication.realtime.child("UserLastVisitedIn").child(chatRoomID)
                            .get()
                            .addOnSuccessListener { InTimestamps ->
                                for (timestamp in InTimestamps.children) {
                                    if (timestamp.key != myUser) {
                                        userLastVisitedInTimeStamp = timestamp.value as Long
                                        Log.d(
                                            "test-userLastVisitedIn",
                                            userLastVisitedInTimeStamp.toString()
                                        )
                                    }
                                }

                                MyApplication.realtime.child("UserLastVisitedOut").child(chatRoomID)
                                    .get()
                                    .addOnSuccessListener { OutTimestamps ->
                                        for (timestamp in OutTimestamps.children) {
                                            if (timestamp.key != myUser) {
                                                userLastVisitedOutTimeStamp =
                                                    timestamp.value as Long
                                                Log.d(
                                                    "test-userLastVisitedOut",
                                                    userLastVisitedOutTimeStamp.toString()
                                                )
                                            }
                                        }

                                        if (userLastVisitedInTimeStamp < userLastVisitedOutTimeStamp) {
                                            if (msgTimeStamp < userLastVisitedOutTimeStamp) {
                                                count -= 1
                                            }
                                        } else {
                                            count -= 1
                                        }

                                        if (count == 0) {
                                            tvReceivemsgRead.visibility = View.GONE
                                        } else {
                                            tvReceivemsgRead.text = count.toString()
                                        }
                                    }
                            }
                    }
            }
        }
    }
        inner class dateViewHolder(private val binding: ItemDatetxtBinding) :
            RecyclerView.ViewHolder(binding.root) {
            fun bind(data: Messages) {
                binding.apply {
                    tvChatroomDatetxt.text =
                        SimpleDateFormat("yyyy년 MM월 dd일").format(data.timestamp)
                }
            }
        }

        override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
            val data = messages[position]
            time = SimpleDateFormat("hh:mm").format(data.timestamp)

            when (type) {
                0 -> (holder as sendViewHolder).bind(data)
                1 -> (holder as receiveViewHolder).bind(data)
                2 -> (holder as dateViewHolder).bind(data)
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
            if (messages != null) return messages.size
            else return 0
        }

        fun removeItem(removeMsg: Messages) {
            messages.remove(removeMsg)
            notifyDataSetChanged()
        }
    }