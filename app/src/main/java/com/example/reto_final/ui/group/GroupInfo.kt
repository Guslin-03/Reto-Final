package com.example.reto_final.ui.group

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.example.reto_final.data.model.Group
import com.example.reto_final.databinding.GroupInfoActivityBinding
import com.example.reto_final.ui.user.UserAdapter

class GroupInfo: AppCompatActivity() {

    private lateinit var binding: GroupInfoActivityBinding
    private lateinit var userAdapter: UserAdapter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = GroupInfoActivityBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.userList.adapter = userAdapter

        setDefaultData()

    }

    private fun setDefaultData() {
//        val receivedGroup: Group? = intent.getParcelableExtra("grupo_seleccionado")
//        // Verificar si se recibió el objeto Group
//        if (receivedGroup != null) {
//            receivedGroup.id?.let { messageViewModel.updateMessageList(it) }
//        } else {
//            Log.d("Grupo recibido", "Objeto Group es nulo")
//        }
    }

}