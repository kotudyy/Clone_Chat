package com.example.chatting.PlusFragment

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.chatting.Login.LoginActivity
import com.example.chatting.MyApplication
import com.example.chatting.R
import com.example.chatting.databinding.FragmentPlusBinding

class PlusFragment : Fragment() {

    lateinit var binding: FragmentPlusBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentPlusBinding.inflate(inflater, container, false)

        //프래그먼트에서 toolbar 사용하기 위함
        binding.plusToolbar.apply {
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
                        startActivity(Intent(activity,LoginActivity::class.java))
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
        val dummyData = mutableListOf<RvItemPlusAdapter.PlusData>()
        for (i in 1..20) {
            dummyData.add(RvItemPlusAdapter.PlusData(R.drawable.auth_icon, "인증"))
        }

        val adapter = RvItemPlusAdapter(dummyData)
        binding.rvPlus.adapter = adapter
        binding.rvPlus.layoutManager = LinearLayoutManager(this.context)
        return binding.root

    }

    override fun onResume() {
        super.onResume()
        if(MyApplication.checkAuth()) {
        }else {
            activity?.finish()
        }
    }
}