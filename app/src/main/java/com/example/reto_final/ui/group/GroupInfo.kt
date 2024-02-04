package com.example.reto_final.ui.group

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.example.reto_final.data.model.group.Group
import com.example.reto_final.data.model.InternetChecker
import com.example.reto_final.data.model.user.User
import com.example.reto_final.data.repository.local.group.RoomGroupDataSource
import com.example.reto_final.data.repository.local.user.RoomUserDataSource
import com.example.reto_final.data.repository.remote.RemoteGroupDataSource
import com.example.reto_final.data.repository.remote.RemoteUserDataSource
import com.example.reto_final.databinding.GroupInfoActivityBinding
import com.example.reto_final.ui.user.RoomUserViewModelFactory
import com.example.reto_final.ui.user.UserAdapter
import com.example.reto_final.ui.user.UserFragment
import com.example.reto_final.ui.user.UserViewModel
import com.example.reto_final.utils.MyApp
import com.example.reto_final.utils.Resource

class GroupInfo: AppCompatActivity() {

    private lateinit var binding: GroupInfoActivityBinding
    private lateinit var userAdapter: UserAdapter
    private val userRepository = RoomUserDataSource()
    private val remoteUserRepository = RemoteUserDataSource()
    private val userViewModel: UserViewModel by viewModels { RoomUserViewModelFactory(userRepository,remoteUserRepository,applicationContext) }
    private val groupRepository = RoomGroupDataSource()
    private val remoteGroupRepository = RemoteGroupDataSource()
    private val groupViewModel: GroupViewModel by viewModels { RoomGroupViewModelFactory(groupRepository, remoteGroupRepository, applicationContext) }
    private val loginUser = MyApp.userPreferences.getUser()
    private lateinit var userFragment : UserFragment
    private var selectedGroup = Group()
    private lateinit var selectedUser : User
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = GroupInfoActivityBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setDefaultData()
        setDefaultView()

        userFragment = UserFragment(selectedGroup)
        userAdapter = UserAdapter(
            ::onIsAdmin
        )
        binding.userList.adapter = userAdapter

        binding.addUser.setOnClickListener {
            val fragmentManager = supportFragmentManager
            userFragment.show(fragmentManager, "user_fragment_dialog")
        }

        userFragment.setOnDismissListener {
            userFragment = UserFragment(selectedGroup)
            if (selectedGroup.id != null) userViewModel.onUsersGroup(selectedGroup.id!!)
        }

        userViewModel.usersGroup.observe(this) {
            when(it.status) {
                Resource.Status.SUCCESS -> {
                    setTotalUsersInGroup(it.data?.size.toString())
                    userAdapter.submitList(it.data)
                }
                Resource.Status.ERROR -> {
                    Toast.makeText(this, it.message, Toast.LENGTH_LONG).show()
                }
                Resource.Status.LOADING -> {
                }

            }
        }

        userViewModel.delete.observe(this) {
            when(it.status) {
                Resource.Status.SUCCESS -> {
                    selectedGroup.id?.let { it1 -> userViewModel.onUsersGroup(it1) }
                }
                Resource.Status.ERROR -> {
                    Toast.makeText(this, it.message, Toast.LENGTH_LONG).show()
                }
                Resource.Status.LOADING -> {
                }

            }
        }
        groupViewModel.throwOutFromChat.observe(this) {
            when(it.status) {
                Resource.Status.SUCCESS -> {
                    if (it.data?.chatId != null && it.data?.userId != null) {
                        userViewModel.onDelete(it.data.userId, it.data.chatId)
                    }
                }
                Resource.Status.ERROR -> {
                    Toast.makeText(this, it.message, Toast.LENGTH_LONG).show()
                }
                Resource.Status.LOADING -> {
                }

            }
        }
        groupViewModel.leaveGroup.observe(this) {
            when(it.status) {
                Resource.Status.SUCCESS -> {
                    if (it.data?.chatId != null && it.data?.userId != null) {
                        userViewModel.onDelete(it.data.userId, it.data.chatId)
                    }
                }
                Resource.Status.ERROR -> {
                    Toast.makeText(this, it.message, Toast.LENGTH_LONG).show()
                }
                Resource.Status.LOADING -> {
                }

            }
        }
    }
    private fun onIsAdmin(user: User) {
        selectedUser = user
        if (loginUser != null && selectedGroup.id != null) {
            if (loginUser.id == selectedGroup.adminId && InternetChecker.isNetworkAvailable(applicationContext)) {
                checkActionDelete()
            }else{
                Toast.makeText(this, "No puedes eliminar a usuarios sin internet", Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun checkActionDelete() {
        if (selectedUser.id != null && selectedGroup.id != null) {
            val options = arrayOf<CharSequence>("Aceptar", "Cancelar")
            val builder = AlertDialog.Builder(this)
            builder.setTitle("¿Seguro que quieres expulsar a ${selectedUser.name} ${selectedUser.surname} del grupo?")
            builder.setItems(options) { dialog, which ->
                when (which) {
                    0 -> groupViewModel.onChatThrowOut(selectedGroup.id!!, selectedUser.id!!)
                    1 -> dialog.dismiss()
                }
            }
            builder.show()

        }

    }

    private fun setTotalUsersInGroup(totalUsers: String) {
        val completeText = if (totalUsers == "1") {
            "$totalUsers Miembro"
        }else {
            "$totalUsers Miembros"
        }
        binding.totalUsers.text = completeText
    }

    private fun setDefaultData() {
        val receivedGroup: Group? = intent.getParcelableExtra("grupo_seleccionado")
        if (receivedGroup != null) {
            selectedGroup = receivedGroup
            receivedGroup.id?.let { userViewModel.onUsersGroup(receivedGroup.id!!) }
        } else {
            Toast.makeText(this, "Ha sucedido un error!", Toast.LENGTH_LONG).show()
            finish()
        }
    }

    private fun setDefaultView() {
        if(loginUser?.id != selectedGroup.adminId) {
            binding.addUser.visibility = View.INVISIBLE
            binding.addUser.isClickable = false
        }
    }

}