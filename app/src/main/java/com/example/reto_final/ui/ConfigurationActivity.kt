package com.example.reto_final.ui

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.example.reto_final.R
import com.example.reto_final.data.Module
import com.example.reto_final.data.User
import com.example.reto_final.data.repository.RemoteUserDataSource
import com.example.reto_final.databinding.ConfigurationActvityBinding
import com.example.reto_final.ui.module.ModuleAdapter
import com.example.reto_final.ui.user.UserViewModel
import com.example.reto_final.ui.user.UserViewModelFactory
import com.example.reto_final.utils.MyApp
import com.example.reto_final.utils.Resource

class ConfigurationActivity : AppCompatActivity() {

    private lateinit var binding: ConfigurationActvityBinding
    private lateinit var moduleAdapter: ModuleAdapter
    private lateinit var nombresDeGrados: Array<String>
    private val userRepository = RemoteUserDataSource()
    private val viewModel: UserViewModel by viewModels { UserViewModelFactory(userRepository) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ConfigurationActvityBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.next.setText(R.string.confirmar)
        binding.textViewNumberPage.setText(R.string.paso_2_2)
        val user = MyApp.userPreferences.getUser()
        if(user != null) {
            setData(user)
            nombresDeGrados = user.degrees.map { it.name }.toTypedArray()
        }

        val autoCompleteTextView = findViewById<AutoCompleteTextView>(R.id.autoCompleteText)

        val adapterItems = ArrayAdapter(this, R.layout.item_degree, nombresDeGrados)

        autoCompleteTextView.setAdapter(adapterItems)
        if (nombresDeGrados.isNotEmpty()) {
            val primeraOpcion = nombresDeGrados[0]
            autoCompleteTextView.setText(primeraOpcion, false)
            autoCompleteTextView.setSelection(0)
        }
        autoCompleteTextView.setOnItemClickListener { parent, _, position, _ ->
            val selectedItem = parent.getItemAtPosition(position).toString()
            moduleAdapter.submitList(user?.let { obtenerModulesPorNombre(it, selectedItem) })
        }

        binding.next.setOnClickListener {

            if (user != null) {
                Log.i("Prueba", ""+user.name)
                viewModel.onUpdateProfile(
                    user.DNI,
                    user.name,
                    user.surname,
                    user.phoneNumber1,
                    user.phoneNumber2,
                    user.address,
                    "photo",
                    user.email
                )
            }
//            backToGroupActivity()
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
                    Toast.makeText(this, it.message, Toast.LENGTH_LONG).show()
                }
                Resource.Status.LOADING -> {
                }
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

    private fun backToPersonalConfiguration() {
        //Pasariamos el Objeto User por parametro para settear los valores en los campos por defecto
        val intent = Intent(this, PersonalConfigurationActivity::class.java)
        startActivity(intent)
        finish()
    }

    private fun backToGroupActivity() {
        val intent = Intent(this, GroupActivity::class.java)
        startActivity(intent)
        finish()
    }

}