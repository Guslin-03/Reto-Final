package com.example.reto_final

import android.content.Intent
import android.net.ConnectivityManager
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.reto_final.ui.LogInActivity

class MainActivity : AppCompatActivity() {
    private val handler = Handler(Looper.getMainLooper())
    private var count = 0
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        if (isNetworkAvailable()) {
            handler.postDelayed({ logIn() }, 3000)
        } else {
            networkRetry()
        }


    }
    private fun logIn() {
        val intent = Intent(this, LogInActivity::class.java)
        startActivity(intent)
        finish()
    }
    private fun isNetworkAvailable(): Boolean {
        val connectivityManager = getSystemService(CONNECTIVITY_SERVICE) as ConnectivityManager
        if (connectivityManager != null) {
            val activeNetworkInfo = connectivityManager.activeNetworkInfo
            return activeNetworkInfo != null && activeNetworkInfo.isConnected
        }
        return false
    }
    private fun networkRetry() {
        if (count == 0) {
            Toast.makeText(this, "Sin conexión, reintentando conexión...", Toast.LENGTH_SHORT).show()
            count++
            handler.postDelayed({
                if (isNetworkAvailable()) {
                    logIn()
                } else {
                    Toast.makeText(this, "Sin conexión, acceso a funcionalidades limitadas", Toast.LENGTH_SHORT).show()
                    logIn()
                }
            }, 4000)
        }
    }

}