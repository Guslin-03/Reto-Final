package com.example.reto_final.ui.message

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.DrawableCompat
import com.example.reto_final.R
import com.example.reto_final.data.model.Group
import com.example.reto_final.data.model.Message
import com.example.reto_final.data.repository.local.group.ChatEnumType
import com.example.reto_final.data.repository.local.group.RoomGroupDataSource
import com.example.reto_final.data.repository.local.message.RoomMessageDataSource
import com.example.reto_final.data.repository.local.user.UserRoleType
import com.example.reto_final.data.repository.remote.RemoteGroupDataSource
import com.example.reto_final.data.repository.remote.RemoteMessageDataSource
import com.example.reto_final.databinding.MessageActivityBinding
import com.example.reto_final.ui.group.GroupActivity
import com.example.reto_final.ui.group.GroupInfo
import com.example.reto_final.ui.group.GroupViewModel
import com.example.reto_final.ui.group.RoomGroupViewModelFactory
import com.example.reto_final.utils.MyApp
import com.example.reto_final.utils.Resource
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import java.util.Date

class MessageActivity : AppCompatActivity(){

    private lateinit var binding: MessageActivityBinding
    private lateinit var messageAdapter: MessageAdapter
    private val messageRepository = RoomMessageDataSource()
    private val messageViewModel: MessageViewModel by viewModels { RoomMessageViewModelFactory(messageRepository, remoteMessageRepository, applicationContext) }
    private val groupRepository = RoomGroupDataSource()
    private val remoteGroupRepository = RemoteGroupDataSource()
    private val remoteMessageRepository = RemoteMessageDataSource()
    private val groupViewModel: GroupViewModel by viewModels { RoomGroupViewModelFactory(groupRepository, remoteGroupRepository, applicationContext) }
    private lateinit var group: Group
    private val user = MyApp.userPreferences.getUser()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = MessageActivityBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbarChatConfiguration)

        setDefaultData()

        messageAdapter = MessageAdapter(group)
        binding.messageList.adapter = messageAdapter

        messageViewModel.message.observe(this) {
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

        messageViewModel.incomingMessage.observe(this) {
            when(it.status) {
                Resource.Status.SUCCESS -> {
                    messageAdapter.currentList.add(it.data)
                }
                Resource.Status.ERROR -> {
                    Toast.makeText(this, it.message, Toast.LENGTH_LONG).show()
                }
                Resource.Status.LOADING -> {
                }
            }
        }

        groupViewModel.groupPermissionToDelete.observe(this) {
            when(it.status) {
                Resource.Status.SUCCESS -> {
                    groupViewModel.onDelete(group)
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
                    Toast.makeText(this, "El grupo ha sido eliminado con éxito.", Toast.LENGTH_LONG).show()
                    goToGroups()
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
                    Log.d("MensajeGrupo", "Entra en success")
                    Toast.makeText(this, "Has abandonado el grupo con éxito.", Toast.LENGTH_LONG).show()
                    goToGroups()
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
                R.id.leaveGroup -> {
                    userCanLeaveGroup()
                    true
                }
                R.id.deleteGroup -> {
                    userHasPermissionToDelete()
                    true
                }
                else -> false // Manejo predeterminado para otros elementos
            }
        }
        binding.include.send.setOnClickListener {
            val message = binding.include.inputMessage.text.toString()
            if (message.isNotBlank()) {
                binding.include.inputMessage.setText("")
                if (group.id != null && user != null) {
                    messageViewModel.onSendMessage(message, Date(), group.id!!, user.id)
                }

            }
        }

        startChatService(this)
    }

    private fun setDefaultData() {
        val receivedGroup: Group? = intent.getParcelableExtra("grupo_seleccionado")
        // Verificar si se recibió el objeto Group
        if (receivedGroup != null) {
            this.group = receivedGroup
            receivedGroup.id?.let { messageViewModel.updateMessageList(it) }
            binding.configurationTitle.text = this.group.name
        } else {
            Log.d("Grupo recibido", "Objeto Group es nulo")
        }
    }

    private fun userCanLeaveGroup() {

        if (group.type == ChatEnumType.PUBLIC.toString()) {
            if (user != null) {
                if (group.id != null) {
                    groupViewModel.onLeaveGroup(group.id!!, user.id)
                }
            }
        }else {
            Toast.makeText(this, "No puedes abandonar un grupo privado.", Toast.LENGTH_LONG).show()
        }

    }

    private fun showGroupInfo() {
        val intent = Intent(this, GroupInfo::class.java)
        intent.putExtra("grupo_seleccionado", this.group)
        startActivity(intent)
        finish()
    }

    private fun goToGroups() {
        val intent = Intent(this, GroupActivity::class.java)
        startActivity(intent)
        finish()
    }

    private fun userHasPermissionToDelete() {
        if (user != null) {
            if (group.type == ChatEnumType.PRIVATE.toString() && userIsTeacher()) {
                popToDeleteGroup()
            }else if(group.type == ChatEnumType.PUBLIC.toString()){
                popToDeleteGroup()
            }
        }
    }

    private fun popToDeleteGroup() {
        if (user != null) {
            val options = arrayOf<CharSequence>("Aceptar", "Cancelar")
            val builder = AlertDialog.Builder(this)
            builder.setTitle("¿Seguro que quieres eliminar el grupo?")
            builder.setItems(options) { dialog, which ->
                when (which) {
                    0 -> groupViewModel.onUserHasPermissionToDelete(group.id!!, user.id)
                    1 -> dialog.dismiss()
                }
            }
            builder.show()
        }
    }

    private fun userIsTeacher() : Boolean {
        if (user != null) {
            return user.roles.any { it.type == UserRoleType.PROFESOR.toString() }
        }
        return false
    }
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


    // EVENT BUS
    override fun onStart() {
        super.onStart()
        EventBus.getDefault().register(this)
    }

    override fun onStop() {
        super.onStop()
        EventBus.getDefault().unregister(this)
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun a(string: String) {
        Log.d("prueba", "onNotificationEmployee $string")
    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onSocketIncomingMessage(message: Message) {
        messageViewModel.onSaveIncomingMessage(message, group)
    }


    private fun startChatService(context: Context) {
        val intent = Intent(context, ChatsService::class.java)
        ContextCompat.startForegroundService(context, intent)
    }
}