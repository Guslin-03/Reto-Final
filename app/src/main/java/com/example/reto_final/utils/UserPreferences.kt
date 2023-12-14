package com.example.reto_final.utils

import android.content.Context
import android.content.SharedPreferences
import com.example.reto_final.data.User
import com.google.gson.Gson

class UserPreferences {
    private val sharedPreferences: SharedPreferences by lazy{
        MyApp.context.getSharedPreferences(MyApp.context.packageName, Context.MODE_PRIVATE)
    }
    companion object {
        const val USER_TOKEN = "user_token"
        const val USER_INFO = "user_info"
        const val REMEMBER_ME = "remember_me"
        const val PASS = "pass"
    }
    fun saveAuthToken(token:String){
        val editor = sharedPreferences.edit()
        editor.putString(USER_TOKEN, token)
        editor.apply()
    }
    fun fetchAuthToken():String?{
        return sharedPreferences.getString(USER_TOKEN,null)
    }

    fun saveUser(user: User) {
        val editor = sharedPreferences.edit()
        val gson = Gson()
        val userJson = gson.toJson(user)
        editor.putString(USER_INFO, userJson)
        editor.apply()
    }

    fun getUser(): User? {
        val userJson = sharedPreferences.getString(USER_INFO, null)
        if (userJson != null) {
            val gson = Gson()
            return gson.fromJson(userJson, User::class.java)
        }
        return null
    }
    fun saveRememberMeState(rememberMe: Boolean) {
        val editor = sharedPreferences.edit()
        editor.putBoolean(REMEMBER_ME, rememberMe)
        editor.apply()
    }

    fun getRememberMeState(): Boolean {
        return sharedPreferences.getBoolean(REMEMBER_ME, false)
    }
    fun removeData() {
        val editor = sharedPreferences.edit()
        editor.remove("user_token")
        editor.remove("user_info")
        editor.putBoolean(REMEMBER_ME, false)
        editor.apply()
    }
    fun savePass(pass: String) {
        val editor = sharedPreferences.edit()
        editor.putString(PASS, pass)
        editor.apply()
    }

    fun getPass(): String? {
        return sharedPreferences.getString(PASS,null)
    }
}
