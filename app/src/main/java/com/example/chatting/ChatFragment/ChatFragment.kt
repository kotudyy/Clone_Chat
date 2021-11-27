package com.example.chatting.ChatFragment

import android.os.Bundle
import android.view.*
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.chatting.R
import com.example.chatting.databinding.FragmentChatBinding

class ChatFragment : Fragment() {

    lateinit var binding: FragmentChatBinding

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

        //RecyclerView DummyData
        val dummyData = mutableListOf<ChatData>()
        for (i in 1..20){
            dummyData.add(ChatData("Text", "$i"))
        }

        val adapter = RvItemChatAdapter(dummyData)
        binding.rvChat.adapter = adapter
        binding.rvChat.layoutManager = LinearLayoutManager(this.context)
        return binding.root
    }


}