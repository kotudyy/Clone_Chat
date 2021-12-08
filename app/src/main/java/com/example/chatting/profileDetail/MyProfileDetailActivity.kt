package com.example.chatting.profileDetail

import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.PackageManagerCompat
import com.example.chatting.Model.UserData
import com.example.chatting.MyApplication
import com.example.chatting.R
import com.example.chatting.databinding.ActivityMyProfileDetailBinding
import com.example.chatting.userFragment.UsersFragment
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileInputStream
import java.lang.Exception
import java.util.jar.Manifest

class MyProfileDetailActivity : AppCompatActivity() {

    private lateinit var userData: UserData
    val binding by lazy{ActivityMyProfileDetailBinding.inflate(layoutInflater)}
    private lateinit var documentName: String
    lateinit var galleryIntent: ActivityResultLauncher<Intent>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        editState("normal")

        //리사이클러 뷰 항목 클릭시 넘어온 userData 정보를 화면 뷰에 구성
        buildScreenWithUserInfo()
        documentName = userData.email

        //취소 버튼 클릭 시
        binding.myProfileBack.setOnClickListener { finish() }
        binding.myProfileBackEdit.setOnClickListener {
            editState("normal")
        }

        //즐겨찾기 버튼 클릭 시

        //프로필 사진 클릭 시

        val profileImgRef = MyApplication.storage.reference.child("$documentName/profile")
        val backgroundImgRef = MyApplication.storage.reference.child("$documentName/background")
        galleryIntent =
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
                        bitmap?.let { b ->
                            binding.myProfileImage.setImageBitmap(b)

                            //갤러리의 사진을 스토리지에 저장
                            val baos = ByteArrayOutputStream()
                            b.compress(Bitmap.CompressFormat.JPEG, 100, baos)
                            val data = baos.toByteArray()
                            val uploadTask = profileImgRef.putBytes(data)
                            uploadTask
                                .addOnSuccessListener {

                            }
                                .addOnFailureListener{

                                }

                        } ?: let {
                            Toast.makeText(this, "Bitmap Null", Toast.LENGTH_SHORT).show()
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
            }

        binding.myProfileImage.setOnClickListener {
            checkPermissionAndOpenGallery()
        }

        //채팅 버튼 클릭 시

        //편집 버튼 클릭 시
        binding.myProfileEdit.setOnClickListener {
            editState("edit")
        }

        //저장 버튼 클릭 시
        binding.myProfileSaveEdit.setOnClickListener {

            val newName = binding.myProfileNameEdit.text.toString()
            val newStatusMsg = binding.myProfileStatusMsgEdit.text.toString()
            val newProfileMusic = binding.myProfileMusicEdit.text.toString()

            if (newName.isEmpty()){
                Toast.makeText(this, "이름을 다시 설정해주세요.", Toast.LENGTH_SHORT).show()
            } else {
                updateAndGetValue("name", newName)
                updateAndGetValue("statusMsg", newStatusMsg)
                updateAndGetValue("profileMusic", newProfileMusic)

                editState("normal")
            }
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        when(requestCode) {
            10 -> {

            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d("test", userData.toString())
    }

    private fun checkPermissionAndOpenGallery() {
        val galleryPermission =ContextCompat.checkSelfPermission(this, android.Manifest.permission.READ_EXTERNAL_STORAGE)
        if (galleryPermission == PackageManager.PERMISSION_GRANTED){

            val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            intent.type = "image/*"
            galleryIntent.launch(intent)
        } else {
            ActivityCompat.requestPermissions(this, arrayOf(android.Manifest.permission.READ_EXTERNAL_STORAGE), 10)
        }
    }

    private fun updateAndGetValue(field: String, newValue: String) {

        //생각해보니,,, userData RV adapter가 database에 있는 정보를 가져오면 친구들 프로필 수정도 내가 하는 것이고
        //그럼 본인이 친구들 database에 접근하는 것인데,,, 이걸 방지하려면 개인이 기억하는 정보 저장 공간이 따로 필요할 것

        MyApplication.db.collection("profile")
            .document(documentName!!)
            .update(field, newValue)
            .addOnSuccessListener {
                //update 성공 시 newValue get -> userData에 삽입
                MyApplication.db.collection("profile")
                    .document(documentName)
                    .get()
                    .addOnSuccessListener { document ->
                         when(field) {
                             "name" -> {
                                 userData.name = document[field] as String
                             }
                             "statusMsg" -> {
                                 userData.statusMsg = document[field] as String
                             }
                             "profileMusic" -> {
                                 userData.profileMusic = document[field] as String
                             }
                         }

                        loadUserData()
                    }
            .addOnFailureListener {
                Toast.makeText(this, "설정 실패", Toast.LENGTH_SHORT).show() }
            }
    }

    private fun buildScreenWithUserInfo() {
        userData = intent.getParcelableExtra<UserData>("userData")!!
        loadUserData()
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun loadUserData() {
        binding.run{
            myProfileName.text = userData.name
            myProfileStatusMsg.text = userData.statusMsg
            myProfileMusic.text = userData.profileMusic
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
                val hintProfileMusic = binding.myProfileMusic.text

                binding.myProfileNameEdit.setText(hintName)
                binding.myProfileStatusMsgEdit.setText(hintStatusMsg)
                binding.myProfileMusicEdit.setText(hintProfileMusic)

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
            val inputStream = contentResolver.openInputStream(fileUri)
            BitmapFactory.decodeStream(inputStream, null, options)
            inputStream!!.close()
        } catch (e: Exception) {
            e.printStackTrace()
        }

        val (height: Int, width: Int) = options.run{ outHeight to outWidth }
        var inSampleSize = 1

        if(height > reqHeight || width > reqWidth){
            val halfHeight = height / 2
            val halfwidth = width / 2
            while(halfHeight / inSampleSize >= reqHeight || halfwidth / inSampleSize >= reqWidth){
                inSampleSize *= 2
            }
        }

        return inSampleSize
    }
}