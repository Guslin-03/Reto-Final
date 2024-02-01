package com.example.reto_final.ui.group

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.Menu
import android.view.View
import android.widget.CheckBox
import android.widget.EditText
import android.widget.RadioButton
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.DrawableCompat
import com.example.reto_final.R
import com.example.reto_final.data.model.Group
import com.example.reto_final.data.model.InternetChecker
import com.example.reto_final.data.repository.RemoteLoginUserDataSource
import com.example.reto_final.data.repository.local.PopulateLocalDataBase
import com.example.reto_final.data.repository.local.PopulateLocalDataBaseFactory
import com.example.reto_final.data.repository.local.group.ChatEnumType
import com.example.reto_final.data.repository.local.group.RoomGroupDataSource
import com.example.reto_final.data.repository.local.message.RoomMessageDataSource
import com.example.reto_final.data.repository.local.role.RoomRoleDataSource
import com.example.reto_final.data.repository.local.user.RoomUserDataSource
import com.example.reto_final.data.repository.local.user.UserRoleType
import com.example.reto_final.data.repository.remote.RemoteGroupDataSource
import com.example.reto_final.data.repository.remote.RemoteMessageDataSource
import com.example.reto_final.data.repository.remote.RemoteRoleDataSource
import com.example.reto_final.data.repository.remote.RemoteUserDataSource
import com.example.reto_final.databinding.GroupActivityBinding
import com.example.reto_final.ui.message.MessageActivity
import com.example.reto_final.ui.configuration.ChangePasswordActivity
import com.example.reto_final.ui.user.loginUser.LogInActivity
import com.example.reto_final.ui.configuration.PersonalConfigurationActivity
import com.example.reto_final.ui.message.ChatsService
import com.example.reto_final.ui.user.loginUser.LoginUserViewModel
import com.example.reto_final.ui.user.loginUser.LoginUserViewModelFactory
import com.example.reto_final.ui.user.RoomUserViewModelFactory
import com.example.reto_final.ui.user.UserViewModel
import com.example.reto_final.utils.MyApp
import com.example.reto_final.utils.Resource
import java.io.File

class GroupActivity: AppCompatActivity() {

    private lateinit var binding: GroupActivityBinding
    private lateinit var groupAdapter: GroupAdapter
    private val loginUserRepository = RemoteLoginUserDataSource()
    private val loginUserViewModel: LoginUserViewModel by viewModels { LoginUserViewModelFactory(loginUserRepository,applicationContext) }
    private val userRepository = RoomUserDataSource()
    private val remoteUserRepository = RemoteUserDataSource()
    private val userViewModel: UserViewModel by viewModels { RoomUserViewModelFactory(userRepository,remoteUserRepository,applicationContext) }
    private val groupRepository = RoomGroupDataSource()
    private val remoteGroupRepository = RemoteGroupDataSource()
    private lateinit var group: Group
    private val groupViewModel: GroupViewModel by viewModels { RoomGroupViewModelFactory(groupRepository, remoteGroupRepository, applicationContext) }
    private val messageRepository = RoomMessageDataSource()
    private val remoteMessageRepository = RemoteMessageDataSource()
    private val localRoleRepository = RoomRoleDataSource()
    private val remoteRoleRepository = RemoteRoleDataSource()
    private val populateLocalDataBase: PopulateLocalDataBase by viewModels {
        PopulateLocalDataBaseFactory(
            groupRepository, remoteGroupRepository, messageRepository ,remoteMessageRepository ,userRepository, remoteUserRepository, localRoleRepository, remoteRoleRepository ) }
    private val loginUser = MyApp.userPreferences.getUser()
    private lateinit var radioButtonPrivate: RadioButton
    private lateinit var radioButtonPublic: RadioButton
    lateinit var context: Context

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = GroupActivityBinding.inflate(layoutInflater)
        setContentView(binding.root)
        if (!MyApp.userPreferences.getSaveDataBaseIsCreated()) {
            populateLocalDataBase.toInit()
            MyApp.userPreferences.saveDataBaseIsCreated(true)
        }

        groupViewModel.updateGroupList()
        radioButtonPrivate = findViewById(R.id.radioButtonFilterPrivate)
        radioButtonPublic = findViewById(R.id.radioButtonFilterPublic)
        setSupportActionBar(binding.toolbarPersonalConfiguration)

        groupAdapter = GroupAdapter(
            ::onGroupListClickItem
        )

        binding.groupList.adapter = groupAdapter

        populateLocalDataBase.finish.observe(this) {
            when(it.status) {
                Resource.Status.SUCCESS -> {
                    Log.d("p1", "hola")
                    groupViewModel.updateGroupList()
                }
                Resource.Status.ERROR -> {
                    Toast.makeText(this, it.message, Toast.LENGTH_LONG).show()
                }
                Resource.Status.LOADING -> {
                }

            }
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
                    Toast.makeText(this, "El nombre del grupo ya existe", Toast.LENGTH_LONG).show()
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
                    group.id?.let { it1 -> userViewModel.onUsersGroup(it1) }
                }
                Resource.Status.ERROR -> {
                    Toast.makeText(this, "No tienes permiso para acceder al grupo", Toast.LENGTH_LONG).show()
                }
                Resource.Status.LOADING -> {
                }
            }
        }

        userViewModel.usersGroup.observe(this) {
            when(it.status) {
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

        groupViewModel.joinGroup.observe(this) {
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
                R.id.filter -> {
                    filter()
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
                    if (loginUser != null) {
                        loginUserViewModel.onLogOut()
                    }
                    backToLogIn()
                    true
                }
                else -> false // Manejo predeterminado para otros elementos
            }
        }

        binding.radioGroupFilter.setOnCheckedChangeListener { _, checkedId ->
            var originalList = groupViewModel.group.value?.data
            if (originalList == null) originalList = emptyList()
            when (checkedId) {
                R.id.radioButtonFilterPrivate -> {
                    if(binding.editTextFilter.text.toString() == "") {
                        groupAdapter.filtrateTypeGroup(originalList, ChatEnumType.PRIVATE)
                    }else if(binding.editTextFilter.text.toString() != ""){
                        val filterList = filterByText(binding.editTextFilter.text.toString())
                        groupAdapter.filtrateTypeGroup(filterList, ChatEnumType.PRIVATE)
                    }

                }

                R.id.radioButtonFilterPublic -> {
                    if(binding.editTextFilter.text.toString() == "") {
                        groupAdapter.filtrateTypeGroup(originalList, ChatEnumType.PUBLIC)
                    }else if(binding.editTextFilter.text.toString() != ""){
                        val filterList = filterByText(binding.editTextFilter.text.toString())
                        groupAdapter.filtrateTypeGroup(filterList, ChatEnumType.PUBLIC)
                    }

                }

                R.id.radioButtonFilterAll -> {
                    if(binding.editTextFilter.text.toString() == "") {
                        groupAdapter.submitList(originalList)
                    }else if(binding.editTextFilter.text.toString() != ""){
                        filterByText(binding.editTextFilter.text.toString())
                    }

                }
            }
        }

        binding.editTextFilter.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                filterByText(s)
            }

            override fun afterTextChanged(s: Editable?) {}
        })

        binding.hideFilterMenu.setOnClickListener {
            filter()
        }

        startChatService(this)

    }

    private fun filterByText(s: CharSequence?): List<Group>{
        val searchText = s.toString().trim()
        var originalList = groupViewModel.group.value?.data
        if (originalList == null) originalList = emptyList()

        val filteredList : List<Group> = if (binding.radioButtonFilterPrivate.isChecked) {

            val filterByType = groupAdapter.filtrateTypeGroup(originalList, ChatEnumType.PRIVATE)
            filterByType.filter { group ->
                group.name.contains(searchText, ignoreCase = true)
            }

        }else if (binding.radioButtonFilterPublic.isChecked) {

            val filterByType = groupAdapter.filtrateTypeGroup(originalList, ChatEnumType.PUBLIC)
            filterByType.filter { group ->
                group.name.contains(searchText, ignoreCase = true)
            }

        }else {

            originalList.filter { group ->
                group.name.contains(searchText, ignoreCase = true)
            }

        }

        groupAdapter.submitList(filteredList)
        return filteredList
    }

    private fun onGroupListClickItem(group: Group) {
        this.group = group
        if (loginUser != null) {
            if (group.type == ChatEnumType.PRIVATE.name) {
                group.id?.let {
                    groupViewModel.onUserHasPermission(it)
                }
            } else {
                group.id?.let { groupViewModel.onUserHasAlreadyInGroup(it, loginUser.id) }
            }
        }

    }

    private fun joinGroup() {
        if (loginUser != null) {
            if (group.id != null) {
                val options = arrayOf<CharSequence>("Aceptar", "Cancelar")
                val builder = AlertDialog.Builder(this)
                builder.setTitle("¿Quieres entrar al grupo?")
                builder.setItems(options) { dialog, which ->
                    when (which) {
                        0 -> groupViewModel.onJoinGroup(group.id!!)
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
            if(!InternetChecker.isNetworkAvailable(applicationContext)){
                Toast.makeText(this, "Necesitas internet para crear un grupo", Toast.LENGTH_LONG).show()
            }
            else if (isChecked && userIsTeacher()) {
                MyApp.userPreferences.getUser()?.let { groupViewModel.onCreate(text, "PRIVATE", it.id) }
            } else if (isChecked) {
                Toast.makeText(this, "No tienes permisos para crear grupos privados", Toast.LENGTH_LONG).show()
            } else {
                MyApp.userPreferences.getUser()?.let { groupViewModel.onCreate(text, "PUBLIC", it.id) }
            }

        }
        builder.setNegativeButton("Cancelar") { dialog, _ ->
            dialog.dismiss()
        }

        builder.show()
    }

    private fun userIsTeacher() : Boolean {
        if (loginUser != null) {
            return loginUser.roles.any { it.type == UserRoleType.PROFESOR.toString() }
        }
        return false
    }

    private fun checkBd(){
        context=this
        val dbFile = context.getDatabasePath("chat-db")
      if (dbFile.exists() && !MyApp.userPreferences.getRememberMeState()){
            dbFile.delete()
            MyApp.userPreferences.saveDataBaseIsCreated(false)
        }
    }

    private fun backToLogIn() {
        checkBd()
        deleteFolder()
        MyApp.userPreferences.removeData()
        val intent = Intent(this, LogInActivity::class.java)
        startActivity(intent)
        finish()
    }
    private fun deleteFolder(){
        val folder = File(getExternalFilesDir(null), "RetoFinal")
        try {
            folder.deleteRecursively()
        } catch (e: SecurityException) {
            false
        }
    }

    private fun showProfile() {
        val intent = Intent(this, PersonalConfigurationActivity::class.java)
        startActivity(intent)
        finish()
    }

    private fun changePassword() {
        if (InternetChecker.isNetworkAvailable(applicationContext)){
            val intent = Intent(this, ChangePasswordActivity::class.java)
            startActivity(intent)
            finish()
        }else{
            Toast.makeText(this, "No puedes cambiar la contraseña sin internet", Toast.LENGTH_LONG).show()
        }

    }

    private fun filter(){
        if (binding.filterLayout.visibility == View.GONE) {
            // El elemento está oculto, mostrarlo con animación
            binding.filterLayout.visibility = View.VISIBLE
            binding.filterLayout.animate().translationY(0f)
        } else {
            // El elemento está visible, ocultarlo con animación
            binding.filterLayout.animate().translationY(-binding.filterLayout.height.toFloat()).withEndAction {
                binding.filterLayout.visibility = View.GONE
            }
        }
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

    private fun startChatService(context: Context) {
        val intent = Intent(context, ChatsService::class.java)
        ContextCompat.startForegroundService(context, intent)
    }

}
