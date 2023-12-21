package com.example.reto_final.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.reto_final.databinding.GroupActivityBinding

class GroupActivity: AppCompatActivity() {

    private lateinit var binding: GroupActivityBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = GroupActivityBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }

}