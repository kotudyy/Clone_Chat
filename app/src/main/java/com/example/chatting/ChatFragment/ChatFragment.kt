package com.example.chatting.ChatFragment

import android.annotation.SuppressLint
import android.os.Bundle
import android.preference.PreferenceManager
import android.util.Log
import android.view.*
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.chatting.Model.Messages
import com.example.chatting.Model.UserRoom
import com.example.chatting.Model.chatRoomUser
import com.example.chatting.MyApplication
import com.example.chatting.R
import com.example.chatting.databinding.FragmentChatBinding
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
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

//        // 스와이프 시 삭제
//        val itemTouchCallback = object : ItemTouchHelper.SimpleCallback (
//            ItemTouchHelper.UP or ItemTouchHelper.DOWN, ItemTouchHelper.LEFT
//        ){
//            override fun onMove(
//                recyclerView: RecyclerView,
//                viewHolder: RecyclerView.ViewHolder,
//                target: RecyclerView.ViewHolder
//            ): Boolean {
//                TODO("Not yet implemented")
//            }
////            override fun onMove(
////                recyclerView: RecyclerView,
////                viewHolder: RecyclerView.ViewHolder,
////                target: RecyclerView.ViewHolder
////            ): Boolean {
////                val fromPos: Int = viewHolder.adapterPosition
////                val toPos: Int = target.adapterPosition
////                rvAdapter.swapData(fromPos, toPos)
////                return true
////            }
//
//            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
//                adapter.removeData(viewHolder.layoutPosition)
////                chatListDatas.removeAt(viewHolder.layoutPosition)
////                adapter.notifyDataSetChanged()
//                refreshFragment(this@ChatFragment, parentFragmentManager)
//            }
//        }
//
//        ItemTouchHelper(itemTouchCallback).attachToRecyclerView(binding.rvChat)


        return binding.root
    }

    override fun onStart() {
        super.onStart()

        chatListDatas = mutableListOf<UserRoom>()
        searchChatRoom()
        initDogRecyclerView()
    }

    fun initDogRecyclerView() {
        adapter.chatData = chatListDatas
        binding.rvChat.adapter = adapter
        binding.rvChat.layoutManager = LinearLayoutManager(context) //레이아웃 매니저 연결
    }

    //채팅방 리스트 가져오기 searchChatRoom() -> getChatRoomList()
    fun searchChatRoom() {
        val userDataListener = object : ChildEventListener {
            override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                user1 = null
                user2 = null
                val chatRoomUserData = snapshot.getValue<chatRoomUser>()!!
                Log.d("chatRoomuserdata", "${chatRoomUserData}")
                user1 = chatRoomUserData.user1
                user2 = chatRoomUserData.user2
                if(chatRoomUserData.user2 == userEmail){
                    user1 = chatRoomUserData.user2
                    user2 = chatRoomUserData.user1
                }
                    if (user1 == userEmail) {
                        chatRoomId = snapshot.key as String
                        getChatRoomList(chatRoomId!!, user2!!)
                        adapter.notifyDataSetChanged()
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
    }

    fun getChatRoomList(chatroomid: String, chatroomuser: String) {
        val chatRoomListener = object : ChildEventListener{
            override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                if(snapshot.key == chatroomid) {
                    var lastmessage = ""
                    var sender = chatroomuser       //이름 및 이미지는 어댑터에서 sender로 처리하기 위해 상대방 이메일 지정해줌
                    var timestamp: Long = 0
                    val userRoomData = snapshot.getValue<UserRoom>()!!
                    Log.d("snapshot", "${userRoomData}")
                    lastmessage = userRoomData.lastmessage
                    timestamp = userRoomData.timestamp

                    chatListDatas.add(UserRoom(chatroomid, lastmessage, timestamp, sender))
                    Log.d("snapshot", "chatListDatas${chatListDatas}")
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
