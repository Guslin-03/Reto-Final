package com.example.reto_final.ui.group

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.widget.CheckBox
import android.widget.EditText
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.DrawableCompat
import com.example.reto_final.R
import com.example.reto_final.data.model.Group
import com.example.reto_final.data.repository.RemoteLoginUserDataSource
import com.example.reto_final.data.repository.local.group.ChatEnumType
import com.example.reto_final.data.repository.local.group.RoomGroupDataSource
import com.example.reto_final.data.repository.local.user.RoomUserDataSource
import com.example.reto_final.data.repository.remote.RemoteGroupDataSource
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
    private val remoteGroupRepository = RemoteGroupDataSource()
    private lateinit var group: Group
    private val groupViewModel: GroupViewModel by viewModels { RoomGroupViewModelFactory(groupRepository, remoteGroupRepository) }
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

        groupViewModel.group.observe(this) {
            when(it.status) {
                Resource.Status.SUCCESS -> {
                    groupAdapter.submitList(it.data)
                }
                Resource.Status.ERROR -> {
                    Toast.makeText(this, it.message, Toast.LENGTH_LONG).show()
                    Log.d("Error", ""+it.message)
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

        groupViewModel.userHasAlreadyInGroup.observe(this) {
            when (it.status) {
                Resource.Status.SUCCESS -> {
                    goToChat()
                }
                Resource.Status.ERROR -> {
                    joinGroup()
                }
                Resource.Status.LOADING -> {
                }
            }
        }

        groupViewModel.addUserToGroup.observe(this) {
            when (it.status) {
                Resource.Status.SUCCESS -> {
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
                    popUpCreate()
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

        if (user != null) {
            if (group.type == ChatEnumType.PRIVATE.name) {
                groupViewModel.onUserHasPermission(group.id, user.id)
            } else {
                groupViewModel.onUserHasAlreadyInGroup(group.id, user.id)
            }
        }

    }

    private fun joinGroup() {
        if (user != null) {
            if (group.id != null) {
                Log.d("Prueba", "prueba")
                val options = arrayOf<CharSequence>("Aceptar", "Cancelar")
                val builder = AlertDialog.Builder(this)
                builder.setTitle("¿Quieres entrar al grupo?")
                builder.setItems(options) { dialog, which ->
                    when (which) {
                        0 -> groupViewModel.onAddUserToGroup(group.id!!, user.id)
                        1 -> dialog.dismiss()
                    }
                }
                builder.show()
            }

        }
    }

    private fun goToChat() {
        val intent = Intent(this, MessageActivity::class.java)
        intent.putExtra("grupo_seleccionado", this.group)
        startActivity(intent)
        finish()
    }

    private fun popUpCreate() {
        val builder = AlertDialog.Builder(this)
        val inflater = layoutInflater
        val dialogView = inflater.inflate(R.layout.custom_dialog_group, null)

        val editText = dialogView.findViewById<EditText>(R.id.editText)
        val checkBox = dialogView.findViewById<CheckBox>(R.id.checkBoxPrivate)

        builder.setView(dialogView)
        builder.setTitle("Elige una opción")

        builder.setPositiveButton("Aceptar") { _, _ ->
            val text = editText.text.toString()
            val isChecked = checkBox.isChecked

            if (isChecked) {
                MyApp.userPreferences.getUser()?.let { groupViewModel.onCreate(text, "PRIVATE", it.id) }
            } else{
                MyApp.userPreferences.getUser()?.let { groupViewModel.onCreate(text, "PUBLIC", it.id) }
            }

        }
        builder.setNegativeButton("Cancelar") { dialog, _ ->
            dialog.dismiss()
        }

        builder.show()
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