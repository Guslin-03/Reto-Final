package com.example.reto_final.ui.register

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.DrawableCompat
import com.example.reto_final.R
import com.example.reto_final.data.Module
import com.example.reto_final.data.User
import com.example.reto_final.data.repository.RemoteUserDataSource
import com.example.reto_final.databinding.ConfigurationActvityBinding
import com.example.reto_final.ui.LogInActivity
import com.example.reto_final.ui.module.ModuleAdapter
import com.example.reto_final.ui.user.UserViewModel
import com.example.reto_final.ui.user.UserViewModelFactory
import com.example.reto_final.utils.MyApp

class RegisterConfigurationActivity : AppCompatActivity() {

    private lateinit var binding: ConfigurationActvityBinding
    private lateinit var moduleAdapter: ModuleAdapter
    private lateinit var nombresDeGrados: Array<String>

    private val userRepository = RemoteUserDataSource()
    private val viewModel: UserViewModel by viewModels { UserViewModelFactory(userRepository) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ConfigurationActvityBinding.inflate(layoutInflater)
        setContentView(binding.root)
        moduleAdapter = ModuleAdapter()
        binding.courseList.adapter = moduleAdapter
        setSupportActionBar(binding.toolbarPersonalConfiguration)

        val user = MyApp.userPreferences.getUser()
        if(user != null) {
            setData(user)
            nombresDeGrados = user.degrees.map { it.name }.toTypedArray()
        }

        val autoCompleteTextView = findViewById<AutoCompleteTextView>(R.id.autoCompleteText)

        val adapterItems = ArrayAdapter(this, R.layout.item_degree, nombresDeGrados)

        autoCompleteTextView.setAdapter(adapterItems)

        autoCompleteTextView.setOnItemClickListener { parent, _, position, _ ->
            val selectedItem = parent.getItemAtPosition(position).toString()
            moduleAdapter.submitList(user?.let { obtenerModulesPorNombre(it, selectedItem) })
        }

        binding.next.setOnClickListener {
            changePassword()
        }

        binding.back.setOnClickListener {
            backToPersonalConfiguration()
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

    }

    private fun obtenerModulesPorNombre(user: User, nombreDegree: String): MutableList<Module> {
        val degreeEncontrado = user.degrees.find { it.name == nombreDegree }
        if (degreeEncontrado != null) {
            return degreeEncontrado.modules.toMutableList()
        }
        return emptyList<Module>().toMutableList()
    }

    private fun setData(user: User) {
        binding.email.setText(user.email)
        if (user.FCTDUAL == 1) binding.DUAL.isChecked = true
        binding.DUAL.isEnabled = false
    }

    private fun backToLogIn() {
        val intent = Intent(this, LogInActivity::class.java)
        startActivity(intent)
        finish()
    }

    private fun changePassword() {
        val intent = Intent(this, RegisterChangePasswordActivity::class.java)
        startActivity(intent)
        finish()
    }

    private fun backToPersonalConfiguration() {
        //Pasariamos el Objeto User por parametro para settear los valores en los campos por defecto
        val intent = Intent(this, RegisterPersonalConfigurationActivity::class.java)
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
