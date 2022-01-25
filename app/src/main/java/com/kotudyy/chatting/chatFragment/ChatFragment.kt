package com.kotudyy.chatting.chatFragment

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.kotudyy.chatting.model.UserRoom
import com.kotudyy.chatting.model.ChatRoomUser
import com.kotudyy.chatting.storage.MyApplication
import com.kotudyy.chatting.R
import com.kotudyy.chatting.databinding.FragmentChatBinding
import com.kotudyy.chatting.login.LoginActivity
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ktx.getValue


class ChatFragment : Fragment() {

    lateinit var binding: FragmentChatBinding

    var chatListDatas = mutableListOf<UserRoom>()
    var userEmail = MyApplication.auth.currentUser?.email!!
    var adapter = RvItemChatAdapter(chatListDatas)

    var user1: String? = null
    var user2: String? = null
    var chatRoomId: String? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentChatBinding.inflate(inflater, container, false)

        //프래그먼트에서 toolbar 사용하기 위함
        binding.chattingToolbar.apply {
            inflateMenu(R.menu.menu_chatting)
            setOnMenuItemClickListener {
                when (it.itemId) {
                    R.id.chat_menu_login_screen -> {
                        startActivity(Intent(activity, LoginActivity::class.java))
                    }
                }
                false
            }
        }

        return binding.root
    }

    override fun onStart() {
        super.onStart()

        chatListDatas = mutableListOf()
        searchChatRoom()
        initDogRecyclerView()
    }

    fun initDogRecyclerView() {
        adapter.chatData = chatListDatas
        binding.rvChat.adapter = adapter
        binding.rvChat.layoutManager = LinearLayoutManager(context) //레이아웃 매니저 연결
    }

    //채팅방 리스트 가져오기 searchChatRoom() -> getChatRoomList()
    @SuppressLint("NotifyDataSetChanged")
    private fun searchChatRoom() {
        val userDataListener = object : ChildEventListener {
            override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                user1 = null
                user2 = null
                val chatRoomUserData = snapshot.getValue<ChatRoomUser>()!!
                user1 = chatRoomUserData.user1
                user2 = chatRoomUserData.user2
                if(chatRoomUserData.user2 == userEmail){
                    user1 = chatRoomUserData.user2
                    user2 = chatRoomUserData.user1
                }
                    if (user1 == userEmail) {
                        chatRoomId = snapshot.key as String
                        getChatRoomList(chatRoomId!!, user2!!)
                    }
            }

            override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {
                TODO("Not yet implemented")
            }

            override fun onChildRemoved(snapshot: DataSnapshot) {
                TODO("Not yet implemented")
            }

            override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {
                TODO("Not yet implemented")
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }
        }
        MyApplication.realtime.child("chatRoomUser").addChildEventListener(userDataListener)
        adapter.notifyDataSetChanged()
    }


    fun getChatRoomList(chatroomid: String, chatroomuser: String) {
        val chatRoomListener = object : ChildEventListener{
            @SuppressLint("NotifyDataSetChanged")
            override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                if(snapshot.key == chatroomid) {
                    val lastmessage: String
                    val timestamp: Long
                    val userRoomData = snapshot.getValue<UserRoom>()!!
                    lastmessage = userRoomData.lastmessage
                    timestamp = userRoomData.timestamp

                    chatListDatas.add(
                        UserRoom(
                            chatroomid,
                            lastmessage,
                            timestamp,
                            chatroomuser       //이름 및 이미지는 어댑터에서 sender로 처리하기 위해 상대방 이메일 지정해줌
                        ))
                    chatListDatas.sortByDescending { it.timestamp }     //시간 순으로 정렬
                    adapter.notifyDataSetChanged()
                }
            }

            override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {
            }

            override fun onChildRemoved(snapshot: DataSnapshot) {
                TODO("Not yet implemented")
            }

            override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {
                TODO("Not yet implemented")
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }
        }
        MyApplication.realtime.child("UserRoom").addChildEventListener(chatRoomListener)
    }
}
