package com.example.reto_final.utils

import android.content.Context
import android.content.SharedPreferences
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.util.Base64
import com.example.reto_final.data.model.user.LoginUser
import com.google.gson.Gson
import io.socket.client.Socket
import org.greenrobot.eventbus.EventBus
import java.io.ByteArrayOutputStream

class UserPreferences {
    private val sharedPreferences: SharedPreferences by lazy{
        MyApp.context.getSharedPreferences(MyApp.context.packageName, Context.MODE_PRIVATE)
    }
    lateinit var mSocket: Socket

    companion object {
        const val USER_TOKEN = "user_token"
        const val USER_INFO = "user_info"
        const val REMEMBER_ME = "remember_me"
        const val PASS = "pass"
        const val HIBERNATE_TOKEN = "hibernate_token"
        const val DATABASE_CREATED = "isDatabaseCreated"
        const val PROFILE_PICTURE = "profile_picture"
        const val PROFILE_PICTURE_CAMERA = "profile_picture_camera"


    }
    fun saveProfilePicture(uri: Uri) {
        val editor = sharedPreferences.edit()
        editor.putString(PROFILE_PICTURE, uri?.toString())
        editor.apply()
    }

    fun getProfilePictureUri(): Uri? {
        val uriString = sharedPreferences.getString(PROFILE_PICTURE, null)
        return uriString?.let { Uri.parse(it) }
    }

    fun saveProfilePictureCamera(uri: Uri) {
        val editor = sharedPreferences.edit()
        editor.putString(PROFILE_PICTURE_CAMERA, uri?.toString())
        editor.apply()
    }

    fun getProfilePictureUriCamera(): Uri? {
        val uriString = sharedPreferences.getString(PROFILE_PICTURE_CAMERA, null)
        return uriString?.let { Uri.parse(it) }
    }
    fun removeProfilePictureUri() {
        val editor = sharedPreferences.edit()
        editor.remove("profile_picture")
        editor.apply()
    }
    fun removeProfilePictureUriCamera() {
        val editor = sharedPreferences.edit()
        editor.remove("profile_picture_camera")
        editor.apply()
    }


    fun saveDataBaseIsCreated(isDatabaseCreated: Boolean) {
        val editor = sharedPreferences.edit()
        editor.putBoolean(DATABASE_CREATED, isDatabaseCreated)
        editor.apply()
    }

    fun getSaveDataBaseIsCreated(): Boolean {
        return sharedPreferences.getBoolean(DATABASE_CREATED, false)
    }

    fun saveAuthToken(token:String){
        val editor = sharedPreferences.edit()
        editor.putString(USER_TOKEN, token)
        editor.apply()
    }
    fun fetchAuthToken():String?{
        return sharedPreferences.getString(USER_TOKEN,null)
    }
    fun saveHibernateToken(token:String){
        val editor = sharedPreferences.edit()
        editor.putString(HIBERNATE_TOKEN, token)
        editor.apply()
    }
    fun fetchHibernateToken():String?{
        return sharedPreferences.getString(HIBERNATE_TOKEN,null)
    }

    fun saveUser(loginUser: LoginUser) {
        val editor = sharedPreferences.edit()
        val gson = Gson()
        val userJson = gson.toJson(loginUser)
        editor.putString(USER_INFO, userJson)
        editor.apply()
    }

    fun getUser(): LoginUser? {
        val userJson = sharedPreferences.getString(USER_INFO, null)
        if (userJson != null) {
            val gson = Gson()
            val loginUser = gson.fromJson(userJson, LoginUser::class.java)
            return loginUser
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
        editor.remove("hibernate_token")
        editor.remove("profile_picture_camera")
        editor.remove("profile_picture")
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

    fun prueba() {

        EventBus.getDefault().post("asd")
    }
}
