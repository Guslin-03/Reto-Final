package com.example.reto_final.utils

import android.annotation.SuppressLint
import android.app.Application
import android.content.Context
import androidx.room.Room
import com.example.reto_final.data.repository.local.MyAppRoomDataBase

class MyApp : Application() {
    companion object {
        @SuppressLint("StaticFieldLeak")
        lateinit var context: Context
        lateinit var userPreferences: UserPreferences
        lateinit var db: MyAppRoomDataBase
    }
    override fun onCreate(){
        super.onCreate()
        context = this
        userPreferences = UserPreferences()

        db = Room
            .databaseBuilder(this, MyAppRoomDataBase::class.java, "chat-db")
            .build()

    }
}