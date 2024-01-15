package com.example.reto_final.ui.group

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.example.reto_final.data.model.Group
import com.example.reto_final.data.repository.local.user.RoomUserDataSource
import com.example.reto_final.databinding.GroupInfoActivityBinding
import com.example.reto_final.ui.user.RoomUserViewModelFactory
import com.example.reto_final.ui.user.UserAdapter
import com.example.reto_final.ui.user.UserViewModel
import com.example.reto_final.utils.Resource

class GroupInfo: AppCompatActivity() {

    private lateinit var binding: GroupInfoActivityBinding
    private lateinit var userAdapter: UserAdapter
    private val userRepository = RoomUserDataSource()
    private val userViewModel: UserViewModel by viewModels { RoomUserViewModelFactory(userRepository) }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = GroupInfoActivityBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setDefaultData()
        userAdapter = UserAdapter()
        binding.userList.adapter = userAdapter

        userViewModel.usersGroup.observe(this) {
            when(it.status) {
                Resource.Status.SUCCESS -> {
                    Log.d("Prueba", "Success")
                    userAdapter.submitList(it.data)
                }
                Resource.Status.ERROR -> {
                    Log.d("Prueba", "Error")
                    Toast.makeText(this, it.message, Toast.LENGTH_LONG).show()
                }
                Resource.Status.LOADING -> {
                }

            }
        }
    }

    private fun setDefaultData() {
        val receivedGroup: Group? = intent.getParcelableExtra("grupo_seleccionado")
        // Verificar si se recibió el objeto Group
        if (receivedGroup != null) {
            receivedGroup.id?.let { userViewModel.onUsersGroup(receivedGroup.id) }
        } else {
            Log.d("Grupo recibido", "Objeto Group es nulo")
        }
    }

}