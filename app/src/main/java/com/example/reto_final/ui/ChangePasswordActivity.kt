package com.example.reto_final.ui

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.reto_final.databinding.ChangePasswordActivityBinding

class ChangePasswordActivity : AppCompatActivity() {

    private lateinit var binding: ChangePasswordActivityBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ChangePasswordActivityBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.changePassword.setOnClickListener {
            logIn()
        }

        binding.back.setOnClickListener {
            //Crear una variable global o singelton para validar si viene de la actividad de
            //PersonalConfiguration o de LogIn
            backToConfigurationActivity()
        }

    }

    private fun logIn() {
        val intent = Intent(this, LogInActivity::class.java)
        startActivity(intent)
        finish()
    }

    private fun backToConfigurationActivity() {
        //Pasariamos el Objeto User por parametro para settear los valores en los campos por defecto
        val intent = Intent(this, ConfigurationActivity::class.java)
        startActivity(intent)
        finish()
    }

}