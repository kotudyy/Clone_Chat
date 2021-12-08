package com.example.chatting.ProfileDetail

import android.content.Intent
import android.graphics.BitmapFactory
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.chatting.Model.UserData
import com.example.chatting.MyApplication
import com.example.chatting.R
import com.example.chatting.UserFragment.UsersFragment
import com.example.chatting.databinding.ActivityMainBinding
import com.example.chatting.databinding.ActivityMyProfileDetailBinding
import com.example.chatting.util.myCheckPermission
import com.google.firebase.storage.StorageReference
import java.io.File
import java.lang.Exception

class MyProfileDetailActivity : AppCompatActivity() {

    private lateinit var userData: UserData
    lateinit var filePath: String

    val binding by lazy{ActivityMyProfileDetailBinding.inflate(layoutInflater)}
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        editState("normal")

        //리사이클러 뷰 항목 클릭시 넘어온 userData 정보를 화면 뷰에 구성
        userData = intent.getParcelableExtra<UserData>("userData")!!

        var imgRef = MyApplication.storage.reference.child("${userData.email}/profile.jpg")
        Glide.with(this).load(imgRef).error(R.drawable.img_profile).into(binding.myProfileImage)

        binding.run{
            myProfileName.text = userData.name
            myProfileStatusMsg.text = userData.statusMsg
            myProfileMusic.text = userData.profileMusic
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
                val cursor = contentResolver.query(it.data!!.data as Uri,
                    arrayOf<String>(MediaStore.Images.Media.DATA), null, null, null);
                cursor?.moveToFirst().let {
                    filePath = cursor?.getString(0) as String
                }
                if (it.resultCode == RESULT_OK) {
                        try{
                            saveStorage("profile")
                            Glide
                                .with(this)
                                .load(it.data!!.data)
                                .apply(RequestOptions().override(150,150))
                                .centerCrop()
                                .into(binding.myProfileImage)
                        } catch (e:Exception){
                            Log.d("grusie", "exception : $e")
                        }

                }
            }

        binding.myProfileImage.setOnClickListener{
            val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            intent.type = "image/*"
            try {
                galleryIntent.launch(intent)
            }catch (e:Exception){
            }
            myCheckPermission(this)
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

            if (changedName.isEmpty()) {
                Toast.makeText(this, "이름을 다시 설정해주세요.", Toast.LENGTH_SHORT).show()
            }
            else {
                MyApplication.db.collection("profile").document("$documentName")
                    .update("name", changedName)
                    .addOnSuccessListener {
                        // 이름 변경 성공 시 상태메세지도 변경 진행
                        val changedStatusMsg = binding.myProfileStatusMsgEdit.text.toString()
                        MyApplication.db.collection("profile").document("$documentName")
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

                val textName = binding.myProfileName.text
                val hintStatusMsg = binding.myProfileStatusMsg.text as String

                if(textName != "")
                    binding.myProfileNameEdit.setText(textName)
                if (hintStatusMsg != "")
                    binding.myProfileStatusMsgEdit.setText(hintStatusMsg)

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


    private fun saveStorage(imageKind:String) {
        val storage = MyApplication.storage
        val storageRef = storage.reference
        val imgRef = storageRef.child("${MyApplication.auth.currentUser?.email}/${imageKind}.jpg")
        var file = Uri.fromFile(File(filePath))
        imgRef.putFile(file).addOnSuccessListener{
            Log.d("grusie","저장됨")
            Toast.makeText(this, "사진이 저장되었습니다.",Toast.LENGTH_SHORT).show()
        }.addOnFailureListener{
            Log.d("grusie","error : $it")
        }
    }
}