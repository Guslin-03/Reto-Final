package com.example.reto_final.ui.message

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.CreationExtras
import com.example.reto_final.data.model.Message
import com.example.reto_final.data.repository.local.message.RoomMessageDataSource
import com.example.reto_final.utils.Resource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MessageViewModel(private val messageLocalRepository: RoomMessageDataSource) : ViewModel() {

    private val _messaage = MutableLiveData<Resource<List<Message>>>()
    val messaage : LiveData<Resource<List<Message>>> get() = _messaage

    private val _create = MutableLiveData<Resource<Boolean>>()
    val create : LiveData<Resource<Boolean>> get() = _create

    //init { updateMessageList() }
    fun updateMessageList(groupId: Int) {
        viewModelScope.launch {
            _messaage.value = getMessagesFromGroup(groupId)
        }
    }
    private suspend fun getMessagesFromGroup(groupId: Int) : Resource<List<Message>> {
        return withContext(Dispatchers.IO) {
            messageLocalRepository.getMessagesFromGroup(groupId)
        }
    }
    private suspend fun create(text: String, groupId: Int) : Resource<Message> {
        return withContext(Dispatchers.IO) {
            val message = Message(null, text, groupId)
            messageLocalRepository.createMessage(message)
        }
    }
    fun onCreate(text:String, groupId: Int) {
        viewModelScope.launch {
            create(text, groupId)
            _create.value = Resource.success(true)
        }
    }

}

class RoomMessageViewModelFactory(
    private val roomMessageRepository: RoomMessageDataSource
): ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>, extras: CreationExtras): T {
        return MessageViewModel(roomMessageRepository) as T
    }

}