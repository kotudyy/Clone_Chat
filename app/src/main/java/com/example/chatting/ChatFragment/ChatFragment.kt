package com.example.chatting.ChatFragment

import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.chatting.Model.UserData
import com.example.chatting.Model.UserRoom
import com.example.chatting.MyApplication
import com.example.chatting.R
import com.example.chatting.databinding.FragmentChatBinding
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener

class ChatFragment : Fragment() {

    lateinit var binding: FragmentChatBinding
    val chatListDatas = mutableListOf<UserRoom>()
    var userEmail = MyApplication.auth.currentUser?.email!!
    val adapter = RvItemChatAdapter(chatListDatas)

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
//        시간 순으로 정렬하기
//        chatListDatas = MutableList(chatListDatas.sortedBy { it.timestamp })
        adapter.chatData = chatListDatas
        binding.rvChat.adapter = adapter
        binding.rvChat.layoutManager = LinearLayoutManager(context) //레이아웃 매니저 연결
    }

    //채팅방 리스트 가져오기 searchChatRoom() -> getChatRoomList()
    fun searchChatRoom() {
        MyApplication.realtime.child("chatRoomUser").orderByKey("User1").equalTo(userEmail)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onCancelled(error: DatabaseError) {
                }

                override fun onDataChange(snapshot: DataSnapshot) {
                    for (data in snapshot.children) {
                        getChatRoomList(data.key.toString())
                        Log.d("채팅방있음 !! ", data.key.toString())
                    }
                }
            })
    }

    fun getChatRoomList(chatRoomId: String) {
        MyApplication.realtime.child("UserRoom").child(chatRoomId)
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    var lastmessage = ""
                    var sender = ""
                    var timestamp: Long = 0
                    for (data in snapshot.children) {
                        when (data.key) {
                            "lastmessage" -> lastmessage = data.value as String
                            "timestamp" -> timestamp = data.value as Long
                            "sender" -> sender = data.value as String
                        }

                    }
                    chatListDatas.add(UserRoom(lastmessage, timestamp, sender))
                    adapter.notifyDataSetChanged()
                }

                override fun onCancelled(error: DatabaseError) {
                }
            })
    }
}
