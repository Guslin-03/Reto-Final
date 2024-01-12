package com.example.reto_final.ui.message

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.DrawableCompat
import com.example.reto_final.R
import com.example.reto_final.data.model.Group
import com.example.reto_final.data.repository.local.message.RoomMessageDataSource
import com.example.reto_final.databinding.MessageActivityBinding
import com.example.reto_final.ui.group.GroupActivity
import com.example.reto_final.ui.group.GroupInfo
import com.example.reto_final.utils.Resource

class MessageActivity : AppCompatActivity(){

    private lateinit var binding: MessageActivityBinding
    private lateinit var messageAdapter: MessageAdapter
    private val messageRepository = RoomMessageDataSource()
    private val messageViewModel: MessageViewModel by viewModels { RoomMessageViewModelFactory(messageRepository) }
    private lateinit var group: Group
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = MessageActivityBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbarChatConfiguration)

        setDefaultData()

        messageAdapter = MessageAdapter()
        binding.messageList.adapter = messageAdapter

        messageViewModel.messaage.observe(this) {
            when(it.status) {
                Resource.Status.SUCCESS -> {
                    messageAdapter.submitList(it.data)
                }
                Resource.Status.ERROR -> {
                    Toast.makeText(this, it.message, Toast.LENGTH_LONG).show()
                }
                Resource.Status.LOADING -> {
                }

            }
        }

        binding.toolbarChatConfiguration.setOnMenuItemClickListener { item ->
            when (item.itemId) {
                R.id.infoGroup -> {
                    showGroupInfo()
                    true
                }
                R.id.addPeople -> {
                    addPeople()
                    //TODO crear activity para añadir usuarios a un grupo
                    true
                }
                R.id.leaveGroup -> {
                    leveGroup()
                    true
                }
                R.id.deleteGroup -> {
                    deleteGroup()
                    true
                }
                else -> false // Manejo predeterminado para otros elementos
            }
        }

    }

    private fun setDefaultData() {
        val receivedGroup: Group? = intent.getParcelableExtra("grupo_seleccionado")
        // Verificar si se recibió el objeto Group
        if (receivedGroup != null) {
            this.group = receivedGroup
            receivedGroup.id?.let { messageViewModel.updateMessageList(it) }
        } else {
            Log.d("Grupo recibido", "Objeto Group es nulo")
        }
    }

    private fun showGroupInfo() {
//        val intent = Intent(this, GroupInfo::class.java)
//        intent.putExtra("grupo_seleccionado", this.group.joinedUsers)
//        startActivity(intent)
//        finish()
    }

    private fun addPeople() {}

    private fun leveGroup() {
        //TODO llamar al viewModel para eliminar al usuario del grupo
        val intent = Intent(this, GroupActivity::class.java)
        startActivity(intent)
        finish()
    }

    private fun deleteGroup() {}

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.chat_configuration_top_menu,menu)

        binding.toolbarChatConfiguration.overflowIcon?.let {
            val color = ContextCompat.getColor(this, R.color.white)
            val newIcon = DrawableCompat.wrap(it)
            DrawableCompat.setTint(newIcon, color)
            binding.toolbarChatConfiguration.overflowIcon = newIcon
        }
        return true
    }


}