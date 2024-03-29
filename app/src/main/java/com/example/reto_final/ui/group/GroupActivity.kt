package com.example.reto_final.ui.group

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
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
import com.example.reto_final.data.model.group.Group
import com.example.reto_final.data.model.InternetChecker
import com.example.reto_final.data.repository.RemoteLoginUserDataSource
import com.example.reto_final.data.repository.local.group.ChatEnumType
import com.example.reto_final.data.repository.local.group.RoomGroupDataSource
import com.example.reto_final.data.repository.local.user.UserRoleType
import com.example.reto_final.data.repository.remote.RemoteGroupDataSource
import com.example.reto_final.databinding.GroupActivityBinding
import com.example.reto_final.ui.message.MessageActivity
import com.example.reto_final.ui.configuration.ChangePasswordActivity
import com.example.reto_final.ui.user.loginUser.LogInActivity
import com.example.reto_final.ui.configuration.PersonalConfigurationActivity
import com.example.reto_final.ui.message.ChatsService
import com.example.reto_final.ui.user.loginUser.LoginUserViewModel
import com.example.reto_final.ui.user.loginUser.LoginUserViewModelFactory
import com.example.reto_final.utils.MyApp
import com.example.reto_final.utils.Resource
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import java.io.File

class GroupActivity: AppCompatActivity() {

    private lateinit var binding: GroupActivityBinding
    private lateinit var groupAdapter: GroupAdapter
    private val loginUserRepository = RemoteLoginUserDataSource()
    private val loginUserViewModel: LoginUserViewModel by viewModels { LoginUserViewModelFactory(loginUserRepository) }
    private val groupRepository = RoomGroupDataSource()
    private val remoteGroupRepository = RemoteGroupDataSource()
    private lateinit var group: Group
    private val groupViewModel: GroupViewModel by viewModels { RoomGroupViewModelFactory(groupRepository, remoteGroupRepository, applicationContext) }
    private val loginUser = MyApp.userPreferences.getUser()
    private lateinit var radioButtonPrivate: RadioButton
    private lateinit var radioButtonPublic: RadioButton
    lateinit var context: Context

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = GroupActivityBinding.inflate(layoutInflater)
        setContentView(binding.root)
        radioButtonPrivate = findViewById(R.id.radioButtonFilterPrivate)
        radioButtonPublic = findViewById(R.id.radioButtonFilterPublic)
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
                    Toast.makeText(this, R.string.toast_error_generic, Toast.LENGTH_LONG).show()
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
                    Toast.makeText(this, R.string.toast_error_generic, Toast.LENGTH_LONG).show()
                }
                Resource.Status.LOADING -> {
                }
            }
        }

        groupViewModel.groupPermission.observe(this) {
            when(it.status) {
                Resource.Status.SUCCESS -> {
                    val groupPermission = it.data
                    if (groupPermission == 1) {
                        goToChat()
                    } else {
                        Toast.makeText(this, R.string.toast_no_permission_access, Toast.LENGTH_LONG).show()
                    }
                }
                Resource.Status.ERROR -> {
                    Toast.makeText(this, R.string.toast_error_generic, Toast.LENGTH_LONG).show()
                }
                Resource.Status.LOADING -> {
                }
            }
        }

        groupViewModel.userHasAlreadyInGroup.observe(this) {
            when (it.status) {
                Resource.Status.SUCCESS -> {
                    val groupPermission = it.data
                    if (groupPermission == 1) {
                        goToChat()
                    } else {
                        joinGroup()
                    }
                }
                Resource.Status.ERROR -> {
                    Toast.makeText(this, R.string.toast_error_generic, Toast.LENGTH_LONG).show()
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
                    Toast.makeText(this, R.string.toast_error_generic, Toast.LENGTH_LONG).show()
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

    override fun onResume() {
        super.onResume()
        groupViewModel.updateGroupList()
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
        if (loginUser != null && group.id != null) {
            if (group.type == ChatEnumType.PRIVATE.name) {
                groupViewModel.onUserHasPermission(group.id!!, loginUser.id)
            } else {
                groupViewModel.onUserHasAlreadyInGroup(group.id!!, loginUser.id)
            }
        }

    }
    private fun joinGroup() {
        if (loginUser != null) {
            if (group.id != null) {
                val options = arrayOf<CharSequence>(getString(R.string.accept), getString(R.string.cancel))
                val builder = AlertDialog.Builder(this)
                builder.setTitle(R.string.enter_group)
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
        builder.setTitle(R.string.choose)

        builder.setPositiveButton(R.string.accept) { _, _ ->
            val text = editText.text.toString()
            val isChecked = checkBox.isChecked
            if(!InternetChecker.isNetworkAvailable(applicationContext)){
                Toast.makeText(this, R.string.toast_no_internet, Toast.LENGTH_LONG).show()
            }
            else if (isChecked && userIsTeacher()) {
                MyApp.userPreferences.getUser()?.let { groupViewModel.onCreate(text, "PRIVATE", it.id) }
            } else if (isChecked) {
                Toast.makeText(this, R.string.toast_no_permission_create, Toast.LENGTH_LONG).show()
            } else {
                MyApp.userPreferences.getUser()?.let { groupViewModel.onCreate(text, "PUBLIC", it.id) }
            }

        }
        builder.setNegativeButton(R.string.cancel) { dialog, _ ->
            dialog.dismiss()
        }

        builder.show()
    }

    private fun userIsTeacher() : Boolean {
        if (loginUser != null) {
            return loginUser.roles.any { it.name == UserRoleType.Profesor.toString() }
        }
        return false
    }

    private fun checkBd(){
        context = this
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
        MyApp.userPreferences.removePicture()
        val intent = Intent(this, LogInActivity::class.java)
        startActivity(intent)
        finish()
    }
    private fun deleteFolder(){
        val folder = File(getExternalFilesDir(null), "RetoFinalImage")
        val folder2 = File(getExternalFilesDir(null), "RetoFinalPdf")
        try {
            folder.deleteRecursively()
            folder2.deleteRecursively()
        } catch (e: SecurityException) {
            Toast.makeText(this, R.string.toast_delete_data, Toast.LENGTH_LONG).show()
        }
    }

    private fun showProfile() {
        val intent = Intent(this, PersonalConfigurationActivity::class.java)
        startActivity(intent)
    }

    private fun changePassword() {
        if (InternetChecker.isNetworkAvailable(applicationContext)){
            val intent = Intent(this, ChangePasswordActivity::class.java)
            startActivity(intent)
        }else{
            Toast.makeText(this, R.string.toast_no_internet, Toast.LENGTH_LONG).show()
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

    override fun onStart() {
        super.onStart()
        EventBus.getDefault().register(this)
    }

    override fun onStop() {
        super.onStop()
        EventBus.getDefault().unregister(this)
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onNewGroupEvent(group: Group) {
        groupViewModel.updateGroupList()
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onGroupEvent(listPendingGroup: List<Group>) {
        groupViewModel.updateGroupList()
    }

}
