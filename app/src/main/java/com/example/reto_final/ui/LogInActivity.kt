package com.example.reto_final.ui

import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.util.Patterns
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatCheckBox
import androidx.core.content.ContextCompat
import com.example.reto_final.R
import com.example.reto_final.data.repository.RemoteUserDataSource
import com.example.reto_final.databinding.LoginActivityBinding
import com.example.reto_final.ui.user.UserViewModel
import com.example.reto_final.ui.user.UserViewModelFactory
import com.example.reto_final.utils.MyApp
import com.example.reto_final.utils.Resource
import java.util.Locale
import java.util.regex.Pattern

class LogInActivity : AppCompatActivity(){

    private lateinit var binding: LoginActivityBinding
    private val userRepository = RemoteUserDataSource()
    private val viewModel: UserViewModel by viewModels { UserViewModelFactory(userRepository) }
    private lateinit var rememberMeCheckBox: AppCompatCheckBox

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = LoginActivityBinding.inflate(layoutInflater)
        setContentView(binding.root)

        rememberMeCheckBox= binding.rememberMe
        rememberMeCheckBox.buttonTintList = ColorStateList.valueOf(ContextCompat.getColor(this, R.color.blue_green))
        //Pone el checkbox true o false segun lo guardado
        rememberMeCheckBox.isChecked= MyApp.userPreferences.getRememberMeState()
        //Pone pass y user si hay uno guardado
        if(rememberMeCheckBox.isChecked){
            binding.email.setText(MyApp.userPreferences.getUser()!!.email)
            binding.password.setText(MyApp.userPreferences.getPass())
        }else {
            MyApp.userPreferences.removeData()
        }

        binding.login.setOnClickListener {
//            var email = binding.email.text.toString()
//            email = lowerCaseEmail(email)
//            val password = binding.password.text.toString()
//            if(checkData()){
//                viewModel.onSearchUser(email, password)
//            }
            logIn()
        }

        binding.changePassword.setOnClickListener {
            changePassword()
        }

        viewModel.user.observe(this) {
            when (it.status) {
                Resource.Status.SUCCESS -> {
                    val userResource = viewModel.user.value
                    if (userResource != null && userResource.status == Resource.Status.SUCCESS) {
                        val user = userResource.data
                        if (user != null && binding.rememberMe.isChecked) {
                            Log.i("prueba", "prueba3" + binding.rememberMe.isChecked)
                            MyApp.userPreferences.saveUser(user)
                            MyApp.userPreferences.saveRememberMeState(binding.rememberMe.isChecked)
                            MyApp.userPreferences.savePass(binding.password.text.toString())
                        } else if (user != null && !binding.rememberMe.isChecked) {
                            MyApp.userPreferences.saveUser(user)
                            MyApp.userPreferences.saveRememberMeState(false)
                        }
                    }
                    logIn()
                }
                Resource.Status.ERROR -> {
                    Log.i("Prueba", "" + it.message)
                    Toast.makeText(this, it.message, Toast.LENGTH_LONG)
                        .show()
                }
                Resource.Status.LOADING -> {

                }
            }
        }

    }

    private fun checkData(): Boolean {
        val email = binding.email.text.toString()
        val password = binding.password.text.toString()

        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Ningún campo puede estar vacío", Toast.LENGTH_LONG).show()
            binding.email.setHintTextColor(Color.RED)
            binding.password.setHintTextColor(Color.RED)
            return false
        }
        val emailCorrecto = validarEmail(email)
        if (!emailCorrecto) {
            Toast.makeText(this, "El correo tiene un formato erróneo", Toast.LENGTH_LONG).show()
            binding.email.setTextColor(Color.RED)
            binding.password.setTextColor(Color.BLACK)

            binding.email.setHintTextColor(Color.BLACK)
            binding.password.setHintTextColor(Color.BLACK)
            return false
        }

        return if (password.length >= 8) {
            true
        } else {
            Toast.makeText(this, "La contraseña debe tener 8 caracteres o más", Toast.LENGTH_LONG
            ).show()
            binding.email.setTextColor(Color.BLACK)
            binding.password.setTextColor(Color.RED)

            binding.email.setHintTextColor(Color.BLACK)
            binding.password.setHintTextColor(Color.BLACK)
            false
        }

    }

    private fun validarEmail(email: String): Boolean {
        val pattern: Pattern = Patterns.EMAIL_ADDRESS
        return pattern.matcher(email).matches()
    }
    private fun lowerCaseEmail(input: String): String {
        return input.lowercase(Locale.ROOT)
    }

    private fun logIn() {
        val intent = Intent(this, PersonalConfigurationActivity::class.java)
        startActivity(intent)
        finish()
    }

    private fun changePassword() {
        val intent = Intent(this, ChangePasswordActivity::class.java)
        startActivity(intent)
        finish()
    }

}