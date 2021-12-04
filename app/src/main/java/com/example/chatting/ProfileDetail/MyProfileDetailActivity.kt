package com.example.chatting.ProfileDetail

import android.content.Intent
import android.graphics.BitmapFactory
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import com.bumptech.glide.Glide
import com.example.chatting.Model.UserData
import com.example.chatting.MyApplication
import com.example.chatting.R
import com.example.chatting.UserFragment.UsersFragment
import com.example.chatting.databinding.ActivityMainBinding
import com.example.chatting.databinding.ActivityMyProfileDetailBinding
import java.lang.Exception

class MyProfileDetailActivity : AppCompatActivity() {

    private lateinit var userData: UserData

    val binding by lazy{ActivityMyProfileDetailBinding.inflate(layoutInflater)}
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        editState("normal")

        //리사이클러 뷰 항목 클릭시 넘어온 userData 정보를 화면 뷰에 구성
        userData = intent.getParcelableExtra<UserData>("userData")!!

        binding.run{

            myProfileName.text = userData.name
            myProfileStatusMsg.text = userData.status_message
            myProfileMusic.text = userData.profile_music

        }
        //취소 버튼 클릭 시
        binding.myProfileBack.setOnClickListener { finish() }
        binding.myProfileBackEdit.setOnClickListener {
            editState("normal")
        }

        //즐겨찾기 버튼 클릭 시

        //프로필 사진 클릭 시
        val galleryIntent =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
                if (it.resultCode == RESULT_OK) {
                    try {
                        val option = BitmapFactory.Options()
                        option.inSampleSize = calculateInSampleSize(
                            it.data!!.data!!,
                            resources.getDimensionPixelSize(R.dimen.profile_image_size),
                            resources.getDimensionPixelSize(R.dimen.profile_image_size)
                        )

                        var inputStream = contentResolver.openInputStream(it.data!!.data!!)
                        val bitmap = BitmapFactory.decodeStream(inputStream, null, option)
                        inputStream!!.close()
                        inputStream=null
                        bitmap?.let{
                            binding.myProfileImage.setImageBitmap(bitmap)
                        } ?: let{
                            Toast.makeText(this, "Bitmap Null", Toast.LENGTH_SHORT).show()
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
            }

        binding.myProfileImage.setOnClickListener{
            val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            intent.type = "image/*"
            galleryIntent.launch(intent)

            //스토리지에 사진 저장
        }

        //채팅 버튼 클릭 시

        //편집 버튼 클릭 시
        binding.myProfileEdit.setOnClickListener {
            editState("edit")
        }

        //저장 버튼 클릭 시
        binding.myProfileSaveEdit.setOnClickListener {
            val documentName = MyApplication.auth.currentUser?.email

            //이름 변경
            val changedName = binding.myProfileNameEdit.text.toString()
            val existingName = binding.myProfileName.text.toString()

            if (changedName.isEmpty() || changedName == existingName) {
                Toast.makeText(this, "이름을 다시 설정해주세요.", Toast.LENGTH_SHORT).show()
            }
            else {
                MyApplication.db.collection("profile_dongk00").document("$documentName")
                    .update("name", changedName)
                    .addOnSuccessListener {
                        // 이름 변경 성공 시 상태메세지도 변경 진행
                        val changedStatusMsg = binding.myProfileStatusMsgEdit.text.toString()
                        MyApplication.db.collection("profile_dongk00").document("$documentName")
                            .update("statusMsg", changedStatusMsg)
                            .addOnSuccessListener {
                                //둘 다 성공이면 상태 변경
                                editState("normal")
                            }
                            .addOnFailureListener { Toast.makeText(this, "설정 실패", Toast.LENGTH_SHORT).show() }

                    }
                    .addOnFailureListener { Toast.makeText(this, "설정 실패", Toast.LENGTH_SHORT).show() }
            }
        }
    }

    private fun editState(state: String) {
        when(state){
            "edit" -> {
                binding.run {
                    myProfileChat.visibility = View.GONE
                    myProfileEdit.visibility = View.GONE
                    myProfileBack.visibility = View.GONE
                    myProfileFavorites.visibility = View.GONE
                    myProfileName.visibility = View.INVISIBLE
                    myProfileStatusMsg.visibility = View.INVISIBLE
                    myProfileMusic.visibility = View.GONE

                    myProfileNameEdit.visibility = View.VISIBLE
                    myProfileStatusMsgEdit.visibility = View.VISIBLE
                    myProfileBackEdit.visibility = View.VISIBLE
                    myProfileSaveEdit.visibility = View.VISIBLE
                    myProfileMusicEdit.visibility = View.VISIBLE
                }

                val hintName = binding.myProfileName.text
                val hintStatusMsg = binding.myProfileStatusMsg.text

                binding.myProfileNameEdit.hint = hintName
                binding.myProfileStatusMsgEdit.hint = hintStatusMsg

            }

            "normal" -> {
                binding.run {
                    myProfileChat.visibility = View.VISIBLE
                    myProfileEdit.visibility = View.VISIBLE
                    myProfileBack.visibility = View.VISIBLE
                    myProfileFavorites.visibility = View.VISIBLE
                    myProfileName.visibility = View.VISIBLE
                    myProfileStatusMsg.visibility = View.VISIBLE
                    myProfileMusic.visibility = View.VISIBLE

                    myProfileNameEdit.visibility = View.GONE
                    myProfileStatusMsgEdit.visibility = View.GONE
                    myProfileBackEdit.visibility = View.GONE
                    myProfileSaveEdit.visibility = View.GONE
                    myProfileMusicEdit.visibility = View.GONE

                }
            }
        }
    }

    private fun calculateInSampleSize(fileUri: Uri, reqWidth: Int, reqHeight: Int): Int {
        val options = BitmapFactory.Options()
        options.inJustDecodeBounds = true

        try {
            var inputStream = contentResolver.openInputStream(fileUri)
            BitmapFactory.decodeStream(inputStream, null, options)
            inputStream!!.close()
            inputStream = null
        } catch (e: Exception) {
            e.printStackTrace()
        }

        val (height: Int, width: Int) = options.run{ outHeight to outWidth }
        var insampleSize = 1

        if(height > reqHeight || width > reqWidth){
            val halfHeight = height / 2
            val halfwidth = width / 2
            while(halfHeight / insampleSize >= reqHeight || halfwidth / insampleSize >= reqWidth){
                insampleSize *= 2
            }
        }

        return insampleSize
    }
}