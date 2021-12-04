package com.example.chatting.UserFragment

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.chatting.MyApplication
import com.example.chatting.R
import com.example.chatting.databinding.FragmentUsersBinding
import com.example.chatting.model.infoData
import com.google.firebase.firestore.auth.User

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
        return binding.root
    }

    override fun onResume() {
        super.onResume()
    }

    override fun onStart() {
        makeRecyclerView()
        super.onStart()
    }

    fun makeRecyclerView() {
        //firebase에서 정보 받아오기
        MyApplication.db.collection("profile").get().addOnSuccessListener { result ->
            val userData = mutableListOf<infoData>()
            val myData = mutableListOf<infoData>()
            var totalData = mutableListOf<infoData>()
            for (document in result) {
                val item = document.toObject(infoData::class.java)
                item.docId = document.id
                if(item.email == MyApplication.email)
                {
                    myData.add(item)
                }else {
                    userData.add(item)
                }
                totalData = (myData + userData) as MutableList<infoData>
                Log.d("grusie", "myData : ${myData}, userData : ${userData}, totalData : ${totalData}")
            }
            val adapter = RvItemUserAdapter(totalData)
            binding.rvUser.layoutManager = LinearLayoutManager(this.context)
            binding.rvUser.adapter = adapter

        }
    }

}