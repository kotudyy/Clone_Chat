package com.example.chatting.storage

import android.content.Context
import android.content.SharedPreferences

class MySharedPreferences(context: Context) {

    private val prefs: SharedPreferences = context.getSharedPreferences(PREFS_FILENAME, 0)

    var globalToken: String?
        get() = prefs.getString(PREF_KEY, "")
        set(value) = prefs.edit().putString(PREF_KEY, value).apply()

    companion object {
        const val PREF_KEY = "token"
        const val PREFS_FILENAME = "prefs"
    }
}