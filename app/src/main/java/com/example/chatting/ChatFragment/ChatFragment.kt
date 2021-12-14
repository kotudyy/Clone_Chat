package com.example.chatting.ChatFragment

import android.os.Bundle
import android.view.*
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.chatting.MyApplication
import com.example.chatting.R
import com.example.chatting.databinding.FragmentChatBinding

class ChatFragment : Fragment() {

    lateinit var binding: FragmentChatBinding
    val chatListDatas = mutableListOf<ChatData>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentChatBinding.inflate(inflater, container, false)

        //프래그먼트에서 toolbar 사용하기 위함
        binding.chattingToolbar.apply {
            inflateMenu(R.menu.menu_chatting)
            setOnMenuItemClickListener {
                when(it.itemId){
                    R.id.chatting_menu_edit ->{
                        Toast.makeText(this.context,"Edit", Toast.LENGTH_SHORT).show()
                        true
                    }
                    R.id.chatting_menu_sort ->{
                        Toast.makeText(this.context,"Sort", Toast.LENGTH_SHORT).show()
                        true
                    }
                    R.id.chatting_menu_allsettings ->{
                        Toast.makeText(this.context,"All Settings", Toast.LENGTH_SHORT).show()
                        true
                    }
                    R.id.chatting_menu_music ->{
                        Toast.makeText(this.context,"Music", Toast.LENGTH_SHORT).show()
                        true
                    }
                    R.id.chatting_menu_add ->{
                        Toast.makeText(this.context,"Add", Toast.LENGTH_SHORT).show()
                        true
                    }

                }
                false
            }
        }
        initDogRecyclerView()
        initializelist()

        //채팅방 리스트 가져오기

        return binding.root
    }

    fun initDogRecyclerView() {
        val adapter = RvItemChatAdapter(chatListDatas)
        adapter.chatData = chatListDatas
        binding.rvChat.adapter = adapter
        binding.rvChat.layoutManager = LinearLayoutManager(context) //레이아웃 매니저 연결
    }

    fun initializelist() { //임의로 데이터 넣어서 만들어봄
        with(chatListDatas) {
            add(ChatData("one", "hello", "어제"))
            add(ChatData("two", "world", "00:32"))
            add(ChatData("three", "hi", "23:27"))
            add(ChatData("four", "kakaoTalk", "03:24"))
            add(ChatData("five", "Test", "01:59"))
        }
    }
}