package com.example.reto_final.ui.message

import android.Manifest.permission.POST_NOTIFICATIONS
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Intent
import android.os.IBinder
import android.util.Log
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.PermissionChecker.PERMISSION_GRANTED
import androidx.lifecycle.MutableLiveData
import com.example.reto_final.R
import com.example.reto_final.data.model.userGroup.UserGroup
import com.example.reto_final.data.model.message.Message
import com.example.reto_final.data.repository.local.group.RoomGroupDataSource
import com.example.reto_final.data.repository.local.message.MessageEnumClass
import com.example.reto_final.data.repository.local.message.RoomMessageDataSource
import com.example.reto_final.data.socket.SocketEvents
import com.example.reto_final.data.socket.SocketMessageRes
import com.example.reto_final.utils.MyApp
import com.example.reto_final.utils.MyApp.Companion.API_SERVER
import com.example.reto_final.utils.MyApp.Companion.API_SOCKET_PORT
import com.example.reto_final.utils.MyApp.Companion.AUTHORIZATION_HEADER
import com.example.reto_final.utils.MyApp.Companion.BEARER
import com.example.reto_final.utils.Resource
import com.google.gson.Gson
import io.socket.client.IO
import io.socket.emitter.Emitter
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.greenrobot.eventbus.EventBus
import org.json.JSONObject

class ChatsService : Service() {

    private val channelId = "download_channel"
    private val notificationId = 1
    private lateinit var serviceScope: CoroutineScope

    private val localGroupRepository = RoomGroupDataSource()
    private val localMessageRepository = RoomMessageDataSource()
    private val fileManager = FileManager(this)
    private val _savedMessage = MutableLiveData<Resource<Message>>()

    override fun onCreate() {
        super.onCreate()
        serviceScope = CoroutineScope(Dispatchers.Main)
        createNotificationChannel()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Log.i("services", "onStartCommand")
        val contentText = "Iniciando socket"
        startForeground(notificationId, createNotification(contentText))
        startSocket()
        return START_NOT_STICKY
    }

    override fun onBind(intent: Intent?): IBinder? = null

    private fun createNotificationChannel() {
        val channel = NotificationChannel(
            channelId,
            "Descargas Channel",
            NotificationManager.IMPORTANCE_LOW
        )
        val notificationManager = getSystemService(NotificationManager::class.java)
        notificationManager.createNotificationChannel(channel)
    }

    private fun createNotification(contentText: String): Notification {
        val context = this
        val intent = Intent(context, MessageActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(
            context,
            0,
            intent,
            PendingIntent.FLAG_IMMUTABLE
        )

        return NotificationCompat.Builder(context, channelId)
            .setContentTitle("Chat en directo")
            .setContentText(contentText)
            .setSmallIcon(R.drawable.resume_logo)
            .setContentIntent(pendingIntent)
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .build()
    }

    override fun onDestroy() {
        Log.i("services", "onDestroy")
        if (MyApp.userPreferences.mSocket.connected()) {
            MyApp.userPreferences.mSocket.disconnect()
        }
        super.onDestroy()
    }

    private fun startSocket() {
        val socketOptions = createSocketOptions()
        MyApp.userPreferences.mSocket = IO.socket("${API_SERVER}:${API_SOCKET_PORT}", socketOptions)
        MyApp.userPreferences.mSocket.on(SocketEvents.ON_CONNECT.value, onConnect())
        MyApp.userPreferences.mSocket.on(SocketEvents.ON_DISCONNECT.value, onDisconnect())
        MyApp.userPreferences.mSocket.on(SocketEvents.ON_MESSAGE_RECEIVED.value, onNewMessage())
        MyApp.userPreferences.mSocket.on(SocketEvents.ON_CHAT_JOIN.value, onChatJoin())
        MyApp.userPreferences.mSocket.on(SocketEvents.ON_CHAT_LEFT.value, onChatLeft())
        MyApp.userPreferences.mSocket.on(SocketEvents.ON_CHAT_ADDED.value, onChatAdded())
        MyApp.userPreferences.mSocket.on(SocketEvents.ON_CHAT_THROW_OUT.value, onChatThrowOut())
        serviceScope.launch {
            connect()
        }
    }

    private suspend fun connect() {
        withContext(Dispatchers.IO) {
            MyApp.userPreferences.mSocket.connect()
        }
    }

    private fun onConnect(): Emitter.Listener {
        return Emitter.Listener {
            EventBus.getDefault().post("connect")
            updateNotification("conectado")
        }
    }

    private fun onDisconnect(): Emitter.Listener {
        return Emitter.Listener {
            updateNotification("disConnect")
        }
    }

    private fun onNewMessage(): Emitter.Listener {
        return Emitter.Listener {
            Log.d("Prueba", "Lo recibio")
            val response = onJSONtoAnyClass(it[0], SocketMessageRes::class.java) as SocketMessageRes
            val newMessage = response.toMessage()
            val updatedMessage = onNewMessageOwner(newMessage)
            Log.d("Hola", "$updatedMessage")
            if (_savedMessage.value?.status != Resource.Status.ERROR) {
                EventBus.getDefault().post(newMessage)
            }
            updateNotification(response.message)
        }
    }

    private fun onNewMessageOwner(incomingMessage : Message) {
        serviceScope.launch {
            val loginUser = MyApp.userPreferences.getUser()
            if (loginUser != null) {
                val updateMessage = onIsFile(incomingMessage)
                _savedMessage.value = if (incomingMessage.userId == loginUser.id) {
                    localMessageRepository.updateMessage(updateMessage)
                } else {
                    Log.d("prueba1", "$updateMessage")
                    localMessageRepository.createMessage(updateMessage)
                }
            }
        }
    }

    private fun onIsFile(incomingMessage : Message) : Message {
        return if (incomingMessage.type == MessageEnumClass.FILE.toString()){
            val location = fileManager.saveBase64ToFile(incomingMessage.text)
            Message(incomingMessage.id,incomingMessage.idServer, location, incomingMessage.sent, incomingMessage.saved, incomingMessage.chatId, incomingMessage.userId, incomingMessage.type)
        }else{
            incomingMessage
        }
    }

    private fun onChatJoin(): Emitter.Listener {
        return Emitter.Listener {
            val response = onJSONtoAnyClass(it[0], UserGroup::class.java) as UserGroup
            updateNotification("${response.name} se ha unido al grupo.")
            serviceScope.launch {
                addUserToGroup(response)
            }
        }
    }

    private fun onChatAdded(): Emitter.Listener {
        return Emitter.Listener {
            val response = onJSONtoAnyClass(it[0], UserGroup::class.java) as UserGroup
            updateNotification("${response.adminName} ha añadido a ${response.name}.")
            serviceScope.launch {
                addUserToGroup(response)
            }
        }
    }

    private suspend fun addUserToGroup(userGroupRes: UserGroup) {
//        groupRepository.addUserToGroup(userGroupRes.roomId, userGroupRes.userId)
    }

    private fun onChatLeft(): Emitter.Listener {
        return Emitter.Listener {
            val response = onJSONtoAnyClass(it[0], UserGroup::class.java) as UserGroup
            updateNotification("${response.name} ha salido del grupo.")
            serviceScope.launch {
                leaveGroup(response)
            }
        }
    }

    private fun onChatThrowOut(): Emitter.Listener {
        return Emitter.Listener {
            val response = onJSONtoAnyClass(it[0], UserGroup::class.java) as UserGroup
            updateNotification("${response.adminName} ha expulsado a ${response.name}.")
            serviceScope.launch {
                addUserToGroup(response)
            }
        }
    }

    private suspend fun leaveGroup(userGroupRes: UserGroup) {
        localGroupRepository.leaveGroup(userGroupRes.roomId, userGroupRes.userId)
    }

    private fun updateNotification(contentText: String) {
        if (ActivityCompat.checkSelfPermission(this, POST_NOTIFICATIONS) == PERMISSION_GRANTED) {
            val notification = createNotification(contentText)

            val notificationManager = NotificationManagerCompat.from(this)
            notificationManager.notify(notificationId, notification)
        }
    }

    private fun createSocketOptions(): IO.Options {
        val options = IO.Options()
        val headers = mutableMapOf<String, MutableList<String>>()
        headers[AUTHORIZATION_HEADER] = mutableListOf(BEARER + MyApp.userPreferences.fetchHibernateToken())

        options.extraHeaders = headers

        return options
    }

    private fun onJSONtoAnyClass(data : Any, convertClass: Class<*>): Any? {
        return try {
            val jsonObject = data as JSONObject
            val jsonObjectString = jsonObject.toString()
            Gson().fromJson(jsonObjectString, convertClass)
        } catch (ex: Exception) {
//            Toast.makeText(context, ex.message, Toast.LENGTH_LONG).show()
            null
        }
    }

    private fun SocketMessageRes.toMessage() = Message(localId, messageServerId, message, sent, saved, room, authorId, type)

}