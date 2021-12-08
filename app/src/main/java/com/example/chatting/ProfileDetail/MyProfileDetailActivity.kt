package com.example.chatting.ProfileDetail

import android.content.Intent
import android.graphics.BitmapFactory
import android.graphics.drawable.Drawable
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.transition.Transition
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.constraintlayout.widget.ConstraintLayout
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.CustomTarget
import com.example.chatting.Model.UserData
import com.example.chatting.MyApplication
import com.example.chatting.R
import com.example.chatting.databinding.ActivityMyProfileDetailBinding
import com.google.firebase.storage.StorageReference
import java.lang.Exception
import java.io.InputStream

class MyProfileDetailActivity : AppCompatActivity() {

    private lateinit var userData: UserData
    private lateinit var userEmail: String
    lateinit var filename: String

    val binding by lazy { ActivityMyProfileDetailBinding.inflate(layoutInflater) }
    var filepath: Uri? = null
    val documentName = MyApplication.auth.currentUser?.email

    private fun binding() {
        //리사이클러 뷰 항목 클릭시 넘어온 userData 정보를 화면 뷰에 구성
        userData = intent.getParcelableExtra<UserData>("userData")!!
        userEmail = intent.getStringExtra("userEmail")!!

        binding.run {
            myProfileName.text = userData.name
            myProfileStatusMsg.text = userData.status_message
            myProfileMusic.text = userData.profile_music

            var imgRefProfile =
                MyApplication.storage.reference.child("${userEmail}/profile.jpg")
            var imgRefBackground =
                MyApplication.storage.reference.child("${userEmail}/background.jpg")
            Glide.with(this@MyProfileDetailActivity).load(imgRefProfile)
                .error(R.drawable.img_profile).into(myProfileImage)
            Glide.with(this@MyProfileDetailActivity).load(imgRefBackground)
                .into(myProfileBackgroundImg)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        editState("normal")

        binding()

        //취소 버튼 클릭 시
        binding.myProfileBack.setOnClickListener { finish() }
        binding.myProfileBackEdit.setOnClickListener {
            editState("normal")
        }

        //즐겨찾기 버튼 클릭 시

        //갤러리 인텐트
        val galleryIntent =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
                if (it.resultCode == RESULT_OK) {
                    try {
                        val option = BitmapFactory.Options()
                        filepath = it.data!!.data
                        option.inSampleSize = calculateInSampleSize(
                            it.data!!.data!!,
                            resources.getDimensionPixelSize(R.dimen.profile_image_size),
                            resources.getDimensionPixelSize(R.dimen.profile_image_size)
                        )

                        var inputStream = contentResolver.openInputStream(it.data!!.data!!)
                        val bitmap = BitmapFactory.decodeStream(inputStream, null, option)
                        inputStream!!.close()
                        inputStream = null
                        bitmap?.let {
                            if (filename.equals("profile")) binding.myProfileImage.setImageBitmap(bitmap)
                            else if(filename.equals("background")) binding.myProfileBackgroundImg.setImageBitmap(bitmap)

                            val imgRef: StorageReference =
                                MyApplication.storage.reference.child("$documentName/$filename.jpg")

                            imgRef.run {
                                imgRef.delete().addOnSuccessListener {
                                }.addOnFailureListener {
                                }
                            }

                            imgRef.run {
                                filepath?.let { it1 -> putFile(it1) }
                            }

                            binding()

                        } ?: let {
                            Toast.makeText(this, "Bitmap Null", Toast.LENGTH_SHORT).show()
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
            }

        //프로필 사진 클릭 시
        binding.myProfileImage.setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            intent.type = "image/*"
            filename = "profile"
            galleryIntent.launch(intent)
        }

        //배경 사진 편집 클릭 시
        binding.myProfileEditCamera.setOnClickListener {
            val intent =
                Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            intent.type = "image/*"
            filename = "background"
            galleryIntent.launch(intent)
        }

        //채팅 버튼 클릭 시

        //편집 버튼 클릭 시
        binding.myProfileEdit.setOnClickListener {
            editState("edit")
        }

        //저장 버튼 클릭 시
        binding.myProfileSaveEdit.setOnClickListener {
            var flag: Boolean = false

            //이름 변경
            var existingName = binding.myProfileName.text.toString()
            val changedName = binding.myProfileNameEdit.text.toString()
            if (!changedName.equals("")) {
                flag = true
                existingName = changedName
            }

            var existingStatusMsg = binding.myProfileStatusMsg.text.toString()
            val changedStatusMsg = binding.myProfileStatusMsgEdit.text.toString()
            if (!changedStatusMsg.equals("")) {
                flag = true
                existingStatusMsg = changedStatusMsg
            }

            var existingProfileMusic = binding.myProfileMusic.text.toString()
            val changedProfileMusic = binding.myProfileMusicEdit.text.toString()
            if (!changedProfileMusic.equals("")) {
                flag = true
                existingProfileMusic = changedProfileMusic
            }

            if (!flag) {
                Toast.makeText(this, "변경된 내용이 없습니다.", Toast.LENGTH_SHORT).show()
            } else {
                MyApplication.db.collection("profile").document("$documentName")
                    .update(
                        "name", existingName,
                        "profileMusic", existingProfileMusic,
                        "statusMsg", existingStatusMsg
                    )
                    .addOnSuccessListener {
                    }
                    .addOnFailureListener {
                        Toast.makeText(this, "설정 실패", Toast.LENGTH_SHORT).show()
                    }
                finish()
            }
        }
    }

    private fun editState(state: String) {
        when (state) {
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

        val (height: Int, width: Int) = options.run { outHeight to outWidth }
        var insampleSize = 1

        if (height > reqHeight || width > reqWidth) {
            val halfHeight = height / 2
            val halfwidth = width / 2
            while (halfHeight / insampleSize >= reqHeight || halfwidth / insampleSize >= reqWidth) {
                insampleSize *= 2
            }
        }

        return insampleSize
    }
}