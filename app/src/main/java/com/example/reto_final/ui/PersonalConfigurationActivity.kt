package com.example.reto_final.ui

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.Menu
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.DrawableCompat
import com.example.reto_final.R
import com.example.reto_final.databinding.PersonalConfigurationActvityBinding

class PersonalConfigurationActivity : AppCompatActivity() {

    private lateinit var binding: PersonalConfigurationActvityBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = PersonalConfigurationActvityBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbarPersonalConfiguration)

        binding.next.setOnClickListener {
            //if (checkData()) nextConfiguration()
            nextConfiguration()
        }

        binding.back.setOnClickListener {
            backToLogIn()
        }

        binding.toolbarPersonalConfiguration.setOnMenuItemClickListener { item ->
            when (item.itemId) {
                R.id.closeSesion -> {
                    backToLogIn()
                    true
                }
                else -> false // Manejo predeterminado para otros elementos
            }
        }

    }

    private fun checkData(): Boolean {
        val hintColor = ContextCompat.getColor(this, R.color.hint)
        val name = binding.name.text.toString()
        val surname = binding.surname.text.toString()
        val address = binding.address.text.toString()
        val mobilePhoneNumber = binding.mobilePhoneNumber.text.toString()
        val phoneNumber = binding.phoneNumber.text.toString()
        val dni = binding.dni.text.toString()

        if (name.isEmpty() || surname.isEmpty() || address.isEmpty() || mobilePhoneNumber.isEmpty() || dni.isEmpty()) {
            Toast.makeText(this, "Hay campos obligatorios sin rellenar", Toast.LENGTH_LONG).show()
            binding.name.setHintTextColor(Color.RED)
            binding.surname.setHintTextColor(Color.RED)
            binding.address.setHintTextColor(Color.RED)
            binding.mobilePhoneNumber.setHintTextColor(Color.RED)
            binding.dni.setHintTextColor(Color.RED)

            return false
        }

        if (!validatePhone(mobilePhoneNumber)) {
            Toast.makeText(this, "El formato del teléfono móvil es erróneo", Toast.LENGTH_LONG).show()
            binding.mobilePhoneNumber.setTextColor(Color.RED)

            binding.name.setHintTextColor(hintColor)
            binding.surname.setHintTextColor(hintColor)
            binding.address.setHintTextColor(hintColor)
            binding.phoneNumber.setHintTextColor(hintColor)
            binding.dni.setHintTextColor(hintColor)

            return false

        }
        if (phoneNumber != "" ) {
            if(!validatePhone(phoneNumber)) {
                Toast.makeText(this, "El formato del teléfono fijo es erróneo", Toast.LENGTH_LONG).show()
                binding.phoneNumber.setTextColor(Color.RED)

                binding.name.setHintTextColor(hintColor)
                binding.surname.setHintTextColor(hintColor)
                binding.address.setHintTextColor(hintColor)
                binding.mobilePhoneNumber.setHintTextColor(hintColor)
                binding.dni.setHintTextColor(hintColor)

                return false
            }
        }

        if (!validateDNI(dni)) {

            Toast.makeText(this, "El DNI debe tener el formato 00000000A", Toast.LENGTH_LONG).show()
            binding.dni.setTextColor(Color.RED)

            binding.name.setHintTextColor(hintColor)
            binding.surname.setHintTextColor(hintColor)
            binding.address.setHintTextColor(hintColor)
            binding.mobilePhoneNumber.setHintTextColor(hintColor)
            binding.phoneNumber.setHintTextColor(hintColor)
            return false
        }

        return true
    }

    private fun nextConfiguration() {
        val intent = Intent(this, ConfigurationActivity::class.java)
        startActivity(intent)
        finish()
    }

    private fun backToLogIn() {
        val intent = Intent(this, LogInActivity::class.java)
        startActivity(intent)
        finish()
    }

    private fun validatePhone(cadena: String): Boolean {
        val regex = Regex("\\d{9}")
        return cadena.matches(regex)
    }

    private fun validateDNI(cadena: String): Boolean {
        val regex = Regex("\\d{8}[A-Z]")
        return cadena.matches(regex)
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