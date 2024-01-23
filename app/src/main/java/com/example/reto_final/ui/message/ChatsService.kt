package com.example.reto_final.ui.message

import android.Manifest.permission.POST_NOTIFICATIONS
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Intent
import android.os.Build
import android.os.IBinder
import android.util.Log
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.PermissionChecker.PERMISSION_GRANTED
import com.example.reto_final.R
import com.example.reto_final.data.socket.SocketEvents
import com.example.reto_final.data.socket.SocketMessageRes
import com.example.reto_final.utils.MyApp
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
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                "Descargas Channel",
                NotificationManager.IMPORTANCE_LOW
            )
            val notificationManager = getSystemService(NotificationManager::class.java)
            notificationManager.createNotificationChannel(channel)
        }
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
            .setSmallIcon(R.drawable.ic_launcher_foreground)
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
        MyApp.userPreferences.mSocket = IO.socket("${MyApp.API_SERVER}:${MyApp.API_SOCKET_PORT}", socketOptions)
        MyApp.userPreferences.mSocket.on(SocketEvents.ON_CONNECT.value, onConnect())
        MyApp.userPreferences.mSocket.on(SocketEvents.ON_DISCONNECT.value, onDisconnect())
        MyApp.userPreferences.mSocket.on(SocketEvents.ON_MESSAGE_RECEIVED.value, onNewMessage())
        serviceScope.launch {
            connect()
        }
    }

    private suspend fun connect() {
        withContext(Dispatchers.IO) {
            Log.d("Prueba", "Coonnect")
            MyApp.userPreferences.mSocket.connect()
        }
    }

    private fun onConnect(): Emitter.Listener {
        return Emitter.Listener {
            // Manejar el mensaje recibido
            Log.d("Prueba", "onConnect")
            // _connected.postValue(Resource.success(true))
            updateNotification("conectado")
        }
    }

    private fun onDisconnect(): Emitter.Listener {
        return Emitter.Listener {
            // Manejar el mensaje recibido

            Log.d("Prueba", "disConnect")
            updateNotification("disConnect")
            // _connected.postValue(Resource.success(false))
        }
    }

    private fun onNewMessage(): Emitter.Listener {
        return Emitter.Listener {
            Log.d("Prueba", "onNewMessage")
            onNewMessageJsonObject(it[0])
        }
    }

    private fun onNewMessageJsonObject(data : Any) {
        try {
            val jsonObject = data as JSONObject
            val jsonObjectString = jsonObject.toString()
            val message = Gson().fromJson(jsonObjectString, SocketMessageRes::class.java)

            // TODO guardar en ROOM

            EventBus.getDefault().post(message)
            updateNotification(message.message)

            // updateMessageListWithNewMessage(message)
        } catch (ex: Exception) {
//            Toast.makeText(context, ex.message, Toast.LENGTH_LONG).show()
        }
    }

    private fun updateNotification(contentText: String) {
        if (ActivityCompat.checkSelfPermission(this, POST_NOTIFICATIONS) == PERMISSION_GRANTED) {
            val notification = createNotification(contentText)

            val notificationManager = NotificationManagerCompat.from(this)
            notificationManager.notify(notificationId, notification)
        }
    }
/*
    private fun updateMessageListWithNewMessage(message: SocketMessageRes) {
        try {
            val incomingMessage = Message(null, message.message, message.dateTime, 1, message.authorId)
            val msgsList = _message.value?.data?.toMutableList()
            if (msgsList != null) {
                msgsList.add(incomingMessage)
                _message.postValue(Resource.success(msgsList))
            } else {
                _message.postValue(Resource.success(listOf(incomingMessage)))
            }
        } catch (ex: Exception) {
//            Toast.makeText(context, ex.message, Toast.LENGTH_LONG).show()
        }
    }


 */

    private fun createSocketOptions(): IO.Options {
        val options = IO.Options()

        // Add custom headers
        val headers = mutableMapOf<String, MutableList<String>>()
        // TODO el token tendria que salir de las sharedPrefernces para conectarse
        headers[MyApp.AUTHORIZATION_HEADER] = mutableListOf("Bearer " + MyApp.userPreferences.fetchHibernateToken())

        options.extraHeaders = headers

        return options
    }
}