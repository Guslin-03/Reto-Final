package com.example.reto_final.ui.user.loginUser

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
import com.example.reto_final.data.model.LoginUser
import com.example.reto_final.data.model.Role
import com.example.reto_final.data.repository.RemoteLoginUserDataSource
import com.example.reto_final.data.repository.local.user.UserRoleType
import com.example.reto_final.databinding.LoginActivityBinding
import com.example.reto_final.ui.group.GroupActivity
import com.example.reto_final.ui.register.RegisterChangePasswordActivity
import com.example.reto_final.ui.register.RegisterPersonalConfigurationActivity
import com.example.reto_final.utils.MyApp
import com.example.reto_final.utils.Resource
import java.util.Locale
import java.util.regex.Pattern

class LogInActivity : AppCompatActivity(){

    private lateinit var binding: LoginActivityBinding
    private val userRepository = RemoteLoginUserDataSource()
    private val viewModel: LoginUserViewModel by viewModels { LoginUserViewModelFactory(userRepository,applicationContext) }
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
            var email = binding.email.text.toString()
            email = lowerCaseEmail(email)
            val password = binding.password.text.toString()
            if(checkData()) {
                if (InternetChecker.isNetworkAvailable(applicationContext)) {
                    viewModel.onLogIn(email, password)
                }else {
                    Toast.makeText(this, "No se puede hacer login sin internet", Toast.LENGTH_LONG)
                        .show()
                }
//            mockData()
//            logIn()
            }
//            mockData()
        }

        binding.changePassword.setOnClickListener {
            changePassword()
        }

        viewModel.loginUser.observe(this) {
            when (it.status) {
                Resource.Status.SUCCESS -> {
                    val userResource = viewModel.loginUser.value
                    if (userResource != null && userResource.status == Resource.Status.SUCCESS) {
                        var user = userResource.data
                        Log.d("Prueba", "Primer Login da error"+user)
                        if (user != null && binding.rememberMe.isChecked) {
                            MyApp.userPreferences.saveUser(user)
                            MyApp.userPreferences.saveRememberMeState(binding.rememberMe.isChecked)
                            MyApp.userPreferences.savePass(binding.password.text.toString())
                        } else if (user != null && !binding.rememberMe.isChecked && MyApp.userPreferences.fetchHibernateToken()!=null) {
                            user.accessToken= MyApp.userPreferences.fetchHibernateToken()!!
                            MyApp.userPreferences.saveUser(user)
                            MyApp.userPreferences.saveRememberMeState(false)
                        }
                        if (user != null) {
                            MyApp.userPreferences.saveAuthToken(user.token)
                        }
                        if (binding.password.text.toString() == "elorrieta00") {
                            logIn()
                            Toast.makeText(this, R.string.toast_edit_profile, Toast.LENGTH_LONG).show()
                        }else {
                            chat()
                        }
                    }
                }
                Resource.Status.ERROR -> {
                    Toast.makeText(this, "El servidor de Laravel no está encendido", Toast.LENGTH_LONG).show()
                }
                Resource.Status.LOADING -> {
                }
            }
        }
        viewModel.secondLogin.observe(this) {
            when (it.status) {
                Resource.Status.SUCCESS -> {
                    val userResource = viewModel.secondLogin.value
                    if (userResource != null && userResource.status == Resource.Status.SUCCESS) {
                        val user = userResource.data
                        if (user != null) {
                            MyApp.userPreferences.saveHibernateToken(user.accessToken)
                        }
                    }

                }
                Resource.Status.ERROR -> {
                    Toast.makeText(this, "El servidor no está encendido", Toast.LENGTH_LONG).show()
                }
                Resource.Status.LOADING -> {

                }
            }
        }

    }

    private fun mockData() {

        val listRoles = arrayOf(Role(1, UserRoleType.PROFESOR.toString()))
        val loginUser = LoginUser(
            1,
            "78771912V",
            "David",
            "Comeron",
            601269008,
            601269008,
            "address",
            "photo",
            1,
            "davidcomeron@elorrieta.com",
            emptyArray(),
            listRoles,
            1,
            "",
            "")
        MyApp.userPreferences.saveUser(loginUser)
        chat()

    }

    private fun checkData(): Boolean {
        val email = binding.email.text.toString()
        val password = binding.password.text.toString()

        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, R.string.toast_empty_1, Toast.LENGTH_LONG).show()
            binding.email.setHintTextColor(Color.RED)
            binding.password.setHintTextColor(Color.RED)
            return false
        }
        if (!validarEmail(email)) {
            Toast.makeText(this, R.string.toast_format_email, Toast.LENGTH_LONG).show()
            binding.email.setTextColor(Color.RED)
            binding.password.setTextColor(Color.BLACK)

            binding.email.setHintTextColor(Color.BLACK)
            binding.password.setHintTextColor(Color.BLACK)
            return false
        }

        if (password.length < 8) {
            Toast.makeText(this, R.string.toast_password_lenght, Toast.LENGTH_LONG).show()
            binding.email.setTextColor(Color.BLACK)
            binding.password.setTextColor(Color.RED)

            binding.email.setHintTextColor(Color.BLACK)
            binding.password.setHintTextColor(Color.BLACK)
            return false
        }

        return true

    }

    private fun validarEmail(email: String): Boolean {
        val pattern: Pattern = Patterns.EMAIL_ADDRESS
        return pattern.matcher(email).matches()
    }
    private fun lowerCaseEmail(input: String): String {
        return input.lowercase(Locale.ROOT)
    }

    private fun logIn() {
        val intent = Intent(this, RegisterPersonalConfigurationActivity::class.java)
        startActivity(intent)
        finish()
    }

    private fun chat() {
        val intent = Intent(this, GroupActivity::class.java)
        startActivity(intent)
        finish()
    }

    private fun changePassword() {
        val intent = Intent(this, RegisterChangePasswordActivity::class.java)
        startActivity(intent)
        finish()
    }

}