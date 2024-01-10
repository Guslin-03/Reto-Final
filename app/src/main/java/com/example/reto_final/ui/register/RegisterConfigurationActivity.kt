package com.example.reto_final.ui.register

import android.content.Intent
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import androidx.appcompat.app.AppCompatActivity
import com.example.reto_final.R
import com.example.reto_final.data.Module
import com.example.reto_final.data.User
import com.example.reto_final.databinding.ConfigurationActvityBinding
import com.example.reto_final.ui.LogInActivity
import com.example.reto_final.ui.module.ModuleAdapter
import com.example.reto_final.utils.MyApp

class RegisterConfigurationActivity : AppCompatActivity() {

    private lateinit var binding: ConfigurationActvityBinding
    private lateinit var moduleAdapter: ModuleAdapter
    private lateinit var nombresDeGrados: Array<String>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ConfigurationActvityBinding.inflate(layoutInflater)
        setContentView(binding.root)
        moduleAdapter = ModuleAdapter()
        binding.moduleList.adapter = moduleAdapter

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

}
