package com.example.reto_final.ui.configuration

import android.content.Intent
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.example.reto_final.R
import com.example.reto_final.data.model.InternetChecker
import com.example.reto_final.data.model.Module
import com.example.reto_final.data.model.user.LoginUser
import com.example.reto_final.data.repository.RemoteLoginUserDataSource
import com.example.reto_final.databinding.ConfigurationActvityBinding
import com.example.reto_final.ui.group.GroupActivity
import com.example.reto_final.ui.module.ModuleAdapter
import com.example.reto_final.ui.user.loginUser.LoginUserViewModel
import com.example.reto_final.ui.user.loginUser.LoginUserViewModelFactory
import com.example.reto_final.utils.MyApp
import com.example.reto_final.utils.Resource

class ConfigurationActivity : AppCompatActivity() {

    private lateinit var binding: ConfigurationActvityBinding
    private lateinit var moduleAdapter: ModuleAdapter
    private lateinit var nombresDeGrados: Array<String>
    private val userRepository = RemoteLoginUserDataSource()
    private val viewModel: LoginUserViewModel by viewModels { LoginUserViewModelFactory(userRepository) }
    private val user = MyApp.userPreferences.getUser()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ConfigurationActvityBinding.inflate(layoutInflater)
        setContentView(binding.root)
        moduleAdapter = ModuleAdapter()
        binding.moduleList.adapter = moduleAdapter
        binding.next.setText(R.string.confirmar)
        binding.textViewNumberPage.setText(R.string.paso_2_2)

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

            if (user != null &&  InternetChecker.isNetworkAvailable(applicationContext)) {
                viewModel.onUpdateProfile(
                    user.DNI,
                    user.name,
                    user.surname,
                    user.phone_number1,
                    user.phone_number2,
                    user.address,
                    "photo",
                    user.email
                )
            } else{
                Toast.makeText(this, "No puedes cambiar datos personales sin internet", Toast.LENGTH_LONG).show()
        }
            backToGroupActivity()
        }

        binding.back.setOnClickListener {
            backToPersonalConfiguration()
        }

        viewModel.updateProfile.observe(this) {
            when (it.status) {
                Resource.Status.SUCCESS -> {
                    backToGroupActivity()
                }
                Resource.Status.ERROR -> {
                    Toast.makeText(this, "No se ha podido actualizar el perfil", Toast.LENGTH_LONG).show()
                }
                Resource.Status.LOADING -> {
                }
            }
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

    private fun backToPersonalConfiguration() {
        //Pasariamos el Objeto User por parametro para settear los valores en los campos por defecto
        val intent = Intent(this, PersonalConfigurationActivity::class.java)
        startActivity(intent)
        finish()
    }

    private fun backToGroupActivity() {
        finish()
    }

}