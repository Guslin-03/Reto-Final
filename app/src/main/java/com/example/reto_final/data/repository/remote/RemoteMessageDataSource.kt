package com.example.reto_final.data.repository.remote

import com.example.reto_final.data.model.Message
import com.example.reto_final.utils.Resource

class RemoteMessageDataSource :BaseDataSource(), RemoteMessageRepository {
    override suspend fun getMessages() = getResult{
        RetrofitClient.apiInterface.getMessages()
    }

    override suspend fun getMessagesFromGroup(idGroup: Int) = getResult {
        RetrofitClient.apiInterface.getMessageByChatId(idGroup)
    }

    override suspend fun createMessage(message: Message)= getResult {
       RetrofitClient.apiInterface.createMessage(message)
    }
}