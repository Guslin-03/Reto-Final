package com.example.reto_final.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.reto_final.databinding.LoginActivityBinding

class LogInActivity : AppCompatActivity(){

    private lateinit var binding: LoginActivityBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = LoginActivityBinding.inflate(layoutInflater)
        setContentView(binding.root)

    }

}