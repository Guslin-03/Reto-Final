package com.example.reto_final.ui.user.loginUser

import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Patterns
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatCheckBox
import androidx.core.content.ContextCompat
import com.example.reto_final.R
import com.example.reto_final.data.model.InternetChecker
import com.example.reto_final.data.repository.RemoteLoginUserDataSource
import com.example.reto_final.databinding.LoginActivityBinding
import com.example.reto_final.ui.group.GroupActivity
import com.example.reto_final.ui.register.RegisterPersonalConfigurationActivity
import com.example.reto_final.utils.MyApp
import com.example.reto_final.utils.Resource
import java.util.Locale
import java.util.regex.Pattern

class LogInActivity : AppCompatActivity(){

    private lateinit var binding: LoginActivityBinding
    private val userRepository = RemoteLoginUserDataSource()
    private val viewModel: LoginUserViewModel by viewModels { LoginUserViewModelFactory(userRepository) }
    private lateinit var rememberMeCheckBox: AppCompatCheckBox
    private lateinit var email:String
    private lateinit var loginButton: Button
    private val handler = Handler(Looper.getMainLooper())
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = LoginActivityBinding.inflate(layoutInflater)
        setContentView(binding.root)

        loginButton = binding.login
        loginButton.isEnabled=false

        handler.postDelayed({
            loginButton.isEnabled=true
        }, 2000)

        previousLoginState()

        binding.forgot.setOnClickListener {
            popUpCreate()
        }
        binding.login.setOnClickListener {
            tryToLogin()
        }

        viewModel.loginUser.observe(this) {
            loginButton.isEnabled=false
            when (it.status) {
                Resource.Status.SUCCESS -> {
                    val userResource = viewModel.loginUser.value
                    if (userResource != null && userResource.status == Resource.Status.SUCCESS) {
                        val user = userResource.data
                        if (user != null && binding.rememberMe.isChecked) {
                            MyApp.userPreferences.savePass(binding.password.text.toString())
                        }
                        if (user != null) {
                            MyApp.userPreferences.saveAuthToken(user.token)
                            MyApp.userPreferences.saveUser(user)
                            MyApp.userPreferences.saveRememberMeState(binding.rememberMe.isChecked)
                        }
                        redirectAfterLogin()
                    }
                }
                Resource.Status.ERROR -> {
                    Toast.makeText(this, "Los datos introducidos no pertenecen a un usuario del centro", Toast.LENGTH_LONG).show()
                    loginButton.isEnabled=true
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
                            loginButton.isEnabled=true
                        }
                    }
                }
                Resource.Status.ERROR -> {
                    Toast.makeText(this, it.message, Toast.LENGTH_LONG).show()
                    loginButton.isEnabled=true
                }
                Resource.Status.LOADING -> {

                }
            }
        }

        viewModel.email.observe(this) {
            when (it.status) {
                Resource.Status.SUCCESS -> {
                    if(it.data==1){
                        viewModel.onResetPassword(email)
                    }
                }
                Resource.Status.ERROR -> {
                    Toast.makeText(this, "Ha habido algun error procesando la solicitud", Toast.LENGTH_LONG).show()
                }
                Resource.Status.LOADING -> {
                }
            }
        }

        viewModel.reset.observe(this) {
            when (it.status) {
                Resource.Status.SUCCESS -> {
                    Toast.makeText(this, "Si el correo introducido es correcto, recibirás una nueva contraseña", Toast.LENGTH_LONG).show()
                }
                Resource.Status.ERROR -> {
                    Toast.makeText(this, it.message, Toast.LENGTH_LONG).show()
                }
                Resource.Status.LOADING -> {
                }
            }
        }
    }
    private fun popUpCreate() {
        val builder = AlertDialog.Builder(this)
        val inflater = layoutInflater
        val dialogView = inflater.inflate(R.layout.custom_dialog_forgot, null)

        builder.setView(dialogView)
        builder.setTitle("Introduce tu correo electrónico")
        val editText = dialogView.findViewById<EditText>(R.id.editText)

        builder.setPositiveButton("Aceptar") { _, _ ->
            email = editText.text.toString()
           sendEmail(email)
        }

        builder.setNegativeButton("Cancelar") { dialog, _ ->
            dialog.dismiss()
        }

        builder.show()
    }
    private fun sendEmail(email:String){
        if (InternetChecker.isNetworkAvailable(applicationContext)) {
            viewModel.onFindByMail(email)
        } else {
            Toast.makeText(this, "No se puede resetear la contraseña sin conexión", Toast.LENGTH_LONG).show()
        }
    }
    private fun tryToLogin(){
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
        }
    }
    private fun redirectAfterLogin(){
        if (binding.password.text.toString() == MyApp.DEFAULT_PASS) {
            logIn()
            Toast.makeText(this, R.string.toast_edit_profile, Toast.LENGTH_LONG).show()
        } else {
            chat()
        }
    }
    private fun previousLoginState(){
        rememberMeCheckBox= binding.rememberMe
        rememberMeCheckBox.buttonTintList = ColorStateList.valueOf(ContextCompat.getColor(this, R.color.blue_green))
        //Pone el checkbox true o false segun lo guardado
        rememberMeCheckBox.isChecked= MyApp.userPreferences.getRememberMeState()
        //Pone pass y user si hay uno guardado
        if(rememberMeCheckBox.isChecked){
            binding.email.setText(MyApp.userPreferences.getUser()!!.email)
            binding.password.setText(MyApp.userPreferences.getPass())
            tryToLogin()
        }else {
            MyApp.userPreferences.removeData()
        }
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

}