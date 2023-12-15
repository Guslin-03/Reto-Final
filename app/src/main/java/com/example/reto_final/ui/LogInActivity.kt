package com.example.reto_final.ui

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.example.reto_final.data.repository.RemoteUserDataSource
import com.example.reto_final.databinding.LoginActivityBinding
import com.example.reto_final.utils.MyApp
import com.example.reto_final.utils.Resource

class LogInActivity : AppCompatActivity(){

    private lateinit var binding: LoginActivityBinding
    private val userRepository = RemoteUserDataSource()
    private val viewModel: UserViewModel by viewModels { UserViewModelFactory(userRepository) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = LoginActivityBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.login.setOnClickListener {
            logIn()
        }

        binding.changePassword.setOnClickListener {
            changePassword()
        }

        viewModel.found.observe(this) {
            when (it.status) {
                Resource.Status.SUCCESS -> {
                    val user = viewModel.found.value

                    if (user != null) {
                        val accessToken = user.data?.accessToken
                        Log.i("Login", "" + accessToken)
                        if (accessToken != null) {
                            MyApp.userPreferences.saveAuthToken(accessToken)
                            viewModel.getUserInfo()
                        }
                    }
                }
                Resource.Status.ERROR -> {
                    Toast.makeText(this, it.message, Toast.LENGTH_LONG)
                        .show()
                }
                Resource.Status.LOADING -> {

                }
            }
        }

        viewModel.user.observe(this) {
            when (it.status) {
                Resource.Status.SUCCESS -> {
                    val userResource = viewModel.user.value
                    if (userResource != null && userResource.status == Resource.Status.SUCCESS) {
                        val user = userResource.data
                        Log.i("checkbox", "" + binding.rememberMe.isChecked)
                        if (user != null && binding.rememberMe.isChecked) {
                            MyApp.userPreferences.saveUser(user)
                            MyApp.userPreferences.saveRememberMeState(binding.rememberMe.isChecked)
                            MyApp.userPreferences.savePass(binding.password.text.toString())
                        } else if (user != null && !binding.rememberMe.isChecked) {
                            MyApp.userPreferences.saveUser(user)
                            MyApp.userPreferences.saveRememberMeState(false)
                        }
                    }
//                    val intent = Intent(this, SongActivity::class.java)
//                    startActivity(intent)
                }
                Resource.Status.ERROR -> {
                    Toast.makeText(this, "Los datos introducidos no son correctos", Toast.LENGTH_LONG)
                        .show()
                }
                Resource.Status.LOADING -> {

                }
            }
        }

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