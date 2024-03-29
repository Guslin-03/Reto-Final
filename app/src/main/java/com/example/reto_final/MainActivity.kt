package com.example.reto_final

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.reto_final.data.model.InternetChecker
import com.example.reto_final.ui.group.GroupActivity
import com.example.reto_final.ui.user.loginUser.LogInActivity
import com.example.reto_final.utils.MyApp

class MainActivity : AppCompatActivity() {
    private val handler = Handler(Looper.getMainLooper())
    private var count = 0
    private lateinit var context: Context
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        context = applicationContext
        if (InternetChecker.isNetworkAvailable(context)) {
            handler.postDelayed({ logIn() }, 1000)
        } else {
            networkRetry()
        }


    }
    private fun logIn() {
        val intent = Intent(this, LogInActivity::class.java)
        startActivity(intent)
        finish()
    }
    private fun chat() {
        val intent = Intent(this, GroupActivity::class.java)
        startActivity(intent)
        finish()
    }
    private fun networkRetry() {
        if (count == 0) {
            Toast.makeText(this, R.string.toast_loading_internet, Toast.LENGTH_SHORT).show()
            count++
            handler.postDelayed({
                if (InternetChecker.isNetworkAvailable(context)) {
                    logIn()
                } else {
                    Toast.makeText(this, R.string.toast_second_loading_internet, Toast.LENGTH_SHORT).show()
                     if (MyApp.userPreferences.getUser() != null && !InternetChecker.isNetworkAvailable(
                            applicationContext)) {
                         chat()
                     }else{
                         logIn()
                     }
                }
            }, 4000)
        }
    }

}