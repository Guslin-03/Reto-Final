package com.example.reto_final.ui

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.reto_final.databinding.ConfigurationActvityBinding

class ConfigurationActivity : AppCompatActivity() {

    private lateinit var binding: ConfigurationActvityBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ConfigurationActvityBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.next.setOnClickListener {
            changePassword()
        }

        binding.back.setOnClickListener {
            backToPersonalConfiguration()
        }

    }

    private fun changePassword() {
        val intent = Intent(this, ChangePasswordActivity::class.java)
        startActivity(intent)
        finish()
    }

    private fun backToPersonalConfiguration() {
        //Pasariamos el Objeto User por parametro para settear los valores en los campos por defecto
        val intent = Intent(this, PersonalConfigurationActivity::class.java)
        startActivity(intent)
        finish()
    }

}