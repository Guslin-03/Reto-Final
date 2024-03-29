package com.example.reto_final.ui.message

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.provider.Settings
import android.util.Log
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.view.menu.MenuBuilder
import androidx.appcompat.view.menu.MenuPopupHelper
import androidx.appcompat.widget.PopupMenu
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.DrawableCompat
import com.example.reto_final.R
import com.example.reto_final.data.model.group.Group
import com.example.reto_final.data.model.InternetChecker
import com.example.reto_final.data.model.message.Message
import com.example.reto_final.data.model.message.MessageGetResponse
import com.example.reto_final.data.model.userGroup.UserChatInfo
import com.example.reto_final.data.repository.local.group.ChatEnumType
import com.example.reto_final.data.repository.local.group.RoomGroupDataSource
import com.example.reto_final.data.repository.local.message.MessageEnumClass
import com.example.reto_final.data.repository.local.message.RoomMessageDataSource
import com.example.reto_final.data.repository.local.user.UserRoleType
import com.example.reto_final.data.repository.remote.RemoteGroupDataSource
import com.example.reto_final.data.socket.SocketEvents
import com.example.reto_final.data.socket.SocketMessageReq
import com.example.reto_final.databinding.MessageActivityBinding
import com.example.reto_final.ui.group.GroupActivity
import com.example.reto_final.ui.group.GroupInfo
import com.example.reto_final.ui.group.GroupViewModel
import com.example.reto_final.ui.group.RoomGroupViewModelFactory
import com.example.reto_final.utils.MyApp
import com.example.reto_final.utils.Resource
import com.google.gson.Gson
import com.karumi.dexter.Dexter
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionDeniedResponse
import com.karumi.dexter.listener.PermissionGrantedResponse
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.single.PermissionListener
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import org.json.JSONObject
import java.util.Date
import kotlin.random.Random

@Suppress("DEPRECATION")
class MessageActivity : AppCompatActivity() {

    private lateinit var binding: MessageActivityBinding
    private lateinit var messageAdapter: MessageAdapter
    private val messageRepository = RoomMessageDataSource()
    private val messageViewModel: MessageViewModel by viewModels { RoomMessageViewModelFactory(messageRepository) }
    private val groupRepository = RoomGroupDataSource()
    private val remoteGroupRepository = RemoteGroupDataSource()
    private val groupViewModel: GroupViewModel by viewModels {
        RoomGroupViewModelFactory(
            groupRepository,
            remoteGroupRepository,
            applicationContext
        )
    }
    private lateinit var group: Group
    private val user = MyApp.userPreferences.getUser()
    private val CAMERA_REQUEST_CODE = 1
    private val IMAGE_REQUEST_CODE = 2
    private val FILE_REQUEST_CODE = 3
    private val CAMERA_PERMISSION_CODE = 4
    private val fileManager = FileManager(this)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = MessageActivityBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbarChatConfiguration)

        setDefaultData()
        messageAdapter = MessageAdapter(::onMapClickItem)

        binding.messageList.adapter = messageAdapter

        binding.messageList.postDelayed({
            if (messageAdapter.itemCount > 0) {
                binding.messageList.scrollToPosition(messageAdapter.itemCount - 1)
            }
        }, 200)

        messageViewModel.message.observe(this) {
            when (it.status) {
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

        messageViewModel.createLocalMessage.observe(this) {
            when (it.status) {
                Resource.Status.SUCCESS -> {
                    val newList = ArrayList(messageAdapter.currentList)
                    val newMessage = it.data
                    newList.add(newMessage)

                    messageAdapter.submitList(newList)

                    if (InternetChecker.isNetworkAvailable(this)) {
                        if (newMessage != null && group.id != null && newMessage.id != null) {
                            //SI ES IMAGEN, ENVIA EN BASE64

                            if(newMessage.type==MessageEnumClass.FILE.toString()){
                               val file=fileManager.convertFileToBase64(newMessage.text)
                               val socketMessage = SocketMessageReq(
                                   group.id!!,
                                   newMessage.id!!, file, newMessage.sent, newMessage.type
                               )

                               val jsonObject = JSONObject(Gson().toJson(socketMessage))
                               MyApp.userPreferences.mSocket.emit(
                                   SocketEvents.ON_SEND_MESSAGE.value,
                                   jsonObject
                               )
                           }else{
                               val socketMessage = SocketMessageReq(
                                   group.id!!,
                                   newMessage.id!!, newMessage.text, newMessage.sent, newMessage.type
                               )
                               val jsonObject = JSONObject(Gson().toJson(socketMessage))
                               MyApp.userPreferences.mSocket.emit(
                                   SocketEvents.ON_SEND_MESSAGE.value,
                                   jsonObject
                               )
                           }

                        }

                    }
                }

                Resource.Status.ERROR -> {
                    Toast.makeText(this, it.message, Toast.LENGTH_LONG).show()
                }

                Resource.Status.LOADING -> {
                }
            }
        }

        groupViewModel.groupPermissionToDelete.observe(this) {
            when (it.status) {
                Resource.Status.SUCCESS -> {
                    val groupPermission = it.data
                    if (groupPermission == 1) {
                        groupViewModel.onDelete(group)
                    } else {
                        Toast.makeText(this, R.string.toast_delete_group_permission, Toast.LENGTH_LONG).show()
                    }
                }

                Resource.Status.ERROR -> {
                    Toast.makeText(this, it.message, Toast.LENGTH_LONG).show()
                }

                Resource.Status.LOADING -> {
                }
            }
        }

        groupViewModel.delete.observe(this) {
            when (it.status) {
                Resource.Status.SUCCESS -> {
                    Toast.makeText(this, R.string.toast_delete_group, Toast.LENGTH_LONG)
                        .show()
                    goToGroups()
                }

                Resource.Status.ERROR -> {
                    Toast.makeText(this, R.string.toast_error_generic, Toast.LENGTH_LONG).show()
                }

                Resource.Status.LOADING -> {
                }
            }
        }

        groupViewModel.leaveGroup.observe(this) {
            when (it.status) {
                Resource.Status.SUCCESS -> {
                    Toast.makeText(this, R.string.toast_leave_group, Toast.LENGTH_LONG)
                        .show()
                    goToGroups()
                }

                Resource.Status.ERROR -> {
                    Toast.makeText(this, R.string.toast_leave_group_admin, Toast.LENGTH_LONG)
                        .show()
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
                    messageViewModel.onSendMessage(
                        message,
                        Date(),
                        MessageEnumClass.TEXT.toString(),
                        group.id!!,
                        user.id
                    )
                }
            }
        }
        binding.include.attatch.setOnClickListener {
            showAttachmentOptions(it)
        }
    }

    override fun onResume() {
        super.onResume()
        group.id?.let { messageViewModel.updateMessageList(it) }
    }

    private fun onMapClickItem(message: Message) {
        val messageClick = message.text
        if (messageClick.startsWith("https://www.google.com/maps?q=")) {
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(messageClick))
            startActivity(intent)
        } else if (messageClick.startsWith(getExternalFilesDir(null).toString() + "/RetoFinalPdf")) {
            fileManager.downloadPDF(message)
        }
    }

    private fun setDefaultData() {
        val receivedGroup: Group? = intent.getParcelableExtra("grupo_seleccionado")
        // Verificar si se recibió el objeto Group
        if (receivedGroup != null) {
            Log.d("Prueba", "" + receivedGroup.id)
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
        } else {
            Toast.makeText(this, R.string.toast_private_group, Toast.LENGTH_LONG).show()
        }
    }

    private fun showGroupInfo() {
        val intent = Intent(this, GroupInfo::class.java)
        intent.putExtra("grupo_seleccionado", this.group)
        startActivity(intent)
    }

    private fun goToGroups() {
        finish()
    }

    private fun userHasPermissionToDelete() {
        if (InternetChecker.isNetworkAvailable(applicationContext)){
            if (group.type == ChatEnumType.PRIVATE.toString() && userIsTeacher()) {
                popToDeleteGroup()
            } else if (group.type == ChatEnumType.PUBLIC.toString()) {
                popToDeleteGroup()
            }
        } else {
            Toast.makeText(this, R.string.toast_no_internet, Toast.LENGTH_LONG).show()
        }

    }

    private fun popToDeleteGroup() {
        if (user != null) {
            val options = arrayOf<CharSequence>(getString(R.string.accept), getString(R.string.cancel))
            val builder = AlertDialog.Builder(this)
            builder.setTitle(R.string.are_you_sure)
            builder.setItems(options) { dialog, which ->
                when (which) {
                    0 -> groupViewModel.onUserHasPermissionToDelete(group.id!!)
                    1 -> dialog.dismiss()
                }
            }
            builder.show()
        }
    }

    private fun userIsTeacher(): Boolean {
        if (user != null) {
            return user.roles.any { it.name == UserRoleType.Profesor.toString() }
        }
        return false
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.chat_configuration_top_menu, menu)

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
        Log.d("Prueba", "Se registra")
        EventBus.getDefault().register(this)
    }

    override fun onStop() {
        super.onStop()
        EventBus.getDefault().unregister(this)
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onSocketIncomingMessage(message: Message) {
        Log.d("Hola", "$message")
        if (message.chatId == group.id) {
            messageViewModel.updateMessageList(group.id!!)
        }

    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onReceivedMessageEvent(message: List<Message>) {
        if (group.id != null) {
            messageViewModel.updateMessageList(group.id!!)
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onPendingMessageEvent(message: List<MessageGetResponse>) {
        if (group.id != null) {
            messageViewModel.updateMessageList(group.id!!)
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onPendingLeaveGroup(listUserChatInfo: List<UserChatInfo>) {
        if (group.id != null) {
            for (userChatInfo in listUserChatInfo) {
                if (userChatInfo.chatId == group.id) {
                    goToGroups()
                }
            }
        }
    }

    @SuppressLint("RestrictedApi")
    private fun showAttachmentOptions(view: View) {
        val popupMenu = PopupMenu(this, view)
        val inflater: MenuInflater = popupMenu.menuInflater
        inflater.inflate(R.menu.popup_menu_attatchment, popupMenu.menu)

        // Con esto se visualizan los iconos
        try {
            val menuHelper = MenuPopupHelper(this, popupMenu.menu as MenuBuilder, view)
            menuHelper.setForceShowIcon(true)
            menuHelper.show()
        } catch (e: Exception) {
            e.printStackTrace()
            return
        }

        popupMenu.setOnMenuItemClickListener { item: MenuItem ->
            when (item.itemId) {
                R.id.action_camera -> {
                    openCamera()
                    true
                }

                R.id.action_file -> {
                    attachFile()
                    true
                }

                R.id.action_image -> {
                    attachImage()
                    true
                }

                R.id.action_location -> {
                    showMyLocation()
                    true
                }

                else -> false
            }
        }

    }

    private fun showMyLocation() {
        val latitude = String.format("%.6f", Random.nextDouble(-90.0, 90.0))
        val longitude = String.format("%.6f", Random.nextDouble(-180.0, 180.0))
        val mapLink = "https://www.google.com/maps?q=$latitude,$longitude"

        if (group.id != null && user != null) {
            messageViewModel.onSendMessage(
                mapLink,
                Date(),
                MessageEnumClass.TEXT.toString(),
                group.id!!,
                user.id
            )
        }
    }

    private fun openCamera() {
        if (checkSelfPermission(Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
            val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            startActivityForResult(takePictureIntent, CAMERA_REQUEST_CODE)
        } else {
            // Solicitar permiso si no está otorgado
            requestPermissions(arrayOf(Manifest.permission.CAMERA), CAMERA_PERMISSION_CODE)
        }
    }
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == CAMERA_PERMISSION_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                openCamera()
            } else {
                Toast.makeText(this, R.string.toast_permission_camera, Toast.LENGTH_SHORT).show()
            }
        }
    }

    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        var fileLocation=""
        if (requestCode == CAMERA_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            val imageBitmap = data?.extras?.get("data") as Bitmap
            fileLocation = fileManager.saveImageToFolder(imageBitmap)
        } else if (requestCode == IMAGE_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            if (data?.data != null) {
                val imageUri: Uri = data.data!!
                val imageBitmap = MediaStore.Images.Media.getBitmap(contentResolver, imageUri)
                fileLocation = fileManager.saveImageToFolder(imageBitmap)
            }
        } else if (requestCode == FILE_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            if (data?.data != null) {
                val fileUri: Uri = data.data!!
                fileLocation = fileManager.saveFileToFolder(fileUri)
            }
        }
        if (user!=null && resultCode==Activity.RESULT_OK){
            messageViewModel.onSendMessage(
                fileLocation,
                Date(),
                MessageEnumClass.FILE.toString(),
                group.id!!,
                user.id
            )
        }
    }

    private fun attachImage() {
        Dexter.withContext(this).withPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
            .withListener(object : PermissionListener {

                override fun onPermissionGranted(p0: PermissionGrantedResponse?) {
                    val intent = Intent(Intent.ACTION_GET_CONTENT)
                    intent.type = "image/*"
                    startActivityForResult(intent, IMAGE_REQUEST_CODE)
                }

                override fun onPermissionDenied(p0: PermissionDeniedResponse?) {
                    val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                    val uri = Uri.fromParts("package", packageName, null)
                    intent.data = uri
                    startActivity(intent)
                }

                override fun onPermissionRationaleShouldBeShown(
                    p0: PermissionRequest?,
                    p1: PermissionToken?
                ) {
                }

            }).check()
    }

    private fun attachFile() {
        Dexter.withContext(this).withPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
            .withListener(object : PermissionListener {

                override fun onPermissionGranted(p0: PermissionGrantedResponse?) {
                    val intent = Intent(Intent.ACTION_GET_CONTENT)
                    intent.type = "application/pdf"
                    startActivityForResult(intent, FILE_REQUEST_CODE)
                }

                override fun onPermissionDenied(p0: PermissionDeniedResponse?) {
                    val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                    val uri = Uri.fromParts("package", packageName, null)
                    intent.data = uri
                    startActivity(intent)
                }

                override fun onPermissionRationaleShouldBeShown(
                    p0: PermissionRequest?,
                    p1: PermissionToken?
                ) {
                }
            }).check()
    }
}