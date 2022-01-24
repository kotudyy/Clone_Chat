package com.example.chatting.userFragment

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.chatting.model.UserData
import com.example.chatting.storage.MyApplication
import com.example.chatting.R
import com.example.chatting.databinding.FragmentUsersBinding
import com.example.chatting.login.LoginActivity

class UsersFragment : Fragment() {

    lateinit var binding: FragmentUsersBinding
    private lateinit var adapter: RvItemUserAdapter
    private val userData = mutableListOf<UserData>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentUsersBinding.inflate(inflater, container, false)

        binding.userToolbar.apply {
            inflateMenu(R.menu.menu_user)
            setOnMenuItemClickListener {
                when (it.itemId) {
                    R.id.user_menu_login_screen -> {
                        startActivity(Intent(activity, LoginActivity::class.java))
                    }
                }
                false
            }
        }

        adapter = RvItemUserAdapter(userData)
        binding.rvUser.adapter = adapter
        binding.rvUser.layoutManager = LinearLayoutManager(this.context)

        insertUser()

        return binding.root
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun insertUser(){
        val documentName = MyApplication.auth.currentUser?.email
        userData.clear()

        MyApplication.db.collection("profile")
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

                    MyApplication.db.collection("profile")
                        .whereNotEqualTo("email", documentName)
                        .get()
                        .addOnSuccessListener { document ->
                            for (field in document) {
                                userData.add(
                                    UserData(
                                        field["email"] as String,
                                        field["name"] as String,
                                        field["statusMsg"] as String,
                                        field["profileMusic"] as String
                                    )
                                )
                            }
                            adapter.notifyDataSetChanged()
                        }
                }
            }
    }
}