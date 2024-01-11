package com.example.reto_final.ui.message

import android.os.Bundle
import android.os.Message
import android.util.Log
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.example.reto_final.data.model.Group
import com.example.reto_final.data.repository.local.message.RoomMessageDataSource
import com.example.reto_final.databinding.MessageActivityBinding
import com.example.reto_final.utils.Resource

class MessageActivity : AppCompatActivity(){

    private lateinit var binding: MessageActivityBinding
    private lateinit var messageAdapter: MessageAdapter
    private val messageRepository = RoomMessageDataSource()
    private val messageViewModel: MessageViewModel by viewModels { RoomMessageViewModelFactory(messageRepository) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = MessageActivityBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbarPersonalConfiguration)

        val receivedGroup: Group? = intent.getParcelableExtra("grupo_seleccionado")
        messageAdapter = MessageAdapter()
        binding.messageList.adapter = messageAdapter

        // Verificar si se recibió el objeto Group
        if (receivedGroup != null) {
            receivedGroup.id?.let { messageViewModel.updateMessageList(it) }
        } else {
            Log.d("Grupo recibido", "Objeto Group es nulo")
        }

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

    }

}