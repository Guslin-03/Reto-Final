package com.example.reto_final.ui

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.reto_final.databinding.LoginActivityBinding

class LogInActivity : AppCompatActivity(){

    private lateinit var binding: LoginActivityBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = LoginActivityBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.login.setOnContextClickListener {
            logIn()
        }

    }

    private fun logIn(): Boolean {
        val intent = Intent(this, ChangePasswordActivity::class.java)
        startActivity(intent)
        finish()
        return true
    }

}