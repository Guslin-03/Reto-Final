package com.example.reto_final.ui

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.reto_final.databinding.PersonalConfigurationActvityBinding

class PersonalConfigurationActivity : AppCompatActivity() {

    private lateinit var binding: PersonalConfigurationActvityBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = PersonalConfigurationActvityBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.next.setOnClickListener {
            nextConfiguration()
        }

        binding.back.setOnClickListener {
            backToLogIn()
        }

    }

    private fun nextConfiguration() {
        val intent = Intent(this, ConfigurationActivity::class.java)
        startActivity(intent)
        finish()
    }

    private fun backToLogIn() {
        val intent = Intent(this, LogInActivity::class.java)
        startActivity(intent)
        finish()
    }

}