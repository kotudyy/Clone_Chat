package com.example.chatting.UserFragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.chatting.ChatFragment.RvItemChatAdapter
import com.example.chatting.R
import com.example.chatting.databinding.FragmentUsersBinding

class UsersFragment : Fragment() {

    lateinit var binding: FragmentUsersBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentUsersBinding.inflate(inflater, container, false)

        //프래그먼트에서 toolbar 사용하기 위함
        binding.userToolbar.apply {
            inflateMenu(R.menu.menu_user)
            setOnMenuItemClickListener {
                when (it.itemId) {
                    R.id.user_menu_edit -> {
                        Toast.makeText(this.context, "Edit", Toast.LENGTH_SHORT).show()
                        true
                    }
                    R.id.user_menu_sort -> {
                        Toast.makeText(this.context, "Sort", Toast.LENGTH_SHORT).show()
                        true
                    }
                    R.id.user_menu_allsettings -> {
                        Toast.makeText(this.context, "All Settings", Toast.LENGTH_SHORT).show()
                        true
                    }
                    R.id.user_menu_music -> {
                        Toast.makeText(this.context, "Music", Toast.LENGTH_SHORT).show()
                        true
                    }
                    R.id.user_menu_add -> {
                        Toast.makeText(this.context, "Add", Toast.LENGTH_SHORT).show()
                        true
                    }

                }
                false
            }
        }

        //RecyclerView DummyData
        val dummyData = mutableListOf<UserData>()
        for (i in 1..20) {
            dummyData.add(UserData("Text", "$i$i"))
        }

        val adapter = RvItemUserAdapter(dummyData)
        binding.rvUser.adapter = adapter
        binding.rvUser.layoutManager = LinearLayoutManager(this.context)
        return binding.root
    }
}