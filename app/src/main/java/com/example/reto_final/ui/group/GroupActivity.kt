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
import com.example.reto_final.data.repository.RemoteLoginUserDataSource
import com.example.reto_final.data.repository.local.group.GroupType
import com.example.reto_final.data.repository.local.group.RoomGroupDataSource
import com.example.reto_final.data.repository.local.user.RoomUserDataSource
import com.example.reto_final.databinding.GroupActivityBinding
import com.example.reto_final.ui.message.MessageActivity
import com.example.reto_final.ui.configuration.ChangePasswordActivity
import com.example.reto_final.ui.user.loginUser.LogInActivity
import com.example.reto_final.ui.configuration.PersonalConfigurationActivity
import com.example.reto_final.ui.user.loginUser.LoginUserViewModel
import com.example.reto_final.ui.user.loginUser.LoginUserViewModelFactory
import com.example.reto_final.ui.user.RoomUserViewModelFactory
import com.example.reto_final.ui.user.UserViewModel
import com.example.reto_final.utils.MyApp
import com.example.reto_final.utils.Resource

class GroupActivity: AppCompatActivity() {

    private lateinit var binding: GroupActivityBinding
    private lateinit var groupAdapter: GroupAdapter
    private val loginUserRepository = RemoteLoginUserDataSource()
    private val loginUserViewModel: LoginUserViewModel by viewModels { LoginUserViewModelFactory(loginUserRepository) }
    private val userRepository = RoomUserDataSource()
    private val userViewModel: UserViewModel by viewModels { RoomUserViewModelFactory(userRepository) }
    private val groupRepository = RoomGroupDataSource()
    private lateinit var group: Group
    private val groupViewModel: GroupViewModel by viewModels { RoomGroupViewModelFactory(groupRepository) }
    private val user = MyApp.userPreferences.getUser()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = GroupActivityBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbarPersonalConfiguration)

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

        groupViewModel.groupPermission.observe(this) {
            when(it.status) {
                Resource.Status.SUCCESS -> {
                    userViewModel.onUsersGroup(group.id)
                }
                Resource.Status.ERROR -> {
                    Toast.makeText(this, it.message, Toast.LENGTH_LONG).show()
                }
                Resource.Status.LOADING -> {
                }
            }
        }

        userViewModel.usersGroup.observe(this) {
            when(it.status) {
                Resource.Status.SUCCESS -> {
                    this.group.joinedUsers = it.data!!
                    goToChat()
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
                    groupViewModel.onCreate("prueba", GroupType.PRIVATE, 1)
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
                        loginUserViewModel.onLogOut()
                    }
                    backToLogIn()
                    true
                }
                else -> false // Manejo predeterminado para otros elementos
            }
        }
    }

    private fun onGroupListClickItem(group: Group) {
        this.group = group

        if (group.groupType == GroupType.PRIVATE) {

            if (user != null) {
                groupViewModel.onUserHasPermission(group.id,user.id)
            }
        }

    }

    private fun goToChat() {
        val intent = Intent(this, MessageActivity::class.java)
        intent.putExtra("grupo_seleccionado", this.group)
        startActivity(intent)
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
        menuInflater.inflate(R.menu.personal_configuration_top_menu,menu)

        binding.toolbarPersonalConfiguration.overflowIcon?.let {
            val color = ContextCompat.getColor(this, R.color.white)
            val newIcon = DrawableCompat.wrap(it)
            DrawableCompat.setTint(newIcon, color)
            binding.toolbarPersonalConfiguration.overflowIcon = newIcon
        }
        return true
    }

}