package com.example.reto_final.ui.group

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.DrawableCompat
import com.example.reto_final.R
import com.example.reto_final.data.model.Group
import com.example.reto_final.data.repository.RemoteUserDataSource
import com.example.reto_final.data.repository.local.group.GroupType
import com.example.reto_final.data.repository.local.group.RoomGroupDataSource
import com.example.reto_final.databinding.GroupActivityBinding
import com.example.reto_final.ui.message.MessageActivity
import com.example.reto_final.ui.user.ChangePasswordActivity
import com.example.reto_final.ui.user.LogInActivity
import com.example.reto_final.ui.user.PersonalConfigurationActivity
import com.example.reto_final.ui.user.UserViewModel
import com.example.reto_final.ui.user.UserViewModelFactory
import com.example.reto_final.utils.MyApp
import com.example.reto_final.utils.Resource

class GroupActivity: AppCompatActivity() {

    private lateinit var binding: GroupActivityBinding
    private lateinit var groupAdapter: GroupAdapter
    private val userRepository = RemoteUserDataSource()
    private val viewModel: UserViewModel by viewModels { UserViewModelFactory(userRepository) }
    private val groupRepository = RoomGroupDataSource()
    private lateinit var group: Group
    private val groupViewModel: GroupViewModel by viewModels { RoomGroupViewModelFactory(groupRepository) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = GroupActivityBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbarPersonalConfiguration)

        val user = MyApp.userPreferences.getUser()

        fun onGroupListClickItem(group: Group) {
            this.group = group

            val intent = Intent(this, MessageActivity::class.java)
            intent.putExtra("grupo_seleccionado", this.group)
            startActivity(intent)

        }

        groupAdapter = GroupAdapter(
            ::onGroupListClickItem
        )

        binding.groupList.adapter = groupAdapter

        binding.eliminarGrupo.setOnClickListener {
            goToChat()
//            if (group.id != null) {
//                groupViewModel.onDelete(group)
//            } else {
//                Toast.makeText(
//                    this, "Debe seleccionar un grupo a eliminar", Toast.LENGTH_LONG
//                ).show()
//            }
        }

        groupViewModel.group.observe(this) {
            when(it.status) {
                Resource.Status.SUCCESS -> {
                    groupAdapter.submitList(it.data)
                }
                Resource.Status.ERROR -> {
                    Toast.makeText(this, it.message, Toast.LENGTH_LONG).show()
                }
                Resource.Status.LOADING -> {
                }

            }
        }

        groupViewModel.create.observe(this) {
            when(it.status) {
                Resource.Status.SUCCESS -> {
                    groupViewModel.updateGroupList()
                }
                Resource.Status.ERROR -> {
                    Toast.makeText(this, it.message, Toast.LENGTH_LONG).show()
                }
                Resource.Status.LOADING -> {
                }

            }
        }

        groupViewModel.delete.observe(this) {
            when(it.status) {
                Resource.Status.SUCCESS -> {
                    groupViewModel.updateGroupList()
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
                R.id.createGroup -> {
                    groupViewModel.onCreate("prueba", GroupType.PRIVATE)
                    true
                }
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

    private fun goToChat() {
        val intent = Intent(this, MessageActivity::class.java)
        startActivity(intent)
        finish()
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