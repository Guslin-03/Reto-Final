package com.example.reto_final.ui.message

import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.CreationExtras
import com.example.reto_final.data.model.Message
import com.example.reto_final.data.repository.remote.RetrofitClient
import com.example.reto_final.data.socket.SocketEvents
import com.example.reto_final.data.socket.SocketMessageReq
import com.example.reto_final.data.socket.SocketMessageRes
import com.example.reto_final.utils.MyApp
import com.example.reto_final.utils.Resource
import com.google.gson.Gson
import io.socket.client.IO

import io.socket.client.Socket
import io.socket.emitter.Emitter
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONObject

class SocketViewModel() : ViewModel() {

    private lateinit var mSocket: Socket

    private val _connected = MutableLiveData<Resource<Boolean>>()
    val connected : LiveData<Resource<Boolean>> get() = _connected

    private val _message = MutableLiveData<Resource<List<Message>>>()
    val message : LiveData<Resource<List<Message>>> get() = _message

    init { startSocket() }

    private fun startSocket() {
        val socketOptions = createSocketOptions()
        mSocket = IO.socket(MyApp.API_SOCKET, socketOptions)
        mSocket.on(SocketEvents.ON_CONNECT.value, onConnect())
        mSocket.on(SocketEvents.ON_DISCONNECT.value, onDisconnect())
        mSocket.on(SocketEvents.ON_MESSAGE_RECEIVED.value, onNewMessage())
        viewModelScope.launch {
            connect()
        }
    }

    private fun createSocketOptions(): IO.Options {
        val options = IO.Options()

        // Add custom headers
        val headers = mutableMapOf<String, MutableList<String>>()
        // TODO el token tendria que salir de las sharedPrefernces para conectarse
        headers[MyApp.AUTHORIZATION_HEADER] = mutableListOf("Bearer " + MyApp.userPreferences.fetchHibernateToken())

        options.extraHeaders = headers
        return options
    }

    private suspend fun connect() {
        withContext(Dispatchers.IO) {
            mSocket.connect()
        }
    }

    private fun onConnect(): Emitter.Listener {
        return Emitter.Listener {
            // Manejar el mensaje recibido
            _connected.postValue(Resource.success(true))
        }
    }

    private fun onDisconnect(): Emitter.Listener {
        return Emitter.Listener {
            // Manejar el mensaje recibido
            _connected.postValue(Resource.success(false))
        }
    }

    private fun onNewMessage(): Emitter.Listener {
        return Emitter.Listener {
            onNewMessageJsonObject(it[0])
        }
    }

    private fun onNewMessageJsonObject(data : Any) {
        try {
            val jsonObject = data as JSONObject
            val jsonObjectString = jsonObject.toString()
            val message = Gson().fromJson(jsonObjectString, SocketMessageRes::class.java)

            updateMessageListWithNewMessage(message)
        } catch (ex: Exception) {
//            Toast.makeText(context, ex.message, Toast.LENGTH_LONG).show()
        }
    }

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

    fun onSendMessage(message: String, groupName: String) {
        Log.d("Prueba", "onSendMessage $message")
        // la sala esta hardcodeada..
        val socketMessage = SocketMessageReq(groupName, message)
        val jsonObject = JSONObject(Gson().toJson(socketMessage))
        mSocket.emit(SocketEvents.ON_SEND_MESSAGE.value, jsonObject)
    }

}

class SocketViewModelFactory(
): ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>, extras: CreationExtras): T {
        return SocketViewModel() as T
    }

}

