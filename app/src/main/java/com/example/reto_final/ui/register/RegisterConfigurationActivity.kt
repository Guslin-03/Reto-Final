package com.example.reto_final.ui.register

import android.content.Intent
import android.os.Bundle
import android.widget.ArrayAdapter
import androidx.appcompat.app.AppCompatActivity
import com.example.reto_final.R
import com.example.reto_final.data.model.Module
import com.example.reto_final.data.model.LoginUser
import com.example.reto_final.databinding.ConfigurationActvityBinding
import com.example.reto_final.ui.module.ModuleAdapter
import com.example.reto_final.utils.MyApp

class RegisterConfigurationActivity : AppCompatActivity() {

    private lateinit var binding: ConfigurationActvityBinding
    private lateinit var moduleAdapter: ModuleAdapter
    private lateinit var nombresDeGrados: Array<String>
    private val user = MyApp.userPreferences.getUser()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ConfigurationActvityBinding.inflate(layoutInflater)
        setContentView(binding.root)
        moduleAdapter = ModuleAdapter()
        binding.moduleList.adapter = moduleAdapter

        if(user != null) {
            setData(user)
            nombresDeGrados = user.degrees.map { it.name }.toTypedArray()
        }

        val adapterItems = ArrayAdapter(this, R.layout.item_degree, nombresDeGrados)

        binding.autoCompleteText.setAdapter(adapterItems)
        setDefaultData()

        binding.autoCompleteText.setOnItemClickListener { parent, _, position, _ ->
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

    private fun setDefaultData() {

        if (nombresDeGrados.isNotEmpty()) {
            val primeraOpcion = nombresDeGrados[0]
            binding.autoCompleteText.setText(primeraOpcion, false)
            binding.autoCompleteText.setSelection(0)

            moduleAdapter.submitList(user?.let { obtenerModulesPorNombre(it, primeraOpcion) })

        }

    }

    private fun obtenerModulesPorNombre(loginUser: LoginUser, nombreDegree: String): MutableList<Module> {
        val degreeEncontrado = loginUser.degrees.find { it.name == nombreDegree }
        if (degreeEncontrado != null) {
            return degreeEncontrado.modules.toMutableList()
        }
        return emptyList<Module>().toMutableList()
    }

    private fun setData(loginUser: LoginUser) {
        binding.email.setText(loginUser.email)
        if (loginUser.FCTDUAL == 1) binding.DUAL.isChecked = true
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
