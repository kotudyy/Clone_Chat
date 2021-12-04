package com.example.chatting.UserFragment

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.chatting.ChatFragment.RvItemChatAdapter
import com.example.chatting.MyApplication
import com.example.chatting.R
import com.example.chatting.databinding.FragmentUsersBinding
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.firestore.FirebaseFirestore

class UsersFragment : Fragment() {

    lateinit var binding: FragmentUsersBinding
    val userData = mutableListOf<UserData>()

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

        val adapter = RvItemUserAdapter(userData)
        adapter.userData = userData
        binding.rvUser.adapter = adapter
        binding.rvUser.layoutManager = LinearLayoutManager(this.context)

        val myEmail = MyApplication.auth.currentUser?.email.toString()

        // 본인/친구 프로필 정보 가져오기
        FirebaseFirestore.getInstance().collection("profile")
            .get()      // 문서 가져오기
            .addOnSuccessListener { result ->
                // 성공할 경우
                userData.clear()
                for (document in result) {  // 가져온 문서들은 result에 들어감
                    if (document["email"] as String == myEmail) {   // 본인인 경우
                        binding.Myname.text = document["name"] as String
                        binding.statusMessage.text = document["status_message"] as String
                        binding.chip4.text = document["music"] as String
                    }
                    else {
                        val item = UserData(
                            document["email"] as String, document["music"] as String,
                            document["name"] as String, document["status_message"] as String
                        )

                        userData.add(item)
                    }
                }
                adapter.notifyDataSetChanged()  // 리사이클러 뷰 갱신
            }
            .addOnFailureListener { exception ->
                // 실패할 경우
                Log.w("MainActivity", "Error getting documents: $exception")
            }

        return binding.root
    }
}