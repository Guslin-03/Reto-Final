package com.example.reto_final.ui

import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.DrawableCompat
import com.example.reto_final.R
import com.example.reto_final.data.repository.RemoteUserDataSource
import com.example.reto_final.databinding.ChangePasswordActivityBinding
import com.example.reto_final.ui.user.UserViewModel
import com.example.reto_final.ui.user.UserViewModelFactory
import com.example.reto_final.utils.MyApp
import com.example.reto_final.utils.Resource

class ChangePasswordActivity : AppCompatActivity() {

    private lateinit var binding: ChangePasswordActivityBinding
    private val userRepository = RemoteUserDataSource()
    private val viewModel: UserViewModel by viewModels { UserViewModelFactory(userRepository) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ChangePasswordActivityBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbarPersonalConfiguration)
        val user = MyApp.userPreferences.getUser()
        binding.changePassword.setOnClickListener {
            if (checkData()) viewModel.onChangePassword(binding.newPassword1.text.toString())
            //backToLogIn()
        }

        binding.back.setOnClickListener {
            //Crear una variable global o singelton para validar si viene de la actividad de
            //PersonalConfiguration o de LogIn
            backToConfigurationActivity()
        }

        binding.toolbarPersonalConfiguration.setOnMenuItemClickListener { item ->
            when (item.itemId) {
                R.id.closeSesion -> {
                    if (user != null) {
                        viewModel.onLogOut()
                    }
                    backToLogIn()
                    true
                }
                else -> false // Manejo predeterminado para otros elementos
            }
        }

        viewModel.update.observe(this) {
            when (it.status) {
                Resource.Status.SUCCESS -> {
                    viewModel.onLogOut()
                }
                Resource.Status.ERROR -> {
                    Log.i("Prueba", ""+it.message)
                    Toast.makeText(this, it.message, Toast.LENGTH_LONG).show()
                }
                Resource.Status.LOADING -> {
                }
            }
        }

    }

    private fun checkData(): Boolean {
        val currentPassword = binding.currentPassword.text.toString()
        val newPassword1 = binding.newPassword1.text.toString()
        val newPassword2 = binding.newPassword2.text.toString()

        if (currentPassword.isEmpty()  || newPassword1.isEmpty() || newPassword2.isEmpty()) {
            Toast.makeText(this, R.string.toast_empty_1, Toast.LENGTH_LONG).show()
            binding.currentPasswordLayout.defaultHintTextColor = ColorStateList.valueOf(Color.RED)
            binding.newPassword1Layout.defaultHintTextColor = ColorStateList.valueOf(Color.RED)
            binding.newPassword2Layout.defaultHintTextColor = ColorStateList.valueOf(Color.RED)

            return false
        }

        if (currentPassword != newPassword1) {
            if (newPassword1 == newPassword2) {
                if (newPassword1 == "elorrieta00") {
                    Toast.makeText(this, R.string.toast_password_default, Toast.LENGTH_LONG).show()
                    binding.currentPasswordLayout.defaultHintTextColor = ColorStateList.valueOf(Color.RED)
                    binding.newPassword1Layout.defaultHintTextColor = ColorStateList.valueOf(Color.RED)
                    binding.newPassword2Layout.defaultHintTextColor = ColorStateList.valueOf(Color.RED)
                    return false
                }

                if (newPassword1.length >= 8) {
                    return true
                } else {
                    Toast.makeText(this, R.string.toast_password_lenght, Toast.LENGTH_LONG).show()
                    binding.newPassword1.setTextColor(Color.RED)
                    binding.newPassword2.setTextColor(Color.BLACK)

                    binding.newPassword1Layout.defaultHintTextColor = ColorStateList.valueOf(Color.BLACK)
                    binding.newPassword2Layout.defaultHintTextColor = ColorStateList.valueOf(Color.BLACK)
                    return false
                }
            } else {
                Toast.makeText(this, R.string.toast_password_matches, Toast.LENGTH_LONG).show()
                binding.newPassword1.setTextColor(Color.RED)
                binding.newPassword2.setTextColor(Color.RED)

                binding.newPassword1Layout.defaultHintTextColor = ColorStateList.valueOf(Color.BLACK)
                binding.newPassword2Layout.defaultHintTextColor = ColorStateList.valueOf(Color.BLACK)
                return false
            }

        }else {
            Toast.makeText(this, R.string.toast_password_old_new_matches, Toast.LENGTH_LONG).show()
            binding.currentPasswordLayout.defaultHintTextColor = ColorStateList.valueOf(Color.RED)
            binding.newPassword1Layout.defaultHintTextColor = ColorStateList.valueOf(Color.RED)
            binding.newPassword2Layout.defaultHintTextColor = ColorStateList.valueOf(Color.RED)
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