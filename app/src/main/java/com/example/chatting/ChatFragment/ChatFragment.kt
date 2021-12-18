package com.example.chatting.ChatFragment

import android.R.attr
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.chatting.Model.UserData
import com.example.chatting.Model.UserRoom
import com.example.chatting.MyApplication
import com.example.chatting.R
import com.example.chatting.databinding.FragmentChatBinding
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import android.R.attr.data
import java.util.*
import kotlin.Comparator


class ChatFragment : Fragment() {

    lateinit var binding: FragmentChatBinding
    val chatListDatas = mutableListOf<UserRoom>()
    var userEmail = MyApplication.auth.currentUser?.email!!
    var adapter = RvItemChatAdapter(chatListDatas)

    var user1: String? = null
    var user2: String? = null
    var chatRoomId: String? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentChatBinding.inflate(inflater, container, false)

        //프래그먼트에서 toolbar 사용하기 위함
        binding.chattingToolbar.apply {
            inflateMenu(R.menu.menu_chatting)
            setOnMenuItemClickListener {
                when (it.itemId) {
                    R.id.chatting_menu_edit -> {
                        Toast.makeText(this.context, "Edit", Toast.LENGTH_SHORT).show()
                        true
                    }
                    R.id.chatting_menu_sort -> {
                        Toast.makeText(this.context, "Sort", Toast.LENGTH_SHORT).show()
                        true
                    }
                    R.id.chatting_menu_allsettings -> {
                        Toast.makeText(this.context, "All Settings", Toast.LENGTH_SHORT).show()
                        true
                    }
                    R.id.chatting_menu_music -> {
                        Toast.makeText(this.context, "Music", Toast.LENGTH_SHORT).show()
                        true
                    }
                    R.id.chatting_menu_add -> {
                        Toast.makeText(this.context, "Add", Toast.LENGTH_SHORT).show()
                        true
                    }

                }
                false
            }
        }

        searchChatRoom()
        initDogRecyclerView()

        return binding.root
    }

    fun initDogRecyclerView() {
        adapter.chatData = chatListDatas
        binding.rvChat.adapter = adapter
        binding.rvChat.layoutManager = LinearLayoutManager(context) //레이아웃 매니저 연결
    }

    //채팅방 리스트 가져오기 searchChatRoom() -> getChatRoomList()
    fun searchChatRoom() {
        MyApplication.realtime.child("chatRoomUser")
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    for (chatRoomInfo in snapshot.children) {
                        user1 = null
                        user2 = null
                        for (chatRoomUserData in chatRoomInfo.children) {
                            if (chatRoomUserData.value == userEmail) {
                                user1 = chatRoomUserData.value as String
                            } else if (chatRoomUserData.value != userEmail) {
                                user2 = chatRoomUserData.value as String
                            }

                            if (user1 != null && user2 != null) {
                                if (user1 == userEmail) {
                                    chatRoomId = chatRoomInfo.key as String
                                    getChatRoomList(chatRoomId!!, user2!!)
                                }
                                break
                            }
                        }
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    Log.d("error", "failed")
                }
            })
    }

    fun getChatRoomList(chatroomid: String, chatroomuser: String) {
        MyApplication.realtime.child("UserRoom").child(chatroomid)
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    var lastmessage = ""
                    var sender = chatroomuser       //이름 및 이미지는 어댑터에서 sender로 처리하기 위해 상대방 이메일 지정해줌
                    var timestamp: Long = 0
                    for (data in snapshot.children) {
                        when (data.key) {
                            "lastmessage" -> lastmessage = data.value as String
                            "timestamp" -> timestamp = data.value as Long
                        }
                    }
                    chatListDatas.add(UserRoom(chatroomid, lastmessage, timestamp, sender))
                    chatListDatas.sortByDescending { it.timestamp }     //시간 순으로 정렬
                    adapter.notifyDataSetChanged()
                }

                override fun onCancelled(error: DatabaseError) {
                    Log.d("error", "failed")
                }
            })
    }
}
