package com.example.chatting.profileDetail

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.media.ExifInterface
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.chatting.chatRoom.ChatRoomActivity
import com.example.chatting.model.UserData
import com.example.chatting.model.chatRoomUser
import com.example.chatting.storage.MyApplication
import com.example.chatting.R
import com.example.chatting.databinding.ActivityMyProfileDetailBinding
import com.example.chatting.util.URIPathHelper
import com.example.chatting.util.myCheckPermission
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import java.lang.Exception

import android.util.Log
import androidx.annotation.RequiresApi
import java.io.*


class MyProfileDetailActivity : AppCompatActivity() {
    private lateinit var userData: UserData
    private lateinit var galleryIntent: ActivityResultLauncher<Intent>
    private lateinit var filename: String

    private var chatRoomId: String? = null
    private var user1: String? = null
    private var user2: String? = null
    private var profileImgFilePath: String? = null
    private var backgroundImgFilePath: String? = null

    private val chatRoomRef = Firebase.database.getReference("chatRoomUser")
    private val userStatusRef = Firebase.database.getReference("UserStatus")
    private val myEmail = MyApplication.auth.currentUser?.email

    val binding by lazy { ActivityMyProfileDetailBinding.inflate(layoutInflater) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        try{
            userData = intent.getParcelableExtra("userData")!!
        } catch (e:Exception){}

        editState(checkProfileUser())
        binding()
        createGalleyIntent()

        //뒤로가기 버튼 클릭 시
        binding.myProfileBack.setOnClickListener {
            finish()
        }

        //채팅 버튼 클릭 시
        binding.myProfileChat.setOnClickListener {
            try {
                createChatRoom()
            } catch (e: Exception) {
                Toast.makeText(this, "채팅방 생성 실패", Toast.LENGTH_SHORT).show()
            }
        }

        //편집 버튼 클릭 시 -> 편집 상태
        binding.myProfileEdit.setOnClickListener {
            editState("edit")
        }

        //편집 상태: 뒤로가기 버튼 클릭 시
        binding.myProfileBackEdit.setOnClickListener {
            editState(checkProfileUser())
            setImages()
        }

        //편집 상태: 프로필 사진 클릭 시
        binding.myProfileImage.setOnClickListener {
            launchGalleryApp("profile")
        }

        //편집 상태: 배경 사진 편집 클릭 시
        binding.myProfileEditCamera.setOnClickListener {
            launchGalleryApp("background")
        }

        //편집 상태: 저장 버튼 클릭 시
        binding.myProfileSaveEdit.setOnClickListener {
            saveProfileChanges()
        }
    }

    private fun editState(state: String) {
        when (state) {
            "edit" -> {
                binding.run {
                    myProfileNameEdit.visibility = View.VISIBLE
                    myProfileStatusMsgEdit.visibility = View.VISIBLE
                    myProfileBackEdit.visibility = View.VISIBLE
                    myProfileSaveEdit.visibility = View.VISIBLE
                    myProfileMusicEdit.visibility = View.VISIBLE
                    myProfileEditCamera.visibility = View.VISIBLE

                    myProfileName.visibility = View.INVISIBLE
                    myProfileStatusMsg.visibility = View.INVISIBLE

                    myProfileChat.visibility = View.GONE
                    myProfileEdit.visibility = View.GONE
                    myProfileBack.visibility = View.GONE
                    myProfileMusic.visibility = View.GONE

                    myProfileImage.isEnabled = true
                }
                val hintName = binding.myProfileName.text
                val hintStatusMsg = binding.myProfileStatusMsg.text
                val hintProfileMusic = binding.myProfileMusic.text

                binding.myProfileNameEdit.setText(hintName)
                binding.myProfileStatusMsgEdit.setText(hintStatusMsg)
                binding.myProfileMusicEdit.setText(hintProfileMusic)
            }
            "myProfile" -> {
                binding.run {
                    myProfileChat.visibility = View.VISIBLE
                    myProfileEdit.visibility = View.VISIBLE
                    myProfileBack.visibility = View.VISIBLE
                    myProfileName.visibility = View.VISIBLE
                    myProfileStatusMsg.visibility = View.VISIBLE
                    myProfileMusic.visibility = View.VISIBLE

                    myProfileNameEdit.visibility = View.GONE
                    myProfileStatusMsgEdit.visibility = View.GONE
                    myProfileBackEdit.visibility = View.GONE
                    myProfileSaveEdit.visibility = View.GONE
                    myProfileMusicEdit.visibility = View.GONE
                    myProfileEditCamera.visibility = View.GONE

                    myProfileImage.isEnabled = false
                }
            }
            "notMyProfile" -> {
                binding.run {
                    myProfileChat.visibility = View.VISIBLE
                    myProfileBack.visibility = View.VISIBLE
                    myProfileName.visibility = View.VISIBLE
                    myProfileStatusMsg.visibility = View.VISIBLE
                    myProfileMusic.visibility = View.VISIBLE

                    myProfileEdit.visibility = View.GONE
                    myProfileNameEdit.visibility = View.GONE
                    myProfileStatusMsgEdit.visibility = View.GONE
                    myProfileBackEdit.visibility = View.GONE
                    myProfileSaveEdit.visibility = View.GONE
                    myProfileMusicEdit.visibility = View.GONE
                    myProfileEditCamera.visibility = View.GONE

                    myProfileImage.isEnabled = false
                }
            }
        }
    }

    private fun checkProfileUser(): String {
        return if (userData.email == myEmail) {
            "myProfile"
        } else {
            "notMyProfile"
        }
    }

    private fun binding() {
        binding.run {
            myProfileName.text = userData.name
            myProfileStatusMsg.text = userData.statusMsg
            myProfileMusic.text = userData.profileMusic

            setImages()
        }
    }

    private fun setImages() {
        MyApplication.storage.reference.child("${userData.email}/profile")
            .downloadUrl
            .addOnSuccessListener {
                Glide
                    .with(this@MyProfileDetailActivity)
                    .load(it)
                    .apply(RequestOptions().override(150, 150))
                    .error(R.drawable.img_profile)
                    .into(binding.myProfileImage)
            }
            .addOnFailureListener {
                Glide
                    .with(this@MyProfileDetailActivity)
                    .load(R.drawable.img_profile)
                    .into(binding.myProfileImage)
            }

        MyApplication.storage.reference.child("${userData.email}/background")
            .downloadUrl
            .addOnSuccessListener {
                Glide
                    .with(this@MyProfileDetailActivity)
                    .load(it)
                    .centerCrop()
                    .into(binding.myProfileBackgroundImg)
            }
    }

    private fun createGalleyIntent() {
        galleryIntent = registerForActivityResult(ActivityResultContracts.StartActivityForResult()){
            if(it.resultCode == RESULT_OK){
                val uriPathHelper = URIPathHelper()
                when(filename){
                    "profile" -> {
                        Glide
                            .with(this)
                            .load(it.data!!.data)
                            .error(R.drawable.img_profile)
                            .apply(RequestOptions().override(150, 150))
                            .into(binding.myProfileImage)
                        profileImgFilePath = uriPathHelper.getPath(this, it.data!!.data!!)
                    }
                    "background" -> {
                        Glide
                            .with(this)
                            .load(it.data!!.data)
                            .centerCrop()
                            .into(binding.myProfileBackgroundImg)
                        backgroundImgFilePath = uriPathHelper.getPath(this, it.data!!.data!!)
                    }
                }
            }
        }
    }

    private fun createChatRoom() {
        chatRoomRef.get().addOnSuccessListener {
            for (chatRoomInfo in it.children){
                chatRoomId = null
                user1 = null
                user2 = null
                for (chatRoomUserData in chatRoomInfo.children) {
                    if (user1 == null && chatRoomUserData.value == MyApplication.auth.currentUser?.email) {
                        user1 = chatRoomUserData.value as String
                    } else if (chatRoomUserData.value == userData.email) {
                        user2 = chatRoomUserData.value as String
                    }
                }
                if (user1 != null && user2 != null) {
                    chatRoomId = chatRoomInfo.key
                    break
                }
            }
            if (chatRoomId == null) {
                createChatRoomData()
            } else {
                openChatRoom()
            }
        }
    }

    private fun createChatRoomData() {
        val key = chatRoomRef.push().key!!

        val chatRoomUserdata = chatRoomUser(
            user1 = MyApplication.auth.currentUser?.email!!,
            user2 = userData.email
        )
        chatRoomRef.child(key).setValue(chatRoomUserdata)

        val userStatusData = chatRoomUser(
            user1 = "In",
            user2 = "N"
        )
        userStatusRef.child(key).setValue(userStatusData)

        Toast.makeText(applicationContext, "채팅방 생성 완료", Toast.LENGTH_SHORT).show()

        val intent = Intent(this@MyProfileDetailActivity, ChatRoomActivity::class.java)
        intent.putExtra("chatRoomId", key)
        startActivity(intent)
    }

    private fun openChatRoom() {
        val intent = Intent(this@MyProfileDetailActivity, ChatRoomActivity::class.java)
        intent.putExtra("chatRoomId", chatRoomId)
        startActivity(intent)
    }


    private fun launchGalleryApp(imageKind: String) {
        myCheckPermission(this)
        if(ContextCompat.checkSelfPermission(
                this@MyProfileDetailActivity,
                Manifest.permission.READ_EXTERNAL_STORAGE)
            == PackageManager.PERMISSION_GRANTED){
                    val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                    intent.type = "image/*"
                    filename = imageKind
                    galleryIntent.launch(intent)
        }
    }

    private fun saveProfileChanges() {
        val newName = binding.myProfileNameEdit.text.toString()
        val newStatusMsg = binding.myProfileStatusMsgEdit.text.toString()
        val newProfileMusic = binding.myProfileMusicEdit.text.toString()

        if (newName.isEmpty()) {
            Toast.makeText(this, "이름을 다시 설정해주세요.", Toast.LENGTH_SHORT).show()
        } else {
            saveImages()

            updateAndGetValue("name", newName)
            updateAndGetValue("statusMsg", newStatusMsg)
            updateAndGetValue("profileMusic", newProfileMusic)

            editState(checkProfileUser())
        }
    }

    private fun saveImages() {
        if(profileImgFilePath != null){
            saveStorage("profile", profileImgFilePath!!)
        }
        if(backgroundImgFilePath != null){
            saveStorage("background", backgroundImgFilePath!!)
        }
    }

    //비트맵 resize 및, url로 변환
    private fun saveBitmapToJpeg(context: Context, bitmap: Bitmap, name: String): String? {
        val maximagesize = 1024 * 1024
        var realimagesize = maximagesize
        var quality = 100 //사진퀄리티는 최대가 100
        val storage: File = context.getCacheDir()
        val fileName = "$name.jpg" // 임시파일로 저장
        val tempFile = File(storage, fileName)
        try {
            tempFile.createNewFile() // 파일을 생성해주고
            while (realimagesize >= maximagesize) {
                if (quality < 0) {
                    return "too big"
                }
                val out = FileOutputStream(tempFile)
                bitmap.compress(Bitmap.CompressFormat.JPEG, quality, out)
                realimagesize = tempFile.length().toInt() //작아진 본 파일의 크기를 저장하여 다시 비교
                quality -= 20   // 용량 줄이기
                out.close()
            }
            Log.d("grusie", "imagelocation resizefilesize result: $realimagesize")
        } catch (e: FileNotFoundException) {
            e.printStackTrace()
        } catch (e: IOException) {
            e.printStackTrace()
        }
        return tempFile.absolutePath //임시파일 경로로 리턴.
    }

    @RequiresApi(Build.VERSION_CODES.N)
    private fun bitmapRotate(uri : Uri, bitmap : Bitmap): Bitmap? {
        val input = contentResolver.openInputStream(uri)!!
        val exif = ExifInterface(input)
        input.close()

        val orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION,ExifInterface.ORIENTATION_NORMAL)
        val metrix = Matrix()
        if(orientation == ExifInterface.ORIENTATION_ROTATE_90){
            metrix.postRotate(90F)
        }
        else if(orientation == ExifInterface.ORIENTATION_ROTATE_180){
            metrix.postRotate(180F)
        }
        else if(orientation == ExifInterface.ORIENTATION_ROTATE_270){
            metrix.postRotate(270F)
        }
        return Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, bitmap.height, metrix, true)
    }

    @RequiresApi(Build.VERSION_CODES.N)
    private fun saveStorage(imageKind: String, filePath: String) {
        val storage = MyApplication.storage
        val storageRef = storage.reference
        val imgRef =
            storageRef.child("${MyApplication.auth.currentUser?.email}/${imageKind}")
        val bitmapImg = BitmapFactory.decodeFile(filePath)
        val rotateImg = bitmapRotate(Uri.fromFile(File(filePath)), bitmapImg)!!
        val file = Uri.fromFile(File(saveBitmapToJpeg(this,rotateImg,"tempFile")))

        imgRef
            .putFile(file)
            .addOnSuccessListener {
                Toast.makeText(this, "사진이 저장되었습니다.", Toast.LENGTH_SHORT).show()
                setImages()
            }
            .addOnFailureListener {
                Toast.makeText(this, "사진 저장에 실패했습니다.", Toast.LENGTH_SHORT).show()
            }
    }

    private fun updateAndGetValue(field: String, newValue: String) {
        MyApplication.db.collection("profile")
            .document(userData.email)
            .update(field, newValue)
            .addOnSuccessListener {
                MyApplication.db.collection("profile")
                    .document(userData.email)
                    .get()
                    .addOnSuccessListener { document ->
                        when (field) {
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
                        binding()
                    }
                    .addOnFailureListener {
                        Toast.makeText(this, "설정 실패", Toast.LENGTH_SHORT).show()
                    }
            }
    }
}