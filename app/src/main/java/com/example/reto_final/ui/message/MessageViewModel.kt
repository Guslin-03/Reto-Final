package com.example.reto_final.ui.message

import android.content.Context
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
import com.example.reto_final.utils.Resource
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MessageViewModel(private val messageLocalRepository: RoomMessageDataSource,
                       private val remoteMessageRepository: RemoteMessageRepository,
                        private val context: Context
) : ViewModel() {

    private val _message = MutableLiveData<Resource<List<Message>>>()
    val message : LiveData<Resource<List<Message>>> get() = _message

    private val _create = MutableLiveData<Resource<Boolean>>()
    val create : LiveData<Resource<Boolean>> get() = _create

    //init { updateMessageList() }
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
    private suspend fun create(message: Message) : Resource<Message> {
        return withContext(IO) {
            messageLocalRepository.createMessage(message)
        }
    }
    private suspend fun createRemote(message: Message) : Resource<Message> {
        return withContext(IO) {
            remoteMessageRepository.createMessage(message)
        }
    }
    fun onCreate(message: Message) {
        viewModelScope.launch {
            if (InternetChecker.isNetworkAvailable(context)) {
                createRemote(message)
            }else{
                create(message)
            }
            _create.value = Resource.success(true)
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