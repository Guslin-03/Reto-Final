package com.example.reto_final.ui.group

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.DrawableCompat
import com.example.reto_final.R
import com.example.reto_final.data.repository.RemoteUserDataSource
import com.example.reto_final.data.repository.local.group.GroupType
import com.example.reto_final.data.repository.local.group.RoomGroupDataSource
import com.example.reto_final.databinding.GroupActivityBinding
import com.example.reto_final.ui.ChangePasswordActivity
import com.example.reto_final.ui.LogInActivity
import com.example.reto_final.ui.PersonalConfigurationActivity
import com.example.reto_final.ui.module.ModuleAdapter
import com.example.reto_final.ui.user.UserViewModel
import com.example.reto_final.ui.user.UserViewModelFactory
import com.example.reto_final.utils.MyApp
import com.example.reto_final.utils.Resource

class GroupActivity: AppCompatActivity() {

    private lateinit var binding: GroupActivityBinding
    private val moduleAdapter = ModuleAdapter()
    private val userRepository = RemoteUserDataSource()
    private val viewModel: UserViewModel by viewModels { UserViewModelFactory(userRepository) }
    private val groupRepository = RoomGroupDataSource()
    private val groupViewModel: GroupViewModel by viewModels { RoomGroupViewModelFactory(groupRepository) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = GroupActivityBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbarPersonalConfiguration)

        val user = MyApp.userPreferences.getUser()

        binding.crearGrupo.setOnClickListener {
            groupViewModel.onCreate("prueba", GroupType.PRIVATE)
        }

        groupViewModel.create.observe(this) {

            when(it.status) {

                Resource.Status.SUCCESS -> {
                    Log.i("Creación", "it.message")
                    Toast.makeText(this, "it.message", Toast.LENGTH_LONG).show()
                }

                Resource.Status.ERROR -> {
                    Toast.makeText(this, it.message, Toast.LENGTH_LONG).show()
                }

                Resource.Status.LOADING -> {
                }

            }

        }

        binding.toolbarPersonalConfiguration.setOnMenuItemClickListener { item ->
            when (item.itemId) {
                R.id.perfil -> {
                    showProfile()
                    true
                }
                R.id.change_password -> {
                    changePassword()
                    true
                }
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

    private fun backToLogIn() {
        val intent = Intent(this, LogInActivity::class.java)
        startActivity(intent)
        finish()
    }

    private fun showProfile() {
        val intent = Intent(this, PersonalConfigurationActivity::class.java)
        startActivity(intent)
        finish()
    }

    private fun changePassword() {
        val intent = Intent(this, ChangePasswordActivity::class.java)
        startActivity(intent)
        finish()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.top_menu,menu)

        binding.toolbarPersonalConfiguration.overflowIcon?.let {
            val color = ContextCompat.getColor(this, R.color.white)
            val newIcon = DrawableCompat.wrap(it)
            DrawableCompat.setTint(newIcon, color)
            binding.toolbarPersonalConfiguration.overflowIcon = newIcon
        }
        return true
    }

}