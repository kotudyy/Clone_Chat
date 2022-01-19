package com.example.chatting.userFragment

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.chatting.model.UserData
import com.example.chatting.storage.MyApplication
import com.example.chatting.R
import com.example.chatting.databinding.FragmentUsersBinding

class UsersFragment : Fragment() {

    lateinit var binding: FragmentUsersBinding
    lateinit var adapter: RvItemUserAdapter
    val userData = mutableListOf<UserData>()

    @SuppressLint("NotifyDataSetChanged")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentUsersBinding.inflate(inflater, container, false)

        //프래그먼트에서 toolbar 사용하기 위함
        binding.userToolbar.apply {
            inflateMenu(R.menu.menu_user)
            setOnMenuItemClickListener {
                when (it.itemId) {
                    R.id.user_menu_edit -> {
                        Toast.makeText(this.context, "Edit", Toast.LENGTH_SHORT).show()
                    }
                    R.id.user_menu_sort -> {
                        Toast.makeText(this.context, "Sort", Toast.LENGTH_SHORT).show()
                    }
                    R.id.user_menu_allsettings -> {
                        Toast.makeText(this.context, "All Settings", Toast.LENGTH_SHORT).show()
                    }
                    R.id.user_menu_music -> {
                        Toast.makeText(this.context, "Music", Toast.LENGTH_SHORT).show()
                    }
                    R.id.user_menu_add -> {
                        Toast.makeText(this.context, "Add", Toast.LENGTH_SHORT).show()
                    }

                }
                false
            }
        }

        //RecyclerView Adapter 연결
        adapter = RvItemUserAdapter(userData)
        binding.rvUser.adapter = adapter
        binding.rvUser.layoutManager = LinearLayoutManager(this.context)

        //RecyclerView에 User 정보 삽입

        val documentName = MyApplication.auth.currentUser?.email
        userData.clear()

        MyApplication.db.collection("profile") // 내 정보 첫 번째 항목으로 출력
            .whereEqualTo("email", documentName)
            .get()
            .addOnSuccessListener { myDocument ->
                for (myField in myDocument) {
                    val myProfileData = UserData(
                        myField["email"] as String,
                        myField["name"] as String,
                        myField["statusMsg"] as String,
                        myField["profileMusic"] as String)

                    userData.add(myProfileData)
                    adapter.notifyDataSetChanged()

                    // 성공 시 나머지 항목들 출력
                    MyApplication.db.collection("profile")
                        .whereNotEqualTo("email", documentName)
                        .get()
                        .addOnSuccessListener { document ->
                            for (field in document) {
                                val myProfileData = UserData(
                                    field["email"] as String,
                                    field["name"] as String,
                                    field["statusMsg"] as String,
                                    field["profileMusic"] as String)

                                userData.add(myProfileData)
                                adapter.notifyDataSetChanged()
                            }
                        }
                        .addOnFailureListener {
                        }
                }
            }
            .addOnFailureListener {
            }
        return binding.root
    }

    @SuppressLint("NotifyDataSetChanged")
    override fun onResume() {
        super.onResume()

        adapter.notifyDataSetChanged()
    }
}