package com.example.reto_final.ui.group

import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.example.reto_final.R
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
    private val remoteUserRepository=RemoteUserDataSource()
    private val userViewModel: UserViewModel by viewModels { RoomUserViewModelFactory(userRepository,remoteUserRepository) }
    private val loginUser = MyApp.userPreferences.getUser()
    private lateinit var selectedGroup : Group
    private lateinit var selectedUser : User
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = GroupInfoActivityBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setDefaultData()
        userAdapter = UserAdapter(
            ::onIsAdmin
        )
        binding.userList.adapter = userAdapter

        binding.addUser.setOnClickListener {
                // Obtener el FragmentManager
                val fragmentManager = supportFragmentManager

                // Iniciar la transacción
                val fragmentTransaction = fragmentManager.beginTransaction()

                // Crear una instancia de tu fragmento con la lista
                val myListFragment = UserFragment()

                // Reemplazar el contenedor con el fragmento
                fragmentTransaction.replace(R.id.fragmentContainer, myListFragment)

                // Agregar a la pila de retroceso (opcional)
                fragmentTransaction.addToBackStack(null)

                // Commit de la transacción
                fragmentTransaction.commit()
        }

        userViewModel.usersGroup.observe(this) {
            when(it.status) {
                Resource.Status.SUCCESS -> {
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
                    checkActionDelete()
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
            userViewModel.onUserIsAdmin(loginUser.id, selectedGroup.id!!)
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

}