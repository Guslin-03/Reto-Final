package com.example.reto_final.ui.message

import android.content.Context
import android.widget.Toast
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.CreationExtras
import com.example.reto_final.data.model.InternetChecker
import com.example.reto_final.data.model.Message
import com.example.reto_final.data.repository.local.message.RoomMessageDataSource
import com.example.reto_final.data.repository.remote.RemoteMessageRepository
import com.example.reto_final.data.socket.SocketEvents
import com.example.reto_final.data.socket.SocketMessageRes
import com.example.reto_final.utils.Resource
import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import io.socket.client.Socket
import io.socket.emitter.Emitter
import org.json.JSONObject

class MessageViewModel(private val messageLocalRepository: RoomMessageDataSource,
                       private val remoteMessageRepository: RemoteMessageRepository,
                        private val context: Context
) : ViewModel() {

    private lateinit var mSocket: Socket

    private val _connected = MutableLiveData<Resource<Boolean>>()
    val connected : LiveData<Resource<Boolean>> get() = _connected

    private val _message = MutableLiveData<Resource<List<Message>>>()
    val message : LiveData<Resource<List<Message>>> get() = _message

    private val _create = MutableLiveData<Resource<Boolean>>()
    val create : LiveData<Resource<Boolean>> get() = _create

    //init { updateMessageList() }
    init { startSocket() }
    fun updateMessageList(groupId: Int) {
        viewModelScope.launch {
            _message.value  = if (InternetChecker.isNetworkAvailable(context)) {
                getMessagesFromGroupRemote(groupId)
            } else {
                getMessagesFromGroup(groupId)
            }
        }
    }
    private suspend fun getMessagesFromGroup(groupId: Int) : Resource<List<Message>> {
        return withContext(IO) {
            messageLocalRepository.getMessagesFromGroup(groupId)
        }
    }
    private suspend fun getMessagesFromGroupRemote(groupId: Int) : Resource<List<Message>> {
        return withContext(IO) {
            remoteMessageRepository.getMessagesFromGroup(groupId)
        }
    }
    private suspend fun create(text: String, groupId: Int, userId: Int) : Resource<Message> {
        return withContext(IO) {
            val message = Message(null, text, null,groupId, userId)
            messageLocalRepository.createMessage(message)
        }
    }
    private suspend fun createRemote(text: String, groupId: Int, userId: Int) : Resource<Message> {
        return withContext(IO) {
            val message = Message(null, text, null,groupId, userId)
            remoteMessageRepository.createMessage(message)
        }
    }
    fun onCreate(text:String, groupId: Int, userId: Int) {
        viewModelScope.launch {
            if (InternetChecker.isNetworkAvailable(context)) {
                createRemote(text, groupId, userId)
            }else{
                create(text, groupId, userId)
            }
            _create.value = Resource.success(true)
        }
    }

    private fun startSocket() {
//        val socketOptions = createSocketOptions()
//        mSocket = IO.socket(SOCKET_HOST, socketOptions)
        mSocket.on(SocketEvents.ON_CONNECT.value, onConnect())
        mSocket.on(SocketEvents.ON_DISCONNECT.value, onDisconnect())
        mSocket.on(SocketEvents.ON_MESSAGE_RECEIVED.value, onNewMessage())
        viewModelScope.launch {
            connect()
        }
    }

    //TODO No creo que sea necesario porque esta info se setea luego en la peticion
//    private fun createSocketOptions(): IO.Options {
//        val options = io.socket.client.IO.Options()
//
//        // Add custom headers
//        val headers = mutableMapOf<String, MutableList<String>>()
//        // TODO el token tendria que salir de las sharedPrefernces para conectarse
//        headers[AUTHORIZATION_HEADER] = mutableListOf("Bearer AppJwt:1:Mikel")
//
//        options.extraHeaders = MyApp.userPreferences.fetchHibernateToken()
//        return options
//    }

    private suspend fun connect() {
        withContext(IO) {
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
            Toast.makeText(context, ex.message, Toast.LENGTH_LONG).show()
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
            Toast.makeText(context, ex.message, Toast.LENGTH_LONG).show()
        }
    }

}

class RoomMessageViewModelFactory(
    private val roomMessageRepository: RoomMessageDataSource,
    private val remoteMessageRepository: RemoteMessageRepository,
    private val context:Context
): ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>, extras: CreationExtras): T {
        return MessageViewModel(roomMessageRepository, remoteMessageRepository, context) as T
    }

}