package com.example.reto_final.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.reto_final.databinding.ChatActivityBinding

class ChatActivity: AppCompatActivity() {

    private lateinit var binding: ChatActivityBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ChatActivityBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }

}