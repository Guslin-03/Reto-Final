package com.example.reto_final.ui.register

import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.example.reto_final.R
import com.example.reto_final.data.model.InternetChecker
import com.example.reto_final.data.repository.RemoteLoginUserDataSource
import com.example.reto_final.databinding.ChangePasswordActivityBinding
import com.example.reto_final.ui.user.loginUser.LogInActivity
import com.example.reto_final.ui.user.loginUser.LoginUserViewModel
import com.example.reto_final.ui.user.loginUser.LoginUserViewModelFactory
import com.example.reto_final.utils.MyApp
import com.example.reto_final.utils.Resource

class RegisterChangePasswordActivity : AppCompatActivity() {

    private lateinit var binding: ChangePasswordActivityBinding
    private val userRepository = RemoteLoginUserDataSource()
    private val viewModel: LoginUserViewModel by viewModels { LoginUserViewModelFactory(userRepository) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ChangePasswordActivityBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val user = MyApp.userPreferences.getUser()

        binding.changePassword.setOnClickListener {

            if (user != null && InternetChecker.isNetworkAvailable(applicationContext)) {
                if (checkData())  {
                    viewModel.onRegister(
                        user.DNI,
                        user.name,
                        user.surname,
                        user.phone_number1,
                        user.phone_number2,
                        user.address,
                        "photo",
                        user.email,
                        binding.currentPassword.text.toString(),
                        binding.newPassword1.text.toString()
                    )
                }
            }else{
                Toast.makeText(this, R.string.toast_profile_error, Toast.LENGTH_LONG).show()
              backToLogIn()
            }
        }

        binding.back.setOnClickListener {
            backToConfigurationActivity()
        }

        viewModel.logOut.observe(this) {
            when (it.status) {
                Resource.Status.SUCCESS -> {
                    Toast.makeText(this, R.string.toast_success_changes, Toast.LENGTH_LONG).show()
                    backToLogIn()
                    MyApp.userPreferences.removeData()
                }
                Resource.Status.ERROR -> {
                    Toast.makeText(this, R.string.toast_error_generic, Toast.LENGTH_LONG).show()
                }
                Resource.Status.LOADING -> {
                }
            }
        }

        viewModel.register.observe(this) {
            when (it.status) {
                Resource.Status.SUCCESS -> {
                    viewModel.onLogOut()
                }
                Resource.Status.ERROR -> {
                    Toast.makeText(this, R.string.toast_error_generic, Toast.LENGTH_LONG).show()
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
                if (newPassword1 == MyApp.DEFAULT_PASS) {
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
        val intent = Intent(this, RegisterConfigurationActivity::class.java)
        startActivity(intent)
        finish()
    }

}