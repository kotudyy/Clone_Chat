package com.example.chatting.ChatFragment

import android.os.Bundle
import android.view.*
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.chatting.Model.UserRoom
import com.example.chatting.R
import com.example.chatting.databinding.FragmentChatBinding

class ChatFragment : Fragment() {

    lateinit var binding: FragmentChatBinding
    val userRoomDatas = mutableListOf<UserRoom>()

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

        return binding.root
    }

    fun initDogRecyclerView() {
        val adapter = RvItemChatAdapter(userRoomDatas)
        adapter.userRoom = userRoomDatas
        binding.rvChat.adapter = adapter
        binding.rvChat.layoutManager = LinearLayoutManager(context) //레이아웃 매니저 연결
    }

    fun initializelist() { //임의로 데이터 넣어서 만들어봄
        with(userRoomDatas) {
            add(UserRoom("grusie@naver.com", "hello", System.currentTimeMillis()))
        }
    }
}