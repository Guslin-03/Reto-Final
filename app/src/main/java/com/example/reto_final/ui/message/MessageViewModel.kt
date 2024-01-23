package com.example.reto_final.ui.message

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.CreationExtras
import com.example.reto_final.data.model.Group
import com.example.reto_final.data.model.InternetChecker
import com.example.reto_final.data.model.Message
import com.example.reto_final.data.repository.local.message.RoomMessageDataSource
import com.example.reto_final.data.repository.remote.RemoteMessageRepository
import com.example.reto_final.data.socket.SocketEvents
import com.example.reto_final.data.socket.SocketMessageReq
import com.example.reto_final.utils.MyApp
import com.example.reto_final.utils.Resource
import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.util.Date

class MessageViewModel(private val messageLocalRepository: RoomMessageDataSource,
                       private val remoteMessageRepository: RemoteMessageRepository,
                        private val context: Context
) : ViewModel() {

    private val _message = MutableLiveData<Resource<List<Message>>>()
    val message : LiveData<Resource<List<Message>>> get() = _message

    private val _incomingMessage = MutableLiveData<Resource<Message>>()
    val incomingMessage : LiveData<Resource<Message>> get() = _incomingMessage

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
    private suspend fun saveIncomingMessage(message: Message) : Resource<Message> {
        return withContext(IO) {
            messageLocalRepository.createMessage(message)
        }
    }

    fun onSaveIncomingMessage(message: Message, selectedGroup: Group) {
        viewModelScope.launch {
            val newMessage = saveIncomingMessage(message)
            if (newMessage.data?.groupId == selectedGroup.id) {
                newMessage.status = Resource.Status.SUCCESS
                _incomingMessage.value = newMessage
            }
        }
    }

    fun onSendMessage(message: String, sent: Date, groupId: Int, authorId: Int) {

        if (InternetChecker.isNetworkAvailable(context)) {
            val socketMessage = SocketMessageReq(groupId, message, sent)
            val jsonObject = JSONObject(Gson().toJson(socketMessage))
            MyApp.userPreferences.mSocket.emit(SocketEvents.ON_SEND_MESSAGE.value, jsonObject)
        } else {
            viewModelScope.launch {
                val sendMessage = Message(null, message, sent, Date(), groupId, authorId)
                sendMessage(sendMessage)
            }
        }

    }

    private suspend fun sendMessage(sendMessage: Message) : Resource<Message> {
        return withContext(IO) {
            messageLocalRepository.createPendingMessage(sendMessage)
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