package com.example.reto_final.utils

import android.annotation.SuppressLint
import android.app.Application
import android.content.Context

class MyApp : Application() {
    companion object {
        @SuppressLint("StaticFieldLeak")
        lateinit var context: Context
        lateinit var userPreferences:UserPreferences
    }
    override fun onCreate(){
        super.onCreate()
        context = this
        userPreferences = UserPreferences()
    }
}