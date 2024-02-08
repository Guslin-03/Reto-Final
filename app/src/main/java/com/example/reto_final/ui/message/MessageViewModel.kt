package com.example.reto_final.ui.message

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.CreationExtras
import com.example.reto_final.data.model.message.Message
import com.example.reto_final.data.repository.local.message.RoomMessageDataSource
import com.example.reto_final.utils.Resource
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.Date

class MessageViewModel(private val messageLocalRepository: RoomMessageDataSource
) : ViewModel() {

    private val _message = MutableLiveData<Resource<List<Message>>>()
    val message : LiveData<Resource<List<Message>>> get() = _message

    private val _createLocalMessage = MutableLiveData<Resource<Message>>()
    val createLocalMessage : LiveData<Resource<Message>> get() = _createLocalMessage

    fun updateMessageList(groupId: Int) {
        viewModelScope.launch {
            _message.value  = getMessagesFromGroup(groupId)
        }
    }
    private suspend fun getMessagesFromGroup(groupId: Int) : Resource<List<Message>> {
        return withContext(IO) {
            messageLocalRepository.getMessagesFromGroup(groupId)
        }
    }

    fun onSendMessage(message: String, sent: Date, type:String,groupId: Int, authorId: Int) {
        viewModelScope.launch {
            val sendMessage = sendMessage(Message(message, sent.time, type, groupId, authorId))
            val localId = sendMessage.data?.id!!
            if (localId != 0) {
                sendMessage.status = Resource.Status.SUCCESS
                _createLocalMessage.value = sendMessage
            }
        }
    }

    private suspend fun sendMessage(sendMessage: Message) : Resource<Message> {
        return withContext(IO) {
            messageLocalRepository.createMessage(sendMessage)
        }
    }
}

class RoomMessageViewModelFactory(
    private val roomMessageRepository: RoomMessageDataSource): ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>, extras: CreationExtras): T {
        return MessageViewModel(roomMessageRepository) as T
    }

}