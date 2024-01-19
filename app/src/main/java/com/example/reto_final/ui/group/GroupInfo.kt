package com.example.reto_final.ui.group

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.example.reto_final.data.model.Group
import com.example.reto_final.data.model.User
import com.example.reto_final.data.repository.local.user.RoomUserDataSource
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
    private val userViewModel: UserViewModel by viewModels { RoomUserViewModelFactory(userRepository,remoteUserRepository) }
    private val loginUser = MyApp.userPreferences.getUser()
    private lateinit var userFragment : UserFragment
    private var selectedGroup = Group()
    private lateinit var selectedUser : User
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = GroupInfoActivityBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setDefaultView()
        setDefaultData()
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

        userViewModel.isAdmin.observe(this) {
            when(it.status) {
                Resource.Status.SUCCESS -> {
                    setDefaultView()
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

    }

    private fun onIsAdmin(user: User) {
        selectedUser = user

        if (loginUser != null && selectedGroup.id != null) {
            if (userViewModel.isAdmin.value?.data == true)
                checkActionDelete()
        }

    }

    private fun checkActionDelete() {
        if (selectedUser.id != null && selectedGroup.id != null) {

            val options = arrayOf<CharSequence>("Aceptar", "Cancelar")
            val builder = AlertDialog.Builder(this)
            builder.setTitle("¿Seguro que quieres expulsar a ${selectedUser.name} ${selectedUser.surname} del grupo?")
            builder.setItems(options) { dialog, which ->
                when (which) {
                    0 -> userViewModel.onDelete(selectedUser.id!!, selectedGroup.id!!)
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
        if(userViewModel.isAdmin.value?.data == false) {
            binding.addUser.visibility = View.INVISIBLE
            binding.addUser.isClickable = false
        }
    }

}