package com.example.chatting.storage

import android.content.Context
import android.content.SharedPreferences

class MySharedPreferences(context: Context) {

    private val prefs: SharedPreferences = context.getSharedPreferences(PREFS_FILENAME, 0)

    var globalToken: String?
        get() = prefs.getString(PREF_KEY, "")
        set(value) = prefs.edit().putString(PREF_KEY, value).apply()
    /* 파일 이름과 EditText 를 저장할 Key 값을 만들고 prefs 인스턴스 초기화 */

    companion object {
        const val PREF_KEY = "token"
        const val PREFS_FILENAME = "prefs"
        /* get/set 함수 임의 설정. get 실행 시 저장된 값을 반환하며 default 값은 ""
         * set(value) 실행 시 value 로 값을 대체한 후 저장 */
    }
}