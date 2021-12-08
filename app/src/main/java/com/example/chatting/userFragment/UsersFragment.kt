package com.example.chatting.userFragment

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.chatting.Model.UserData
import com.example.chatting.MyApplication
import com.example.chatting.R
import com.example.chatting.databinding.FragmentUsersBinding


class UsersFragment : Fragment() {

    private lateinit var binding: FragmentUsersBinding
    private var userData = mutableListOf<UserData>()
    lateinit var adapter: RvItemUserAdapter
    private val documentName = MyApplication.auth.currentUser?.email

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

        adapter = RvItemUserAdapter(userData)
        binding.rvUser.adapter = adapter
        binding.rvUser.layoutManager = LinearLayoutManager(this.context)

        loadMyProfile()

        return binding.root
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun loadMyProfile() {
        MyApplication.db.collection("profile") // 내 정보 첫 번째 항목으로 출력
            .whereEqualTo("email", documentName)
            .get()
            .addOnSuccessListener { document ->
                for (field in document){
                    val myProfileData = UserData(
                        field["email"] as String,
                        field["name"] as String,
                        field["statusMsg"] as String,
                        field["profileMusic"] as String,
                    "//gs://clonechat-a64cf.appspot.com/${documentName}/profile",
                    "//gs://clonechat-a64cf.appspot.com/${documentName}/background")

                    userData.add(myProfileData)
                    adapter.notifyDataSetChanged()
                    //DiffUtil.itemCallback을 이용

                    // 성공 시 나머지 항목들 출력
                    loadUserProfiles()
                }
            }
            .addOnFailureListener {
                Toast.makeText(this.context, "정보를 불러오지 못했습니다.", Toast.LENGTH_SHORT).show()
            }

    }

    @SuppressLint("NotifyDataSetChanged")
    private fun loadUserProfiles() {
        MyApplication.db.collection("profile")
            .whereNotEqualTo("email", documentName)
            .get()
            .addOnSuccessListener { document ->
                for (field in document){
                    val userEmail = field["email"] as String

                    val myProfileData = UserData(
                        field["email"] as String,
                        field["name"] as String,
                        field["statusMsg"] as String,
                        field["profileMusic"] as String,
                        //gs://clonechat-a64cf.appspot.com/dddongk00@gmail.com/profile
                        "//gs://clonechat-a64cf.appspot.com/${userEmail}/profile",
                        "//gs://clonechat-a64cf.appspot.com/${userEmail}/background")

                    userData.add(myProfileData)
                    adapter.notifyDataSetChanged()
                }
            }
            .addOnFailureListener {
                Toast.makeText(this.context, "정보를 불러오지 못했습니다.", Toast.LENGTH_SHORT).show()
            }
    }

}