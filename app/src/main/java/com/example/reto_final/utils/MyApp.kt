package com.example.reto_final.utils

import android.annotation.SuppressLint
import android.app.Application
import android.content.Context
import androidx.room.Room
import com.example.reto_final.data.repository.local.MyAppRoomDataBase
import com.example.reto_final.data.repository.local.MyAppRoomDatabaseCallback
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers

class MyApp : Application() {
    companion object {
        @SuppressLint("StaticFieldLeak")
        lateinit var context: Context
        lateinit var userPreferences: UserPreferences
        lateinit var db: MyAppRoomDataBase
        lateinit var appScope: CoroutineScope
        const val AUTHORIZATION_HEADER = "Authorization"
        const val API_SERVER = "http://10.5.7.97"
        const val API_PORT = "8063"
        const val API_SOCKET_PORT = "8085"
    }
    override fun onCreate(){
        super.onCreate()
        context = this
        userPreferences = UserPreferences()
        appScope = CoroutineScope(Dispatchers.IO)

        db = Room
            .databaseBuilder(this, MyAppRoomDataBase::class.java, "chat-db")
            .addCallback(MyAppRoomDatabaseCallback(appScope))
            .build()

//        db.close()
//        context.deleteDatabase("chat-db")

    }
/*
    override fun onTerminate() {
        super.onTerminate()
        userPreferences.mSocket.disconnect()
    }
*/
}