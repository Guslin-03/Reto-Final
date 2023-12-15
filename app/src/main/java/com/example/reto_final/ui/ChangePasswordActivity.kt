package com.example.reto_final.ui

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.Menu
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.DrawableCompat
import com.example.reto_final.R
import com.example.reto_final.databinding.ChangePasswordActivityBinding

class ChangePasswordActivity : AppCompatActivity() {

    private lateinit var binding: ChangePasswordActivityBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ChangePasswordActivityBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbarPersonalConfiguration)

        binding.changePassword.setOnClickListener {
            //if (checkData()) backToLogIn()
            backToLogIn()
        }

        binding.back.setOnClickListener {
            //Crear una variable global o singelton para validar si viene de la actividad de
            //PersonalConfiguration o de LogIn
            backToConfigurationActivity()
        }

        binding.toolbarPersonalConfiguration.setOnMenuItemClickListener { item ->
            when (item.itemId) {
                R.id.closeSesion -> {
                    backToLogIn()
                    true
                }
                else -> false // Manejo predeterminado para otros elementos
            }
        }

    }

    private fun checkData(): Boolean {
        val currentPassword = binding.currentPassword.text.toString()
        val newPassword1 = binding.newPassword1.text.toString()
        val newPassword2 = binding.newPassword2.text.toString()

        if (currentPassword.isEmpty()  || newPassword1.isEmpty() || newPassword2.isEmpty()) {
            Toast.makeText(this, "Ningún campo puede estar vacío", Toast.LENGTH_LONG).show()
            binding.currentPassword.setHintTextColor(Color.RED)
            binding.newPassword1.setHintTextColor(Color.RED)
            binding.newPassword2.setHintTextColor(Color.RED)
            return false
        }

        if (currentPassword != newPassword1) {
            if (newPassword1 == newPassword2) {
                if (newPassword1 == "Elorrieta00") {
                    Toast.makeText(this, "La nueva contraseña es igual a la contraseña por defecto", Toast.LENGTH_LONG).show()
                    binding.currentPassword.setHintTextColor(Color.RED)
                    binding.newPassword1.setHintTextColor(Color.RED)
                    binding.newPassword2.setHintTextColor(Color.RED)
                    return false
                }

                if (newPassword1.length >= 8) {
                    return true
                } else {
                    Toast.makeText(this, "La nueva contraseña debe tener 8 caracteres o más", Toast.LENGTH_LONG).show()
                    binding.newPassword1.setTextColor(Color.RED)
                    binding.newPassword2.setTextColor(Color.BLACK)

                    binding.newPassword1.setHintTextColor(Color.BLACK)
                    binding.newPassword2.setHintTextColor(Color.BLACK)
                    return false
                }
            } else {
                Toast.makeText(this, "Las dos contraseñas no coinciden", Toast.LENGTH_LONG).show()
                binding.newPassword1.setTextColor(Color.RED)
                binding.newPassword2.setTextColor(Color.RED)

                binding.newPassword1.setHintTextColor(Color.BLACK)
                binding.newPassword2.setHintTextColor(Color.BLACK)
                return false
            }

        }else {
            Toast.makeText(this, "La anterior contraseña y la nueva son iguales", Toast.LENGTH_LONG).show()
            binding.currentPassword.setHintTextColor(Color.RED)
            binding.newPassword1.setHintTextColor(Color.RED)
            binding.newPassword2.setHintTextColor(Color.RED)
            return false
        }

    }

    private fun backToLogIn() {
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

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.top_menu,menu)

        // Cambiar el color del icono del botón de desbordamiento
        binding.toolbarPersonalConfiguration.overflowIcon?.let {
            val color = ContextCompat.getColor(this, R.color.white) // Reemplaza R.color.white por el color deseado
            val newIcon = DrawableCompat.wrap(it)
            DrawableCompat.setTint(newIcon, color)
            binding.toolbarPersonalConfiguration.overflowIcon = newIcon
        }
        return true
    }

}